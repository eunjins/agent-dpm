package kr.co.dpm.agent.device;

import okhttp3.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureRepositoryImpl implements MeasureRepository{
    @Value("${server-ip}")
    private String ipAddress;

    @Override
    public boolean request(Measure measure) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(measure);
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder() //
                .url("http://" + ipAddress + "/scripts/result")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        JSONObject idResponse = new JSONObject(responseBody.string());

        if (idResponse.getString("code") == "200") {
            return true;
        } else {
            return false;
        }
    }
}
