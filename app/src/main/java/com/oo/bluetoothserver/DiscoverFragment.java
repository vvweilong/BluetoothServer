package com.oo.bluetoothserver;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.oo.bluetoothserver.bluetoothsupport.cs.BluetoothClent;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {
    private String TAG = getClass().getSimpleName();

    public DiscoverFragment() {
        // Required empty public constructor
    }

    private View root;
    private ListView listView;
    private Button discoverBtn;
    private BluetoothClent bluetoothClent;


    public void setBluetoothClent(final BluetoothClent bluetoothClent) {
        this.bluetoothClent = bluetoothClent;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = ((DiscoverListAdapter) listView.getAdapter()).getDevices().get(position);
                bluetoothClent.connect(device);
            }
        });
        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothClent.discoverDevice(getActivity(), 5000);
            }
        });
        bluetoothClent.setDiscoverCallback(new BluetoothClent.DiscoverCallback() {
            @Override
            public void onDiscoverFinished(ArrayList<BluetoothDevice> list) {
                listView.setAdapter(new DiscoverListAdapter(getActivity(), list));
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: ");
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_discover, container, false);
            discoverBtn = root.findViewById(R.id.discover);
            listView = root.findViewById(R.id.listview);

        }
        if (createViewCallback != null) {
            createViewCallback.onViewCreated();
        }
        return root;
    }

    private CreateViewCallback createViewCallback;

    public void setCreateViewCallback(CreateViewCallback createViewCallback) {
        this.createViewCallback = createViewCallback;
    }

    public interface CreateViewCallback {

        void onViewCreated();
    }
}
