package kr.co.dpm.agent.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AgentController {
    @Autowired
    AgentService agentService;

    @GetMapping("/test")          //스크립트 수신 테스트
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView("test");
        return modelAndView;
    }

    @PostMapping("/script/distribute")
    public Map<String, String> receiveScript(@RequestParam("scriptFile") MultipartFile multipartFile
            , HttpServletRequest request
            , @RequestParam String encryptId) {
        Map<String, String> status = new HashMap<String, String>();

        try {
            agentService.executeScript(multipartFile, request, encryptId);

            status.put("code", "200");
            status.put("message", "정상 요청");

        } catch (FileNotFoundException e) {
            status.put("code", "400");
            status.put("message", "잘못된 요청");
            e.printStackTrace();

        } catch (Exception e) {
            status.put("code", "500");
            status.put("message", "내부 서버 오류");
            e.printStackTrace();

        }

        return status;
    }
}
