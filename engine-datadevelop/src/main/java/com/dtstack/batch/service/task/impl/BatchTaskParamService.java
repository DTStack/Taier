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

package com.dtstack.batch.service.task.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTaskParamDao;
import com.dtstack.batch.domain.BatchSysParameter;
import com.dtstack.batch.domain.BatchTaskParam;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.dto.BatchParamDTO;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.BatchSysParamService;
import com.dtstack.batch.sync.job.SyncJob;
import com.dtstack.engine.common.enums.EParamType;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class BatchTaskParamService {

    private static final Logger logger = LoggerFactory.getLogger(BatchTaskParamService.class);

    @Autowired
    private BatchTaskParamDao batchTaskParamDao;

    @Autowired
    private BatchSysParamService batchSysParamService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    private static final String PARAM_REGEX = "\\$\\{.*?\\}";

    private static final Pattern PARAM_REGEX_PATTERN = Pattern.compile(PARAM_REGEX);

    public void addOrUpdateTaskParam(final ScheduleTaskVO batchTask) {
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final List<BatchParamDTO> parameterSet = this.paramResolver(batchTask.getTaskVariables());

        //存储
        this.saveTaskParams(batchTask.getId(), parameterSet);
    }

    /**
     * kerberos认证配置中包含${}
     */
    private static final String[] KERBEROS_IGNORE_KEYS = {"hadoopConfig"};

    public void checkParams(final String jobContent, final List parameterSet) {
        String jobStr = jobContent;
        if (CollectionUtils.isNotEmpty(parameterSet)) {
            for (Object batchTaskParam : parameterSet) {
                if (!(batchTaskParam instanceof BatchTaskParam)) {
                    try {
                        batchTaskParam = PublicUtil.objectToObject(batchTaskParam, BatchTaskParam.class);
                    } catch (final IOException e) {
                        logger.error("转换batchTaskParam对象失败,{}", e);
                    }
                }
                if (StringUtils.isBlank(((BatchTaskParam) batchTaskParam).getParamCommand()) || "$[]".equalsIgnoreCase(((BatchTaskParam) batchTaskParam).getParamCommand())) {
                    throw new RdosDefineException("自定义参数赋值不能为空");
                }
            }
        }
        if (StringUtils.isNotEmpty(jobStr)) {
            //注释中的系统参数跳过检查
            String sqlWithoutComments = this.batchSqlExeService.removeComment(jobStr);
            if (StringUtils.isNotEmpty(sqlWithoutComments)) {
                sqlWithoutComments = sqlWithoutComments.replaceAll("\\s*", "");
            }
            //删除 数据同步任务 配置项
            if (sqlWithoutComments.contains("hbaseConfig") || sqlWithoutComments.contains("hadoopConfig")
                    || sqlWithoutComments.contains("kerberosConfig")) {
                sqlWithoutComments = removeConfig(sqlWithoutComments);
            }
            Matcher matcher = PARAM_REGEX_PATTERN.matcher(sqlWithoutComments);
            if (matcher.find()) {
                if (CollectionUtils.isEmpty(parameterSet)) {
                    logger.error("jobContent:{}", jobContent);
                    throw new RdosDefineException("任务中存在未赋值的系统参数或自定义参数,请检查任务参数配置");
                }
            }
        }

    }

    private  String removeConfig(String sql) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(sql);
            removeJsonConfig(jsonObject);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            return sql;
        }
    }

    private void removeJsonConfig(Object json){
        if (json instanceof JSONObject){
            JSONObject jsonObject = ((JSONObject) json);
            if (jsonObject.containsKey("hbaseConfig")){
                jsonObject.remove("hbaseConfig");
            }
            if (jsonObject.containsKey("hadoopConfig")){
                jsonObject.remove("hadoopConfig");
            }
            if (jsonObject.containsKey("kerberosConfig")){
                jsonObject.remove("kerberosConfig");
            }
            for (String key : jsonObject.keySet()){
                if (jsonObject.get(key) instanceof JSONObject){
                    removeJsonConfig(jsonObject.get(key));
                }else if (jsonObject.get(key) instanceof JSONArray){
                    removeJsonConfig(jsonObject.get(key));
                }else if (jsonObject.get(key) instanceof String){
                    jsonObject.put(key,removeConfig(jsonObject.getString(key)));
                }
            }
        }else if (json instanceof JSONArray){
            JSONArray json1 = (JSONArray) json;
            for (int i = 0; i < json1.size(); i++) {
                removeJsonConfig(json1.get(i));
            }
        }
    }

    /**
     * 校验数据同步变量参数 移除hdfs的配置项
     *
     * @param jobContent
     * @return
     */
    public String checkSyncJobParams(String jobContent) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final SyncJob syncJob = SyncJob.getSyncJob(jobContent);
            if (!Objects.isNull(syncJob.reader) && !Objects.isNull(syncJob.reader.parameter)) {
                final Map<String, Object> jsonReaderParam = syncJob.reader.parameter;
                if (jsonReaderParam != null) {
                    for (final String ignoreKey : KERBEROS_IGNORE_KEYS) {
                        jsonReaderParam.remove(ignoreKey);
                    }
                }
                jsonObject.put("reader", jsonReaderParam);
            }
            if (!Objects.isNull(syncJob.writer) && !Objects.isNull(syncJob.writer.parameter)) {
                final Map<String, Object> jsonWriterParam = syncJob.writer.parameter;
                if (jsonWriterParam != null) {
                    for (final String ignoreKey : KERBEROS_IGNORE_KEYS) {
                        jsonWriterParam.remove(ignoreKey);
                    }
                }
                jsonObject.put("writer", jsonWriterParam);
            }

            return jsonObject.toString();
        } catch (final Exception e) {
            logger.error("jobContent:{}", jobContent);
            logger.error("", e);
        }
        return jobContent;
    }

    public List<BatchParamDTO> paramResolver(final List<Map> taskVariables) {

        if (CollectionUtils.isEmpty(taskVariables)) {
            return Collections.emptyList();
        }

        final List<BatchParamDTO> parameters = new ArrayList<>(taskVariables.size());
        for (final Map<String, Object> var : taskVariables) {
            final BatchParamDTO batchParamDTO = new BatchParamDTO(MathUtil.getIntegerVal(var.get("type")),
                    MathUtil.getString(var.get("paramName")), MathUtil.getString(var.get("paramCommand")));
            parameters.add(batchParamDTO);
        }

        return parameters;
    }

    /**
     * TODO 需要优化,当前处理方式是先删除,然后再添加
     *
     * @param taskId
     * @param batchParamDTOS
     */
    public List<BatchTaskParam> saveTaskParams(final Long taskId, final List<BatchParamDTO> batchParamDTOS) {
        this.batchTaskParamDao.deleteByTaskId(taskId);
        final List<BatchTaskParam> batchTaskParams = this.buildBatchTaskParams(taskId, batchParamDTOS);
        return batchTaskParams;
    }

    public BatchTaskParam addOrUpdate(final BatchTaskParam batchTaskParam) {
        if (batchTaskParam.getId() > 0) {
            this.batchTaskParamDao.update(batchTaskParam);
        } else {
            this.batchTaskParamDao.insert(batchTaskParam);
        }
        return batchTaskParam;
    }

    public void deleteTaskParam(final long taskId) {
        this.batchTaskParamDao.deleteByTaskId(taskId);
    }

    public List<BatchTaskParam> buildBatchTaskParams(final long taskId, final List<BatchParamDTO> batchParamDTOS) {

        final List<BatchTaskParam> saves = new ArrayList<>(batchParamDTOS.size());
        final Timestamp nowTime = Timestamp.valueOf(LocalDateTime.now());

        for (final BatchParamDTO tmp : batchParamDTOS) {
            if (StringUtils.isBlank(tmp.getParamCommand())) {
                throw new RdosDefineException("自定义参数赋值不能为空");
            }
            final BatchTaskParam batchTaskParam = new BatchTaskParam();
            batchTaskParam.setTaskId(taskId);
            batchTaskParam.setType(tmp.getType());
            batchTaskParam.setParamName(tmp.getParamName());
            batchTaskParam.setParamCommand(tmp.getParamCommand());
            batchTaskParam.setGmtCreate(nowTime);
            batchTaskParam.setGmtModified(nowTime);

            saves.add(this.addOrUpdate(batchTaskParam));
        }

        return saves;
    }

    public List<BatchTaskParamShade> convertShade(final List<BatchTaskParam> params) throws Exception {
        final List<BatchTaskParamShade> shades = Lists.newArrayList();
        if (params != null) {
            for (final BatchTaskParam param : params) {
                shades.add(PublicUtil.objectToObject(param, BatchTaskParamShade.class));
            }
        }
        return shades;
    }

    public List<BatchTaskParam> convertParam(final List<BatchParamDTO> paramDTOs) throws Exception {
        final List<BatchTaskParam> params = Lists.newArrayList();
        if (paramDTOs != null) {
            for (final BatchParamDTO paramDTO : paramDTOs) {
                params.add(PublicUtil.objectToObject(paramDTO, BatchTaskParam.class));
            }
        }
        return params;
    }

    public List<BatchTaskParam> getTaskParam(final long taskId) {
        List<BatchTaskParam> taskParams = batchTaskParamDao.listByTaskId(taskId);

        // 特殊处理 TaskParam 系统参数
        for (BatchTaskParam taskParamShade : taskParams) {
            if (EParamType.SYS_TYPE.getType() != taskParamShade.getType()) {
                continue;
            }

            // 将 command 属性设置为系统表的 command
            BatchSysParameter sysParameter = batchSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParams;
    }


}

