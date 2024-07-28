package com.lianxin.location.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.SharedPreferencesUtils;
import com.lianxin.location.demo.utils.SocketUtil;
import com.lianxin.location.demo.utils.Util;

/**
 * @author Andrew.Lee
 * @version 1.0
 * @create 2011-5-28 下午02:26:20
 * @see
 */

public class SocketActivity extends BaseActivity {
    EditText editText = null, ip, port;
    Button sendButton = null, save;
    TextView display = null;
    SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance("socket");
    SocketUtil socketUtil = null;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String content = (String) msg.obj;
                if (Util.isNotEmpty(content) && display != null) {
                    display.setText(display.getText().toString() + "\n"
                            + "服务器发来的消息--：" + content);
                }
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.setLayoutId(R.layout.clientsocket);
        super.onCreate(savedInstanceState);
        editText = (EditText) findViewById(R.id.message);
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        sendButton = (Button) findViewById(R.id.send);
        display = (TextView) findViewById(R.id.display);
        sendButton = (Button) findViewById(R.id.send);
        save = (Button) findViewById(R.id.save);
        String ipStr = sharedPreferencesUtils.get("IP");
        String myport = sharedPreferencesUtils.get("PORT");


        socketUtil = new SocketUtil();
        socketUtil.setIP("43.138.182.48");
        socketUtil.setPORT("8080");
        socketUtil.setHandler(handler);
        socketUtil.init(SocketActivity.this);
        socketUtil.resetClient();

        ip.setText(socketUtil.getIP());
        port.setText(socketUtil.getPORT());
//        if(Util.isNotEmpty(ipStr)){
//            ip.setText(ipStr);
//        }
//        if(Util.isNotEmpty(myport)){
//            port.setText(myport+"");
//        }
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                String sendText = editText.getText().toString();
//                sendMsg(sendText);
//                GGAData="$CMD=IMEI=863184063186108";
                GGAData="123";
                socketUtil.sendMsg(GGAData);
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                socketUtil.resetClient();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketUtil.disconnect();
    }


}