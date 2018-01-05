package com.oo.bluetoothserver.bluetoothsupport.cs;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.oo.bluetoothserver.bluetoothsupport.runnables.ConnectRunnable;
import com.oo.bluetoothserver.bluetoothsupport.runnables.ListenRunnable;
import com.oo.bluetoothserver.bluetoothsupport.runnables.SendMsgRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuxiaolong on 2018/1/3.
 * 与 BluetoothServer同理 这个类需要执行一定的蓝牙准备工作
 * 所以  抽象出一个父类进行蓝牙准备工作功能的描述
 */

public class BluetoothClent extends BaseBluetoothCS {
    private static final String TAG = "BluetoothClent";
    private static final String sec_uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private static final String insec_uuid = "8ce255c0-200a-11e0-ac64-0800200c9a66";
    private ArrayList<BluetoothSocket> sockets;
    private ExecutorService connectPool = new ThreadPoolExecutor(5,
            5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
    private DiscoverCallback discoverCallback;

    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == ListenRunnable.RECEIVE_MESSAGE) {
                //接收到消息 回调给 UI
                Log.i(TAG, "handleMessage: is received a msg");
                Log.i(TAG, "handleMessage: callback not null " + messageReceivedCallback);
                if (messageReceivedCallback != null) {

                    messageReceivedCallback.onReceivedMsg((String) msg.obj);
                }
            } else if (msg.what == MESSAGE_CONNECTED) {
                Log.i(TAG, "handleMessage: start listen");
                BluetoothSocket socket = (BluetoothSocket) msg.obj;
                connectPool.execute(new ListenRunnable(mainHandler, socket));
            }
        }
    };


    public BluetoothClent(Context context) {
        super(context);
        sockets = new ArrayList<>();
        bluetoothDevices = new ArrayList<>();
    }

    @Override
    public void sendMessage(String msg) {
        //发送 消息线程
        Log.i(TAG, "sendMessage: ");
        for (BluetoothSocket socket : sockets) {
            if (socket.isConnected()) {
                Log.i(TAG, "sendMessage:socket ");
                connectPool.execute(new SendMsgRunnable(socket, msg));
            }
        }

    }


    private ArrayList<BluetoothDevice> bluetoothDevices;
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: ");
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                }
                if (!bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "onReceive: ");
                if (discoverCallback != null) {
                    discoverCallback.onDiscoverFinished(bluetoothDevices);
                }
            }
        }
    };

    /**
     * 添加广播监听的目标 action
     */
    private IntentFilter createBluetoothFilter() {
        IntentFilter filter = new IntentFilter();
        //扫描部分 发现设备广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //开始执行扫描操作
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //设置可见性请求
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //结束扫描操作
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //连接部分的 连接断开广播
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //设备部分的 使能广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        return filter;
    }

    public void discoverDevice(final Activity activity, long timeout) {
        activity.registerReceiver(bluetoothReceiver, createBluetoothFilter());
        bluetoothAdapter.startDiscovery();
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: postdelay cancel discover & unregister receiver");
                bluetoothAdapter.cancelDiscovery();
                activity.unregisterReceiver(bluetoothReceiver);
                if (discoverCallback != null) {
                    discoverCallback.onDiscoverFinished(bluetoothDevices);
                }
            }
        }, (timeout > 5000 && timeout < 10000) ? timeout : 5000);
    }


    public void connect(final BluetoothDevice device) {
        final ConnectRunnable connectRunnable = new ConnectRunnable(sec_uuid, false, new ConnectRunnable.ConnectToServerCallback() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                Log.i(TAG, "onConnected: ");
                //持有 socket 对象
                sockets.add(socket);
                //监听消息
//                connectPool.execute(new ListenRunnable(mainHandler, socket));
                //回调 ui
                Message msg = mainHandler.obtainMessage();
                msg.what = MESSAGE_CONNECTED;
                msg.obj = socket;
                mainHandler.sendMessage(msg);
                if (connectionCallback != null) {
                    connectionCallback.onConnected();
                }
            }
        }, device);
        connectPool.execute(connectRunnable);
    }

    public void disconnect() {
        for (BluetoothSocket socket : sockets) {
            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDiscoverCallback(DiscoverCallback discoverCallback) {
        this.discoverCallback = discoverCallback;
    }

    public interface DiscoverCallback {
        void onDiscoverFinished(ArrayList<BluetoothDevice> list);
    }


    private ConnectionCallback connectionCallback;

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public interface ConnectionCallback {
        void onConnected();

        void onDisconnected();
    }


}
