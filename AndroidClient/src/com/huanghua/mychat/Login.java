
package com.huanghua.mychat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.BackStageService;
import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.util.Util;
import com.huanghua.mychat.widght.AphoneCheckBox;
import com.huanghua.pojo.User;

public class Login extends Activity implements View.OnClickListener {

    private ImageView mUserPhoto;
    private EditText mUserId;
    private EditText mUserPass;
    private Button mLogin;
    private Button mRegister;
    private AphoneCheckBox mRemeberPass;
    private AphoneCheckBox mAutoLogin;
    private TextView mRemeberLabel;
    private TextView mAutoLabel;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    public static final String ACTION_AUTO_LOGIN = "action_auto_login";

    public static final int HANDLE_MSG_LOGIN_FAIL = 1;
    public static final int HANDLER_MEG_FINASH = 2;
    public static final int DIALOG_NEW_REGISTER = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLE_MSG_LOGIN_FAIL:
                    Bundle data = msg.getData();
                    String error = data.getString("error");
                    if (error.equals("passerror")) {
                        showToast(R.string.passerror);
                    } else if (error.equals("usernotfind")) {
                        showToast(R.string.usernotfind);
                    } else {
                        showToast(error);
                    }
                    mLogin.setClickable(true);
                    removeDialog(DIALOG_NEW_REGISTER);
                    break;
                case HANDLER_MEG_FINASH:
                    Intent intent = new Intent();
                    intent.setClass(Login.this, Home.class);
                    startActivity(intent);
                    mService.setLoginHandler(null);
                    removeDialog(DIALOG_NEW_REGISTER);
                    finish();
                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mUserPhoto = (ImageView) findViewById(R.id.userPhoto);
        mUserId = (EditText) findViewById(R.id.userId);
        mUserPass = (EditText) findViewById(R.id.userPass);
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);
        mRemeberPass = (AphoneCheckBox) findViewById(R.id.remeber);
        mRemeberPass.setChecked(true);
        mAutoLogin = (AphoneCheckBox) findViewById(R.id.autologin);
        mAutoLogin.setChecked(true);
        mRemeberLabel = (TextView) findViewById(R.id.remeberLabel);
        mAutoLabel = (TextView) findViewById(R.id.autoLabel);
        mRemeberLabel.setOnClickListener(this);
        mAutoLabel.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mService = ChatService.getInstance();
        mService.setLoginHandler(mHandler);
    }

    private void showToast(String msg) {
        View toast = mToast.getView();
        TextView m = (TextView) toast.findViewById(R.id.toast_msg);
        m.setText(msg);
        mToast.show();
    }

    private void showToast(int msg) {
        showToast(getString(msg));
    }

    @Override
    public void onClick(View v) {
        if (v == mRegister) {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else if (v == mLogin) {
            startLogin();
        } else if (v == mAutoLabel) {
            mAutoLogin.setChecked(!mAutoLogin.isChecked());
        } else if (v == mRemeberLabel) {
            mRemeberPass.setChecked(!mRemeberPass.isChecked());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String action = getIntent().getAction();
        if (action != null && ACTION_AUTO_LOGIN.equals(action)) {
            String id = getIntent().getStringExtra("user_id");
            String pass = getIntent().getStringExtra("user_pass");
            mUserId.setText(id);
            mUserPass.setText(pass);
            mRemeberPass.setChecked(true);
            mLogin.performClick();
        } else {
            User u = getRemeberUser();
            Intent intent = new Intent(BackStageService.CHAT_ACTION_REMOVE_NOTIFY);
            intent.putExtra("id", 2);
            sendBroadcast(intent);
            if (u.getId() != null && !u.getId().equals("")) {
                mUserId.setText(u.getId());
                mUserPass.setText(u.getPassword());
                mRemeberPass.setChecked(true);
            }
        }
    }

    private void startLogin() {
        String userId = mUserId.getText().toString();
        String userPass = mUserPass.getText().toString();
        if (userId == null || userId.equals("")) {
            showToast(R.string.notid);
        } else if (userPass == null || userPass.equals("")) {
            showToast(R.string.notpass);
        } else {
            if (mRemeberPass.isChecked()) {
                remeberUser(userId, userPass);
            } else {
                removeRemeber();
            }
            mLogin.setClickable(false);
            mService.login(this, userId, userPass);
            showDialog(DIALOG_NEW_REGISTER);
        }
    }

    private User getRemeberUser() {
        User u = new User();
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        u.setId(sp.getString("userId", ""));
        u.setPassword(sp.getString("userPass", ""));
        return u;
    }

    public void remeberUser(String userId, String userPass) {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", userId);
        editor.putString("userPass", userPass);
        editor.commit();
    }

    public void removeRemeber() {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", "");
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
        }
        return dialog;
    }
}
