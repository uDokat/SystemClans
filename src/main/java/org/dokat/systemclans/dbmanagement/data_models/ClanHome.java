package org.dokat.systemclans.dbmanagement.data_models;

public class ClanHome {

    private double x;
    private double y;
    private double z;
    private String woldName;

    public ClanHome(double x, double y, double z, String woldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.woldName = woldName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getWoldName() {
        return woldName;
    }

    public void setWoldName(String woldName) {
        this.woldName = woldName;
    }
}
