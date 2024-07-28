package com.lianxin.location.demo;


import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 保存日志写到本地
 */
public class LogcatHelper {
    public static final String TAG = "LogcatHelper";
    public static String pathLogcat;
    public static String pathTSL;
    private static volatile LogcatHelper mInstance;
    String cmds = null;
    private Process logcatProc;
    private LogDumper mLogDumper;
    private int mPid;
    public static String fileName = "GpsDemo.txt"; //错误日志文件名称
    public static String dateFormat = "yyyy-MM-dd";

    private LogcatHelper(Context context) {
        init(context);
        mPid = android.os.Process.myPid();
    }

    public static LogcatHelper newInstance(Context context) {
        if (mInstance == null) {
            synchronized (LogcatHelper.class) {
                if (mInstance == null) {
                    mInstance = new LogcatHelper(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化目录
     */
    private void init(Context context) {
        pathLogcat =  context.getExternalFilesDir(null) + "/log/";

        File file = new File(pathLogcat);
        if (!file.exists()) {
            file.mkdirs();
            Log.i(TAG, "创建文件夹");
        }
        Log.i(TAG, pathLogcat);
    }

    //如果日志文件过大就删除
    public void checkLogFileIsFull(){
        File logFile = new File(pathLogcat, fileName);
        if(FileUtils.isFileExists(logFile) && FileUtils.getSize(logFile).contains("MB")){
            LogUtils.d("===日志文件大小===size===" + FileUtils.getSize(logFile));
            double fileSize = Double.parseDouble(FileUtils.getSize(logFile).replace("MB", ""));
            if(fileSize > 30) {
                stop();
                FileUtils.delete(logFile);
                start();
            }
        }
    }

    public void start(){
        return;
//        checkLogFileIsFull();

//        File file = new File(pathLogcat, fileName);
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//                LogUtils.d("===创建日志文件===fileName==="+file.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        mLogDumper = new LogDumper(String.valueOf(mPid),pathLogcat);
//
//        mLogDumper.start();
//
//        LogUtils.d("===写入日志文件===fileName==="+file.getAbsolutePath());
//        LogUtils.d("版本号: V" + AppUtils.getAppVersionName());
    }

    public void stop(){
//        if (mLogDumper != null){
//            mLogDumper.stopLogs();
//            mLogDumper = null;
//        }
    }

    private class LogDumper extends Thread {
        private String mPid;
        private BufferedWriter outputStream = null;
        private BufferedReader mReader = null;
        private boolean mIsRunning = true;

        public LogDumper(String pid, String dir) {
            mPid = pid;
            long timeMillis = System.currentTimeMillis();

            try {
                outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir,fileName), true), StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //显示当前mPid程序的日志等级  日志等级：*:v , *:d , *:w , *:e , *:f , *:s
            // cmds = "logcat *:e *:w | grep \"(" + mPid + ")\"";
            cmds = "logcat  | grep \"(" + mPid + ")\"";//打印当前应用日志信息
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream(),StandardCharsets.UTF_8),1024);
                String line = null;
                while (mIsRunning && (line = mReader.readLine()) != null){
                    if (!mIsRunning){
                        break;
                    }
                    if (line.length() == 0){
                        continue;
                    }
                    if (outputStream != null && line.contains(mPid)){//打印当前应用日志信息
//                        outputStream.write((line+"\n").getBytes());
                        outputStream.write((line+"\n"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                Log.d("TAG","===LogcatHelper===stop==finally===");
                if (logcatProc != null){
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null){
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    outputStream = null;
                }
            }
        }

        public void stopLogs(){
            mIsRunning = false;
        }

    }
}
