package com.lianxin.location.demo;

import android.util.Log;

import com.lianxin.location.demo.utils.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import roco.iot.sdk.SPort;

public class GpsHelper {

    private static String TAG = "GpsHelper";
    private static SPort mSPort;
    private static boolean isConnected;

    //打通串口
    public synchronized static boolean connect() {
        if (!isConnected) {
            try{

                //打通串口
                mSPort = new SPort(new File("/dev/ttyWCH0"), 115200, null);
//            mSPort = new SPort(new File("/dev/ttyS1"), 115200, null);
                Util.l(" mSPort="+mSPort);
                mSPort.open();
                int portState = mSPort.getState();
                Util.writeFileToLocalStorage(" open port state="+portState);
                if ( portState == 101) {
                    isConnected = true;
//                init();
                    return true;
                } else {
                    mSPort = null;
                    return false;
                }
            }catch (Exception e){
                Util.l(" open err="+e.getMessage());
                return false;
            }
        }
        return true;
    }

    public synchronized static boolean powerOn(boolean turnOn) {
        try {
            FileWriter fileWriter = new FileWriter("/sys/kernel/usb_switch/etc_power");
            fileWriter.write(turnOn ? "on" : "off");
            fileWriter.flush();
            fileWriter.close();
            Thread.sleep(10);

            if (turnOn) {
                Util.l("扫描模组上电成功");
                Util.writeFileToLocalStorage(" open 扫描模组上电成功");
                Log.d(TAG, "扫描模组上电成功");
            } else {
                Util.l("扫描模组下电成功");
                Log.d(TAG, "扫描模组下电成功");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "扫描模组上电失败");
            return false;
        }
    }

    public synchronized static void close() {
        Util.writeFileToLocalStorage(" close port isConnected="+isConnected);
        if (isConnected) {
            mSPort.close();
            mSPort = null;
            isConnected = false;
            powerOn(false);
        }
    }

    public static byte[] getDataBytes() {
        byte[] result = new byte[]{};
        byte[] read = null;
        if (mSPort != null && (read = mSPort.read()) != null && read.length > 0) {
            result = read;
        }

        return result;
    }

    public static void sendDataBytes(byte[] bytes) {
        if (mSPort != null && isConnected) {
            mSPort.write(bytes);
        }
    }

}
