package ru.willdes.nginxplus;

public class UpstreamName {
    private static UpstreamName upstreamName;
    private int idupstr;
    private String upstrname;

    public static UpstreamName getUpstreamName() {
        if (upstreamName == null) {
            upstreamName = new UpstreamName();
        }
        return upstreamName;
    }

    public int getIdupstr() {
        return idupstr;
    }

    public void setIdupstr(int idupstr) {
        this.idupstr = idupstr;
    }

    public String getUpstrname() {
        return upstrname;
    }

    public void setUpstrname(String upstrname) {
        this.upstrname = upstrname;
    }
}
