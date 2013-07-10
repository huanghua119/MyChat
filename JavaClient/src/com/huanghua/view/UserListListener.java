
package com.huanghua.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class UserListListener implements MouseListener, MouseMotionListener {

    private MainFrame mFrame;

    public UserListListener(MainFrame frame) {
        this.mFrame = frame;
    }

    // 鼠标按键在组件上按下并拖动时调用
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    // 鼠标光标移动到组件上但无按键按下时调用
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // 鼠标按键在组件上单击（按下并释放）时调用
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            int index = mFrame.getUserList().locationToIndex(e.getPoint());
            int clickNum = e.getClickCount();
            // 双击鼠标
            if (clickNum == 2) {
                mFrame.startChat(index);
            }

        }
    }

    // 鼠标进入到组件上时调用
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    // 鼠标离开组件时调用。
    @Override
    public void mouseExited(MouseEvent e) {

    }

    // 鼠标按键在组件上按下时调用。
    @Override
    public void mousePressed(MouseEvent e) {

    }

    // 鼠标按钮在组件上释放时调用
    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
