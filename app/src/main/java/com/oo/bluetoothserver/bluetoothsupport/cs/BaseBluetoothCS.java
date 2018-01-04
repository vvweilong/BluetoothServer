package com.oo.bluetoothserver.bluetoothsupport.cs;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by zhuxiaolong on 2018/1/4.
 * bluetooth server/client 的父类
 * 主要负责蓝牙硬件方面的处理
 * 1、检查硬件支持
 * 2、检查权限支持
 * 3、检查硬件使能
 */

public class BaseBluetoothCS {

    //需要 context 来执行权限检查
    private Context context;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final int REQUEST_CODE=1;

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
     * */

    public void requestPermission(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)) {
            //需要显示 授权解释

        }else {
            //直接申请授权
            String[] permissions={Manifest.permission.BLUETOOTH};
            ActivityCompat.requestPermissions(activity,permissions,1);
        }
    }
    /**
     * 将授权结果拦截处理 减少 view 层的代码
     * */
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

    }





}
