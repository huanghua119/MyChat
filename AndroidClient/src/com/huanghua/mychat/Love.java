
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;

public class Love extends Activity implements View.OnClickListener {

    private Button mExit;
    private TextView mUserName;
    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_love);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
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
            mService.offLine(true);
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

}
