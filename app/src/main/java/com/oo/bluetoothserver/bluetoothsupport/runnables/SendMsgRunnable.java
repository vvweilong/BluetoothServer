package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SendMsgRunnable implements Runnable {


    /**
     * Created by zhuxiaolong on 2018/1/4.
     * 非阻塞线程 发送消息时使用
     */

    private final String TAG=getClass().getSimpleName();
    private BluetoothSocket socket;
    private String message;

    public SendMsgRunnable(BluetoothSocket socket, String message) {
        this.socket = socket;
        this.message = message;
    }


    @Override
    public void run() {
        try {
            Log.i(TAG, "run: sending message");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(message+"\n");
            writer.flush();
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
