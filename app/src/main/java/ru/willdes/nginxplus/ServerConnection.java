package ru.willdes.nginxplus;

public class ServerConnection {
    private static ServerConnection instance;
    private int idconn;
    private String conname;
    private String ipaddr;
    private String port;
    private String user;
    private String password;

    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public int getIdconn() {
        return idconn;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setIdconn(int idconn) {
        this.idconn = idconn;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConname() {
        return conname;
    }

    public void setConname(String conname) {
        this.conname = conname;
    }
}
