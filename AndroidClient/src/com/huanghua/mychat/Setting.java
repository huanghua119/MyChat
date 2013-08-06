
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class Setting extends Activity implements View.OnClickListener, OnTouchListener {

    private Button mExit;
    private View mStauts;
    private View mSignature;
    private View mUserInfo;
    private View mAbout;
    private TextView mUserName;
    private ImageView mUserPhoto;
    private TextView mUserStatus;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_setting);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mStauts = findViewById(R.id.status);
        mStauts.setOnClickListener(this);
        mStauts.setOnTouchListener(this);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserName.setText(mService.getMySelf().getName());
        mUserPhoto = (ImageView) findViewById(R.id.user_photo);
        mUserStatus = (TextView) findViewById(R.id.user_status);
        mUserStatus.setText(Util.getStatus(getResources(), mService.getMySelf().getStatus()));
        mSignature = findViewById(R.id.signature);
        mSignature.setOnClickListener(this);
        mSignature.setOnTouchListener(this);
        mUserInfo = findViewById(R.id.user_info);
        mUserInfo.setOnClickListener(this);
        mUserInfo.setOnTouchListener(this);
        mAbout = findViewById(R.id.about_me);
        mAbout.setOnClickListener(this);
        mAbout.setOnTouchListener(this);
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
        } else if (v == mStauts) {
            Util.ChatLog("mStauts onclick");
        } else if (v == mSignature) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("old_context", "");
            intent.putExtra("title", getString(R.string.signature));
            intent.putExtra("back", getString(R.string.setting));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    for (int i = 0; i < r.getChildCount(); i++) {
                        r.getChildAt(i).setPressed(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (v instanceof RelativeLayout) {
                    RelativeLayout r = ((RelativeLayout) v);
                    for (int i = 0; i < r.getChildCount(); i++) {
                        r.getChildAt(i).setPressed(false);
                    }
                }
                break;
        }
        return false;
    }
}
