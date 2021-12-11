package kr.co.dpm.agent.device;

import kr.co.dpm.agent.util.DeviceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AgentController {
    private static final Logger logger = LogManager.getLogger(AgentService.class);

    @Autowired
    AgentService agentService;

    @Autowired
    DeviceUtil deviceUtil;

    @PostMapping("/script")
    public Map<String, String> receiveScript(@RequestParam("scriptFile") MultipartFile multipartFile
            , HttpServletRequest request
            , @RequestParam String encryptId) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("                        [RECEIVE SCRIPT]                               ");
        logger.info("                                                                       ");
        logger.info("      Script File :   " + multipartFile.getOriginalFilename()           );
        logger.info("      Encrypt Id  :   " + encryptId                                     );
        logger.info("                                                                       ");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Map<String, String> status = new HashMap<>();
        try {
            if (agentService.decryption(encryptId)) {
                status.put("code", "200");
                status.put("message", "");

                File file = agentService.receiveScript(multipartFile, request);
                deviceUtil.setFile(file);
                Thread thread = new Thread(deviceUtil);
                thread.start();
            } else {
                status.put("code", "400");
                status.put("message", "잘못된 요청");
            }
        } catch (FileNotFoundException e) {
            status.put("code", "400");
            status.put("message", "잘못된 요청");
        } catch (Exception e) {
            e.printStackTrace();
            status.put("code", "500");
            status.put("message", "내부 서버 오류");
        }

        return status;
    }
}
