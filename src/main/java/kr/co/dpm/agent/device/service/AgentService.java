package kr.co.dpm.agent.device.service;

import kr.co.dpm.model.Device;
import kr.co.dpm.model.Measure;

import java.io.File;

public interface AgentService {
    public void sendId() throws Exception;
    public void sendDevice() throws Exception;
    public void receiveAgentStatus(Device device);
    public File receiveScript();
    public void executeScript();
    public void sendMeasure(Measure measure);
}
