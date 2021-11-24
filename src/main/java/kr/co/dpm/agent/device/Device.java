package kr.co.dpm.agent.device;

import java.io.Serializable;

public class Device implements Serializable {
    private String deviceId;
    private String hostName;
    private String ipAddress;
    private String jdkVersion;

    public Device() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getJdkVersion() {
        return jdkVersion;
    }

    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", jdkVersion='" + jdkVersion + '\'' +
                '}';
    }
}