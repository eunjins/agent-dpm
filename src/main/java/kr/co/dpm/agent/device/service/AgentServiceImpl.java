package kr.co.dpm.agent.device.service;

import kr.co.dpm.agent.device.util.DeviceUtil;
import kr.co.dpm.model.Device;
import kr.co.dpm.model.Measure;
import okhttp3.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AgentServiceImpl implements AgentService {
    private static final String URL = "";

    @Autowired
    private DeviceUtil deviceUtil;

    @Override
    public void sendId() throws Exception {
        Device device = deviceUtil.createDevice();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String json = "{'id' : '" + device.getId() + "'}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder() //
                .url(URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        //TODO 비밀번호 검증...

        sendDevice();

    }

    @Override
    public void sendDevice() throws Exception {
        Device device = deviceUtil.getDevice();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(device);
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder() //
                .url(URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

    }

    @Override
    public void receiveAgentStatus(Device device) {

    }

    @Override
    public File receiveScript() {
        return null;
    }

    @Override
    public void executeScript() {

    }

    @Override
    public void sendMeasure(Measure measure) {

    }
}
