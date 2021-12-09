package kr.co.dpm.agent.util;

import kr.co.dpm.agent.device.AgentService;
import kr.co.dpm.agent.device.Device;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitializingDevice implements InitializingBean {
    private Logger logger = LogManager.getLogger(InitializingDevice.class);

    @Autowired
    AgentService agentService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Device device = null;
        try {
            device = agentService.executeCommand();
        } catch (Exception e) {
            logger.error("fail to create Device Information : " + e.getMessage());

            return;
        }

        for (int i = 0; i < 10; i++) {
            try {
                agentService.sendDevice(device);
                break;
            } catch (Exception e) {
                logger.error("Fail to send device information");
                Thread.sleep(1000);
            }
        }
    }
}
