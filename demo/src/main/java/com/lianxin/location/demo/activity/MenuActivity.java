package com.lianxin.location.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lianxin.location.demo.MainActivity;
import com.lianxin.location.demo.R;
import com.lianxin.location.demo.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MenuActivity extends BaseActivity {
    TextView item1,item2,item3;
    public static String titleS="空间感知手持终端";
    public static  int index=0;

    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
    };
    private List<String> permissionList = new ArrayList<>();
    private long firstTime=new Date().getTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initTitle();
        findViewById(R.id.item1).setOnClickListener(o->{
            Util.toAc(this, MainActivity.class);
        });
        findViewById(R.id.item2).setOnClickListener(o->{
            Util.toAc(this, SetActivity.class);

        });
        findViewById(R.id.item3).setOnClickListener(o->{
            Util.toAc(this, LogActivity.class);

        });
        findViewById(R.id.item4).setOnClickListener(o->{
            Util.toAc(this, SocketActivity.class);

        });
        getPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTitle() {
        TextView title=findViewById(R.id.title);
        if(!TextUtils.isEmpty(titleS)){
            title.setText(titleS);
        }
        ImageView bacBtn=findViewById(R.id.back_btn);
        bacBtn.setVisibility(View.VISIBLE);
        bacBtn.setImageResource(R.drawable.back);
        bacBtn.setOnClickListener(v -> {
            long now=System.currentTimeMillis();
            if(now-firstTime>3000){
                firstTime=now;
                Util.showErr(context,"再按一次退出");
                return;
            }
            finish();
        });

//        ImageView rb=findViewById(R.id.right_btn);
//        rb.setVisibility(View.VISIBLE);
//        rb.setOnClickListener(v->{
//            AddActivity.type=1;
//            startActivity(new Intent(this,AddActivity.class));
//        });

    }
    private void getPermission() {
        if (permissionList != null) {
            permissionList.clear();
        }
        //版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Util.l("大于23");
            //权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                    Util.l("没有权限="+permission);
                }else {
                    Util.l("有权限="+permission);
                }
            }
            Util.l("没有权限的l="+permissionList.size());
            if (!permissionList.isEmpty()) {
                Util.l("发起请求="+permissionList.size());
//                EasyPermissions.requestPermissions(this, "请允许使用权限", 1000,  permissionList.toArray(new String[permissionList.size()]));
                ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1000);
            } else {
                startActivity(new Intent(this, MainActivity.class));

            }
        }else {

            Util.l("小于23");
        }
    }



        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 1000) {
                //权限请求失败
                if (grantResults.length > 0) {
                    //存放没授权的权限
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //说明都授权了
//                    getPic();
                    } else {
                        //默认false 第二次提醒会有是否不在询问按钮，选择则为true  不在提醒是否申请权限
//                    if (!shouldShowRequestPermissionRationale(deniedPermissions.get(0))) {
////                        permissionDialog();
//                    }
                    }
                }
            }
        }
}
