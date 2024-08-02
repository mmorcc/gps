package com.lianxin.location.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.lianxin.location.demo.activity.BaseActivity;
import com.lianxin.location.demo.activity.MyApplication;
import com.lianxin.location.demo.utils.MyConst;
import com.lianxin.location.demo.utils.SocketUtil;
import com.lianxin.location.demo.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends BaseActivity  implements View.OnClickListener {



    private final static String DEMO_GGA_STR = "$GPGGA,000001,3959.776019,N,11602.363141,E,1,8,1,100.000,M,0,M,3,0*46\r\n";

//    Unbinder mUnbinder;
    private List<SatelliteInfoBean> list = new ArrayList<>();

    private String mCopeString;
    String jingdu = "";
    String weidu = "";
    String gaodu = "";
    String locationQualityTag = "";
    String locationQualityTagStr = "";
    String satelliteNumbers = "";
    String shuipingJingdu = "";
    String chafenLingqi = "";
    String titleS = "定位";
    private MyThread myThread = null;
    private String currentPosition = "";
    String logIndex = "";
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String content = (String) msg.obj;
                if (Util.isNotEmpty(content) && result0 != null) {
                    result0.setText("sever消息:" + content);
                    if (content.startsWith("$cmd=SUCCESS=")) {
                        String position = content.substring(13).trim();
                        Util.writePositionStorage(position, context, logIndex);
                        String direction = Util.getValue(position, 8);
                        Util.showAlert(context, "定向已完成，已保存定位信息 Download/position中\n" +
                                "初始航向：" + direction + "度\n" +
                                position + "\n" +
                                "数据已记录", null);
                    }
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String content = (String) msg.obj;
                if (Util.isNotEmpty(content) && result1 != null) {
                    if (!content.contains("200")) {
                        GpsHelper.sendDataBytes(Util.stringToBytes(content));
                    }
//                    Util.l("byte=" + Util.stringToBytes(content).length);
                    result1.setText("消息:" + content);
                }
            }
            if (msg.what == 3) {
                byte[] bytes = (byte[]) msg.obj;
                String content = Util.byteToString(bytes);
                if (Util.isNotEmpty(content) && result1 != null) {
//                    if(!content.contains("200")){
//                        GpsHelper.sendDataBytes(Util.stringToBytes(content));
//                    }
                    Util.l("byte=" + Util.stringToBytes(content).length);
                    result1.setText("消息:" + content);
                }
            }
            if (msg.what == 2) {
                String content = (String) msg.obj;
                Util.l("收到串口消息==content=" + content);
                if(tv_location_status==null){
                    tv_location_status=findViewById(R.id.tv_location_status22);
                }
                if (Util.isNotEmpty(content) && tv_location_status != null) {
                    tv_location_status=findViewById(R.id.tv_location_status22);
                    tv_location_status.setText("消息:" + content);
                    if (content.contains("GGA")) {
                        setGGADataUI(content);
                        if (GGAData == null && content.contains("M")) {
                            GGAData = content;
                        }
                    }
                }

            }
        }
    };

    private TextView tv_location_status;

    Button btn_start;
    Button btn_stop;
    TextView tv_longitude;
    TextView tv_latitude;
    TextView tv_height;
    TextView tv_location_mistake;
    TextView tv_location_quality;
    TextView tv_shuiping_yinzi;
    TextView tv_chafen_lingqi;
    TextView tv_satellite_numbers;
    Button btn_cope;
    BarChart mBarChart;
    TextView result0;
    TextView result1;
    Button btn_start0;
    Button btn_stop0;
    Button btn_start1;
    Button btn_stop1;
    Button send;
    Button ppp;
    Button ppp_stop;
    TextView ppp_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setLayoutId(R.layout.activity_main);
        super.onCreate(savedInstanceState);
