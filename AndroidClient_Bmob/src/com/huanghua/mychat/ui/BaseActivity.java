
package com.huanghua.mychat.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.huanghua.mychat.CustomApplcation;
import com.huanghua.mychat.R;
import com.huanghua.mychat.bean.User;
import com.huanghua.mychat.util.CollectionUtils;
import com.huanghua.mychat.view.dialog.DialogTips;

import java.util.List;

public class BaseActivity extends FragmentActivity {
    BmobUserManager userManager;
    BmobChatManager manager;

    CustomApplcation mApplication;

    protected int mScreenWidth;
    protected int mScreenHeight;
    public LayoutInflater mInFlater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        mApplication = CustomApplcation.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    Toast mToast;

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = new Toast(BaseActivity.this);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                    }
                    View toast = mToast.getView();
                    TextView m = (TextView) toast.findViewById(R.id.toast_msg);
                    m.setText(text);
                    mToast.show();
                }
            });

        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = new Toast(BaseActivity.this);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                }
                View toast = mToast.getView();
                TextView m = (TextView) toast.findViewById(R.id.toast_msg);
                m.setText(resId);
                mToast.show();
            }
        });
    }

    /** 显示下线的对话框
     * showOfflineDialog
     * @return void
     * @throws
     */
   public void showOfflineDialog(final Context context) {
       DialogTips dialog = new DialogTips(this,getString(R.string.offlineWarn), getString(R.string.again_login));
       // 设置成功事件
       dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialogInterface, int userId) {
               CustomApplcation.getInstance().logout();
               startActivity(new Intent(context, LoginActivity.class));
               finish();
               dialogInterface.dismiss();
           }
       });
       // 显示确认对话框
       dialog.show();
       dialog = null;
   }

    /**
     * 打Log ShowLog
     * 
     * @return void
     * @throws
     */
    public void ShowLog(String msg) {
        BmobLog.i(msg);
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     */
    public void updateUserInfos() {
        // 更新地理位置信息
        updateUserLocation();
        // 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        // 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

            @Override
            public void onError(int arg0, String arg1) {
                if (arg0 == BmobConfig.CODE_COMMON_NONE) {
                    ShowLog(arg1);
                } else {
                    ShowLog("查询好友列表失败：" + arg1);
                }
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // 保存到application中方便比较
                CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(arg0));
            }
        });
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (CustomApplcation.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(CustomApplcation.lastPoint.getLatitude());
            String newLong = String.valueOf(CustomApplcation.lastPoint.getLongitude());
            // ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
            // ShowLog("newLat ="+newLat+",newLong = "+newLong);
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
                final User user = (User) userManager.getCurrentUser(User.class);
                user.setLocation(CustomApplcation.lastPoint);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        CustomApplcation.getInstance().setLatitude(
                                String.valueOf(user.getLocation().getLatitude()));
                        CustomApplcation.getInstance().setLongtitude(
                                String.valueOf(user.getLocation().getLongitude()));
                        // ShowLog("经纬度更新成功");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // ShowLog("经纬度更新 失败:"+msg);
                    }
                });
            } else {
                // ShowLog("用户位置未发生过变化");
            }
        }
    }
}
