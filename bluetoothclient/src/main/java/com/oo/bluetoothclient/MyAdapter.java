package com.oo.bluetoothclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zhuxiaolong on 2017/12/29.
 */

public class MyAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> datalist;

    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
    }

    public void setDatalist(ArrayList<BluetoothDevice> datalist) {
        this.datalist = datalist;
        notifyDataSetChanged();
    }

    public ArrayList<BluetoothDevice> getDatalist() {
        return datalist;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        TextView textView = convertView.findViewById(R.id.name);
        textView.setText(datalist.get(position).getName() + "");
        textView = convertView.findViewById(R.id.mac);
        textView.setText(datalist.get(position).getAddress() + "");
        return convertView;
    }
}
