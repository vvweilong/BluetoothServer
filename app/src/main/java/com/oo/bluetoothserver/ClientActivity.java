package com.oo.bluetoothserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ClientActivity extends AppCompatActivity {
    /**
     * 这是客户端模式界面
     * 主要首先是列表展示扫描结果
     * 点击列表进行连接
     * 连接成功后转入 消息界面
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
    }
}
