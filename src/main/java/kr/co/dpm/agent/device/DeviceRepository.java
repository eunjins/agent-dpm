package kr.co.dpm.agent.device;

public interface DeviceRepository {
    public boolean requestDevice(Device device) throws Exception;
}
