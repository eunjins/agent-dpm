package kr.co.dpm.agent.util;

import kr.co.dpm.agent.device.AgentService;
import kr.co.dpm.agent.device.Measure;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;

@Component
public class DeviceUtil implements Runnable {
    private static final Logger logger = LogManager.getLogger(DeviceUtil.class);
    private File file;

    @Autowired
    private AgentService agentService;

    @Override
    public void run() {
        Measure measure = null;
        try {
            measure = agentService.executeScript(file);
        } catch (Exception e) {
            e.printStackTrace();

            measure.setStatus('N');
            measure.setExecTime("0");
        } finally {
            agentService.sendMeasure(measure);
        }
    }

    public String executeCommand(String command) throws Exception {
        logger.debug("--------> command is : " + command);

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

        } catch (IOException e) {
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

    public void setFile(File file) {
        this.file = file;
    }
}
