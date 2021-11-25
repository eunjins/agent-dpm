package kr.co.dpm.agent.device;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

@Component
public class DeviceUtil {
    private static final Logger logger = LogManager.getLogger(DeviceUtil.class);

    private Device device;

    public Device getDevice() {
        return device;
    }

    public Device createDevice() {
        device = new Device();
        try {
            String systemInfo = executeCommand("systeminfo");

            String[] splitSystemInfo = systemInfo.split("\n");
            for (String line : splitSystemInfo) {
                String[] splitLine = line.split(":");

                if ("제품 ID".equals(splitLine[0])) {
                    device.setDeviceId(splitLine[1].trim());
                } else if ("호스트 이름".equals(splitLine[0])) {
                    device.setHostName(splitLine[1].trim());
                }
            }

            String jdkInfo = executeCommand("java --version");

            String[] springJdk = jdkInfo.split(" ");

            device.setJdkVersion(springJdk[1]);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            device.setIpAddress(ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return device;
    }

    public String executeCommand(String command) throws Exception {
        logger.debug("---> command : " + command);
        Map<String, String> systemInfo = new HashMap<String, String>();

        Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()
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
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
