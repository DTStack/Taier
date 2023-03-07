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
package com.dtstack.taier.develop.cron;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.mapper.DevelopSelectSqlMapper;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.service.develop.IJdbcService;
import com.dtstack.taier.pluginapi.leader.LeaderNode;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yuebai
 * @date 2023/2/10
 */
@Component
public class TempSelectJobClearCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempSelectJobClearCron.class);

    private static final String LOCK_PATH = "TempSelectJobClear";
    @Autowired
    protected SourceLoaderService sourceLoaderService;
    @Autowired
    private DevelopSelectSqlMapper developSelectSqlMapper;
    @Autowired
    private EnvironmentContext environmentContext;
    @Autowired
    private IJdbcService jdbcService;


    @Scheduled(cron = "${temp.job.clean.cron:0 0 2 * * ? } ")
    public void clear() {
        boolean success = LeaderNode.getInstance().tryLock(LOCK_PATH, 1000, TimeUnit.MILLISECONDS);
        if (!success) {
            return;
        }
        try {
            long index = 0L;
            while (index != -1L) {
                index = clearTemp(index);
            }
        } catch (Exception e) {
            LOGGER.error("clear temp job error", e);
        } finally {
            LeaderNode.getInstance().release(LOCK_PATH);
        }
    }

    private Long clearTemp(Long index) {
        List<DevelopSelectSql> selectSqls = developSelectSqlMapper.selectList(Wrappers.lambdaQuery(DevelopSelectSql.class)
                .gt(DevelopSelectSql::getId, index).orderByAsc(DevelopSelectSql::getId).last("limit 100"));
        if (CollectionUtils.isEmpty(selectSqls)) {
            return -1L;
        }
        for (DevelopSelectSql selectSql : selectSqls) {
            index = Math.max(selectSql.getId(), index);
            if (selectSql.getGmtCreate().toInstant().plus(environmentContext.getTempSelectExpireTime(), ChronoUnit.HOURS).isAfter(Instant.now())) {
                continue;
            }
            clearByTaskType(selectSql);
        }
        return index;
    }

    private boolean clearByTaskType(DevelopSelectSql selectSql) {
        EScheduleJobType taskType = EScheduleJobType.getByTaskType(selectSql.getTaskType());
        switch (taskType) {
            case SPARK_SQL:
            case HIVE_SQL:
                String dropSql = "drop table " + selectSql.getTempTableName();
                try {
                    dropTempTable(dropSql, selectSql.getDatasourceId());
                } catch (Exception e) {
                    LOGGER.error("datasource sql delete temp table ", e);
                }
                developSelectSqlMapper.deleteById(selectSql.getId());
                return true;
            default:
                developSelectSqlMapper.deleteById(selectSql.getId());
        }
        return true;
    }

    public void dropTempTable(String sql, Long datasourceId) {
        if (null == datasourceId || 0L == datasourceId) {
            return;
        }
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(datasourceId);
        jdbcService.executeQueryWithoutResult(sourceDTO, sql);
        LOGGER.info("datasource sql [{}] delete temp table with sql [{}] ", datasourceId, sql);
    }

}