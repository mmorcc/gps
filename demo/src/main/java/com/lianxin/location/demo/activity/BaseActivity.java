package com.lianxin.location.demo.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.SharedPreferencesUtils;
import com.lianxin.location.demo.utils.Util;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    protected SharedPreferencesUtils sp= SharedPreferencesUtils.getInstance("isLogin");
    private int layoutId= R.layout.clientsocket;

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
    protected Context context;

//    protected String GGAData ="$GNGGA,093025.00,3128.43587150,N,12015.87953577,E,1,21,1.2,21.0398,M,7.3183,M,,*73";
    protected String GGAData ="";

    protected static String END = "\r\n";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//        String lang=sp.get("lang");
//        String country=sp.get("country");
//        if(!TextUtils.isEmpty(lang)){
//            changeLanguage(this,lang,country);
//        }
        setContentView(layoutId);
        context=this;
    }
    public  final void changeLanguage(Context context, String language, String country) {
        if (context == null || TextUtils.isEmpty(language)) {return;}
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, null);

    }
    public void setEText(EditText et,String str){
        if(et==null){
            return;
        }
        if(Util.isNotEmpty(str)){
            et.setText(str);
        }else {
            et.setText("");
        }
    }
    public TextView setText(int id, String str){
        TextView et=findViewById(id);
        if(et==null){
            return null;
        }
        if(Util.isNotEmpty(str)){
            et.setText(str);
        }else {
            et.setText("");
        }
//        String fonts="fonts/f.ttf";
//        Typeface typeface=Typeface.createFromAsset(getAssets(),fonts);
//        et.setTypeface(typeface);
        return et;
    }
    public RadioButton setRadioButton(int id, String str){
        RadioButton et=findViewById(id);
        if(et==null){
            return null;
        }
        if(Util.isNotEmpty(str)){
            et.setText(str);
        }else {
            et.setText("");
        }
        String fonts="fonts/f.ttf";
        Typeface typeface=Typeface.createFromAsset(getAssets(),fonts);
        et.setTypeface(typeface);
        return et;
    }
    public void setText(EditText et, String str){
        if(et==null){
            return;
        }
        if(Util.isNotEmpty(str)){
            et.setText(str);
        }else {
            et.setText("");
        }
//        String fonts="fonts/f.ttf";
//        Typeface typeface=Typeface.createFromAsset(getAssets(),fonts);
//        et.setTypeface(typeface);
    }
    public void reStart() {
        recreate();
    }

    @Override
    protected void onDestroy() {
        context=null;
        super.onDestroy();
    }
}
