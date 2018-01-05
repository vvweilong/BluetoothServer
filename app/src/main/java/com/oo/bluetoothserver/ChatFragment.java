package com.oo.bluetoothserver;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oo.bluetoothserver.bluetoothsupport.cs.BaseBluetoothCS;
import com.oo.bluetoothserver.bluetoothsupport.cs.MessageReceivedCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    public ChatFragment() {
        // Required empty public constructor
    }

    private BaseBluetoothCS bluetoothCS;

    public void setBluetoothCS(BaseBluetoothCS bluetoothClent) {
        this.bluetoothCS = bluetoothClent;

        bluetoothClent.setMessageReceivedCallback(new MessageReceivedCallback() {
            @Override
            public void onReceivedMsg(String msg) {
                Log.i(TAG, "onReceivedMsg: ");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(receiveText.getText());
                stringBuilder.append(msg);
                receiveText.setText(stringBuilder.toString());
            }
        });
    }


    private View root;
    private EditText inputText;
    private TextView receiveText;
    private Button sendBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_chat, container, false);
            inputText = root.findViewById(R.id.edittext);
            receiveText = root.findViewById(R.id.text);
            sendBtn = root.findViewById(R.id.button);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothCS.sendMessage(inputText.getText().toString());
                }
            });
        }
        if (createViewCallback != null) {
            createViewCallback.onViewCreated();
        }
        return root;
    }

    private DiscoverFragment.CreateViewCallback createViewCallback;

    public void setCreateViewCallback(DiscoverFragment.CreateViewCallback createViewCallback) {
        this.createViewCallback = createViewCallback;
    }

    public interface CreateViewCallback {
        void onViewCreated();
    }

}
