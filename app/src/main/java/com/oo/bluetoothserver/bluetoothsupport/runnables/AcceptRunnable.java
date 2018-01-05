package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by zhuxiaolong on 2018/1/4.
 * 主要执行的是 通过阻塞等待新的连接接入
 * 并提供一个方法 获取新接入的 socket
 */

public class AcceptRunnable implements Runnable {
    /**
     * Created by zhuxiaolong on 2018/1/4.
     * 主要执行的是 通过阻塞等待新的连接接入
     * 并提供一个方法 获取新接入的 socket
     */
    private final String TAG = getClass().getSimpleName();

    private BluetoothSocket socket;
    private String name;
    private String uuid;
    private boolean secquare = false;
    private int timeout = 0;

    private ConnectRequestCallback callback;

    public AcceptRunnable(String name, String uuid, boolean secquare, ConnectRequestCallback callback) {
        this.name = name;
        this.uuid = uuid;
        this.secquare = secquare;
        this.callback = callback;
    }

    public AcceptRunnable(String name, String uuid, boolean secquare, ConnectRequestCallback callback, int timeout) {
        this.name = name;
        this.uuid = uuid;
        this.secquare = secquare;
        this.timeout = timeout;
        this.callback = callback;
    }


    @Override
    public void run() {
        if (name == null || name.equals("")) {
            throw new NullPointerException("have no name for the bluetooth connection");
        }

        if (uuid == null || uuid.equals("")) {
            throw new NullPointerException("have no uuid for the bluetooth connection");
        }

        if (callback == null) {
            throw new NullPointerException("have no callback for the connection");
        }

        try {
            BluetoothServerSocket serverSocket;
//                    BluetoothAdapter.getDefaultAdapter()
//                            .listenUsingRfcommWithServiceRecord(this.name, UUID.fromString(this.uuid)) :
//                    BluetoothAdapter.getDefaultAdapter()
//                            .listenUsingInsecureRfcommWithServiceRecord(this.name, UUID.fromString(this.uuid));

            serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(this.name, UUID.fromString(uuid));
            this.socket = serverSocket.accept();

            if (callback != null) {
                callback.onConnected(socket);
                //释放线程内的引用对象
//                releaseObject();
            }
//            while (socket.isConnected()) {
//                Thread.sleep(1000);
//                Log.i(TAG, "run: serversocket is connected ");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseObject() {
        callback = null;
        name = null;
        uuid = null;
        this.socket = null;
    }


    public interface ConnectRequestCallback {
        void onConnected(BluetoothSocket socket);
    }
}
