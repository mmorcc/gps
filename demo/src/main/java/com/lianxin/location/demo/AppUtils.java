package com.lianxin.location.demo;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class AppUtils {

    /**
     * @return T10 等机型获取sn
     */
    public static String getSN_(Context context) {

        String serial = "";

        //通过反射获取sn号
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            /*if(ConS.DEVICE_U1.equals( SPUtilNoDelete.getInstance().getString(ConS.KEY_DEVICE_TYPE, ConS.DEVICE_T10_DB))){
                serial = (String)get.invoke(c, "ro.serialno");
            }else {
                serial = (String) get.invoke(c, "persist.radio.sn");
            }
            if (!TextUtils.isEmpty(serial) && !serial.equalsIgnoreCase("unknown")) {
                return serial;
            }*/

            serial = (String)get.invoke(c, "ro.serialno");

            //9.0及以上无法获取到sn，此方法为补充，能够获取到多数高版本手机 sn
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                serial = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = Build.getSerial();
            }

        } catch (Exception e) {
            serial = "";
        }
        return serial;
    }


    /**
     * 将两个byte数组合并为一个
     * @param data1  要合并的数组1
     * @param data2  要合并的数组2
     * @return 合并后的新数组
     */
    public static byte[] mergeBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            if(i < b.length - 1) {
                r += hex.toUpperCase() + " ";
            }else{
                r += hex.toUpperCase();
            }
        }
        return r;
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {
        if ((hex == null) || (hex.equals(""))){
            return null;
        }else{
            hex = hex.replace(" ","");
        }

        if (hex.length()%2 != 0){
            return null;
        } else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 防止重复点击
     */
    private static final int MIN_CLICK_DELAY_TIME = 1000; // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static long lastClickTime;
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

}
