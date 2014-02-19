
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.util.Util;

public class OtherUserLogin extends Activity implements OnClickListener {

    private Button mBack;
    private Button mOk;
    private EditText mPassWord;
    private EditText mUserId;
    private TextView mTitle;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    public static final int DIALOG_NEW_REGISTER = 1;
    public static final int HANDLE_MSG_LOGIN_FAIL = 1;
    public static final int HANDLER_MEG_FINASH = 2;

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
                    setLogin(false);
                    removeDialog(DIALOG_NEW_REGISTER);
                    break;
                case HANDLER_MEG_FINASH:
                    Intent intent = new Intent();
                    intent.setClass(OtherUserLogin.this, Home.class);
                    startActivity(intent);
                    mService.setOtherLoginHandle(null);
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
        setContentView(R.layout.othen_login_view);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mService = ChatService.getInstance();
        mService.setOtherLoginHandle(mHandler);
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mBack = (Button) findViewById(R.id.back);
        mOk = (Button) findViewById(R.id.ok);
        mBack.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mUserId = (EditText) findViewById(R.id.user_id);
        mPassWord = (EditText) findViewById(R.id.user_pass);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mTitle.setText(intent.getStringExtra("title"));
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            onBackPressed();
        } else if (v == mOk) {
            startLogin();
        }
    }

    private void startLogin() {
        String userId = mUserId.getText().toString();
        String userPass = mPassWord.getText().toString();
        if (userId == null || userId.equals("")) {
            showToast(R.string.notid);
        } else if (userPass == null || userPass.equals("")) {
            showToast(R.string.notpass);
        } else {
            mService.offLine(false);
            mService.login(this, userId, userPass);
            showDialog(DIALOG_NEW_REGISTER);
        }
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

    public void setLogin(boolean isLogin) {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_login", isLogin);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        if (mService.getMySelf() == null) {
            showToast(R.string.no_user_toast);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
    }
}
