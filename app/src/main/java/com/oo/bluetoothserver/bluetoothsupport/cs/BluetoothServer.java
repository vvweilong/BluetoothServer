package com.oo.bluetoothserver.bluetoothsupport.cs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.oo.bluetoothserver.bluetoothsupport.runnables.AcceptRunnable;
import com.oo.bluetoothserver.bluetoothsupport.runnables.ListenRunnable;

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

public class BluetoothServer extends BaseBluetoothCS{
    private final String TAG = getClass().getSimpleName();
    private final String sec_uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private final String insec_uuid = "8ce255c0-200a-11e0-ac64-0800200c9a66";
    private ArrayList<BluetoothSocket> sockets;
    private ExecutorService connectPool = new ThreadPoolExecutor(3,
            5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
    
    private Handler handler=new Handler(Looper.getMainLooper());


    public BluetoothServer() {
        sockets = new ArrayList<>();
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        Log.i(TAG, "BluetoothServer: " + name);
    }
    
    public void start() {
        //创建一个等待连接的线程
        AcceptRunnable acceptRunnable = new AcceptRunnable(TAG, sec_uuid, false, new AcceptRunnable.ConnectRequestCallback() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                //保存对象
                sockets.add(socket);
                //开启读取信息线程 进行循环监听
                ListenRunnable listenRunnable=new ListenRunnable(handler,socket);
                connectPool.execute(listenRunnable);
            }
        });
        //分配线程执行等待连接操作
        connectPool.execute(acceptRunnable);
    }


}
