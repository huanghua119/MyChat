
package com.huanghua.mychat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghua.mychat.service.ChatService;

public class Contact extends Activity implements View.OnClickListener {

    private Toast mToast;
    private LayoutInflater mInFlater;
    private ChatService mService;
    private ExpandableListView mContactList;

    private String[] mGroup = null;
    private String[][] mChild = null;
    private BaseExpandableListAdapter ContactListAdapter = new BaseExpandableListAdapter() {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChild[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            View child = mInFlater.inflate(R.layout.contact_child_view, null);
            TextView name = (TextView) child.findViewById(R.id.userName);
            name.setText(mChild[groupPosition][childPosition]);
            return child;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChild[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroup[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return mGroup.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            View group = mInFlater.inflate(R.layout.contact_group_view, null);
            TextView groupName = (TextView) group.findViewById(R.id.group_name);
            groupName.setText(mGroup[groupPosition]);
            ImageView flag = (ImageView) group.findViewById(R.id.open_flag);
            if (groupPosition % 2 == 0) {
                flag.setImageResource(isExpanded ? R.drawable.group_flag_down_gree
                        : R.drawable.group_flag_gree);
            } else {
                flag.setImageResource(isExpanded ? R.drawable.group_flag_down_red
                        : R.drawable.group_flag_red);
            }
            TextView userCount = (TextView) group.findViewById(R.id.user_count);
            userCount.setText("0/0");
            return group;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_contact);
        mInFlater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mToast = new Toast(this);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mInFlater.inflate(R.layout.toast_view, null));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mService = ChatService.getInstance();
        mContactList = (ExpandableListView) findViewById(R.id.contactList);
        View serchView = mInFlater.inflate(R.layout.search_view, null);
        mGroup = new String[] {
                getString(R.string.myfriend), getString(R.string.blacklist)
        };
        mChild = new String[mGroup.length][];
        mChild[0] = new String[] {
                "one", "two"
        };
        mChild[1] = new String[] {};
        mContactList.addHeaderView(serchView);
        mContactList.setAdapter(ContactListAdapter);
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

}
