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

package com.dtstack.engine.master.event;

import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.common.util.RenderUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/25 10:10 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class ContentReplaceEvent extends AdapterEventMonitor implements Ordered {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void leaveQueueAndSenderBeforeEvent(AlterContext alterContext) {
        try {
            String alertTemplate = alterContext.getAlertTemplate();

            if (StringUtils.isNotBlank(alertTemplate)) {
                Map<String, String> dynamicParams = Maps.newHashMap();

                dynamicParams.put(ReplaceConst.MESSAGE, alterContext.getContent());

                if (StringUtils.isNotBlank(alterContext.getUserName()) && alertTemplate.contains(ReplaceConst.IS_USER)) {
                    dynamicParams.put(ReplaceConst.USERNAME, alterContext.getUserName());
                }

                alterContext.setContent(RenderUtil.renderTemplate(alertTemplate,dynamicParams));
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    interface ReplaceConst {
        String MESSAGE = "message";
        String USERNAME = "user";

        String IS_USER = "${"+USERNAME+"}";
    }

}
