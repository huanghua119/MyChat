
package com.huanghua.pojo;

public class User {

    private String ip;
    private String id;
    private String name;
    private String password;

    public User() {
    }

    public User(String name, String pass) {
        this.name = name;
        this.password = pass;
    }

    public User(String ip, String id, String name) {
        this.ip = ip;
        this.id = id;
        this.name = name;
    }

    public User(String ip, String id, String name, String pass) {
        this.ip = ip;
        this.id = id;
        this.name = name;
        this.password = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
