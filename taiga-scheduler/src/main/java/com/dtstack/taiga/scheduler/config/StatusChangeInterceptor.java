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

package com.dtstack.taiga.scheduler.config;

import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.pluginapi.util.MathUtil;
import com.dtstack.taiga.scheduler.event.ScheduleJobBatchEvent;
import com.dtstack.taiga.scheduler.event.ScheduleJobEventPublisher;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2020-11-24
 */

@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class StatusChangeInterceptor implements Interceptor {

    private static List<String> watchTable = Lists.newArrayList("com.dtstack.taiga.dao.ScheduleJobDao");

    private static final Logger LOG = LoggerFactory.getLogger(StatusChangeInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        doEventFilter(statementHandler, metaObject);
        return invocation.proceed();
    }

    /**
     * 获得真正的处理对象,可能多层代理
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }


    private void doEventFilter(StatementHandler handler, MetaObject metaStatementHandler) {
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        try {
            if (originalSql != null && !originalSql.equals("")) {
                MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
                        .getValue("delegate.mappedStatement");
                String id = mappedStatement.getId();
                String className = id.substring(0, id.lastIndexOf("."));
                if (!watchTable.contains(className)) {
                    return;
                }
                ParameterHandler parameterHandler = (ParameterHandler) metaStatementHandler
                        .getValue("delegate.parameterHandler");
                Object parameters = parameterHandler.getParameterObject();
                parseScheduleJobChange(parameterHandler, parameters);

            }
        } catch (Throwable e) {
            LOG.error("event filter originalSql 【{}】 error", originalSql, e);
        }

    }

    private void parseScheduleJobChange(ParameterHandler parameterHandler, Object parameters) {
        //直接更新对象
        if (parameters instanceof ScheduleJob) {
            ScheduleJob job = (ScheduleJob) parameters;
            if (StringUtils.isNotBlank(job.getJobId()) && null != job.getStatus()) {
                ScheduleJobEventPublisher.getInstance().publishBatchEvent(new ScheduleJobBatchEvent(job.getJobId(), job.getStatus()));
                return;
            }
        }
        Map parameterObject = (HashMap) parameterHandler.getParameterObject();
        if (null == parameterObject) {
            return;
        }
        if (!parameterObject.containsKey("status")) {
            return;
        }
        Object status = parameterObject.get("status");
        if (null == status) {
            return;
        }
        //只有更新scheduleJob状态才去触发event
        Integer statusVal = MathUtil.getIntegerVal(status);
        if (parameterObject.containsKey("jobIds")) {
            Object jobIds = parameterObject.get("jobIds");
            if (jobIds instanceof List) {
                ScheduleJobBatchEvent scheduleJobBatchEvent = new ScheduleJobBatchEvent((List) jobIds, statusVal);
                ScheduleJobEventPublisher.getInstance().publishBatchEvent(scheduleJobBatchEvent);
            }
        }
        if (parameterObject.containsKey("jobId")) {
            Object jobId = parameterObject.get("jobId");
            if (jobId instanceof String) {
                ScheduleJobBatchEvent scheduleJobBatchEvent = new ScheduleJobBatchEvent((String) jobId, statusVal);
                ScheduleJobEventPublisher.getInstance().publishBatchEvent(scheduleJobBatchEvent);
            }
        }
    }
}
