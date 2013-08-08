
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class Setting extends Activity implements View.OnClickListener, OnTouchListener {

    private Button mExit;
    private View mStauts;
    private View mSignature;
    private View mUserInfo;
    private View mAbout;
    private TextView mUserName;
    private ImageView mUserPhoto;
    private TextView mUserStatus;
    private TextView mUserSignature;
    private String mNewSignature;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    public static final int HANDLER_MEG_UPDATE_SUCCESS = 1;
    public static final int HANDLER_MEG_UPDATE_FAIL = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_UPDATE_SUCCESS:
                    showToast(R.string.update_success, R.drawable.tenpay_toast_logo_success);
                    mUserSignature.setText(mNewSignature);
                    mService.getMySelf().setSignature(mNewSignature);
                    break;
                case HANDLER_MEG_UPDATE_FAIL:
                    showToast(R.string.update_fail, 0);
                    mNewSignature = "";
                    break;
            }
        }
    };

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
        mService.setSettingHandle(mHandler);
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
        mUserSignature = (TextView) findViewById(R.id.user_signature);
        mUserSignature.setText(mService.getMySelf().getSignature());
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
        if (v == mExit) {
            mService.offLine();
        } else if (v == mStauts) {
            Util.ChatLog("mStauts onclick");
        } else if (v == mSignature) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("old_context", mUserSignature.getText().toString());
            intent.putExtra("title", getString(R.string.signature));
            intent.putExtra("back", getString(R.string.setting));
            startActivityForResult(intent, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 2) {
                    mNewSignature = data.getStringExtra("edit_context");
                    if (mNewSignature != null
                            && !mNewSignature.equals(mService.getMySelf().getSignature())) {
                        mService.updateSignature(mNewSignature);
                    }
                }
                break;
        }
    }
}
