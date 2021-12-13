package kr.co.dpm.agent.device;


import kr.co.dpm.agent.util.Cryptogram;
import kr.co.dpm.agent.util.DeviceUtil;
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
    private static final Logger logger = LogManager.getLogger(AgentService.class);
    private Device device;

    @Value("${decryption}")
    private String decryption;

    @Autowired
    private DeviceUtil deviceUtil;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Device executeCommand() throws Exception {
        logger.info("                              RUNNING...                              ");
        logger.info("                                                                      ");
        device = new Device();
        Properties properties = new Properties();

        File file = resourceLoader.getResource("classpath:config/device-id.properties").getFile();
        properties.load(new InputStreamReader(new FileInputStream(file)));

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osName = "win";
        } else if (osName.contains("nix")
                || osName.contains("nux")
                || osName.contains("aix")) {
            osName = "unix";
        } else if (osName.contains("linux")) {
            osName = "linux";
        } else {
            osName = "unix";
        }

        String systemInfoCommand = properties.getProperty(osName + "-command");
        String productIdCommand = properties.getProperty(osName + "-id");

        String systemInfo = deviceUtil.executeCommand(systemInfoCommand);
        String[] splitSystemInfo = systemInfo.split("\n");
        for (String line : splitSystemInfo) {
            String[] splitLine = line.split(":");
            if (productIdCommand.equals(splitLine[0].trim())) {
                device.setId(splitLine[1].trim());
            }
        }

        String hostNameInfo = deviceUtil.executeCommand("hostname").trim();
        device.setHostName(hostNameInfo);

        String jdkInfo = System.getProperty("java.version");
        device.setJdkVersion(jdkInfo);

        String ipAddress = "";
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress()
                        && !inetAddress.isLinkLocalAddress()
                        && inetAddress.isSiteLocalAddress()) {
                    ipAddress = inetAddress.getHostAddress();
                }
            }
        }

        device.setIpAddress(ipAddress);

        return device;
    }

    @Override
    public void sendDevice(Device device) throws Exception {
        if (deviceRepository.request(device)) {

            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info("                        [SEND DEVICE INFORMATION]                     ");
            logger.info("                                                                       ");
            logger.info("      Device ID   :   " + device.getId()                                );
            logger.info("      Host Name   :   " + device.getHostName()                          );
            logger.info("      IP Address  :   " + device.getIpAddress()                         );
            logger.info("      JDK Version :   " + device.getJdkVersion()                        );
            logger.info("                                                                       ");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } else {
            throw new Exception();
        }
    }

    @Override
    public File receiveScript(MultipartFile multipartFile
            , HttpServletRequest request) throws Exception {
        String path = request.getSession()
                .getServletContext()
                .getRealPath("/") + "script";

        File directory = new File(path);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }

        File file = new File(path + File.separator + multipartFile.getOriginalFilename());

        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true, false);

        multipartFile.transferTo(file);

        return file;
    }

    @Override
    public boolean decryption(String word) throws Exception {
        String deviceId = device.getId();
        if ("true".equals(decryption)) {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info("                                                                       ");
            logger.info("                        SUCCESSFUL DECRYPTION !                        ");
            logger.info("                                                                       ");
            try {
                Cryptogram cryptogram = new Cryptogram(deviceId);
                String decryptionId = cryptogram.decryption(word);

                if (deviceId.equals(decryptionId)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                throw new FileNotFoundException();
            }
        }

        return false;
    }

    @Override
    public Measure executeScript(File file) throws Exception {
        String fileName = file.getName();
        String fileDirectory = file.getPath().substring(0, file.getPath().length() - fileName.length() - 1);
        String command = "java -cp " + fileDirectory + " " + fileName.substring(0, fileName.length() - 6);

        logger.info("                              RUNNING...                               ");
        logger.info("                                                                       ");
        long beforeTime = System.currentTimeMillis();
        synchronized (this) {
            deviceUtil.executeCommand(command);
        }
        long afterTime = System.currentTimeMillis();
        logger.info("                     SUCCESSFUL SCRIPT EXECUTION !                     ");
        logger.info("                                                                       ");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        long secDiffTime = (afterTime - beforeTime);

        Measure measure = new Measure();
        measure.setDeviceId(device.getId());
        measure.setExecTime(Long.toString(secDiffTime));
        measure.setStatus('Y');

        return measure;
    }

    @Override
    public void sendMeasure(Measure measure) throws Exception {
        if (measureRepository.request(measure)) {
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info("                        [SEND MEASURE RESULT]                          ");
            logger.info("                                                                       ");
            logger.info("      Device ID   :   " + measure.getDeviceId()                         );
            logger.info("      Execute Time:   " + (measure.getExecTime()).replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",") + " ms"                  );
            logger.info("      Status      :   " + measure.getStatus()                           );
            logger.info("                                                                       ");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } else {
            logger.info("fail to send measure result");

            measure.setStatus('N');
            measure.setExecTime("0");

            sendMeasure(measure);
        }
    }
}
