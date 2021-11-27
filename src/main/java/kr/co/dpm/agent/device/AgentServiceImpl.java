package kr.co.dpm.agent.device;


import kr.co.dpm.agent.device.util.Cryptogram;
import kr.co.dpm.agent.device.util.DeviceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Service
public class AgentServiceImpl implements AgentService, InitializingBean, Runnable {
    @Value("decryption")
    private String decryption;      //복호화 여부
    private static final Logger logger = LogManager.getLogger(AgentService.class);
    private File file;

    @Autowired
    private DeviceUtil deviceUtil;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private MeasureRepository measureRepository;

    @Override
    public void afterPropertiesSet() {     //서버가 실행시 디바이스의 정보를 관리 시스템으로 송신
        sendDevice();
    }

    @Override
    public void run() {               //스크립트를 실행하고 측정 결과를 송신한다.
        String fileName = file.getName();
        String fileDirectory = file.getPath().substring(0, file.getPath().length() - fileName.length() - 1);
        String command = "java -cp " + fileDirectory + " " + fileName.substring(0, fileName.length() - 6);          //실행하기 위한 커맨드 생성

        logger.debug("-----> 커맨드 : " + command);

        Measure measure = new Measure();
        measure.setDeviceId(deviceUtil.getDevice().getId());

        try {
            long beforeTime = System.currentTimeMillis();
            String result = null;
            synchronized (this) {
                result = deviceUtil.executeCommand(command);         //스크립트 실행...
            }

            logger.debug("-----> 스크립트 실행 결과 : " + result);
            long afterTime = System.currentTimeMillis();

            long secDiffTime = (afterTime - beforeTime);                //실행 시간 측정
            measure.setExecTime(Long.toString(secDiffTime));
            measure.setStatus('Y');             //성공하면 상태를 'Y'로 지정

        } catch (Exception e) {
            e.printStackTrace();

            measure.setExecTime("0");
            measure.setStatus('N');             //실행 실패 시 상태를 'N', 실행시간을 0으로 지정한다.
        } finally {
            logger.debug("-----> 측정 결과 정보 : " + measure);

            sendMeasure(measure);

            file.delete();
        }
    }

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

        for (int i = 0; i < 10; i++) {                //전송 실패 시 10번 까지 재전송 한다.
            try {
                if (deviceRepository.request(device)) {
                    logger.debug("------>  디바이스 정보 송신 성공!!");
                    break;

                } else {
                    logger.debug("------>  디바이스 정보 송신 실패");

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("------>  디바이스 정보 송신 실패");
            }
        }

    }

    @Override
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception {
        String deviceId = deviceUtil.getDevice().getId();

        if ("true".equals(decryption)) {
            try {
                Cryptogram cryptogram = new Cryptogram(deviceId);
                String decryptionId = cryptogram.decrypt(id);          //복호화

                if (!deviceId.equals(decryptionId)) {
                    throw new FileNotFoundException();              //복호화한 아이디가 일치하지 않을 경우 400에러 응답
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            }
        }

        String path = request.getSession().getServletContext().getRealPath("/") + "script";

        File directory = new File(path);            //디렉토리 설정

        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        try {
            deviceUtil.executeCommand("sudo chmod -R 777 " + path + File.separator);
        } catch (Exception e) {

        }

        File file = new File(path + File.separator + multipartFile.getOriginalFilename());

        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true, false);

        logger.debug("--------> 파일 권환" + file.canWrite() + file.canExecute());

        multipartFile.transferTo(file);             //파일을 로컬에 수신

        return file;
    }

    @Override
    public void executeScript(MultipartFile multipartFile, HttpServletRequest request, String id) throws Exception {
        file = receiveScript(multipartFile, request, id);

        Thread thread = new Thread(this);      //스크립트를 실행하고 측정 결과를 송신하는 스레드

        thread.start();
    }

    @Override
    public void sendMeasure(Measure measure) {
        try {
            if (measureRepository.request(measure)) {
                logger.debug("------>  측정 결과 정보 송신 성공!!");

            } else {
                logger.debug("------>  측정 결과 정보 송신 실패");
            }

        } catch (Exception e) {
            e.printStackTrace();

            logger.debug("------>  측정 결과 정보 송신 실패");
        }
    }
}
