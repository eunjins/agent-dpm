package kr.co.dpm.agent.device;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public interface AgentService {
    public Device executeCommand();
    public void sendDevice();
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception;
    public void executeScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception;
    public void sendMeasure(Measure measure)throws Exception;
}
