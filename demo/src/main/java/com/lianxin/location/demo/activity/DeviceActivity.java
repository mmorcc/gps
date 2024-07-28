package com.lianxin.location.demo.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lianxin.location.demo.MainActivity;
import com.lianxin.location.demo.R;
import com.lianxin.location.demo.adapter.GoodAdapter;
import com.lianxin.location.demo.bean.Device;
import com.lianxin.location.demo.utils.CallBack;
import com.lianxin.location.demo.utils.MyConst;
import com.lianxin.location.demo.utils.SocketUtil;
import com.lianxin.location.demo.utils.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class DeviceActivity extends BaseActivity {
    TextView item1, item2, item3;
    public static String titleS = "设备设置";
    public static int index = 0;
    private SocketUtil socketUtil = null;

    RecyclerView recycleView, recycleView2;

    GoodAdapter goodAdapter;
    List<Device> arr = new ArrayList<>();
    EditText et;
    TextView bt;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String content = (String) msg.obj;
                Util.l("RECIEVED=" + content);

                if (Util.isNotEmpty(content) && content.indexOf("$cmd=SUCCESS=") >= 0) {
                    String value = content.substring(13);
                    Util.l("position="  + value);
                    setDistance(value);
                } else if (Util.isNotEmpty(content) && content.indexOf("$cmd=") >= 0) {
                    String value = content.substring(5);
                    String[] IMs = value.split(",");
                    for (int i = 0; i < IMs.length; i++) {
                        Device device = new Device();
                        device.IM = IMs[i];

                        MyRunnable runable = new MyRunnable(device.IM);
                        device.runnable=runable;
                        getPosition(i, runable);
                        if (Util.isNotEmpty(IMs[i])) {
                            arr.add(device);
                        }

                    }
                    goodAdapter.notifyDataSetChanged();
                }

            }
        }
    };

    private void setDistance(String value) {
        if(Util.isNotEmpty(value)){
            String IM=Util.getValue(value,0);
            String jing=Util.getValue(value,1);
            String wei=Util.getValue(value,2);
            String myJindu= sp.get(MyConst.MY_JING_DU);
            String myWeidu= sp.get(MyConst.MY_WEI_DU);
            double dis = Util.getDistance(jing, wei, myJindu, myWeidu);
            for (int i = 0; i < arr.size(); i++) {
                if(arr.get(i).IM.equals(IM)){
                    arr.get(i).distance=dis;
                    break;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                arr.sort(new Comparator<Device>() {
                    @Override
                    public int compare(Device device, Device t1) {
                        if(device.distance>t1.distance){
                            return 1;
                        }else  {
                         return -1;
                        }
                    }
                });
            }
            goodAdapter.notifyDataSetChanged();
        }

    }

    public class MyRunnable implements Runnable {
        private String IM = "";

        public MyRunnable(String IM) {
            this.IM = IM;
        }

        @Override
        public void run() {
            if (socketUtil != null) {
                socketUtil.sendMsg("$CMD=GET_POSITION=" + this.IM);
            }
        }
    }


    private void getPosition(int i, MyRunnable runnable) {
        String myJindu= sp.get(MyConst.MY_JING_DU);
        if(!Util.isNotEmpty(myJindu)){
            return;
        }
        if(handler!=null){
            handler.postDelayed(runnable, i * 1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initTitle();

        initList();
        connect();//120.234689158000023,31.448568184166671
        String myJindu= sp.get(MyConst.MY_JING_DU);
        if(!Util.isNotEmpty(myJindu)){
            Util.showErr(context,"暂无定位，无法计算和设备的距离");
        }
    }

    private void initList() {
        recycleView = findViewById(R.id.list);
        goodAdapter = new GoodAdapter(context, arr, new CallBack() {
            @Override
            public void onDo(Object o) {
                sp.put(MyConst.IM, Util.IM);
                Util.l("IM=" + Util.IM);
            }
        });
        recycleView.setLayoutManager(new LinearLayoutManager(context));

        recycleView.setAdapter(goodAdapter);
        recycleView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());


    }

    private void connect() {
        socketUtil = new SocketUtil();
//                socketUtil.setIP("192.168.0.105");
        Util.l("IP=" + sp.get(MyConst.IP));
        Util.l("PORT=" + sp.get(MyConst.PORT));
        socketUtil.setIP(sp.get(MyConst.IP));
        socketUtil.setPORT(sp.get(MyConst.PORT));
        socketUtil.setHandler(handler);
        socketUtil.init(context);
        socketUtil.resetClient();
        recycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                socketUtil.sendMsg("$CMD=GET_DEVICES");
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTitle() {
        TextView title = findViewById(R.id.title);
        if (!TextUtils.isEmpty(titleS)) {
            title.setText(titleS);
        }
        ImageView bacBtn = findViewById(R.id.back_btn);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            if(arr!=null&&arr.size()>0){
                for (int i = 0; i < arr.size(); i++) {
                    if(arr.get(i).runnable!=null){
                        handler.removeCallbacks(arr.get(i).runnable);
                    }
                }
            }
            handler=null;
        }
        if (socketUtil != null) {
            socketUtil.disconnect();
        }
    }
}
