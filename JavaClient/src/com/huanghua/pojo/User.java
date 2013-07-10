
package com.huanghua.pojo;

public class User {

    private String ip;
    private String id;
    private int port;
    private String name;

    public User() {

    }

    public User(String ip, String id, int port, String name) {
        this.ip = ip;
        this.id = id;
        this.port = port;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
