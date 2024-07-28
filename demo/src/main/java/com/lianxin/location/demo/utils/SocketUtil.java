package com.lianxin.location.demo.utils;

import static com.lianxin.location.demo.utils.MyConst.FILE_NAME;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lianxin.location.demo.GpsHelper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketUtil {
    Socket client = null;
    MyHandler myHandler;
    DataOutputStream dout;
    //    public static String IP="192.168.0.105";
//public static String IP="43.138.182.48";
    public String IP = "";
    //    public static int PORT=8080;
    public String PORT = "";
    SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance("socket");
    DataInputStream din;
    private SocketUtil instance = null;
    private Context context;

    String userid = "qxeaky00360";
    String password = "5d7ba0e";
    private Handler outHandler = new Handler();
    MyThread myThread = null;
    private boolean start = true;

    public int type = 1;//1,2,RTK
    public String stateStr = "RTK定位中.";

    public SocketUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SocketUtil();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        myHandler = new MyHandler();

    }

    public Handler getHandler() {
        return outHandler;
    }

    public void setHandler(Handler handler) {
        this.outHandler = handler;
    }

    public void sendMsg(String sendText) {
        new Thread(() -> {
            try {
                if (client == null) {
                    Util.showErr(context, "client 为空");
                    return;
                }
                Util.l(" send msg:" + sendText);
                //获取输出流，向服务器发送数据
                OutputStream os = client.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.write(sendText + "\r\n");
                pw.flush();
                //关闭输出流
//                        client.shutdownOutput();
            } catch (Exception e) {
                Util.l(" send err" + e);
                e.printStackTrace();
            }
        }).start();
    }

    public void resetClient() {
        String ipStr = IP;
        String portStr = PORT + "";

        if (!Util.isNotEmpty(ipStr) || !Util.isNotEmpty(portStr)) {
            Util.showErr(context, "ip,port不能为空");
            return;
        }

        sharedPreferencesUtils.put("IP", ipStr);
        sharedPreferencesUtils.put("PORT", portStr);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (client != null) {
                        disconnect();
                    }
                    start = true;
                    myThread = new MyThread();
                    myThread.start();

                    int port = Integer.parseInt(portStr);
                    //创建客户端Socket，指定服务器的IP地址和端口
                    client = new Socket(ipStr, port);
//                    client = new Socket("192.168.0.105", 12345);
//                    dout = new DataOutputStream(client.getOutputStream());
//                    din = new DataInputStream(client.getInputStream());
                    Util.l("dout =" + dout + "  din=" + din + "  client=" + client);
                } catch (IOException e) {
                    Util.l("err2 =" + e.getMessage());
                    Util.showErr(context, "err=" + e.getMessage());
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }


    public void connect() {
        sendMsg("GET /RTCM32_GGB HTTP/1.0");
        sendMsg("User-Agent: NTRIP GNSSInternetRadio/1.4.10");
        String thirdStr = "Authorization: Basic " + Util.base64(userid + ":" + password) + "\r\n";
        sendMsg(thirdStr);
    }


    class MyHandler extends Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg2) {
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg2);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (client != null && client.isConnected()) {
                        try {
                            //获取输入流，接收服务器发来的数据
                            InputStream is = client.getInputStream();
                            if (type == 2) {
                                Log.i("handler..", " try read from qianxun socket *-----*");
                                appendToFile(is);
                                return;
                            }else {
                                Log.i("handler..", " try read from  socket *-----*");
                            }
//
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String data = null;
                            //读取客户端数据
                            while ((data = br.readLine()) != null) {
                                System.out.println("客户端接收到服务器回应的数据：" + data);
                                Util.l("byte=" + Util.stringToBytes(data).length);
                                if (outHandler != null) {
                                    Message msg = outHandler.obtainMessage();
                                    msg.what = 1;
                                    msg.obj = data;
                                    outHandler.sendMessage(msg);
                                }
                            }
                            //关闭输入流
//                            client.shutdownInput();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();


        }
    }

    public void appendToFile(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                GpsHelper.sendDataBytes(buffer);
                Log.i("handler..", " read from qianxun socket write to pad chuan kou, buffer.length="+buffer.length);
                String data = Util.byteToString(buffer);
                if (!data.equals("")) {
                    if (outHandler != null) {
                        if(data.contains("200")){
                            Message msg = outHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = data;
                            outHandler.sendMessage(msg);
                            return;
                        }else {
                            Message msg = outHandler.obtainMessage();
                            msg.what = 1;
                            if (stateStr.equals("RTK定位中.")) {
                                stateStr = "RTK定位中..";
                            } else if (stateStr.equals("RTK定位中..")) {
                                stateStr = "RTK定位中...";
                            }else   if (stateStr.equals("RTK定位中...")) {
                                stateStr = "RTK定位中.";
                            }
                            msg.obj = stateStr;
                            outHandler.sendMessage(msg);
                        }
                    }
                }
                Util.l("RTK buffer .l=" + buffer.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.l("RTK appendToFile err=" + e.getMessage());
        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    class MyThread extends Thread {
        public void run() {
            while (start) {
                try {
                    if (type == 1) {
                        Thread.sleep(1000);
                    } else if (type == 2) {
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SocketUtil.this.myHandler.sendMessage(myHandler.obtainMessage());
            }
        }
    }

    public void disconnect() {
        new Thread(() -> {
            if (client != null) {
                try {
                    client.shutdownInput();
                    client.shutdownOutput();
                    client.close();
                    client = null;
                    if (myThread != null) {
                        start = false;
                        myThread.interrupt();
                        myThread = null;
                    }
                } catch (Exception e) {
                    Util.l(" disconnect err" + e.getMessage());
                    Util.showErr(context, " dis err=" + e.getMessage());
                }
            }
        }).start();
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPORT() {
        return PORT;
    }

    public void setPORT(String PORT) {
        this.PORT = PORT;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
