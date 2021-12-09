package kr.co.dpm.agent.device;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface AgentService {
    public Device executeCommand() throws Exception;

    public void sendDevice(Device device) throws Exception;

    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception;

    public boolean decryption(String word) throws Exception;

    public Measure executeScript(File file) throws Exception;

    public void sendMeasure(Measure measure) throws Exception;
}
