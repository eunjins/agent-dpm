package kr.co.dpm.agent.device;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public interface AgentService {
    public Device executeCommand();

    public void sendDevice(Device device);

    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception;

    public boolean decryption(String word) throws Exception;

    public Measure executeScript(File file);

    public void sendMeasure(Measure measure);
}
