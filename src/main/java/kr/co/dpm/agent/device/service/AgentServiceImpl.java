package kr.co.dpm.agent.device.service;

import kr.co.dpm.agent.device.repository.DeviceRepository;
import kr.co.dpm.agent.device.util.DeviceUtil;
import kr.co.dpm.model.Device;
import kr.co.dpm.model.Measure;
import okhttp3.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Service
public class AgentServiceImpl implements AgentService {
    @Autowired
    private DeviceUtil deviceUtil;
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public Device executeCommand() {
        Device device = null;

        if ((device = deviceUtil.getDevice()) == null) {
            return deviceUtil.createDevice();
        }

        return device;
    }

    @Override
    public void sendId() {      //TODO 프로그램 목록 보고 변경...
        Device device = executeCommand();

        for (int i = 0; i < 10; i++) {
            try {
                String password = deviceRepository.requestId(device);
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String password = createPassword(device.getDeviceId());
        if (createPassword(device.getDeviceId()).equals(password)) {
            try {
                sendDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendDevice() throws Exception {
        Device device = executeCommand();
        deviceRepository.requestDevice(device);
    }

    @Override
    public File decryption(File file) throws Exception {
        return file;
    }

    @Override
    public String createPassword(String id) {
        StringBuffer password = new StringBuffer();

        for (int i = 0; i < id.length(); i++) {
            int oneLetter = id.charAt(i) + i;

            password.append((char) oneLetter);
        }

        System.out.println(password);

        return password.toString();
    }

    @Override
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception {
        String path = request.getSession().getServletContext().getRealPath("/") + "script";

        File directory = new File(path);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        System.out.println(path + File.separator + "script.class");
        File file = new File(path + File.separator + "script.class");
        multipartFile.transferTo(file);

        file = decryption(file);

        return file;
    }

    @Override
    public void executeScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception {
        File file = receiveScript(multipartFile, request);

        //TODO 측정 결과 송신...
    }

    @Override
    public void sendMeasure(Measure measure) {

    }
}
