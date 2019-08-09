package com.espay.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



public class HttpUtil {
    /**
     * post请求
     *
     * @param url
     * @param json
     * @return
     */
    public static JSONObject doPost(String url, JSONObject json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            ByteArrayEntity s=new ByteArrayEntity(json.toJSONString().getBytes("utf-8"));
            //s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            CloseableHttpResponse res = httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}