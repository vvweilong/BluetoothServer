package com.oo.bluetoothclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuxiaolong on 2018/1/3.
 * 与 BluetoothServer同理 这个类需要执行一定的蓝牙准备工作
 * 所以  抽象出一个父类进行蓝牙准备工作功能的描述
 */

public class BluetoothClent {
    private static final String TAG = "BluetoothClent";
    private static final String sec_uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private static final String insec_uuid = "8ce255c0-200a-11e0-ac64-0800200c9a66";
    private ArrayList<BluetoothSocket> sockets;
    private ExecutorService connectPool = new ThreadPoolExecutor(3,
            5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));


    public void connect(BluetoothDevice device) {
        connectPool.execute(new ConnectRunnable(device));
    }


    private static class ConnectRunnable implements Runnable {

        private BluetoothDevice device;

        public ConnectRunnable(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            try {
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(sec_uuid));
                socket.connect();
                Log.i(TAG, "run: start connect to server " + (socket == null));
                BufferedReader bufferedReader = null;
                if (socket != null) {
                    while (!socket.isConnected()) {
                        Log.i(TAG, "run: while ing");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String line = bufferedReader.readLine();
                        Log.i(TAG, "run: readline" + line);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    ;


}
