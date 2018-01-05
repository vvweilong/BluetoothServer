package com.oo.bluetoothserver.bluetoothsupport.runnables;

import android.bluetooth.BluetoothSocket;
import android.database.CharArrayBuffer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    private final String TAG = getClass().getSimpleName();
    private Handler mainHandler;
    private BluetoothSocket socket;
    public static final int RECEIVE_MESSAGE = 101;
    public static final int CONNECTION_LOSE = 201;

    public ListenRunnable(Handler mainHandler, BluetoothSocket socket) {
        this.mainHandler = mainHandler;
        this.socket = socket;
    }

    @Override
    public void run() {
        //循环 只要还在连接中  就执行输入流的监听
        Log.i(TAG, "run: ");
//        if (mainHandler == null) {
//            throw new NullPointerException("handler is null no message will return");
//        }
//        if (socket == null) {
//            throw new NullPointerException("socket null");
//        }
        try {
            Log.i(TAG, "run: in try");
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder contents = new StringBuilder();

            CharArrayBuffer charArrayBuffer = new CharArrayBuffer(128);
            while (socket.isConnected()) {
                //先清空
                contents.delete(0, contents.length());
                Log.i(TAG, "run: int while");
                contents.append(bufferedReader.readLine());
                Log.i(TAG, "run: while isConnected" + charArrayBuffer.toString());
                Thread.sleep(500);
                Message message = mainHandler.obtainMessage();
                message.obj = contents.toString();
                message.what = RECEIVE_MESSAGE;
                mainHandler.sendMessage(message);
            }
            Log.i(TAG, "run: listener finished");
        } catch (IOException e) {
//            e.printStackTrace();
            Log.i(TAG, "run: io exception" + e.getMessage());
            Log.i(TAG, "run: io exception  socket is connect" + socket.isConnected());

            mainHandler.sendEmptyMessage(CONNECTION_LOSE);

        } catch (InterruptedException e) {
            Log.i(TAG, "run: ");
            e.printStackTrace();
        }
    }
}
