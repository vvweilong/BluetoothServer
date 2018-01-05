package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by zhuxiaolong on 2018/1/4.
 * 主要功能为 client 执行连接到 server 的过程
 * 由 bluetoothdevice 发起 connect 的阻塞过程
 */

public class ConnectRunnable implements Runnable {
    /**
     * Created by zhuxiaolong on 2018/1/4.
     * 主要功能为 client 执行连接到 server 的过程
     * 由 bluetoothdevice 发起 connect 的阻塞过程
     */

    private final String TAG = getClass().getSimpleName();
    private String uuid;
    private boolean secquare = false;
    private ConnectToServerCallback callback;
    private BluetoothDevice device;

    public ConnectRunnable(String uuid, boolean secquare, ConnectToServerCallback callback, BluetoothDevice device) {
        this.uuid = uuid;
        this.secquare = secquare;
        this.callback = callback;
        this.device = device;
    }

    @Override
    public void run() {
        if (device == null) {
            throw new NullPointerException("no deivce");
        }

        if (uuid == null || uuid.equals("")) {
            throw new NullPointerException("no target uuid");
        }

        if (callback == null) {
            throw new NullPointerException("no callback ");
        }


        try {
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
//            BluetoothSocket socket = secquare ?
//                    device.createRfcommSocketToServiceRecord(UUID.fromString(uuid)) :
//                    device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
            socket.connect();

            if (callback != null) {
                callback.onConnected(socket);
            }
//            while (socket.isConnected()) {
//                Thread.sleep(1000);
//                Log.i(TAG, "run:client socket is connected ");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface ConnectToServerCallback {
        void onConnected(BluetoothSocket socket);
    }
}
