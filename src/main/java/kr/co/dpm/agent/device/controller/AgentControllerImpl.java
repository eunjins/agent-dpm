package kr.co.dpm.agent.device.controller;

import kr.co.dpm.agent.device.service.AgentService;
import kr.co.dpm.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class AgentControllerImpl {
    @Autowired
    AgentService agentService;

    @GetMapping("/")
    public String test() {
        try {
            agentService.sendId();
            agentService.sendDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "test";
    }

    public Map<String, String> receiveStatus(Device device) {
        return null;
    }
}
