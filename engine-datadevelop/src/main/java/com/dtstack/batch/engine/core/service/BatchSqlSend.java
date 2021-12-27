/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.batch.engine.core.service;

import com.dtstack.engine.common.engine.EngineResult;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.PublicUtil;
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
                throw new DtCenterDefException("node inner error..., msg:" + result.getErrorMsg());
            }

            return result.getData();
        } catch (Exception e) {
            logger.error(String.format("%s---->%s:", url, bodyData), e);
            throw new DtCenterDefException(e.getMessage());
        }
    }
}
