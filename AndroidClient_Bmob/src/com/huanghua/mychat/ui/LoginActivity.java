
package com.huanghua.mychat.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

import com.huanghua.mychat.R;
import com.huanghua.mychat.util.CommonUtils;
import com.huanghua.mychat.util.Util;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private ImageView mUserPhoto;
    private EditText mUserId;
    private EditText mUserPass;
    private Button mLogin;
    private Button mRegister;

    public static final int DIALOG_NEW_REGISTER = 1;
    public static final int DIALOG_GET_LIST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    private void init() {
        mUserPhoto = (ImageView) findViewById(R.id.userPhoto);
        mUserId = (EditText) findViewById(R.id.userId);
        mUserPass = (EditText) findViewById(R.id.userPass);
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mRegister) {
            Intent intent = new Intent(LoginActivity.this,
                    RegisterActivity.class);
            startActivity(intent);
        } else if (v == mLogin) {
            boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
            if (!isNetConnected) {
                ShowToast(R.string.network_tips);
                return;
            }
            login();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = getRemeberUserName();
        String pass = getRemeberPass();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pass)) {
            mUserId.setText(name);
            mUserPass.setText(pass);
        }
    }

    private void login() {
        String name = mUserId.getText().toString();
        String password = mUserPass.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.notid);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.notpass);
            return;
        }

        remeberUser(name, password);
        showDialog(DIALOG_NEW_REGISTER);
        userManager.login(name, password, new SaveListener() {

            @Override
            public void onSuccess() {
                removeDialog(DIALOG_NEW_REGISTER);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showDialog(DIALOG_GET_LIST);
                    }
                });
                // 更新用户的地理位置以及好友的资料
                updateUserInfos();

                removeDialog(DIALOG_GET_LIST);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errorcode, String arg0) {
                removeDialog(DIALOG_NEW_REGISTER);
                BmobLog.i(arg0);
                ShowToast(arg0);
            }
        });
    }

    private String getRemeberUserName() {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        return sp.getString("name", "");
    }

    private String getRemeberPass() {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        return sp.getString("userPass", "");
    }

    public void remeberUser(String userId, String userPass) {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", userId);
        editor.putString("userPass", userPass);
        editor.commit();
    }

    public void removeRemeber() {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", "");
        editor.putString("userPass", "");
        editor.commit();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_NEW_REGISTER:
                dialog = Util.createLoadingDialog(this, getString(R.string.login_now));
                break;
            case DIALOG_GET_LIST:
                dialog = Util.createLoadingDialog(this, getString(R.string.get_user_list));
        }
        return dialog;
    }
}
