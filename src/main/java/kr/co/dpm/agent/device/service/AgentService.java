package kr.co.dpm.agent.device.service;

import kr.co.dpm.model.Device;
import kr.co.dpm.model.Measure;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public interface AgentService {
    public Device executeCommand();
    public void sendId() throws Exception;
    public String createPassword(String id);
    public void sendDevice() throws Exception;
    public File decryption(File file) throws Exception;
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception;
    public void executeScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception;
    public void sendMeasure(Measure measure);
}
