package com.dtstack.batch.engine.core.service;

import com.dtstack.dtcenter.common.engine.EngineResult;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import com.dtstack.dtcenter.common.http.PoolHttpClient;
import com.dtstack.dtcenter.common.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author sanyue
 * @date 2019/9/26
 */
public class BatchSqlSend {

    public static Logger logger = LoggerFactory.getLogger(BatchSqlSend.class);

    private String node;

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public BatchSqlSend(String node) {
        this.node = node;
    }


    public Object postWithTimeout(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers, int socketTimeout, int connectTimeout) {

        try {
            String real = String.format("http://%s%s", node, url);
            String response = null;
            for (int i = 0; i < 3; i++) {
                response = PoolHttpClient.postWithTimeout(real, bodyData, cookies,socketTimeout,connectTimeout);
                if(StringUtils.isNotBlank(response)){
                    break;
                }

                Thread.sleep(1000);
            }

            if (StringUtils.isBlank(response)) {
                throw new DtCenterDefException("network error...");
            }

            EngineResult result = PublicUtil.strToObject(response, EngineResult.class);
            if (StringUtils.isNotBlank(result.getErrorMsg())) {
                logger.error(result.getErrorMsg());
                ExceptionEnums exEnum = new ExceptionEnums() {
                    @Override
                    public int getCode() {
                        return result.getCode();
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }
                };
                throw new DtCenterDefException("node inner error..., msg:" + result.getErrorMsg(), exEnum);
            }

            return result.getData();
        } catch (Exception e) {
            logger.error(String.format("%s---->%s:", url, bodyData), e);
            throw new DtCenterDefException(e.getMessage(), e instanceof DtCenterDefException ? ((DtCenterDefException) e).getErrorCode() : null);
        }
    }
}