//        mBarChart = findViewById(R.id.barchart);
//        mUnbinder = ButterKnife.bind(this);
        GGAData = null;
        tv_location_status=findViewById(R.id.tv_location_status22);
        btn_start=findViewById(R.id.btn_start);
        btn_stop=findViewById(R.id.btn_stop);
        tv_longitude=findViewById(R.id.tv_longitude);
        tv_latitude=findViewById(R.id.tv_latitude);
        tv_height=findViewById(R.id.tv_height);
        tv_location_mistake=findViewById(R.id.tv_location_mistake);
        tv_location_quality=findViewById(R.id.tv_location_quality);
        tv_shuiping_yinzi=findViewById(R.id.tv_shuiping_yinzi);
        tv_chafen_lingqi=findViewById(R.id.tv_chafen_lingqi);
        tv_satellite_numbers=findViewById(R.id.tv_satellite_numbers);
        btn_cope=findViewById(R.id.btn_cope);
        result0=findViewById(R.id.result0);
        result1=findViewById(R.id.result1);
        btn_start0=findViewById(R.id.btn_start0);
        btn_stop0=findViewById(R.id.btn_stop0);
        btn_start1=findViewById(R.id.btn_start1);
        btn_stop1=findViewById(R.id.btn_stop1);
        send=findViewById(R.id.send);
        ppp=findViewById(R.id.ppp);
        ppp_stop=findViewById(R.id.ppp_stop);
        ppp_result=findViewById(R.id.ppp_result);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_cope.setOnClickListener(this);
//        requestPermission();

//        initBarChart();
        initTitle();
        initSocket();
        initMyView();

        String index = sp.get(MyConst.LOG_INDEX);
        if (Util.isNotEmpty(index) && Util.isInteger(index)) {
            logIndex = index;
        }
    }

    private void initMyView() {
        myThread = new MyThread();
        myThread.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    class MyThread extends Thread {

        public void run() {
            while (true) {
                try {
                    Thread.sleep(300);
//                    Util.l("try receive data fromm chuan kou ");
                    //从串口取数据，发送到界面
                    byte[] dataBytes = GpsHelper.getDataBytes();
                    if (dataBytes != null && dataBytes.length > 0) {
                        String str = new String(dataBytes, "UTF-8");
                        Message msg = handler2.obtainMessage();
                        msg.what = 2;
                        msg.obj = str+"";
                        String isStart = sp.get(MyConst.IS_START);
                        Util.l("串口：tStart="  + " isChoukouStart=" + isStart + str);
                        if (isStart.indexOf("1") >= 0) {
                            Util.l("串口：发送消息");
                            handler2.sendMessage(msg);

                        }
                    }
                } catch (Exception e) {
                    Util.l(" receive chuan kou err" + e.getMessage());
                    break;
                }
            }
        }
    }

    SocketUtil socketUtil = null;
    SocketUtil socketUtil2 = null;
    private long firstSendServerTime = 0;
    private boolean isRTKStart = false;

    private void initSocket() {
        setText(R.id.device, "当前设备IM：" + sp.get(MyConst.IM));
        //云服务器
        btn_start0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketUtil = new SocketUtil();
                socketUtil.type = 1;
                if (!Util.isNotEmpty(sp.get(MyConst.IP))) {
                    Util.showErr(context, "IP为空，请在云服务中设置");
                    return;
                }
                if (!Util.isNotEmpty(sp.get(MyConst.PORT))) {
                    Util.showErr(context, "端口号为空，请在云服务中设置");
                    return;
                }
                socketUtil.setIP(sp.get(MyConst.IP));
                socketUtil.setPORT(sp.get(MyConst.PORT));
                socketUtil.setHandler(handler);
                socketUtil.init(context);
                socketUtil.resetClient();
                result0.setText("服务器连接成功");
            }
        });
        //RTK
        btn_start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketUtil2 = new SocketUtil();
                socketUtil2.type = 2;
                String name = sp.get(MyConst.NAME);
                String pw = sp.get(MyConst.PW);
                if (!Util.isNotEmpty(name)) {
                    Util.showErr(context, "用户名为空，请在 服务续费 中设置");
                    return;
                }
                if (!Util.isNotEmpty(pw)) {
                    Util.showErr(context, "密码为空，请在续费服务中设置");
                    return;
                }
                Util.writeFileToLocalStorage("连接RTK name=" + name + "  pw=" + pw);
                socketUtil2.setUserid(name);
                socketUtil2.setPassword(pw);
                socketUtil2.setIP("ntrip.qxwz.com");
                socketUtil2.setPORT("8002");
                socketUtil2.setHandler(handler2);
                socketUtil2.init(context);
                socketUtil2.resetClient();
                result1.setText("连接RTK。。");
                btn_stop1.setText("发送GGA");
                setChuanKou();
                btn_start1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        socketUtil2.connect();
                    }
                }, 1000);
            }
        });
        //向服务器发送数据
        btn_stop0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socketUtil == null) {
                    Util.showErr(context, "请先连接服务器");
                    return;
                }
