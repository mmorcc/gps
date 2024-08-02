package com.lianxin.location.demo.activity;

import static com.lianxin.location.demo.utils.MyConst.FILE_POSITION;
import static com.lianxin.location.demo.utils.MyConst.PATH;

import android.app.Application;
import android.content.Context;

import com.lianxin.location.demo.GpsHelper;
import com.lianxin.location.demo.utils.CallBack;
import com.lianxin.location.demo.utils.CrashHandler;
import com.lianxin.location.demo.utils.Util;

import java.io.File;

public class MyApplication extends Application {
    private static Context context;
    public static boolean isChoukouStart=false;
    @Override
    public void onCreate() {
        File file = new File(PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        super.onCreate();
        context=this;
        startChuankou();
    }
    public static Context get(){
        return context;
    }

    @Override
    public void onTerminate() {
        stopChuankou();
        super.onTerminate();
    }


    private void startChuankou() {

//        String str="$GNGGA,093025.00,3128.43587150,N,12015.87953577,E,4,21,1.2,21.0398,M,7.3183,M,,*73";
//        setGGADataUI(str);
        if(isChoukouStart){
            Util.showErr(context,"正在定位，请稍后");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    GpsHelper.powerOn(true);
                    Thread.sleep(100);
                    boolean isSuc=GpsHelper.connect();
                    if(isSuc){
                        isChoukouStart = true;
                    }else {
                        Util.showErr(context,"串口打开失败，请退出重新打开");
                    }

//                    GpsHelper.sendDataBytes(Util.stringToBytes("GPGGA 1" + END));
//                    Util.writeFileToLocalStorage(
//                            "GPGGA 1" + END
//                    );
                } catch (Exception e) {
                    Util.writeFileToLocalStorage("启动定位异常：" + e.getMessage());
                }

            }
        }).start();
    }

    private void stopChuankou() {
        if(!isChoukouStart){
            Util.showErr(context,"已停止定位");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

//        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG RTK TIMEOUT 0"+END));
//        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG RTK TIMEOUT 60"+END));
//        GpsHelper.sendDataBytes(Util.stringToBytes("CONFIG DGPS TIMEOUT 60"+END));
//                GpsHelper.sendDataBytes(Util.stringToBytes("unlog" + END));
                GpsHelper.close();
                isChoukouStart = false;
//                Util.writeFileToLocalStorage(
////                "CONFIG RTK TIMEOUT 0"+END
////                +"CONFIG RTK TIMEOUT 60"+END
////                +"CONFIG DGPS TIMEOUT 60"+END
//                        "unlog" + END
//                );

            }
        }).start();
    }
}
