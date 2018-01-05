package com.oo.bluetoothserver.bluetoothsupport.cs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.oo.bluetoothserver.bluetoothsupport.runnables.AcceptRunnable;
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
 * 除了基本的 蓝牙通信功能
 * 还应该有 如何控制蓝牙的指导性操作
 * 如 检查权限、开启蓝牙使能、关闭蓝牙等
 */

public class BluetoothServer extends BaseBluetoothCS {
    private final String TAG = getClass().getSimpleName();
    private final String sec_uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private final String insec_uuid = "8ce255c0-200a-11e0-ac64-0800200c9a66";




    private ArrayList<BluetoothSocket> sockets;
    private ExecutorService connectPool = new ThreadPoolExecutor(5,
            5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ListenRunnable.RECEIVE_MESSAGE) {
                Log.i(TAG, "handleMessage: " + msg.obj);
                if (messageReceivedCallback != null) {
                    messageReceivedCallback.onReceivedMsg((String) msg.obj);
                }
            } else if (msg.what == MESSAGE_CONNECTED) {
                Log.i(TAG, "handleMessage: start listen");
                BluetoothSocket socket = (BluetoothSocket) msg.obj;
                connectPool.execute(new ListenRunnable(handler,socket));
            }
        }
    };


    public BluetoothServer(Context context) {
        super(context);
        sockets = new ArrayList<>();
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        Log.i(TAG, "BluetoothServer: " + name);
    }

    @Override
    public void sendMessage(String msg) {
        for (BluetoothSocket socket : sockets) {
            if (socket.isConnected()) {
                Log.i(TAG, "sendMessage: socket");
                connectPool.execute(new SendMsgRunnable(socket, msg));
            }
        }

    }


    public void start() {
        Log.i(TAG, "start: ");
        //开启蓝牙
        //创建一个等待连接的线程
        AcceptRunnable acceptRunnable = new AcceptRunnable(TAG, sec_uuid, false, new AcceptRunnable.ConnectRequestCallback() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                Log.i(TAG, "onConnected: ");
                //保存对象
                sockets.add(socket);
                //开启读取信息线程 进行循环监听
                Message msg = handler.obtainMessage();
                msg.what = MESSAGE_CONNECTED;
                msg.obj = socket;
                handler.sendMessage(msg);
            }
        });
        //分配线程执行等待连接操作
        connectPool.execute(acceptRunnable);
    }

    public void shutdown() {
        for (BluetoothSocket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectPool.shutdown();
        for (Runnable runnable : connectPool.shutdownNow()) {
        }
    }


}
