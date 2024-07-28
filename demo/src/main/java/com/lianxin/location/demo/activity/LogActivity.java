package com.lianxin.location.demo.activity;

import static com.lianxin.location.demo.utils.MyConst.FILE_POSITION;
import static com.lianxin.location.demo.utils.MyConst.PATH;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.MyConst;
import com.lianxin.location.demo.utils.OnClick;
import com.lianxin.location.demo.utils.Util;

import java.io.File;


public class LogActivity extends BaseActivity {
    TextView tv;
    public static String titleS = "定位信息";
    String logIndex = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initTitle();
        tv = findViewById(R.id.tv);
        String index = sp.get(MyConst.LOG_INDEX);
        if (Util.isNotEmpty(index) && Util.isInteger(index)) {
            logIndex = index;
            Util.l(" logpi 3 logIndex=" + logIndex);
        }
        new Thread(() -> {
            String log = Util.readFileToLocalStorage(logIndex);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = "文件路径：" + PATH + logIndex + FILE_POSITION + "\n";
                  if(tv!=null){
                      tv.setText(str + log);
                  }
                }
            });
        }).start();

    }

    private void initTitle() {
        TextView title = findViewById(R.id.title);
        title.setText("记录");
        if (!TextUtils.isEmpty(titleS)) {
            title.setText(titleS);
        }
        ImageView bacBtn = findViewById(R.id.back_btn);
        bacBtn.setVisibility(View.VISIBLE);
        bacBtn.setImageResource(R.drawable.back);
        bacBtn.setOnClickListener(v -> finish());

        ImageView rb = findViewById(R.id.right_btn);
        rb.setVisibility(View.VISIBLE);
        rb.setImageResource(R.drawable.delete);
        rb.setOnClickListener(v -> {
            Util.showAlert(context, "删除日志(不会删除位置信息原文件)？", new OnClick() {
                @Override
                public void onDo() {
                   doDelete();
                }
            });
        });

    }

    private void doDelete() {
        String index = sp.get(MyConst.LOG_INDEX);
        if (Util.isInteger(index)) {
            logIndex = (Integer.parseInt(index) + 1) + "";
            sp.put(MyConst.LOG_INDEX, logIndex);
            Util.l(" logpi  0 logIndex=" + logIndex);
        } else {
            logIndex = 1 + "";
            sp.put(MyConst.LOG_INDEX, logIndex);
            Util.l(" logpi  6 logIndex=" + logIndex);
        }
        Util.deleteLog();
        Util.l(" logpi logIndex=" + logIndex);
        Util.showErr(context, "删除成功");
        new Thread(() -> {
            String log = Util.readFileToLocalStorage(logIndex);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = "文件路径：" + PATH + logIndex + FILE_POSITION + "\n";
                    tv.setText(str + log);
                }
            });
        }).start();
    }

}
