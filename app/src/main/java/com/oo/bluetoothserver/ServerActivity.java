package com.oo.bluetoothserver;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.oo.bluetoothserver.bluetoothsupport.cs.BluetoothServer;

public class ServerActivity extends AppCompatActivity {

    private BluetoothServer bluetoothServer;
    private ChatFragment chatFragment;

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        bluetoothServer = new BluetoothServer(this);

        chatFragment = new ChatFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
        chatFragment.setCreateViewCallback(new DiscoverFragment.CreateViewCallback() {
            @Override
            public void onViewCreated() {
                chatFragment.setBluetoothCS(bluetoothServer);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (bluetoothServer.checkHardWare()) {
            if (bluetoothServer.hasPermission()) {
                //开启蓝牙
                Log.i(TAG, "onStart: set bluetooth avalable");
                bluetoothServer.setAvalableBluetooth(this);
                //执行完 会进入到 onActivityResult
            } else {
                Log.i(TAG, "onStart: request permission");
                bluetoothServer.requestPermission(this);
                //执行完 会进入到 onRequestPermissionsResult
                //并自动进入 onActivityResult
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothServer.shutdown();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (bluetoothServer.onStartActivityResult(this, requestCode, resultCode, data)) {
        Log.i(TAG, "onActivityResult: ");
        bluetoothServer.start();
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        bluetoothServer.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }


}
