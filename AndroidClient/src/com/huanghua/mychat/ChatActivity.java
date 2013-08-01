
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huanghua.mychat.service.ChatService;
import com.huanghua.mychat.service.MessageService;
import com.huanghua.mychat.util.Util;
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
    private PopupWindow mAlertMessage = null;
    private TextView mNewMessage = null;
    private int mStatusHeight = 0;
    private int mWindowWidth = 0;

    public static final int HANDLER_MEG_REFRESHLIST = 1;
    public static final int HANDLER_MEG_FINISH = 2;

    private Runnable mDismissWindow = new Runnable() {
        @Override
        public void run() {
            if (mAlertMessage != null && mAlertMessage.isShowing()) {
                mAlertMessage.dismiss();
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case HANDLER_MEG_REFRESHLIST:
                    Bundle data =  msg.getData();
                    User u = mService.getUserById(data.getString("user_id"));
                    if (u == null || u.equals(mCurrentUser)) {
                        refreshList();
                        mAdapter.notifyDataSetInvalidated();
                        mChatList.setSelection(mAllMessage.size());
                    } else {
                        ArrayList<NewMessage> newMessage = MessageService.getMessageByUser(u);
                        Util.ChatLog("newMessage:" + newMessage.size());
                        if (newMessage != null && newMessage.size() > 0) {
                            getWindowData();
                            if (mAlertMessage == null) {
                                View view = mInFlater.inflate(R.layout.popup_message, null);
                                mNewMessage = (TextView) view.findViewById(R.id.new_message);
                                mNewMessage.setOnClickListener(ChatActivity.this);
                                mAlertMessage = new PopupWindow(view, mWindowWidth, 40);
                            }
                            mNewMessage.setTag(u);
                            mNewMessage.setText(u.getName() + " : "
                                    + newMessage.get(newMessage.size() - 1).getContext());
                            mAlertMessage.update();
                            mAlertMessage.showAtLocation(mChatList, Gravity.TOP, 0,
                                    mChatList.getTop()
                                            + mStatusHeight);
                            mHandler.removeCallbacks(mDismissWindow);
                            mHandler.postDelayed(mDismissWindow, 3000);
                        }
                        mBack.setText(getString(R.string.message)
                                + MessageService.getAllNewMessage(mCurrentUser));
                    }

                    break;
                case HANDLER_MEG_FINISH:
                    finish();
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
            message.setNew(false);
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

        @Override
        public boolean isEnabled(int position) {
            return false;
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
        mChatList.setSelection(mAllMessage.size());
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
                refreshList();
                mAdapter.notifyDataSetInvalidated();
                mChatList.setSelection(mAllMessage.size());
                mContext.setText("");
            }
        } else if (v == mNewMessage) {
            mHandler.removeCallbacks(mDismissWindow);
            User u = (User) v.getTag();
            if (mAlertMessage != null && mAlertMessage.isShowing()) {
                mAlertMessage.dismiss();
            }
            mCurrentUser = u;
            mUserName.setText(u.getName());
            MessageService.setMessageReadByUser(mCurrentUser);
            mBack.setText(getString(R.string.message)
                    + MessageService.getAllNewMessage(mCurrentUser));
            Message m = new Message();
            Bundle data = new Bundle();
            data.putString("user_id", u.getId());
            m.setData(data);
            m.what = ChatActivity.HANDLER_MEG_REFRESHLIST;
            mHandler.sendMessage(m);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBack.setText(getString(R.string.message) + MessageService.getAllNewMessage(mCurrentUser));
        if (mChatList != null) {
            mChatList.setSelection(mAllMessage.size());
        }
    }

    @Override
    public void onBackPressed() {
        mService.setChatHandler(null);
        super.onBackPressed();
    }

    private void getWindowData() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusHeight = frame.top;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWindowWidth = dm.widthPixels;
    }
}
