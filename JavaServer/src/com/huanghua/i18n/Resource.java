package com.huanghua.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;

public class Resource {
    private static final String HEAD_FILE = "string";
    private static final String LAST_FILE = ".properties";
    public static final String Language_en_US = "en_US";
    public static final String Language_zh_CN = "zh_CN";
    private static String slanguage = Language_en_US;

    public Resource() {

    }

    public static String getString(String disStr) {
        String ret = "";
        try {
            Locale locale = Locale.getDefault();
            System.out.println("locale:" + locale);
            String baseName = new StringBuffer().append(HEAD_FILE).append("_")
                    .append(locale.toString()).append(LAST_FILE).toString();

            String fileName = new StringBuffer().append(baseName).toString();
            URL url = Resource.class.getClassLoader().getResource(fileName);
            File file = new File(url.toURI());

            InputStream is = new FileInputStream(file);
            PropertyResourceBundle pr = new PropertyResourceBundle(is);
            ret = pr.getString(disStr);
            if (locale.equals(Locale.CHINA)) {
                ret = new String(ret.getBytes("ISO-8859-1"), "GB2312");
            }
            is.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return disStr;
        }
    }

    public static String getStringForSet(String disStr) {
        String ret = "";
        try {
            String baseName = new StringBuffer().append(HEAD_FILE).append("_")
                    .append(slanguage).append(LAST_FILE).toString();

            String fileName = new StringBuffer().append(baseName).toString();
            URL url = Resource.class.getClassLoader().getResource(fileName);
            File file = new File(url.toURI());

            InputStream is = new FileInputStream(file);
            PropertyResourceBundle pr = new PropertyResourceBundle(is);
            ret = pr.getString(disStr);
            is.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return disStr;
        }
    }

    public static void setLanguage(String language) {
        slanguage = language;
    }
}
