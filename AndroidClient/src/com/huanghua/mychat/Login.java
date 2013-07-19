
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
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
    }

    private void showToast(String msg) {
        mToast.cancel();
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
        User u = getRemeberUser();
        if (u.getId() != null && !u.getId().equals("")) {
            mUserId.setText(u.getId());
            mUserPass.setText(u.getPassword());
            mRemeberPass.setChecked(true);
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
        }
    }

    private User getRemeberUser() {
        User u = new User();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        u.setId(sp.getString("userId", ""));
        u.setPassword(sp.getString("userPass", ""));
        return u;
    }

    public void remeberUser(String userId, String userPass) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", userId);
        editor.putString("userPass", userPass);
        editor.commit();
    }

    public void removeRemeber() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", "");
        editor.putString("userPass", "");
        editor.commit();
    }
}
