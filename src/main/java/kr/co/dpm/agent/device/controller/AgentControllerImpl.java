package kr.co.dpm.agent.device.controller;

import kr.co.dpm.agent.device.service.AgentService;
import kr.co.dpm.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AgentControllerImpl {
    @Autowired
    AgentService agentService;

    @GetMapping("/test")
    public ModelAndView test() {
        ModelAndView mav = new ModelAndView("test");
        return mav;
    }

    @PostMapping("/script/distribute")
    public Map<String, String> receiveScript(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        Map<String, String> status = new HashMap<String, String>();

        try {
            agentService.executeScript(multipartFile, request);

            status.put("code", "200");
            status.put("message", "요청이 성공적입니다.");
        } catch (Exception e) {
            status.put("code", "500");
            status.put("message", "서버에 오류가 발생하여 요청을 수행할 수 없습니다.");
            e.printStackTrace();
        }

        return status;
    }
}
