package kr.co.dpm.agent.device;

public interface DeviceRepository {
    public boolean request(Device device) throws Exception;
}
