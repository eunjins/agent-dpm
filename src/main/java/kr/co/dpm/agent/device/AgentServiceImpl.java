package kr.co.dpm.agent.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

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
    public void sendDevice() {
        Device device = executeCommand();

        for (int i = 0; i < 10; i++) {
            try {
                if (deviceRepository.requestDevice(device)) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception {
        String deviceId = deviceUtil.getDevice().getDeviceId();

        Cryptogram cryptogram = new Cryptogram(deviceId);

        String decryptionId = cryptogram.decrypt(id);

        if (!deviceId.equals(decryptionId)) {
            throw new FileNotFoundException();
        }

        String path = request.getSession().getServletContext().getRealPath("/") + "script";

        File directory = new File(path);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        File file = new File(path + File.separator + "script.class");
        multipartFile.transferTo(file);

        return file;
    }

    @Override
    public void executeScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception {
        File file = receiveScript(multipartFile, request, id);

        //TODO 스크립트 실행..
        //TODO 측정 결과 송신...
    }

    @Override
    public void sendMeasure(Measure measure) {

    }
}
