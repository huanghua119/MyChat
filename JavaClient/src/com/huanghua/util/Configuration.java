
package com.huanghua.util;

import java.io.File;
import java.util.List;

public class Configuration {

    public static final String SERVER_IP = "huanghua119.xicp.net";

    public static final int SERVER_PORT = 26741;

    public static final float MESSAGE_TEXT_INTEND = 21;

    public static final float MESSAGE_TITLE_INTEND = 2;

    public static boolean sIsSavePass = false;
    public static boolean sIsAutoLogin = false;

    public static final String SAVE_DIRECTORY = "lib/userdata/";

    public static final String USER_LIST = SAVE_DIRECTORY + "userlist";

    public static List<String> sUserList = null;

    static {
        init();
    }

    public static void init() {
        sUserList = FileUtil.getUserList();
    }

    public static void saveUser(String id, String password) {
        for (int i = 0; i < sUserList.size(); i++) {
            String s = sUserList.get(i);
            String userId = s.split("::")[0];
            if (userId.equals(id)) {
                sUserList.remove(i);
                break;
            }
        }
        sUserList.add(id + "::" + password + "::" + sIsSavePass + "::" + sIsAutoLogin);
        File file = new File(USER_LIST);
        file.delete();
        File userData = new File(SAVE_DIRECTORY + "/" + id);
        userData.mkdirs();
        for (String s : sUserList) {
            FileUtil.saveFile(s, USER_LIST);
        }
    }
}