//                String register= "$CMD=IMEI=s868188079208405"+END;
//                socketUtil.sendMsg(register);

                Util.IM = sp.get(MyConst.IM);
                if (!Util.isNotEmpty(Util.IM)) {
                    Util.showErr(context, "请先在“设置”->设备设置中，选择远程设备");
                    return;
                }
                if (!Util.isNotEmpty(currentPosition)) {
                    Util.showErr(context, "请等待 '定位质量标记' 固定解时,才能上传");
                    return;
                }
                long now = new Date().getTime();
                long dur = now - firstSendServerTime;
                if (dur < 1000 * 10) {
                    Util.showErr(context, "请稍等10秒...");
                    return;
                }
                String up_position = "$CMD=UP_POSITION=" + Util.IM + "," + currentPosition + END;
                socketUtil.sendMsg(up_position);

                Util.writeFileToLocalStorage(" 上传位置到云:" + up_position);
                handler.postDelayed(() -> {
                    String get_position = "$CMD=GET_POSITION2=" + Util.IM + END;
                    Util.writeFileToLocalStorage(" 取云位置:" + get_position);
                    socketUtil.sendMsg(get_position);
                }, 20000);
            }
        });
        //
        btn_stop1.setText("等待RTK连接");
        btn_stop1.setOnClickListener(view -> {

            if (isRTKStart) {
                stopRTK();
                isRTKStart = false;
                btn_stop1.setText("等待RTK连接");
            } else {
                if (socketUtil2 == null) {
                    Util.showErr(context, "请先连RTK");
                    return;
                }
                if (socketUtil2 != null) {
                    btn_stop1.setText("停止RTK");
                    isRTKStart = true;
                    Util.showErr(context, "已发送GGA" + GGAData + END);
                    socketUtil2.sendMsg(GGAData + END);
                    Util.writeFileToLocalStorage(" 已发送GGA:" + GGAData + END);
                    Util.l("已发送GGA" + GGAData);
                }
            }
//            socketUtil2.disconnect();
//            socketUtil2.sendMsg(data);
        });

        ppp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPPP();
            }
        });
        ppp_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPPP();
            }
        });

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

