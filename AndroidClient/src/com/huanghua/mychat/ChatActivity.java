
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.service.MessageService;
import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatActivity extends Activity implements OnClickListener {

    private LayoutInflater mInFlater;
    private ChatService mService;
    private User mCurrentUser = null;
    private TextView mUserName = null;
    private Button mBack = null;
    private Button mSend = null;
    private EditText mContext = null;
    private ListView mChatList = null;
    private ArrayList<NewMessage> mAllMessage = null;

    public static final int HANDLER_MEG_REFRESHLIST = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_REFRESHLIST:
                    refreshList();
                    mAdapter.notifyDataSetInvalidated();
                    mChatList.setSelection(mAllMessage.size());
                    break;
            }
        }
    };

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = mInFlater.inflate(R.layout.chat_list_item, null);
            View self = v.findViewById(R.id.self_chat);
            View it = v.findViewById(R.id.it_chat);
            NewMessage message = mAllMessage.get(position);
            User u = message.getUser();
            if (u.getId().equals(mService.getMySelf().getId())) {
                it.setVisibility(View.GONE);
                self.setVisibility(View.VISIBLE);
                TextView name = (TextView) self.findViewById(R.id.self_chat_name);
                TextView date = (TextView) self.findViewById(R.id.self_chat_time);
                TextView contextView = (TextView) self.findViewById(R.id.self_chat_context);
                name.setText(message.getUser().getName());
                SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
                String time = sdf.format(message.getMessageDate());
                date.setText(time);
                contextView.setText(message.getContext());
            } else {
                self.setVisibility(View.GONE);
                it.setVisibility(View.VISIBLE);
                TextView name2 = (TextView) it.findViewById(R.id.it_chat_name);
                TextView date2 = (TextView) it.findViewById(R.id.it_chat_time);
                TextView contextView = (TextView) it.findViewById(R.id.it_chat_context);
                name2.setText(message.getUser().getName());
                SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
                String time = sdf.format(message.getMessageDate());
                date2.setText(time);
                contextView.setText(message.getContext());
            }
            return v;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mAllMessage.get(position);
        }

        @Override
        public int getCount() {
            return mAllMessage.size();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String userId = getIntent().getStringExtra("userId");
        mAllMessage = new ArrayList<NewMessage>();
        mService = ChatService.getInstance();
        mCurrentUser = mService.getUserById(userId);
        mService.setChatHandler(mHandler);
        refreshList();
        init();
    }

    private void init() {
        mUserName = (TextView) findViewById(R.id.chat_name);
        mUserName.setText(mCurrentUser.getName());
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSend = (Button) findViewById(R.id.send_context);
        mSend.setOnClickListener(this);
        mContext = (EditText) findViewById(R.id.chat_context);
        mChatList = (ListView) findViewById(R.id.chatList);
        mChatList.setAdapter(mAdapter);
    }

    private void refreshList() {
        mAllMessage = mService.getMessageByUser(mCurrentUser);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            mService.setChatHandler(null);
            finish();
        } else if (v == mSend) {
            String context = mContext.getText().toString();
            if (context != null && !"".equals(context)) {
                mService.sendMessageToUser(mCurrentUser, context);
                MessageService.addMessage(context, mCurrentUser, false, mService.getMySelf());
                mHandler.sendEmptyMessage(HANDLER_MEG_REFRESHLIST);
                mContext.setText("");
            }
        }
    }
}
