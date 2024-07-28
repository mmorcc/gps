package server;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Util {
    public static ArrayList<Device> data = new ArrayList<>();

    public static void saveData(Device device) {
        if (device != null) {
            Device old = findDeviceByIM(device.IM);
            if (old != null) {
                data.remove(old);
            }
            data.add(device);
        }
    }

    private static Device findDeviceByIM(String im) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).IM.equals(im)) {
                return data.get(i);
            }
        }
        return new Device();
    }

    public static void removeDeviceByIM(String im) {
        if(isNullOrEmpty(im)){
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).IM.equals(im)) {
                Device device= data.get(i);
                data.remove(device);
               return ;
            }
        }
    }
    private static String findAllDevice() {
        String str = "";
        l("device.length=" + data.size());
        for (int i = 0; i < data.size(); i++) {
            Device item = data.get(i);
            if(item.out!=null){
                str += item.IM + ",";
            }
        }
        if (!isNullOrEmpty(str)) {
            str = str.substring(0, str.length() - 1);
        }
        return str+END;
    }

    private static void l(String s) {
        System.out.println(s);
    }

    private static void deleteDeviceByIM(String im) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).IM.equals(im)) {
                data.remove(i);
                return;
            }
        }
    }

    public static boolean isNullOrEmpty(String el) {
        if (el == null || el.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static String getValue(String str, int i) {
        String result = "";
        if (!isNullOrEmpty(str)) {
            String[] arr = str.split(",");
            if (arr.length > i) {
                result = arr[i];
            }
        }
        return result;
    }

    private static String END = "\r\n";

    public static String dealCMD(String cmd, PrintWriter out, String IMDef) {
        String err = "cmd err"+END;
        if (isNullOrEmpty(cmd)) {
            return err;
        }
        //$CMD=IMEI=863184063186108
        //注册设备$CMD=IMEI=<IMEI>
        if (cmd.startsWith("$CMD=IMEI=")) {
            Device device = new Device();
            device.IM = cmd.substring(10).trim();

            l("  device.IM=" + device.IM);
            device.out = out;
            saveData(device);
            findAllDevice();
            return "$cmd=IMEI" + END;
        }
        // c2->server      $CMD=POSITION=863184063186108,120.21473687400,30.20593629500,15.214399,4,2024-06-26 15:24:23
        //上传位置 $CMD=POSITION=<IMEI>,<lng0>,<lat0>,<alt0>,<state0>,<yyyy-MM-DD hh:mm:ss>
        if (cmd.startsWith("$CMD=POSITION=")) {
            String value = cmd.substring(14).trim();
            String IM = getValue(value, 0);
            Device device = findDeviceByIM(IM);
            String positionStr = cmd.substring(14).trim();
            device.position = positionStr;
            saveData(device);
            return "$cmd=POSITION" + END;
        }
        //pad -->server
        //$CMD=UP_POSITION=<IM>,<lng1>,<lat1>,<alt1>,<state1>
        if (cmd.startsWith("$CMD=UP_POSITION=")) {
            String myResult = "$cmd=SUCCESS"+END;
            String value = cmd.substring(17).trim();
            String IM = getValue(value, 0);
            String position = "";
            if (cmd.length() > cmd.indexOf(",") + 1) {
                position = cmd.substring(cmd.indexOf(",") + 1).trim();
            }
            if (!isNullOrEmpty(IM)) {
                Device device = findDeviceByIM(IM);
                if (device.out != null) {
                    String result = "$CMD=DIRECTION?" + position + END;
                    device.out.println(result);
                    l(" DIRECTION out send " + result);
                } else {
                    myResult = "$cmd=remote device offline"+END;
                    l(" DIRECTION out closed");
                }
            }
            return myResult;
        }
        //c2-->server
        //定向请求$cmd=DIRECTION=<lng1>,<lat1>,<alt1>,<state1>,<lng0>,<lat0>,<alt0>,<state0>,<direction>,<yyyy-MM-DD hh:mm:ss>
        if (cmd.startsWith("$cmd=DIRECTION=")) {
            String position = cmd.substring(15).trim();
            if (!isNullOrEmpty(IMDef)) {
                Device device = findDeviceByIM(IMDef);
                device.position2 = position;
                saveData(device);
            }
            return ""+END;
        }
        //查询设备位置$CMD=GET_POSITION=<IMEI>
        if (cmd.startsWith("$CMD=GET_POSITION=")) {
            String myResult = "";
            String IM = cmd.substring(18).trim();
            if (!isNullOrEmpty(IM)) {
                Device device = findDeviceByIM(IM);
                if(!isNullOrEmpty(device.position)){
                    myResult = "$cmd=SUCCESS="+device.position;
                }
            }
            return myResult;
        }
        //查询设备位置2 $CMD=GET_POSITION2=<IMEI>
        if (cmd.startsWith("$CMD=GET_POSITION2=")) {
            String myResult = "";
            String IM = cmd.substring(19).trim();
            if (!isNullOrEmpty(IM)) {
                Device device = findDeviceByIM(IM);
                if(!isNullOrEmpty(device.position2)){
                    myResult = "$cmd=SUCCESS="+device.position2;
                }
            }
            return myResult;
        }
        //取设备IM
        if (cmd.startsWith("$CMD=GET_DEVICES")) {
            return "$cmd=" + findAllDevice();
        }
        return err;
    }

    //$CMD=DIRECTION?<lng1>,<lat1>,<alt1>,<state1>
    public static String getDirect(String IM) {
        if (!isNullOrEmpty(IM)) {
            Device device = findDeviceByIM(IM);
            return "$CMD=DIRECTION?" + device.position + END;
        }
        return "";
    }
}
