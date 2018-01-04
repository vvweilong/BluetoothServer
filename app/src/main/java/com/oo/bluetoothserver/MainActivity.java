package com.oo.bluetoothserver;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oo.bluetoothserver.bluetoothsupport.cs.BluetoothServer;

/**
 * 这部分 主要是建立一个 蓝牙服务器 等待接受蓝牙连接请求
 * 并返回一个连接成功的 字符串给 client
 */

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private String uuid = "testbluetooth";

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    private TextView textView;
    private Button resetBtn;
    private int REQUEST_ENABLE_BLUETOOTH = 11;

    private BluetoothServer bluetoothServer;

    private Button createBtn;
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBtn = findViewById(R.id.create);
        resetBtn = findViewById(R.id.reset);
        textView = findViewById(R.id.textview);
        bluetoothServer = new BluetoothServer();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    /**
     * 开启蓝牙功能
     * */
    private void createBluetoothServer() {
        //首先检查 蓝牙状态
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent avaliableIntnt=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //添加 可见性 action 请求 request discoverable
                avaliableIntnt.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //设置 可见时间  最长300秒
                avaliableIntnt.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(avaliableIntnt);
                bluetoothServer.start();
            } else {//如果 没有打开蓝牙 弹出对话框 邀请用户打开蓝牙
                Intent requestEnableInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //添加 可见性 action 请求 request discoverable
                requestEnableInten.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //设置 可见时间  最长300秒
                requestEnableInten.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                //开启弹框请求
                startActivityForResult(requestEnableInten, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果蓝牙使能结果为成功
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK) {
            bluetoothServer.start();
        }
    }




    /**
     * 检查权限  包括一系列判断流程
     */
    private void checkPermission() {
        Log.i(TAG, "checkPermission: ");
        //判断是否已经授权
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        //判断检查结果
        if (result == PackageManager.PERMISSION_GRANTED) {
            //已经授权
            Toast.makeText(this, "已经拥有授权", Toast.LENGTH_SHORT).show();
            createBluetoothServer();
        } else if (result == PackageManager.PERMISSION_DENIED) {
            //没有授权或已经拒绝过
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {
                //需要显示 解释对话框   用户拒绝过权限请求
            } else {
                //直接请求权限
                requestPermission(Manifest.permission.BLUETOOTH);
            }
        }
    }

    private final int PERMISSION_REQUEST_CODE = 11;

    /**
     * 发起权限请求
     */
    private void requestPermission(String... permissions) {
        Log.i(TAG, "requestPermission: ");
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
    }


}
