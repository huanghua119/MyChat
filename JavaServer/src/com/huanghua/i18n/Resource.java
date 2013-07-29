
package com.huanghua.i18n;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Resource {

    private static final String HEAD_FILE = "string";
    private static final String LAST_FILE = ".xml";
    public static final String Language_en_US = "en_US";
    public static final String Language_zh_CN = "zh_CN";
    private static String slanguage = Language_zh_CN;
    private static Map<String, String> mAllString = new HashMap<String, String>();
    static {
        init();
    }

    public static void init() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            String baseName = new StringBuffer()
                    .append(HEAD_FILE).append("_").append(slanguage)
                    .append(LAST_FILE).toString();

            String fileName = new StringBuffer().append(baseName)
                    .toString();
            File file = new File("lib/" + fileName);
            DocumentBuilder domParser = factory.newDocumentBuilder();
            Document document = domParser.parse(file);
            NodeList nodeList = document.getChildNodes();
            Node node = nodeList.item(0);
            Element elementNode = (Element) node;
            NodeList nodes = elementNode.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) n;
                    String name = element.getAttribute("name");
                    String text = element.getTextContent();
                    mAllString.put(name, text);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String name) {
        String result = name;
        if (mAllString != null && mAllString.size() != 0) {
            result = mAllString.get(name);
        }
        if (result == null || "".equals(result)) {
            result = name;
        }
        return result;
    }

    public static String getString(String... name) {
        String one = getString(name[0]);
        for (int i = 1; i < name.length; i++) {
            String temp = "%" + i + "$s";
            if (one.contains(temp)) {
                one = one.replace(temp, name[i]);
            } else {
                one = one.replace("%s", name[i]);
            }
        }
        return one;
    }
    public static String getStringForColor(String name, String color) {
        String result = name;
        if (mAllString != null && mAllString.size() != 0) {
            result = mAllString.get(name);
        }
        if (result == null || "".equals(result)) {
            result = name;
        }
        return "<html><font color=" + color + ">" + result + "</font></html>";
    }

    public static String getColor(String name, String color) {
        return "<html><font color=" + color + ">" + name + "</font></html>";
    }

    public static void setLanguage(String language) {
        slanguage = language;
    }
}
