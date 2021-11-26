package kr.co.dpm.agent.device;

import java.io.Serializable;

public class Measure implements Serializable {
    private String deviceId;
    private String execTime;
    private char status;

    public Measure() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String executeTime) {
        this.execTime = executeTime;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Measure{" +
                "deviceId='" + deviceId + '\'' +
                ", execTime='" + execTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
