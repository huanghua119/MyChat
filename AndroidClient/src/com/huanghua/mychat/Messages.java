
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Messages extends Activity implements View.OnClickListener, OnItemClickListener {

    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;
    private ListView mMessageList;
    private User[] mMessageUser;
    private HashMap<User, ArrayList<NewMessage>> mAllMessage;

    public static final int HANDLER_MEG_REFRESHLIST = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_REFRESHLIST:
                    mAdapter.notifyDataSetInvalidated();
                    refreshList();
                    break;
            }
        }
    };

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = mInFlater.inflate(R.layout.message_item, null);
            }
            TextView name = (TextView) v.findViewById(R.id.userName);
            TextView context = (TextView) v.findViewById(R.id.newMessage);
            TextView date = (TextView) v.findViewById(R.id.messageDate);
            TextView count = (TextView) v.findViewById(R.id.messageCount);
            User u = mMessageUser[position];
            ArrayList<NewMessage> message = mAllMessage.get(u);
            name.setText(u.getName());
            if (message != null && message.size() > 0) {
                int messsageCount = message.size();
                context.setText(message.get(messsageCount - 1).getContext());
                count.setText(message.size() + "");
                SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
                String time = sdf.format(message.get(messsageCount - 1).getMessageDate());
                date.setText(time);
            }
            return v;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return mMessageUser.length;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_message);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mService.setMessagesHandle(mHandler);
        mMessageList = (ListView) findViewById(R.id.messageList);
        refreshList();
        View head = mInFlater.inflate(R.layout.message_headview, null);
        mMessageList.addHeaderView(head);
        mMessageList.setAdapter(mAdapter);
        mMessageList.setOnItemClickListener(this);
    }

    private void refreshList() {
        mAllMessage = mService.getMessageBox();
        mMessageUser = new User[mAllMessage.size()];
        Set<User> mAllUser = mAllMessage.keySet();
        Iterator<User> iterator = mAllUser.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            User u = iterator.next();
            mMessageUser[i] = u;
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 == 0) {
            return;
        }
        int position = arg2 - 1;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", mMessageUser[position].getId());
        startActivity(intent);
    }

}
