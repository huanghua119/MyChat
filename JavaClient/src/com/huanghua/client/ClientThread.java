
package com.huanghua.client;

import com.huanghua.view.MainFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
    private Socket mSocket;
    private DataInputStream mDis;
    private DataOutputStream mDos;
    private MainFrame mFrame = null;

    public ClientThread(MainFrame frame) {
        mFrame = frame;
    }

    public ClientThread() {

    }

    public static void main(String[] args) {
        ClientThread thread = new ClientThread();
        thread.start();
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket("192.168.1.94", 12345);
            mDis = new DataInputStream(mSocket.getInputStream());
            mDos = new DataOutputStream(mSocket.getOutputStream());
            //ScannerThread mScannerThread = new ScannerThread(mDos);
            //mScannerThread.start();
            while (true) {
                String msg = mDis.readUTF();
                if (mFrame != null) {
                    mFrame.setMessage("it:" + msg);
                } else {
                    System.out.println("it: " + msg);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            if (mDos != null) {
                mFrame.setMessage("me:" + msg);
                mDos.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
