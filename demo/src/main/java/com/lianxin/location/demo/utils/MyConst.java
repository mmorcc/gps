package com.lianxin.location.demo.utils;

import android.os.Environment;

import java.io.File;

public class MyConst {
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "Download" + File.separator + "GISLog" + File.separator;

    public static final String FILE_NAME = "myLog.txt";
    //
    public static final String FILE_POSITION = "position.txt";
    public static final String FILE_RTK =  "RTK.txt";
    public static final String IM = "IM";

    public static final String NAME="usernameStr";
    public static final String PW="passwordStr";
    public static final String IP="IPStr";
    public static final String PORT="PortStr";
    public static final String JING_DU_1="JING_DU_1Str";
    public static final String JING_DU_2="JING_DU_2_Str";

    public static final String LOG_INDEX="logIndex";



    public static final String MY_JING_DU="MY_JING_DU";
    public static final String MY_WEI_DU="MY_WEI_DU";
    public static final String IS_START= "isStart";
}
