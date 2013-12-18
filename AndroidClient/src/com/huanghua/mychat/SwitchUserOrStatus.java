
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.util.Util;

public class SwitchUserOrStatus extends Activity implements View.OnClickListener, OnTouchListener {

    private TextView mUserName;
    private ImageView mUserPhoto;
    private TextView mUserStatus;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;
    private View mStatusOnline;
    private View mStatusStealth;
    private View mStatusLeave;
    private View mAddUser;
    private Button mBack;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_switchuser);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mService.setSettingHandle(mHandler);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserPhoto = (ImageView) findViewById(R.id.user_photo);
        mUserStatus = (TextView) findViewById(R.id.user_status);
        mStatusOnline = findViewById(R.id.status_online);
        mStatusStealth = findViewById(R.id.status_stealth);
        mStatusLeave = findViewById(R.id.status_leave);
        mAddUser = findViewById(R.id.add_user);
        mStatusOnline.setOnClickListener(this);
        mStatusOnline.setOnTouchListener(this);
        mStatusStealth.setOnClickListener(this);
        mStatusStealth.setOnTouchListener(this);
        mStatusLeave.setOnClickListener(this);
        mStatusLeave.setOnTouchListener(this);
        mAddUser.setOnClickListener(this);
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(this);
    }

    private void showToast(String msg, int image) {
        View toast = mToast.getView();
        TextView m = (TextView) toast.findViewById(R.id.toast_msg);
        ImageView iv = (ImageView) toast.findViewById(R.id.toast_image);
        m.setText(msg);
        if (image != 0) {
            iv.setBackgroundResource(image);
        }
        mToast.show();
    }

    private void showToast(int msg, int image) {
        showToast(getString(msg), image);
    }

    @Override
    public void onClick(View v) {
        if (v == mStatusOnline) {

        } else if (v == mStatusLeave) {

        } else if (v == mStatusStealth) {

        } else if (v == mBack) {
            mService.setSwitchTab(Home.TAB_TAG_SETTING);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserName.setText(mService.getMySelf().getName());
        int status = mService.getMySelf().getStatus();
        mUserStatus.setText(Util.getStatus(getResources(), status));
        showSelectStatusFlag(status);
    }

    public void onBackPressed() {
        if (null != getParent()) {
            getParent().moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (v instanceof RelativeLayout) {
                    RelativeLayout r = ((RelativeLayout) v);
                    r.setPressed(true);
                    for (int i = 0; i < r.getChildCount(); i++) {
                        r.getChildAt(i).setPressed(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (v instanceof RelativeLayout) {
                    RelativeLayout r = ((RelativeLayout) v);
                    r.setPressed(false);
                    for (int i = 0; i < r.getChildCount(); i++) {
                        r.getChildAt(i).setPressed(false);
                    }
                }
                break;
        }
        return false;
    }

    private void showSelectStatusFlag(int status) {
        ImageView online = (ImageView) mStatusOnline.findViewById(R.id.status_online_flag);
        ImageView leave = (ImageView) mStatusLeave.findViewById(R.id.status_leave_flag);
        ImageView stedlth = (ImageView) mStatusStealth.findViewById(R.id.status_stealth_flag);
        switch (status) {
            case Util.USER_STATUS_ONLINE:
                online.setVisibility(View.VISIBLE);
                leave.setVisibility(View.GONE);
                stedlth.setVisibility(View.GONE);
                break;
            case Util.USER_STATUS_LEAVE:
                online.setVisibility(View.GONE);
                leave.setVisibility(View.VISIBLE);
                stedlth.setVisibility(View.GONE);
                break;
            case Util.USER_STATUS_STEDLTH:
                online.setVisibility(View.GONE);
                leave.setVisibility(View.GONE);
                stedlth.setVisibility(View.VISIBLE);
                break;
        }
    }
}
