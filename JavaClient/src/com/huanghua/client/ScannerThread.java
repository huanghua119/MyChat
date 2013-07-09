
package com.huanghua.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ScannerThread extends Thread {

    private DataOutputStream mDos;
    private boolean mFlag = true;

    public ScannerThread(DataOutputStream dos) {
        this.mDos = dos;
        mFlag = true;
    }

    @Override
    public void run() {
        Scanner mScanner = new Scanner(new InputStreamReader(System.in));
        while (mFlag) {
            String scanner = mScanner.nextLine();
            try {
                mDos.writeUTF(scanner);
            } catch (IOException e) {
                mFlag = false;
                if (mDos != null) {
                    try {
                        mDos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public void close() {
        mFlag = false;
        if (mDos != null) {
            try {
                mDos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
