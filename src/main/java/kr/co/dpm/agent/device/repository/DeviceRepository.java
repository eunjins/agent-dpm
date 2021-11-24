package kr.co.dpm.agent.device.repository;

import kr.co.dpm.model.Device;

public interface DeviceRepository {
    public String requestId(Device device) throws Exception;
    public boolean requestDevice(Device device) throws Exception;
}
