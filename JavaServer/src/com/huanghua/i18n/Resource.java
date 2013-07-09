
package com.huanghua.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;

public class Resource {
    private static final String HEAD_FILE = "string"; // 属性文件名的前缀
    private static final String LAST_FILE = ".properties"; // 属性文件名的后缀
    public static final String Language_en_US = "en_US";
    public static final String Language_zh_CN = "zh_CN";
    private static String slanguage = Language_en_US;

    public Resource() {

    }

    /* 以下是根据传入的属性文件中的"键",而得到与区域与语言设置相对应的"值" */
    public static String getString(String disStr) {
        String ret = "";
        try {
            Locale locale = Locale.getDefault(); // 获取系统的区域与语言默认设置
            System.out.println("locale:" + locale);
            String baseName = new StringBuffer()
                    .append(HEAD_FILE).append("_").append(locale.toString())
                    .append(LAST_FILE).toString(); // 根据local属性,前缀以及后缀生成文件名

            String fileName = new StringBuffer().append(baseName)
                    .toString(); // 获取文件的完整路径
            URL url = Resource.class.getClassLoader().getResource(fileName);
            File file = new File(url.toURI());

            InputStream is = new FileInputStream(file); // 生成文件输入流
            PropertyResourceBundle pr = new PropertyResourceBundle(is); // 根据输入流构造PropertyResourceBundle的实例
            ret = pr.getString(disStr);
            if (locale.equals(Locale.CHINA)) {
                ret = new String(ret.getBytes("ISO-8859-1"), "GB2312");
            } // 如果是要显示中文,则要进行内码的转换
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
            String baseName = new StringBuffer()
                    .append(HEAD_FILE).append("_").append(slanguage)
                    .append(LAST_FILE).toString();

            String fileName = new StringBuffer().append(baseName)
                    .toString();
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
