package com.lianxin.location.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianxin.location.demo.R;


public class BiaodingActivity extends BaseActivity {
    TextView item1,item2,item3;
    public static String titleS="RTK模式标定";
    public static  int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initTitle();
        item1=findViewById(R.id.item1);
        item1.setOnClickListener(o->{

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
