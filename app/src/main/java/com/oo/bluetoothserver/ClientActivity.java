package com.oo.bluetoothserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.oo.bluetoothserver.bluetoothsupport.cs.BluetoothClent;

public class ClientActivity extends AppCompatActivity {
    /**
     * 这是客户端模式界面
     * 主要首先是列表展示扫描结果
     * 点击列表进行连接
     * 连接成功后转入 消息界面
     */
    private final String TAG = getClass().getSimpleName();
    private BluetoothClent bluetoothClient;
    private DiscoverFragment discoverFragment;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        fragmentInit();
        bluetoothClient = new BluetoothClent(this);
        bluetoothClient.setConnectionCallback(new BluetoothClent.ConnectionCallback() {
            @Override
            public void onConnected() {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
            }

            @Override
            public void onDisconnected() {

            }
        });

    }

    private void fragmentInit() {
        discoverFragment = new DiscoverFragment();
        chatFragment = new ChatFragment();
        chatFragment.setCreateViewCallback(new DiscoverFragment.CreateViewCallback() {
            @Override
            public void onViewCreated() {
                chatFragment.setBluetoothCS(bluetoothClient);
            }
        });
        discoverFragment.setCreateViewCallback(new DiscoverFragment.CreateViewCallback() {
            @Override
            public void onViewCreated() {
                discoverFragment.setBluetoothClent(bluetoothClient);
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container, discoverFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bluetoothClient.checkHardWare()) {
            if (bluetoothClient.hasPermission()) {
                bluetoothClient.setAvalableBluetooth(this);
            } else {
                bluetoothClient.requestPermission(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bluetoothClient.onStartActivityResult(this, requestCode, resultCode, data)) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        bluetoothClient.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }
}
