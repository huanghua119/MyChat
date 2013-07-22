
package com.huanghua.listener;

import com.huanghua.i18n.Resource;
import com.huanghua.service.ChatService;
import com.huanghua.view.MessageFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class TrayListener implements MouseListener, ActionListener {

    private ChatService mService;

    public TrayListener(ChatService service) {
        this.mService = service;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        List<MessageFrame> box = mService.getMessageBox();
        if (box.size() > 0) {
            MessageFrame mf = box.get(0);
            box.remove(0);
            mf.startChat();
            if (box.size() == 0) {
                mService.stopFlash();
            }
        } else {
            if (e.getClickCount() == 2) {
                mService.showMainFrame();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd != null && cmd.equals(Resource.getString("openFrame"))) {
            mService.showMainFrame();
        } else if (cmd != null && cmd.equals(Resource.getString("exit"))) {
            mService.exit();
        }
    }

}
