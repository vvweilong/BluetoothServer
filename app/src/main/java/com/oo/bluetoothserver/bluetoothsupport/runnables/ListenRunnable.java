package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhuxiaolong on 2018/1/4.
 * 这个线程主要用于 循环监听输入流
 * 输出流 只在需要输出时创建即可
 * 接收消息时间不可控所以需要实时监控
 * 对于接受到消息的返回方式采用传入 handler 进行信息传递
 * 由于监听需要采取循环形式 需要设置循环条件
 * 可暂定为 传入 socket 循环判断 socket 的连接状态
 * 线程的等待情况 是否应该采用 waite 方式？ 释放锁 来降低 cpu 的占用？
 */

public class ListenRunnable implements Runnable {
    /**
     * Created by zhuxiaolong on 2018/1/4.
     * 这个线程主要用于 循环监听输入流
     * 输出流 只在需要输出时创建即可
     * 接收消息时间不可控所以需要实时监控
     * 对于接受到消息的返回方式采用传入 handler 进行信息传递
     * 由于监听需要采取循环形式 需要设置循环条件
     * 可暂定为 传入 socket 循环判断 socket 的连接状态
     * 线程的等待情况 是否应该采用 waite 方式？ 释放锁 来降低 cpu 的占用？
     */

    private Handler mainHandler;
    private BluetoothSocket socket;

    public ListenRunnable(Handler mainHandler, BluetoothSocket socket) {
        this.mainHandler = mainHandler;
        this.socket = socket;
    }
    @Override
    public void run() {
        //循环 只要还在连接中  就执行输入流的监听
        if (mainHandler == null) {
            throw new NullPointerException("handler is null no message will return");
        }
        if (socket == null) {
            throw new NullPointerException("socket null");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder contents = new StringBuilder();
            Message message = mainHandler.obtainMessage();
            while (socket.isConnected()) {
                contents.append(reader.readLine());
                contents.append("\n");
                message.obj = contents.toString();
                message.what = 1;
                mainHandler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
