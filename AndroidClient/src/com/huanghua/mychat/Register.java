
package com.huanghua.mychat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.util.Util;

public class Register extends Activity implements View.OnClickListener {

    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;
    private View mRegisterSuccess;
    private View mRegisterView;
    private Button mBack;
    private Button mCommit;
    private Button mAutoLogin;
    private EditText mUserName;
    private EditText mPass;
    private EditText mPassTwo;
    private RadioButton mRadioMan;
    private RadioButton mRadioWoman;
    private TextView mProcessCommit;
    private TextView mProcessAccount;
    private TextView mSuccessAlert;
    private String userId, userPass;

    public static final int HANDLER_MEG_REGISTER_SUCCESS = 1;
    public static final int HANDLER_MEG_REGISTER_FAIL = 2;
    public static final int HANDLER_MEG_FINASH = 3;

    public static final int DIALOG_NEW_REGISTER = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_REGISTER_SUCCESS:
                    Bundle b = msg.getData();
                    userId = b.getString("user_id");
                    userPass = b.getString("user_pass");
                    goToRegisterSuccess(userId);
                    removeDialog(DIALOG_NEW_REGISTER);
                    break;
                case HANDLER_MEG_REGISTER_FAIL:
                    showToast(R.string.registerFail);
                    goToRegister();
                    removeDialog(DIALOG_NEW_REGISTER);
                    break;
                case HANDLER_MEG_FINASH:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mService.setRegisterHandler(mHandler);
        mRegisterSuccess = findViewById(R.id.register_success);
        mRegisterView = findViewById(R.id.register_view);
        mCommit = (Button) findViewById(R.id.commit_register);
        mAutoLogin = (Button) findViewById(R.id.auto_login);
        mBack = (Button) findViewById(R.id.back);
        mAutoLogin.setOnClickListener(this);
        mCommit.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.userName);
        mPass = (EditText) findViewById(R.id.pass);
        mPassTwo = (EditText) findViewById(R.id.twopass);
        mRadioMan = (RadioButton) findViewById(R.id.six_man);
        mRadioWoman = (RadioButton) findViewById(R.id.six_woman);
        mProcessCommit = (TextView) findViewById(R.id.process_commit);
        mProcessAccount = (TextView) findViewById(R.id.process_account);
        mSuccessAlert = (TextView) findViewById(R.id.success_alert);
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
        if (v == mBack) {
            finish();
        } else if (v == mCommit) {
            startRegister();
        } else if (v == mAutoLogin) {
            Intent intent = new Intent(this, Login.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_pass", userPass);
            intent.setAction(Login.ACTION_AUTO_LOGIN);
            startActivity(intent);
            mService.setRegisterHandler(null);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void startRegister() {
        String name = mUserName.getText().toString();
        String pass = mPass.getText().toString();
        String twopass = mPassTwo.getText().toString();
        int six = 0;
        if (mRadioMan.isChecked()) {
            six = 1;
        } else if (mRadioWoman.isChecked()) {
            six = 0;
        }
        if (name == null || "".equals(name)) {
            showToast(R.string.namenotnull);
        } else if (pass == null || "".equals(pass)) {
            showToast(R.string.passnotnull);
        } else if (twopass == null || "".equals(twopass) || !pass.equals(twopass)) {
            showToast(R.string.twopassnotpass);
        } else {
            mService.userRegister(name, twopass, six);
            mCommit.setEnabled(false);
            showDialog(DIALOG_NEW_REGISTER);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_NEW_REGISTER:
                dialog = Util.createLoadingDialog(this, getString(R.string.register_now));
                break;
        }
        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void goToRegister() {
        mCommit.setEnabled(true);
        mRegisterView.setVisibility(View.VISIBLE);
        mRegisterSuccess.setVisibility(View.GONE);
        mProcessCommit.setTextColor(getResources().getColor(R.color.tab_text_color));
        mProcessAccount.setTextColor(getResources().getColor(R.color.context_search_color));
    }

    private void goToRegisterSuccess(String id) {
        mRegisterView.setVisibility(View.GONE);
        mRegisterSuccess.setVisibility(View.VISIBLE);
        mProcessCommit.setTextColor(getResources().getColor(R.color.context_search_color));
        mProcessAccount.setTextColor(getResources().getColor(R.color.tab_text_color));
        mSuccessAlert.setText(getString(R.string.succesAlert, id));
    }

}
