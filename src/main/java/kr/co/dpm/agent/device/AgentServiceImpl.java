package kr.co.dpm.agent.device;


import kr.co.dpm.agent.device.util.Cryptogram;
import kr.co.dpm.agent.device.util.DeviceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

@Service
public class AgentServiceImpl implements AgentService {
    @Value("${decryption}")
    private String decryption;      //복호화 여부
    private static final Logger logger = LogManager.getLogger(AgentService.class);
    private Device device;
    @Autowired
    private DeviceUtil deviceUtil;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private MeasureRepository measureRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Device executeCommand(){
        device = new Device();
        Properties properties = new Properties();

        try {
            File file = resourceLoader.getResource("classpath:config/device-id.properties").getFile();      //운영체제 별 명령어를 가지고 있는 properties 가져오기
            properties.load(new InputStreamReader(new FileInputStream(file)));

            String osName = System.getProperty("os.name").toLowerCase();                                       //운영체제 이름 가져오기
            if (osName.contains("win")) {
                osName = "win";

            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                osName = "unix";

            } else if (osName.contains("linux")) {
                osName = "linux";

            } else {
                osName = "unix";

            }

            String systemInfoCommand = properties.getProperty(osName + "-command");         //운영체제에 맞는 명령어 찾기
            String productIdCommand = properties.getProperty(osName + "-id");

            String systemInfo = deviceUtil.executeCommand(systemInfoCommand);          //제품 ID를 찾기 위한 명령어 실행
            String[] splitSystemInfo = systemInfo.split("\n");
            for (String line : splitSystemInfo) {
                String[] splitLine = line.split(":");

                if (productIdCommand.equals(splitLine[0].trim())) {
                    device.setId(splitLine[1].trim());                      //제품 ID 지정
                }
            }

            String hostNameInfo = deviceUtil.executeCommand("hostname").trim();        //호스트 이름 지정
            device.setHostName(hostNameInfo);

            String jdkInfo = deviceUtil.executeCommand("java --version");              //JDK-version 지정
            String[] springJdk = jdkInfo.split(" ");
            device.setJdkVersion(springJdk[1]);

            String ipAddress = "";
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaceEnumeration.hasMoreElements()) {                  //IP 주소 지정
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddresses= networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {

                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }

            device.setIpAddress(ipAddress);

            logger.debug("-----------> productIdCommand and information of device are : " + productIdCommand + device);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("-----------> fail to create information of device");
        }

        return device;
    }

    @Override
    public void sendDevice(Device device) {
        for (int i = 0; i < 10; i++) {                //전송 실패 시 10번 까지 재전송 한다.
            try {
                if (deviceRepository.request(device)) {
                    logger.debug("------>  Success in sending device information!!");
                    break;

                } else {
                    logger.debug("------>  Fail to send device information...");

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("------>  Fail to send device information in catch block");
            }
        }

    }

    @Override
    public File receiveScript(MultipartFile multipartFile, HttpServletRequest request) throws Exception {
        String path = request.getSession().getServletContext().getRealPath("/") + "script";

        File directory = new File(path);            //디렉토리 설정

        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        try {
            deviceUtil.executeCommand("chmod -R 777 " + request.getSession().getServletContext().getRealPath("/"));
        } catch (Exception e) {

        }

        File file = new File(path + File.separator + multipartFile.getOriginalFilename());

        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true, false);

        logger.debug("--------> permission of file : " + file.canWrite() + file.canExecute());

        multipartFile.transferTo(file);             //파일을 로컬에 수신

        return file;
    }

    @Override
    public boolean decryption(String word) throws Exception {
        String deviceId = device.getId();

        logger.debug("------>  decryption is : " + decryption);
        if ("true".equals(decryption)) {
            try {
                Cryptogram cryptogram = new Cryptogram(deviceId);
                String decryptionId = cryptogram.decrypt(word);

                if (deviceId.equals(decryptionId)) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            }
        }

        return false;
    }

    @Override
    public Measure executeScript(File file){
        String fileName = file.getName();
        String fileDirectory = file.getPath().substring(0, file.getPath().length() - fileName.length() - 1);
        String command = "java -cp " + fileDirectory + " " + fileName.substring(0, fileName.length() - 6);          //실행하기 위한 커맨드 생성

        logger.debug("-----> input command is : " + command);

        Measure measure = new Measure();
        measure.setDeviceId(device.getId());

        try {
            long beforeTime = System.currentTimeMillis();

            String result = null;
            synchronized (this) {
                result = deviceUtil.executeCommand(command);         //스크립트 실행...

            }

            logger.debug("-----> script result is : " + result);
            long afterTime = System.currentTimeMillis();

            long secDiffTime = (afterTime - beforeTime);                //실행 시간 측정
            measure.setExecTime(Long.toString(secDiffTime));
            measure.setStatus('Y');             //성공하면 상태를 'Y'로 지정

        } catch (Exception e) {
            e.printStackTrace();

            measure.setExecTime("0");
            measure.setStatus('N');             //실행 실패 시 상태를 'N', 실행시간을 0으로 지정한다.
        }

        return measure;
    }

    @Override
    public void sendMeasure(Measure measure) {
        try {
            if (measureRepository.request(measure)) {
                logger.debug("------>  success in sending measure result!!");

            } else {
                logger.debug("------>  fail to send measure result");
            }

        } catch (Exception e) {
            e.printStackTrace();

            logger.debug("------>  fail to send measure result in catch block");
        }
    }
}
