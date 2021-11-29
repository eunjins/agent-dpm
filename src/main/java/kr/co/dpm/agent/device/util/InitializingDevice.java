package kr.co.dpm.agent.device.util;

import kr.co.dpm.agent.device.AgentService;
import kr.co.dpm.agent.device.Device;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitializingDevice implements InitializingBean {
    @Autowired
    AgentService agentService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Device device = agentService.executeCommand();
        agentService.sendDevice(device);
    }
}
