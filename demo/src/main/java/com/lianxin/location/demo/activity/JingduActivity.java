package com.lianxin.location.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lianxin.location.demo.GpsHelper;
import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.MyConst;
import com.lianxin.location.demo.utils.Util;


public class JingduActivity extends BaseActivity {
    TextView item1,item2,item3;
    public static String titleS="ppp精度设置";
    public static  int index=0;
    EditText name;
    EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jingdu);
        initTitle();

        name=findViewById(R.id.ed_login_user);
        pwd=findViewById(R.id.ed_login_password);

        if(!Util.isNotEmpty(sp.get(MyConst.JING_DU_1))){
            sp.put(MyConst.JING_DU_1, "10");
            sp.put(MyConst.JING_DU_2, "20");
        }
        name.setText(sp.get(MyConst.JING_DU_1));
        pwd.setText(sp.get(MyConst.JING_DU_2));

        ImageView clear = findViewById(R.id.clear);
        ImageView clear2= findViewById(R.id.clear2);
        Util.setEditEvent(name,clear);
        Util.setEditEvent(pwd,clear2);
        findViewById(R.id.save).setOnClickListener(view -> {
            if(TextUtils.isEmpty(name.getText().toString())||
                    TextUtils.isEmpty(pwd.getText().toString())) {
                Toast.makeText(JingduActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Util.isInteger(name.getText().toString())||
                    !Util.isInteger(pwd.getText().toString())){
                Toast.makeText(JingduActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                return;
            }
            String username=name.getText().toString();
            String password=pwd.getText().toString();

            GpsHelper.sendDataBytes(Util.stringToBytes("config PPP converge "+username.trim()+" "+password.trim()+END));
            Util.writeFileToLocalStorage("config PPP converge "+username.trim()+" "+password.trim()+END);
            sp.put(MyConst.JING_DU_1,username);
            sp.put(MyConst.JING_DU_2,password);
            Toast.makeText(JingduActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
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
