
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
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

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.widght.AphoneCheckBox;
import com.huanghua.pojo.User;

public class Home extends Activity implements View.OnClickListener {

    private Button mExit;
    private TextView mUserName;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    private static final int MSG_LOGIN_FAIL = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_LOGIN_FAIL:
                    Bundle data = msg.getData();
                    String error = data.getString("error");
                    if (error.equals("passerror")) {
                        showToast(R.string.passerror);
                    } else {
                        showToast(R.string.usernotfind);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mService.setHome(this);
        mUserName = (TextView) findViewById(R.id.userName);
        mUserName.setText(mService.getMySelf().getName());
        mExit = (Button) findViewById(R.id.exit);
        mExit.setOnClickListener(this);
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
        if (v == mExit) {
            mService.offLine();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
