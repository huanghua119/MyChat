package com.huanghua.mychat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.huanghua.mychat.CustomApplcation;
import com.huanghua.mychat.MyMessageReceiver;
import com.huanghua.mychat.R;
import com.huanghua.mychat.fragment.ContactFragment;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

/**
 * 登陆
 */
public class MainActivity extends ActivityBase implements EventListener{

    private Button[] mTabs;
    private ContactFragment contactFragment;
    private ContactFragment recentFragment;
    private ContactFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    
    ImageView iv_recent_tips,iv_contact_tips;//消息提示
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTab();
    }

    private void initView(){
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.btn_message);
        mTabs[1] = (Button) findViewById(R.id.btn_contract);
        mTabs[2] = (Button) findViewById(R.id.btn_set);
        iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
        iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);
        //把第一个tab设为选中状态
        mTabs[0].setSelected(true);
    }
    
    private void initTab(){
        contactFragment = new ContactFragment();
        recentFragment = new ContactFragment();
        settingFragment = new ContactFragment();
        fragments = new Fragment[] {recentFragment, contactFragment, settingFragment };
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment).
            add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment).commit();
    }
    
    /**
     * button点击事件
     * @param view
     */
    public void onTabSelect(View view) {
        switch (view.getId()) {
        case R.id.btn_message:
            index = 0;
            break;
        case R.id.btn_contract:
            index = 1;
            break;
        case R.id.btn_set:
            index = 2;
            break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        //把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //小圆点提示
        if(BmobDB.create(this).hasUnReadMsg()){
            iv_recent_tips.setVisibility(View.VISIBLE);
        }else{
            iv_recent_tips.setVisibility(View.GONE);
        }
        if(BmobDB.create(this).hasNewInvite()){
            iv_contact_tips.setVisibility(View.VISIBLE);
        }else{
            iv_contact_tips.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum=0;
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }
    
    @Override
    public void onMessage(BmobMsg message) {
        // 声音提示
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);
        //保存接收到的消息-并发送已读回执给对方
        BmobChatManager.getInstance(this).saveReceiveMessage(true,message);
        if(currentTabIndex==0){
            //当前页面如果为会话页面，刷新此页面
            if(recentFragment != null){
                recentFragment.refresh();
            }
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if(isNetConnected){
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        // 声音提示
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        iv_contact_tips.setVisibility(View.VISIBLE);
        if(currentTabIndex==1){
            if(contactFragment != null){
                contactFragment.refresh();
            }
        }else{
            //同时提醒通知
            String tickerText = message.getFromname()+ getString(R.string.request_add_friend);
            BmobNotifyManager.getInstance(this).showNotify(R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);
        }
    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }
    
    @Override
    public void onReaded(String conversionId, String msgTime) {
    }
    
    
    private static long firstTime;
    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            ShowToastOld(R.string.pass_exit);
        }
        firstTime = System.currentTimeMillis();
    }

    
}
