package com.oo.bluetoothclient;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private final String TAG = getClass().getSimpleName();

    private ListView listView;
    private MyAdapter adapter;
    private Button scanBtn;
    private Button disconnectBtn;

    private BluetoothClent bluetoothClent;

    private ArrayList<BluetoothDevice> bluetoothDevices;

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                }

                bluetoothDevices.add(device);
                adapter.notifyDataSetChanged();
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        bluetoothDevices = new ArrayList<>();
        adapter = new MyAdapter(this);
        adapter.setDatalist(bluetoothDevices);
        listView.setAdapter(adapter);

        bluetoothClent = new BluetoothClent();

        scanBtn = findViewById(R.id.scan);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        disconnectBtn = findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothClent.connect(bluetoothDevices.get(position));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(bluetoothReceiver, createBluetoothFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(bluetoothReceiver);
    }

    /**
     * 添加广播监听的目标 action
     */
    private IntentFilter createBluetoothFilter() {
        IntentFilter filter = new IntentFilter();
        //扫描部分 发现设备广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //开始执行扫描操作
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //设置可见性请求
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //结束扫描操作
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //连接部分的 连接断开广播
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //设备部分的 使能广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        return filter;
    }

    /**
     * 开启蓝牙功能
     */
    private void createBluetoothClient() {
        //首先检查 蓝牙状态
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            } else {//如果 没有打开蓝牙 弹出对话框 邀请用户打开蓝牙
                Intent requestEnableInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //开启弹框请求
                startActivityForResult(requestEnableInten, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    private Handler handler = new Handler();

    /**
     * 检查权限  包括一系列判断流程
     */
    private void checkPermission() {
        Log.i(TAG, "checkPermission: ");
        String targetPermission = Manifest.permission.BLUETOOTH;
        //判断是否已经授权
        int result = ActivityCompat.checkSelfPermission(this, targetPermission);
        //判断检查结果
        if (result == PackageManager.PERMISSION_GRANTED) {
            //已经授权
            Toast.makeText(this, "已经拥有授权", Toast.LENGTH_SHORT).show();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                }
            }, 5000);
        } else if (result == PackageManager.PERMISSION_DENIED) {
            //没有授权或已经拒绝过
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, targetPermission)) {
                //需要显示 解释对话框   用户拒绝过权限请求
            } else {
                //直接请求权限
                requestPermission(targetPermission);
            }
        }
    }

    private final int PERMISSION_REQUEST_CODE = 11;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK) {
            createBluetoothClient();
        }
    }

    /**
     * 发起权限请求
     */
    private void requestPermission(String... permissions) {
        Log.i(TAG, "requestPermission: ");
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * 处理请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
    }


}
