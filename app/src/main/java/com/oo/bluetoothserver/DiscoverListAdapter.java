package com.oo.bluetoothserver;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zhuxiaolong on 2018/1/4.
 */

public class DiscoverListAdapter extends BaseAdapter {
    private Context context;

    public DiscoverListAdapter(Context context) {
        this.context = context;
    }

    public DiscoverListAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    public void setDevices(ArrayList<BluetoothDevice> devices) {
        this.devices = devices;
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    private ArrayList<BluetoothDevice> devices;

    @Override
    public int getCount() {
        return devices == null ? 0 : devices.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.deivce_item, null);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView mac = convertView.findViewById(R.id.mac);
        title.setText(devices.get(position).getName());
        mac.setText(devices.get(position).getAddress());
        return convertView;
    }
}
