package com.dtstack.task.server.send;


import com.dtstack.dtcenter.common.http.PoolHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
public class HttpSendClient {

    private static final Logger logger = LoggerFactory.getLogger(PoolHttpClient.class);

    public static void masterSendJobs(String target) {
        try {
            PoolHttpClient.post(String.format("http://%s/%s", target, Urls.MASTER_SEND_JOBS), "", null);
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }
}
