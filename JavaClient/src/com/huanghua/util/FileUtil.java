
package com.huanghua.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> getUserList() {
        File file = new File(Configuration.USER_LIST);
        List<String> result = new ArrayList<String>();

        try {
            RandomAccessFile in = new RandomAccessFile(file, "rw");
            long fileLength = in.length();
            long readPosition = 0;
            while (readPosition < fileLength) {
                String id = in.readUTF();
                String pass = in.readUTF();
                String pass2 = "";
                for (int i = 0; i < pass.length(); i++) {
                    char c = pass.charAt(i);
                    int c2 = (int) c - 2;
                    char c3 = (char) c2;
                    pass2 += c3;
                }
                boolean remeber = in.readBoolean();
                boolean auto = in.readBoolean();
                readPosition = in.getFilePointer();
                result.add(id + "::" + pass2 + "::" + remeber + "::" + auto);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveFile(String msg, String fileName) {
        try {
            File file = new File(fileName);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            String[] temp = msg.split("::");
            raf.seek(raf.length());
            raf.writeUTF(temp[0]);
            String pass = temp[1];
            String pass2 = "";
            for (int i = 0; i < pass.length(); i++) {
                char c = pass.charAt(i);
                int c2 = (int) c + 2;
                char c3 = (char) c2;
                pass2 += c3;
            }
            raf.writeUTF(pass2);
            raf.writeBoolean(Boolean.parseBoolean(temp[2]));
            raf.writeBoolean(Boolean.parseBoolean(temp[3]));
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
