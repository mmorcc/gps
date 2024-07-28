package com.lianxin.location.demo.utils;


import static com.lianxin.location.demo.utils.MyConst.FILE_NAME;
import static com.lianxin.location.demo.utils.MyConst.FILE_POSITION;
import static com.lianxin.location.demo.utils.MyConst.PATH;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lianxin.location.demo.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;


public class Util {
    public static String IM = "";
    public static String TOKEN = "";
    public static int USER_ID = 0;

    private static final String TAG = "UTIL";


    public static void setEditEvent(EditText username, ImageView clear) {

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    clear.setVisibility(View.GONE);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("");
            }
        });
    }


    /**
     * 示例，get加载Json数据
     */
    public static void sendLoc(Context c, double x, double y) {

    }

    /**
     * 示例，get加载Json数据
     */
    public static void sendMac(Context c, String mac) {

    }

    public static void l(Object msg) {
        Log.e("日志", "消息=" + String.valueOf(msg));
//        writeFileToLocalStorage(String.valueOf(msg));
    }

    public static void showErr(Context context, Object msg) {
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg == null ? "" : String.valueOf(msg), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void log(Object str) {
        Log.e(TAG, "xpf=" + str);
    }

    public static Long getLong(String value) {
        if (isNotEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
                return 0L;
            }
        }
        return 0L;
    }

    public static Float getFloat(String value) {
        if (isNotEmpty(value)) {
            try {
                return Float.parseFloat(value);
            } catch (Exception e) {
                return 0F;
            }
        }
        return 0F;
    }

    public static String getFloatStr(float value) {
        try {
            DecimalFormat df = new DecimalFormat("#.##");

            return df.format(value);
        } catch (Exception e) {
            return "0";
        }
    }

    public static boolean isNotEmpty(String el) {
        if (el == null || el.trim().equals("")) {
            return false;
        }
        return true;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

    public static String formatTime(Long createTime) {
        if (createTime == null) {
            return "";
        } else {
            return sdf.format(createTime);
        }
    }

    public static Long formatTimeToLong(String date) {
        if (date == null) {
            return new Date().getTime();
        } else {
            try {
                return sdf.parse(date).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } finally {
                return new Date().getTime();
            }
        }
    }

    public static String getValue(Object str) {
        if (str == null) {
            return "";
        }
        return String.valueOf(str);
    }


    public static String getValue(String str, int i) {
        String result = "";
        if (isNotEmpty(str)) {
            String[] arr = str.split(",");
            if (arr.length > i) {
                result = arr[i];
            }
        }
        return result;
    }

    public static String formatTime2(Long time) {
        if (time == null) {
            return "";
        } else {
            return sdf2.format(time);
        }
    }

    public static void writeFileToLocalStorage(String contents) {
        new Thread(() -> {
            writeLog(contents);
        }).start();
    }

    private synchronized static void writeLog(String contents) {
        FileWriter writer = null;
        try {
            String content = contents;
            File file = new File(PATH + FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content = sdf.format(new Date()) + ":" + content + "\n";
            writer.append(content);
            writer.flush();
            writer.close();
            // 文件写入成功
        } catch (Exception e) {
            e.printStackTrace();
            try {
                FileWriter writer2 = null;
                File file2 = new File(PATH +new Date().getTime()+ FILE_NAME);
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                writer2 = new FileWriter(file2, true);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String content2 = sdf2.format(new Date()) + ": 写log异常 err=" + e.getMessage() + "\n";
                writer2.append(content2);
                writer2.flush();
                writer2.close();
            }catch (Exception e2) {

            }
            // 文件写入发生错误
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writePositionStorage(String contents, Context context, String logIndex) {
        new Thread(() -> {
            writeposition(contents, context, logIndex);

        }).start();
    }

    private synchronized static void writeposition(String contents, Context context, String logIndex) {
        try {
            String content = contents;
            File file = new File(PATH + logIndex + FILE_POSITION);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content = content + "\n";
            writer.append(content);
            writer.flush();
            writer.close();
            // 文件写入成功
        } catch (Exception e) {
            e.printStackTrace();
            Util.showErr(context, e.getMessage() + "");
            writeFileToLocalStorage("写入position异常 err：" + e.getMessage() + "");
            // 文件写入发生错误
        }
    }

    public synchronized static String readFileToLocalStorage(String logIndex) {
        try {
            File file = new File(PATH + logIndex + FILE_POSITION);
            if (!file.exists()) {
                file.createNewFile();
                Log.i(TAG, "创建文件");
            }
            FileInputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {  //
                if (!"".equals(lineTxt)) {
                    sb.append(lineTxt + "\n");
                }
            }
            is.close();
            isr.close();
            br.close();
            return sb.toString();
            // 文件写入成功
        } catch (IOException e) {
            e.printStackTrace();
            // 文件写入发生错误
        }
        return "";
    }

    public static void transFile(String filePath, String destPath) {
        if (!isNotEmpty(filePath)) {
            return;
        }
        new Thread(() -> {

            try {
                Util.l(" sourc file=" + filePath);
                Util.l(" sourc dest file=" + destPath);
                File originalFile = new File(filePath); // 获得原始文件
                File newFile = new File(destPath); // 创建目标文件

                InputStream in = new FileInputStream(originalFile);
                OutputStream out = new FileOutputStream(newFile);

                // 拷贝文件内容
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                // 如果需要，删除原始文件
//                originalFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getFileName(String filePath) {
        if (!isNotEmpty(filePath)) {
            return "";
        }
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        if (isNotEmpty(fileName)) {
            return fileName;
        } else {
            return "";
        }

    }

    public static String base64(String originalString) {
        String encodedString = null;
        l(" is biger=" + (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        }
        l(" encodedString=" + encodedString);
        return encodedString;
    }

    public static byte[] streamToBytes(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] arr = output.toByteArray();
        l("streamToBytes arr.s=" + arr.length);
        byteToString(arr);
        return arr;
    }

    public static String byteToString(byte[] aa) {
        String str = null;
        try {
            str = new String(aa, "UTF-8");
        } catch (Exception e) {
            l(" byteToString err=" + e);
        }
        l(" byteToString str=" + str);
        return str;
    }

    public static byte[] stringToBytes(String s) {
        //return s.getBytes(StandardCharsets.UTF_8);
        //return s.getBytes("UTF-8");
        return s.getBytes();
    }

    public static void toAc(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static boolean isNum(String str) {
        if (!isNotEmpty(str)) {
            return false;
        }
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isInteger(String str) {
        try {
            int num = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void showAlert(Context context, String str,OnClick onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View content = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        TextView tv = content.findViewById(R.id.tv);
        tv.setText(str);
        builder.setView(content);

        Button ok = content.findViewById(R.id.ok);
        AlertDialog dialog = builder.create();
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(onClick!=null){
                    onClick.onDo();
                }
            }
        });
    }

    public static void deleteLog() {
        new Thread(() -> {
            try {
                File file = new File(PATH + FILE_NAME);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers
    public static double getDistance(String lon1Str, String lat1Str, String lon2Str, String lat2Str) {
        // Convert latitude and longitude from strings to doubles
        double lat1 = Double.parseDouble(lat1Str);
        double lon1 = Double.parseDouble(lon1Str);
        double lat2 = Double.parseDouble(lat2Str);
        double lon2 = Double.parseDouble(lon2Str);

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine formula
        double dlon = lon2Rad - lon1Rad;
        double dlat = lat2Rad - lat1Rad;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance ; // Distance in kilometers
    }
}
