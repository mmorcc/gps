package com.lianxin.location.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.Util;


public class SetActivity extends BaseActivity {
    TextView item1,item2,item3;
    public static String titleS="系统设置";
    public static  int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initTitle();
        findViewById(R.id.item1).setOnClickListener(o->{
            Util.toAc(context,JingduActivity.class);
        });
        findViewById(R.id.item2).setOnClickListener(o->{
            Util.toAc(context,ServerActivity.class);
        });
        findViewById(R.id.item3).setOnClickListener(o->{
            Util.toAc(context,DeviceActivity.class);
        });
        findViewById(R.id.item4).setOnClickListener(o->{
            Util.toAc(context,PayActivity.class);
        });
        findViewById(R.id.item5).setOnClickListener(o->{
            Util.toAc(context,VersionActivity.class);
        });
        findViewById(R.id.item6).setOnClickListener(o->{
            Util.toAc(context,HelpActivity.class);
        });
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
