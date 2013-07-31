
package com.huanghua.mychat.util;

import android.content.res.Resources;

import com.huanghua.mychat.R;

public class Util {

    public static String getStatus(Resources r, int status) {
        String result = r.getString(R.string.status_offline);
        switch (status) {
            case 1:
                result = r.getString(R.string.status_online);
                break;
            case 2:
                result = r.getString(R.string.status_leave);
                break;
            case 3:
                result = r.getString(R.string.status_stedlth);
                break;
            case 4:
                result = r.getString(R.string.status_offline);
                break;
        }
        return "[" + result + "]";
    }
}
