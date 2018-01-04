package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SendMsgRunnable implements Runnable {


    /**
     * Created by zhuxiaolong on 2018/1/4.
     * 非阻塞线程 发送消息时使用
     */

    private BluetoothSocket socket;
    private String message;

    public SendMsgRunnable(BluetoothSocket socket, String message) {
        this.socket = socket;
        this.message = message;
    }


    @Override
    public void run() {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
