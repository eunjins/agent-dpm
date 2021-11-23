package kr.co.dpm.model;

import java.io.Serializable;

public class Device implements Serializable {
    private String id;
    private String hostName;
    private String ipAddress;

    public Device() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", hostName='" + hostName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}