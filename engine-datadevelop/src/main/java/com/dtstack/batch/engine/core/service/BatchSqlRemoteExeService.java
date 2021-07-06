package com.dtstack.batch.engine.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.env.EnvironmentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author sanyue
 * @date 2019/9/26
 */
@Service
public class BatchSqlRemoteExeService {

    private static final Logger logger = LoggerFactory.getLogger(BatchSqlRemoteExeService.class);

    private static final String START_SQL_IMMEDIATELY = "/api/rdos/batch/batchJob/startSqlImmediately";
    private static final String HOST_PORT = "%s:%s";

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Object remoteExecute(String remoteNode,
                                Boolean isEnd,
                                Long userId,
                                Long tenantId,
                                Long taskId,
                                String sql,
                                String dtToken,
                                String uniqueKey,
                                Long projectId,
                                Long dtuicTenantId) {
        JSONObject body = new JSONObject();
        body.put("isEnd", isEnd);
        body.put("userId", userId);
        body.put("tenantId", tenantId);
        body.put("taskId", taskId);
        body.put("sql", sql);
        body.put("projectId", projectId);
        body.put("dtuicTenantId", dtuicTenantId);
        body.put("uniqueKey", uniqueKey);
        Map<String, Object> cookies = new HashMap<>(0);
        cookies.put("dt_token", dtToken);

        BatchSqlSend libraSend = new BatchSqlSend(remoteNode);
        return libraSend.postWithTimeout(START_SQL_IMMEDIATELY, body.toString(), cookies, null,300000,2000);
    }


    public String getRemoteNode(String uniqueKey) {
        try {
            String host = environmentContext.getHttpAddress();
            if("0.0.0.0".equalsIgnoreCase(host.trim())){
                //0.0.0.0 host 需要转换为真实的ip
                InetAddress addr = InetAddress.getLocalHost();
                host = addr.getHostAddress();
            }
            Object o = this.stringRedisTemplate.opsForValue().get(uniqueKey);
            logger.info("uniqueKey={}, host={}", uniqueKey, o);
            if (!Objects.isNull(o)) {
                if (host.equals(String.valueOf(o))) {
                    return null;
                } else {
                    return String.format(HOST_PORT, String.valueOf(o), environmentContext.getHttpPort());
                }
            } else {
                stringRedisTemplate.opsForValue().setIfAbsent(uniqueKey, host);
                stringRedisTemplate.expire(uniqueKey, 1, TimeUnit.HOURS);
                return null;
            }
        } catch (Throwable e) {
            logger.error("getRemoteNode failed : {}",uniqueKey, e);
        }
        return null;
    }
}
