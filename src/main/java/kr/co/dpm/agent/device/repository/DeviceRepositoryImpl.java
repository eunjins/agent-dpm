package kr.co.dpm.agent.device.repository;

import kr.co.dpm.model.Device;
import okhttp3.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository{
    private static final String IP_ADDRESS = "";

    @Override
    public String requestId(Device device) throws Exception {   //TODO 프로그램 목록으로 메소드 명 변경될 수 있음
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String json = "{'deviceId' : '" + device.getDeviceId() + "'}";
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder() //
                .url("http://" + IP_ADDRESS + "/id/check")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        JSONObject idResponse = new JSONObject(responseBody.string());

        if (idResponse.getString("code") != "200") {
            return null;        //TODO 로깅 프레임 워크로 메시지 출력...
        }
        return idResponse.getString("password");
    }

    @Override
    public boolean requestDevice(Device device) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(device);
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder() //
                .url("http://" + IP_ADDRESS + "/device/data")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();  // TODO 400 오류 처리...
        ResponseBody responseBody = response.body();
        JSONObject idResponse = new JSONObject(responseBody.string());

        if (idResponse.getString("code") == "200") {
            return true;        //TODO 로깅 프레임 워크로 메시지 출력...
        } else {
            return false;
        }
    }
}
