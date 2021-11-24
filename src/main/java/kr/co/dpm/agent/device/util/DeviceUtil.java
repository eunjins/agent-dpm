package kr.co.dpm.agent.device.util;

import kr.co.dpm.model.Device;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceUtil {
    private Device device;

    public Device getDevice() {
        return device;
    }

    public Device createDevice() {
        device = new Device();
        try {
            Map<String, String> systemInfo = executeCommand("systeminfo");

            device.setDeviceId(systemInfo.get("제품 ID"));
            device.setHostName(systemInfo.get("호스트 이름"));

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            device.setIpAddress(ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return device;
    }

    public Map<String, String> executeCommand(String command) throws Exception {
        Map<String, String> systemInfo = new HashMap<String, String>();

        Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        process.getInputStream()
                        , "euc-kr"));

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splitLine = line.split(":");

            if (splitLine.length > 1) {
                systemInfo.put(splitLine[0].trim(), splitLine[1].trim());
            }
        }

        return systemInfo;
    }
}
