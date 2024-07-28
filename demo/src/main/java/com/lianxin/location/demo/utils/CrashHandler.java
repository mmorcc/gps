package com.lianxin.location.demo.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Try
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "Crash";
    private static CrashHandler instance; // 单例模式
    private Context mContext;
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefalutHandler;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);

    private CrashHandler() {
    }
    /**
     * 获取CrashHandler实例
     * @return CrashHandler
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 异常处理初始化
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 自定义错误处理
        boolean result = handleException(ex);
        if (!result && mDefalutHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefalutHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // TODO Try 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
//        new Thread() {
//            @Override
//            public void run() {
//                //此处的Looper是为了弹出Toast所用，不然子线程中Toast不会显示
//                Looper.prepare();
//                String err = "[" + ex.getMessage() + "]";
//                Toast.makeText(mContext, "程序出现异常." + err, Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();

        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 保存错误信息到文件中
     * 返回文件名称,便于将文件传送到服务器
     * @param ex
     * @return
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        if (printWriter != null) {
            printWriter.close();
        }
        String result = writer.toString();
        sb.append(result);
        Log.d(TAG, "crashhandler 异常toString = " + sb.toString());
        try {
            long timestamp = System.currentTimeMillis();
            String time = mSimpleDateFormat.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            Log.d(TAG,"fileName = " + fileName);
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Download"+File.separator+"aLog"+File.separator;
                File dir = new File(path);
                Util.l("是否存在"+dir.exists());
                if (!dir.exists()) {
                    boolean isSuccess = dir.mkdirs();
                    Util.l("是否存在22"+isSuccess);

                }
                FileOutputStream fos = new FileOutputStream(new File(path + fileName));

                fos.write(sb.toString().getBytes());
                if (fos != null) {
                    fos.close();
                }
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }


}
