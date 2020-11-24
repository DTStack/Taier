package com.dtstack.engine.common;

import com.dtstack.engine.common.http.PoolHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class PoolHttpClientTest {

    @Test
    public void testPost() {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("username", "666666@qq.com");
        bodyData.put("password", "666");
        String result = PoolHttpClient.post("https://httpbin.org/post", bodyData);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGet() {
        try {
            String result = PoolHttpClient.get("https://www.baidu.com/");
            Assert.assertNotNull(result);
        } catch (Exception e) {
            fail("Unexpect an exception: " + e.getMessage());
        }
    }
}