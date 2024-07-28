package com.lianxin.location.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.MyConst;
import com.lianxin.location.demo.utils.Util;


public class HelpActivity extends BaseActivity {
    TextView item1,item2,item3;
    public static String titleS="帮助说明";
    public static  int index=0;
    EditText name;
    EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        initTitle();

        setText(R.id.tv,"北微定位软件\n1、本软件支持单点定位，RTK定位等\n2、需要先在续费服务中设置千寻帐号\n3、在云服务设置中设置云服务IP和端口号。" +
                "\n4、在设备设置中，选择可用的IM设务，并勾选。");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTitle() {
        TextView title=findViewById(R.id.title);
        if(!TextUtils.isEmpty(titleS)){
            title.setText(titleS);
        }
        ImageView bacBtn=findViewById(R.id.back_btn);
        bacBtn.setVisibility(View.VISIBLE);
        bacBtn.setImageResource(R.drawable.back);
        bacBtn.setOnClickListener(v -> finish());

//        ImageView rb=findViewById(R.id.right_btn);
//        rb.setVisibility(View.VISIBLE);
//        rb.setOnClickListener(v->{
//            AddActivity.type=1;
//            startActivity(new Intent(this,AddActivity.class));
//        });

    }

}
