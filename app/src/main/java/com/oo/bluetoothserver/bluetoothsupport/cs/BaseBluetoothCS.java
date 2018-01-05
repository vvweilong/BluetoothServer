package com.oo.bluetoothserver.bluetoothsupport.cs;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by zhuxiaolong on 2018/1/4.
 * bluetooth server/client 的父类
 * 主要负责蓝牙硬件方面的处理
 * 1、检查硬件支持
 * 2、检查权限支持
 * 3、检查硬件使能
 */

public abstract class BaseBluetoothCS {

    //需要 context 来执行权限检查
    private Context context;
    protected BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final int REQUEST_CODE = 1;
    public final int REQUEST_ENABLE_BLUETOOTH = 11;
    protected final int MESSAGE_CONNECTED = 100;
    private final String TAG = getClass().getSimpleName();

    public BaseBluetoothCS(Context context) {
        this.context = context;
    }

    /**
     * 检查硬件是否支持
     * 如果 defaultbluetoothAdapter 是 null
     * 说明硬件不支持蓝牙
     *
     * @return true 表示支持 false 不支持
     */
    public boolean checkHardWare() {
        return bluetoothAdapter != null;
    }

    /**
     * 检查用户是否授权蓝牙权限
     *
     * @return true 已经获取权限 false 没有获取权限或用户拒绝授权
     */
    public boolean hasPermission() {
        int result = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求用户授权
     * 分为两种情况
     * 1、第一次授权 直接弹出权限请求
     * 2、用户拒绝过授权 弹出解释说明
     */

    public void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)) {
            //需要显示 授权解释
            // TODO: 2018/1/4 show a dialog

        } else {
            //直接申请授权
            String[] permissions = {Manifest.permission.BLUETOOTH};
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }

    /**
     * 将授权结果拦截处理 减少 view 层的代码
     */
    public void onRequestPermissionResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult: 目前一定会成功先不处理");

        //授权后 打开蓝牙
        setAvalableBluetooth(activity);
    }

    /**
     * 开启蓝牙请求处理
     */
    public boolean onStartActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                //打开蓝牙成功
                //建立
                return true;
            } else {
                Log.i(TAG, "onStartActivityResult: ");
            }
        }
        return false;
    }

    /**
     * 开启蓝牙
     */
    public void setAvalableBluetooth(Activity activity) {
        //首先判断 蓝牙是否已经启动
        //如果已经启动 设置可见性
        //如果没有启动 提示用户开启蓝牙 并设置可见时间
        if (!bluetoothAdapter.isEnabled()) {
            Intent requestEnableInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //添加 可见性 action 请求 request discoverable
            requestEnableInten.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置 可见时间  最长300秒
            requestEnableInten.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            //开启弹框请求
            activity.startActivityForResult(requestEnableInten, REQUEST_ENABLE_BLUETOOTH);
        } else {
            Intent avaliableIntnt = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //添加 可见性 action 请求 request discoverable
            avaliableIntnt.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置 可见时间  最长300秒
            avaliableIntnt.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivityForResult(avaliableIntnt, REQUEST_ENABLE_BLUETOOTH);

        }
    }

    abstract public void sendMessage(String msg);

    protected MessageReceivedCallback messageReceivedCallback;

    public void setMessageReceivedCallback(MessageReceivedCallback messageReceivedCallback) {
        this.messageReceivedCallback = messageReceivedCallback;
    }
}
