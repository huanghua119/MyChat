
package com.huanghua.mychat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

import com.huanghua.mychat.R;
import com.huanghua.mychat.bean.User;
import com.huanghua.mychat.util.CommonUtils;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private Button mBack;
    private Button mCommit;
    private EditText mUserName;
    private EditText mPass;
    private EditText mPassTwo;
    private RadioButton mRadioMan;
    private RadioButton mRadioWoman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
    }

    private void init() {
        mCommit = (Button) findViewById(R.id.commit_register);
        mCommit.setOnClickListener(this);
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.userName);
        mPass = (EditText) findViewById(R.id.pass);
        mPassTwo = (EditText) findViewById(R.id.twopass);
        mRadioMan = (RadioButton) findViewById(R.id.six_man);
        mRadioWoman = (RadioButton) findViewById(R.id.six_woman);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            finish();
        } else if (v == mCommit) {
            startRegister();
        }
    }
    private void startRegister(){
        String name = mUserName.getText().toString();
        String password = mPass.getText().toString();
        String pwd_again = mPassTwo.getText().toString();
        
        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.namenotnull);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.passnotnull);
            return;
        }
        if (!pwd_again.equals(password)) {
            ShowToast(R.string.twopassnotpass);
            return;
        }
        
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ShowToast(R.string.network_tips);
            return;
        }
        
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(name);
        bu.setPassword(password);
        bu.setSex(mRadioMan.isChecked() ? true : false);
        //将user和设备id进行绑定
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                progress.dismiss();
                ShowToast(R.string.register_succes);
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(bu.getUsername());
                //更新地理位置信息
                updateUserLocation();
                // 启动主页
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                BmobLog.i(arg1);
                ShowToast(getString(R.string.registerFail) + arg1);
                progress.dismiss();
            }
        });
    }

}
