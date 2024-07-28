package com.lianxin.location.demo.activity;

import static com.lianxin.location.demo.utils.MyConst.FILE_POSITION;
import static com.lianxin.location.demo.utils.MyConst.PATH;

import android.app.Application;
import android.content.Context;

import com.lianxin.location.demo.utils.CrashHandler;

import java.io.File;

public class MyApplication extends Application {
    private static Context context;
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
    }
    public static Context get(){
        return context;
    }
}