//        ImageView rb=findViewById(R.id.right_btn);
//        rb.setVisibility(View.VISIBLE);
//        rb.setOnClickListener(v->{
//            AddActivity.type=1;
//            startActivity(new Intent(this,AddActivity.class));
//        });

    }

    private boolean isChoukouStart = false;

    public void onClick(View view) {
        if (!AppUtils.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.btn_start:
                startChuankou();
                break;
            case R.id.btn_stop:
                stopChuankou();
//                stopInterval();
//                GpsHelper.close();
//                //停止服务
//                mRetcodestart = SixentsSDKManagerImp.getInstance().sdkStop();
//                tv_location_status.setText("定位状态：停止定位");
//                isStartLocation = false;
                break;
            //复制位置信息
            case R.id.btn_cope:
                mCopeString = "";
                mCopeString += tv_longitude.getText().toString().trim() + "\n";
                mCopeString += tv_latitude.getText().toString().trim() + "\n";
                mCopeString += tv_height.getText().toString().trim();

                if (!TextUtils.isEmpty(jingdu) || !TextUtils.isEmpty(weidu) || !TextUtils.isEmpty(gaodu)) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", mCopeString);
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.showShort("已复制");
                } else {
                    ToastUtils.showShort("暂无位置信息可复制");
                }
                break;
        }

    }

    private void startChuankou() {
        Util.l("mmk startChuankou 0");
//        String str="$GNGGA,093025.00,3128.43587150,N,12015.87953577,E,4,21,1.2,21.0398,M,7.3183,M,,*73";
//        setGGADataUI(str);
        if (isChoukouStart) {
            Util.l("mmk startChuankou 1");
            Util.showErr(context, "正在定位，请稍后");
            return;
        }

        if (tv_location_status != null) {
            Util.l("mmk startChuankou 2");
            tv_location_status.setText("定位状态：定位中，请稍后...");
        }
        Util.l("startChuankou==" + Thread.currentThread().getName()+ Thread.currentThread().getId());
        isChoukouStart = true;
        sp.put(MyConst.IS_START, "1");
//        if(myThread!=null){
//            sp.put(MyConst.IS_START,"1");
//            Util.l("mmk startChuankou 3 isChoukouStart="+isChoukouStart);
//            while (!myThread.isStart()){
//                myThread.setStart(true);
//            }
//        }
//        Util.l("mmk startChuankou 3 isChoukouStart="+isChoukouStart);
    }

    private void stopChuankou() {
        if (!isChoukouStart) {
            Util.showErr(context, "已停止定位");
            return;
        }
        if (tv_location_status != null) {
            tv_location_status.setText("定位状态：已停止");
        }
        isChoukouStart = false;
        sp.put(MyConst.IS_START, "0");
//        if(myThread!=null){
//            sp.put(MyConst.IS_START,"0");
////            while (myThread.isStart()){
////                myThread.setStart(false);
////            }
//        }
    }

    private void setChuanKou() {
        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG RTK TIMEOUT 0" + END));
        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG RTK TIMEOUT 60" + END));
        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG DGPS TIMEOUT 60" + END));
        Util.writeFileToLocalStorage(
                "CONFIG RTK TIMEOUT 0" + END
                        + "CONFIG RTK TIMEOUT 60" + END
                        + "CONFIG DGPS TIMEOUT 60" + END
        );
    }

    private void startPPP() {
        ppp_result.setText("定位状态：ppp定位中");
        GpsHelper.sendDataBytes(Util.stringToBytes("config ppp enable B2b-PPP" + END));
        GpsHelper.sendDataBytes(Util.stringToBytes("config PPP TIMEOUT 120" + END));
        GpsHelper.sendDataBytes(Util.stringToBytes("pppnava 1" + END));
        Util.writeFileToLocalStorage("config ppp enable B2b-PPP" + END
                + "config PPP TIMEOUT 120" + END
                + "pppnava 1" + END);
    }

    private void stopPPP() {
        ppp_result.setText("定位状态：ppp定位停止");
        GpsHelper.sendDataBytes(Util.stringToBytes("config PPP disable" + END));
        Util.writeFileToLocalStorage("config PPP disable" + END);
    }

    private void stopRTK() {
        if (socketUtil2 != null) {
            socketUtil2.disconnect();
            socketUtil2 = null;
        }
        Message msg = handler2.obtainMessage();
        msg.what = 1;
        msg.obj = "停止RTK";
        handler2.sendMessage(msg);
    }

    private synchronized void setGGADataUI(String GGAString) {
        jingdu = "";
        weidu = "";
        gaodu = "";
        locationQualityTag = "";
        satelliteNumbers = "";
        shuipingJingdu = "";
        chafenLingqi = "";

        locationQualityTagStr = "";

        try {
            if (GGAString.contains("M")) {
                String[] splitArr = GGAString.split(",");
                for (int i = 0; i < splitArr.length; i++) {
                    String value = splitArr[i];
                    if (i == 2 && !TextUtils.isEmpty(value) && Util.isNum(value)) {
                        int zhengshu = (int) CalculationUtil.div(Double.parseDouble(value), 100);
                        //----------------根据"NMEA经纬度换算度分秒示例文档"，计算得出正确的小数值---------------------
                        double xiaoshu = (CalculationUtil.div(Double.parseDouble(value), 100, 10) - zhengshu);
                        xiaoshu = CalculationUtil.div(xiaoshu, 60) * 100;
                        xiaoshu = CalculationUtil.div(xiaoshu, 1, 10);
                        //----------------根据"NMEA经纬度换算度分秒示例文档"，计算得出正确的小数值---------------------
                        DecimalFormat decimalFormat = new DecimalFormat("0.0000000000#");//保留6位小数
                        weidu = decimalFormat.format(zhengshu + xiaoshu);
                        sp.put(MyConst.MY_WEI_DU, weidu);
                        Log.d("TAG", "zhengshu: " + zhengshu + ",,,xiaoshu: " + xiaoshu + ",,纬度结果: " + (zhengshu + xiaoshu));
                    } else if (i == 4 && !TextUtils.isEmpty(value) && Util.isNum(value)) {
                        int zhengshu = (int) CalculationUtil.div(Double.parseDouble(value), 100);
                        //----------------根据"NMEA经纬度换算度分秒示例文档"，计算得出正确的小数值---------------------
                        double xiaoshu = (CalculationUtil.div(Double.parseDouble(value), 100, 10) - zhengshu);
                        xiaoshu = CalculationUtil.div(xiaoshu, 60) * 100;
                        xiaoshu = CalculationUtil.div(xiaoshu, 1, 10);
                        //----------------根据"NMEA经纬度换算度分秒示例文档"，计算得出正确的小数值---------------------
                        DecimalFormat decimalFormat = new DecimalFormat("0.0000000000#"); //保留6位小数
                        jingdu = decimalFormat.format(zhengshu + xiaoshu);
                        sp.put(MyConst.MY_JING_DU, jingdu);
                        Log.d("TAG", "zhengshu: " + zhengshu + ",,,xiaoshu: " + xiaoshu + ",,经度结果: " + (zhengshu + xiaoshu));
                    } else if (i == 6 && !TextUtils.isEmpty(value) && Util.isInteger(value)) {
                        int qualityTag = Integer.parseInt(value);
                        locationQualityTagStr = qualityTag + "";
                        switch (qualityTag) {
                            case 0:
                                locationQualityTag = "初始化";
                                break;
                            case 1:
                                locationQualityTag = "单点定位";
                                break;
                            case 2:
                                locationQualityTag = "码差分";
                                break;
                            case 3:
                                locationQualityTag = "无效";
                                break;
                            case 4:
                                locationQualityTag = "固定解";
                                break;
                            case 5:
                                locationQualityTag = "浮点解";
                                break;
                            case 6:
                                locationQualityTag = "正在估算";
                                break;
                            case 7:
                                locationQualityTag = "人工输入固定解";
                                break;
                            case 8:
                                locationQualityTag = "模拟模式";
                                break;
                            case 9:
                                locationQualityTag = "WAAS差分";
                                break;
                        }

                    } else if (i == 7 && !TextUtils.isEmpty(value)) {
                        satelliteNumbers = value;
                    } else if (i == 8 && !TextUtils.isEmpty(value)) {
                        shuipingJingdu = value;
                    } else if (i == 9 && !TextUtils.isEmpty(value) && GGAString.contains("M")) {
                        gaodu = value;
                    } else if (i == 13 && !TextUtils.isEmpty(value)) {
                        chafenLingqi = value;
                    }
                }
            }
        } catch (Exception e) {
            Util.writeFileToLocalStorage("setGGADataUI err=" + e.getMessage());
        }
        if (locationQualityTagStr.equals("4")) {
            currentPosition = jingdu + "," + weidu + "," + gaodu + "," + locationQualityTagStr;
            Util.writeFileToLocalStorage("GGAString=" + GGAString + "\n"
                    + "loc success:" + currentPosition);
        }
        final String finalWeidu = weidu;
        final String finalJingdu = jingdu;
        final String finalGaodu = gaodu;
        final String finalLocationQualityTag = locationQualityTag;
        final String finalSatelliteNumbers = satelliteNumbers;
        final String finalShuipingJingdu = shuipingJingdu;
        final String finalChafenLingqi = chafenLingqi;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(finalJingdu)) {
                    tv_longitude.setText("经度：" + finalJingdu + "°");
                } else {
                    tv_longitude.setText("经度：");
                }

                if (!TextUtils.isEmpty(finalJingdu)) {
                    tv_latitude.setText("纬度：" + finalWeidu + "°");
                } else {
                    tv_latitude.setText("纬度：");
                }

                if (!TextUtils.isEmpty(finalGaodu)) {
                    tv_height.setText("海拔(M)：" + finalGaodu);
                } else {
                    tv_height.setText("海拔(M)：");
                }

                tv_location_quality.setText("定位质量标记：" + finalLocationQualityTag);
                tv_satellite_numbers.setText("使用卫星数量：" + finalSatelliteNumbers);
                tv_shuiping_yinzi.setText("水平精度因子：" + finalShuipingJingdu);
                tv_chafen_lingqi.setText("差分零期(s)：" + finalChafenLingqi);
            }
        });
    }

    private void initBarChart() {
        XAxis xAxis = mBarChart.getXAxis();
        YAxis yAxis = mBarChart.getAxisLeft();
        YAxis rightYAxis = mBarChart.getAxisRight();

        yAxis.setEnabled(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);

        rightYAxis.setEnabled(false);
        rightYAxis.setDrawAxisLine(false);
        rightYAxis.setDrawGridLines(false); //是否显示X坐标轴上的刻度竖线，默认是true
        rightYAxis.setDrawLabels(false); //是否显示X坐标轴上的刻度，默认是true
        mBarChart.getDescription().setEnabled(false); //获取描述,是否显示右下角描述
        mBarChart.setScaleEnabled(false); //设置是否可以缩放

//        list.add(new SatelliteInfoBean("a",20));
//        list.add(new SatelliteInfoBean("b",22));
//        list.add(new SatelliteInfoBean("c",25));
//        list.add(new SatelliteInfoBean("d",18));

        setBarChatData();
    }


    private void setBarChatData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<BarEntry> barEntries = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    barEntries.add(new BarEntry(i, list.get(i).getSignal()));
                }


                XAxis xAxis = mBarChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    public String getAxisLabel(float index, AxisBase axisBase) {
                        if ((int) index < list.size()) {
                            return list.get((int) index).getName();
                        } else {
                            return "";
                        }
                    }
                });

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(true);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(list.size());

                BarData data = new BarData();
                BarDataSet set = new BarDataSet(barEntries, "");
                data.setBarWidth(0.3f);
                data.addDataSet(set);
                mBarChart.setData(data);

                mBarChart.setExtraBottomOffset(0f);

                mBarChart.invalidate();
            }
        });
    }


    public static final int REQUEST_ALL = 102;

    private void requestPermission() {

        String[] permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,


        };

//        EasyPermissions.requestPermissions(this, "请允许使用权限", REQUEST_ALL, permissions);
    }

    @Override
    protected void onDestroy() {
        //停止服务
//        int retcodestop = SixentsSDKManagerImp.getInstance().sdkStop();

        //注销SDK
//        if (retcodestop == 0) {
//            int retcodefinal = SixentsSDKManagerImp.getInstance().sdkFinal();
//        }

        stopChuankou();
        LogUtils.d("===onDestroy===");

        if (socketUtil != null) {
            socketUtil.disconnect();
            socketUtil = null;
        }
        if (socketUtil2 != null) {
            socketUtil2.disconnect();
            socketUtil2 = null;
        }

        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
        }
        tv_location_status=null;
//        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
//            mUnbinder.unbind();
//        }
//        mUnbinder = null;
        super.onDestroy();

    }

}
