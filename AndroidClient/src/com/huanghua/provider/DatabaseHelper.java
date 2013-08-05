
package com.huanghua.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "mychat.db";
    public static int version = 1;
    private Context mContext = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
        mContext = context;
    }

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + MyChatTable.MessageColumns.TABLE_NAME + "("
                + MyChatTable.MessageColumns.userId + " varchar(100) not null,"
                + MyChatTable.MessageColumns.send_userId + " varchar(100) not null,"
                + MyChatTable.MessageColumns.from_userId + " varchat(100) not null,"
                + MyChatTable.MessageColumns.to_userId + " varchat(100) not null,"
                + MyChatTable.MessageColumns.context + " varchat(1000) not null,"
                + MyChatTable.MessageColumns.messageDate + " datetime,"
                + MyChatTable.MessageColumns.isNew + " int(10));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists notes");
        onCreate(db);

    }

}
