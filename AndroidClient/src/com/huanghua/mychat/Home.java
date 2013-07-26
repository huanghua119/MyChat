
package com.huanghua.mychat;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.huanghua.mychat.service.ChatService;

@SuppressWarnings("deprecation")
public class Home extends TabActivity implements View.OnClickListener {

    public static String TAB_TAG_MESSAG = "message";
    public static String TAB_TAG_CONTACT = "contact";
    public static String TAB_TAG_LOVE = "love";
    public static String TAB_TAG_SETTING = "setting";
    private ChatService mService;

    private TabHost mTabHost;
    private Intent mMessageIntent, mContactIntent, mLoveIntent, mSettingIntent;
    private View mMessageButton, mContactButton, mLoveButton, mSettingButton;
    private int mCurTabId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        init();
        prepareIntent();
        setupIntent();
        mMessageButton.performClick();
    }

    private void init() {
        mService = ChatService.getInstance();
        mService.setHome(this);
        mMessageButton = findViewById(R.id.message);
        mContactButton = findViewById(R.id.contact);
        mLoveButton = findViewById(R.id.love);
        mSettingButton = findViewById(R.id.setting);
        mMessageButton.setOnClickListener(this);
        mContactButton.setOnClickListener(this);
        mLoveButton.setOnClickListener(this);
        mSettingButton.setOnClickListener(this);
    }

    private void prepareIntent() {
        mMessageIntent = new Intent(this, Messages.class);
        mContactIntent = new Intent(this, Contact.class);
        mLoveIntent = new Intent(this, Love.class);
        mSettingIntent = new Intent(this, Setting.class);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
