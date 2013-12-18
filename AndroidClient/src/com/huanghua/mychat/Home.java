
package com.huanghua.mychat;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TextView;

import com.huanghua.mychat.service.BackStageService;
import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.service.MessageService;
import com.huanghua.pojo.User;

@SuppressWarnings("deprecation")
public class Home extends TabActivity implements View.OnClickListener {

    public static String TAB_TAG_MESSAG = "message";
    public static String TAB_TAG_CONTACT = "contact";
    public static String TAB_TAG_LOVE = "love";
    public static String TAB_TAG_SETTING = "setting";
    public static String TAB_TAG_SWITCH_USER = "switchuser";
    private ChatService mService;

    private TabHost mTabHost;
    private Intent mMessageIntent, mContactIntent, mLoveIntent, mSettingIntent, mSwitchUserIntent;
    private View mMessageButton, mContactButton, mLoveButton, mSettingButton;
    private TextView mNewCount;
    private int mCurTabId;

    private Animation mLeftIn, mLeftOut;
    private Animation mRightIn, mRightOut;

    public static final int HANDLER_MEG_FINISH = 1;
    public static final int HANDLER_MEG_NEW_COUNT = 2;
    public static final int HANDLER_MEG_SWITCH_USER_TAB = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_FINISH:
                    setLogin(false);
                    Intent intent = new Intent();
                    intent.setClass(Home.this, Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                    break;
                case HANDLER_MEG_NEW_COUNT:
                    int count = MessageService.getAllNewMessage2(null);
                    if (count != 0) {
                        mNewCount.setVisibility(View.VISIBLE);
                        mNewCount.setText(count + "");
                    } else {
                        mNewCount.setVisibility(View.GONE);
                    }
                    break;
                case HANDLER_MEG_SWITCH_USER_TAB:
                    String tab = msg.getData().getString("switch_tab");
                    if (tab != null && !"".equals(tab)) {
                        if (tab.equals(TAB_TAG_SWITCH_USER)) {
                            mTabHost.getCurrentView().startAnimation(mRightOut);
                        } else if (tab.equals(TAB_TAG_SETTING)) {
                            mTabHost.getCurrentView().startAnimation(mLeftOut);
                        }
                        setCurrentTabByTag(tab);
                        if (tab.equals(TAB_TAG_SWITCH_USER)) {
                            mTabHost.getCurrentView().startAnimation(mRightIn);
                        } else if (tab.equals(TAB_TAG_SETTING)) {
                            mTabHost.getCurrentView().startAnimation(mLeftIn);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        init();
        prepareIntent();
        setupIntent();
        mLeftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
        mLeftOut = AnimationUtils.loadAnimation(this, R.anim.left_out);
        mRightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(this, R.anim.right_out);
        mMessageButton.performClick();
    }

    private void init() {
        Intent service = new Intent(this, BackStageService.class);
        startService(service);
        mService = ChatService.getInstance();
        mService.setHomeHandler(mHandler);
        isLogin();
        mMessageButton = findViewById(R.id.message);
        mContactButton = findViewById(R.id.contact);
        mLoveButton = findViewById(R.id.love);
        mSettingButton = findViewById(R.id.setting);
        mMessageButton.setOnClickListener(this);
        mContactButton.setOnClickListener(this);
        mLoveButton.setOnClickListener(this);
        mSettingButton.setOnClickListener(this);
        mNewCount = (TextView) findViewById(R.id.new_count);
    }

    private void prepareIntent() {
        mMessageIntent = new Intent(this, Messages.class);
        mContactIntent = new Intent(this, Contact.class);
        mLoveIntent = new Intent(this, Love.class);
        mSettingIntent = new Intent(this, Setting.class);
        mSwitchUserIntent = new Intent(this, SwitchUserOrStatus.class);
    }

    private void setupIntent() {
        mTabHost = getTabHost();
        mTabHost.addTab(buildTabSpec(TAB_TAG_MESSAG, R.string.message,
                R.drawable.tab_message, mMessageIntent));
        mTabHost.addTab(buildTabSpec(TAB_TAG_CONTACT,
                R.string.contact, R.drawable.tab_love, mContactIntent));
        mTabHost.addTab(buildTabSpec(TAB_TAG_LOVE, R.string.love,
                R.drawable.tab_love, mLoveIntent));
        mTabHost.addTab(buildTabSpec(TAB_TAG_SETTING,
                R.string.setting, R.drawable.tab_setting, mSettingIntent));
        mTabHost.addTab(buildTabSpec(TAB_TAG_SWITCH_USER,
                R.string.setting, R.drawable.tab_setting, mSwitchUserIntent));
    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
            final Intent content) {
        return mTabHost
                .newTabSpec(tag)
                .setIndicator(getString(resLabel),
                        getResources().getDrawable(resIcon))
                .setContent(content);
    }

    public void setCurrentTabByTag(String tab) {
        mTabHost.setCurrentTabByTag(tab);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (mCurTabId == viewId) {
            return;
        }
        mMessageButton.setBackgroundResource(R.drawable.home_bottom);
        mContactButton.setBackgroundResource(R.drawable.home_bottom);
        mLoveButton.setBackgroundResource(R.drawable.home_bottom);
        mSettingButton.setBackgroundResource(R.drawable.home_bottom);
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        TextView tv4 = (TextView) findViewById(R.id.textView4);
        tv1.setTextColor(Color.WHITE);
        tv2.setTextColor(Color.WHITE);
        tv3.setTextColor(Color.WHITE);
        tv4.setTextColor(Color.WHITE);
        boolean anim = mTabHost.getCurrentTabTag().equals(TAB_TAG_SWITCH_USER);
        if (anim) {
            mTabHost.getCurrentView().startAnimation(mLeftOut);
        }

        switch (viewId) {
            case R.id.message:
                setCurrentTabByTag(TAB_TAG_MESSAG);
                mMessageButton.setBackgroundResource(R.drawable.home_bottom_select);
                tv1.setTextColor(getResources().getColor(R.color.tab_text_color));
                break;
            case R.id.contact:
                setCurrentTabByTag(TAB_TAG_CONTACT);
                mContactButton.setBackgroundResource(R.drawable.home_bottom_select);
                tv2.setTextColor(getResources().getColor(R.color.tab_text_color));
                break;
            case R.id.love:
                setCurrentTabByTag(TAB_TAG_LOVE);
                mLoveButton.setBackgroundResource(R.drawable.home_bottom_select);
                tv3.setTextColor(getResources().getColor(R.color.tab_text_color));
                break;
            case R.id.setting:
                setCurrentTabByTag(TAB_TAG_SETTING);
                mSettingButton.setBackgroundResource(R.drawable.home_bottom_select);
                tv4.setTextColor(getResources().getColor(R.color.tab_text_color));
                break;
        }
        if (anim) {
            mTabHost.getCurrentView().startAnimation(mLeftIn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(HANDLER_MEG_NEW_COUNT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            moveTaskToBack(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mService != null) {
            Intent i = new Intent(BackStageService.CHAT_ACTION_REMOVE_NOTIFY);
            i.putExtra("id", 1);
            sendBroadcast(i);
        }
    }

    private void isLogin() {
        if (!getIsLogin()) {
            Intent intent = new Intent();
            intent.setClass(this, Login.class);
            startActivity(intent);
            finish();
            return;
        } else {
            if (mService.getMySelf() == null) {
                User u = getRemeberUser();
                mService.login(this, u.getId(), u.getPassword());
            }

        }
    }

    public void setLogin(boolean isLogin) {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_login", isLogin);
        editor.commit();
    }

    public boolean getIsLogin() {
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        return sp.getBoolean("is_login", false);
    }

    private User getRemeberUser() {
        User u = new User();
        SharedPreferences sp = getSharedPreferences("mychat", MODE_PRIVATE);
        u.setId(sp.getString("userId", ""));
        u.setPassword(sp.getString("userPass", ""));
        return u;
    }

}
