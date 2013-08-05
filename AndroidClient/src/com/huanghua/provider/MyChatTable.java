
package com.huanghua.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class MyChatTable {

    public static class MessageColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.huanghua/messagetable");

        public static final String CONTENT_TYPE = "vnd.Android.cursor.dir/messagetable";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/messagetable";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String TABLE_NAME = "messagetable";

        public static final String userId = "userId";

        public static final String send_userId = "send_userId";

        public static final String from_userId = "from_userId";

        public static final String to_userId = "to_userId";

        public static final String context = "context";

        public static final String messageDate = "messageDate";

        public static final String isNew = "isNew";
    }

}
