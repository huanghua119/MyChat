
package com.huanghua.mychat.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanghua.mychat.R;

public class Util {
    public static final int USER_STATUS_ONLINE = 1;
    public static final int USER_STATUS_LEAVE = 2;
    public static final int USER_STATUS_STEDLTH = 3;
    public static final int USER_STATUS_OFFLINE = 4;

    public static String getStatus(Resources r, int status) {
        String result = r.getString(R.string.status_offline);
        switch (status) {
            case USER_STATUS_ONLINE:
                result = r.getString(R.string.status_online);
                break;
            case USER_STATUS_LEAVE:
                result = r.getString(R.string.status_leave);
                break;
            case USER_STATUS_STEDLTH:
                result = r.getString(R.string.status_stedlth);
                break;
            case USER_STATUS_OFFLINE:
                result = r.getString(R.string.status_offline);
                break;
        }
        return "[" + result + "]";
    }

    public static void ChatLog(String msg) {
        Log.i("chat_log", msg);
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);

        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    public static boolean isConnectivity(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}
