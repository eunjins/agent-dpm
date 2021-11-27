package kr.co.dpm.agent.device.util;

import kr.co.dpm.agent.device.Device;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

@Component
public class DeviceUtil {
    private static final Logger logger = LogManager.getLogger(DeviceUtil.class);
    private Device device;              //디바이스 정보를 공유하기 위한 필드
    @Autowired
    private ResourceLoader resourceLoader;


    public Device getDevice() {
        return device;
    }

    public Device createDevice() {       //디바이스 정보 생성 로직
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

            String systemInfo = executeCommand(systemInfoCommand);          //제품 ID를 찾기 위한 명령어 실행
            String[] splitSystemInfo = systemInfo.split("\n");
            for (String line : splitSystemInfo) {
                String[] splitLine = line.split(":");

                if (productIdCommand.equals(splitLine[0].trim())) {
                    device.setId(splitLine[1].trim());                      //제품 ID 지정
                }
            }

            String hostNameInfo = executeCommand("hostname").trim();        //호스트 이름 지정
            device.setHostName(hostNameInfo);

            String jdkInfo = executeCommand("java --version");              //JDK-version 지정
            String[] springJdk = jdkInfo.split(" ");
            device.setJdkVersion(springJdk[1]);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();     //IP 주소 지정
            device.setIpAddress(ipAddress);

            logger.debug("-----------> productIdCommand,  디바이스 정보 : " + productIdCommand + device);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("-----------> 디바이스 생성 실패");
        }

        return device;
    }

    public String executeCommand(String command) throws Exception {     //명령어 실행
        logger.debug("---> command : " + command);

        Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()
                            , "euc-kr"));

            StringBuffer resultBuffer = new StringBuffer();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                resultBuffer.append("\n");
                resultBuffer.append(line);
            }

            return resultBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
