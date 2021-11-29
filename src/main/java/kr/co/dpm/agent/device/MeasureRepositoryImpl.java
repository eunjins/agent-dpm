package kr.co.dpm.agent.device;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureRepositoryImpl implements MeasureRepository{
    @Value("${server-ip}")
    private String ipAddress;
    private static final Logger logger = LogManager.getLogger(MeasureRepositoryImpl.class);

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

        String bodyString = responseBody.string();

        logger.debug(bodyString);

        JSONObject idResponse = new JSONObject(bodyString);

        if ("200".equals(idResponse.getString("code"))) {
            return true;
        } else {
            logger.debug("------------> error message of measure result response is : " + idResponse.getString("message"));

            return false;
        }
    }
}
