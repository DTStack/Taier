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

package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.enums.EParamType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.BatchSysParameter;
import com.dtstack.taier.dao.domain.BatchTaskParam;
import com.dtstack.taier.dao.domain.BatchTaskParamShade;
import com.dtstack.taier.dao.mapper.BatchTaskParamDao;
import com.dtstack.taier.develop.dto.devlop.BatchParamDTO;
import com.dtstack.taier.develop.utils.develop.sync.job.SyncJob;
import com.dtstack.taier.scheduler.vo.ScheduleTaskVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class BatchTaskParamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchTaskParamService.class);

    @Autowired
    private BatchTaskParamDao batchTaskParamDao;

    @Autowired
    private BatchSysParamService batchSysParamService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    private static final String PARAM_REGEX = "\\$\\{.*?\\}";

    private static final Pattern PARAM_REGEX_PATTERN = Pattern.compile(PARAM_REGEX);

    /**
     * kerberos认证配置中包含${}
     */
    private static final String[] KERBEROS_IGNORE_KEYS = {"hadoopConfig"};

    public void addOrUpdateTaskParam(final ScheduleTaskVO batchTask) {
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final List<BatchParamDTO> parameterSet = this.paramResolver(batchTask.getTaskVariables());

        //存储
        this.saveTaskParams(batchTask.getId(), parameterSet);
    }

    /**
     * 校验任务中的 系统参数 和 自定义参数
     * @param jobContent SQL内容
     * @param parameterSet 任务参数
     */
    public void checkParams(final String jobContent, final List parameterSet) {
        //校验任务参数不能为空参数
        if (CollectionUtils.isNotEmpty(parameterSet)) {
            for (Object paramObj : parameterSet) {
                BatchTaskParam batchTaskParam = PublicUtil.objectToObject(paramObj, BatchTaskParam.class);
                if(batchTaskParam != null){
                    if (StringUtils.isBlank(batchTaskParam.getParamCommand()) || "$[]".equalsIgnoreCase(batchTaskParam.getParamCommand())) {
                        throw new RdosDefineException("自定义参数赋值不能为空");
                    }
                }
            }
        }

        String jobStr = jobContent;
        if (StringUtils.isBlank(jobStr)){
            return;
        }

        //校验任务参数时，先清除sql中的注释
        String sqlWithoutComments = this.batchSqlExeService.removeComment(jobStr);
        if (StringUtils.isNotEmpty(sqlWithoutComments)) {
            sqlWithoutComments = sqlWithoutComments.replaceAll("\\s*", "");
        }

        //校验任务参数时，先删除 数据同步任务 配置项
        if (sqlWithoutComments.contains(FormNames.HBASE_CONFIG) || sqlWithoutComments.contains(FormNames.HADOOP_CONFIG) || sqlWithoutComments.contains(FormNames.KERBEROS_CONFIG)) {
            sqlWithoutComments = removeConfig(sqlWithoutComments);
        }

        //正则解析SQL中的 系统参数 和 自定义参数
        Matcher matcher = PARAM_REGEX_PATTERN.matcher(sqlWithoutComments);
        if (matcher.find()) {
            if (CollectionUtils.isEmpty(parameterSet)) {
                LOGGER.error("jobContent:{}", jobContent);
                throw new RdosDefineException(ErrorCode.TASK_PARAM_CONTENT_NOT_NULL);
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
            LOGGER.error(String.format("jobContent: %s", jobContent), e);
        }
        return jobContent;
    }

    /**
     * 转换SQL任务中的 自定义参数 和 系统参数 为参数对象
     * @param taskVariables
     * @return
     */
    public List<BatchParamDTO> paramResolver(final List<Map> taskVariables) {

        if (CollectionUtils.isEmpty(taskVariables)) {
            return Collections.emptyList();
        }

        final List<BatchParamDTO> parameters = new ArrayList<>(taskVariables.size());
        for (Map<String, Object> var : taskVariables) {
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

    public void deleteTaskParam(long taskId) {
        this.batchTaskParamDao.deleteByTaskId(taskId);
    }

    public List<BatchTaskParam> buildBatchTaskParams(final long taskId, final List<BatchParamDTO> batchParamDTOS) {

        final List<BatchTaskParam> saves = new ArrayList<>(batchParamDTOS.size());

        for (final BatchParamDTO tmp : batchParamDTOS) {
            if (StringUtils.isBlank(tmp.getParamCommand())) {
                throw new RdosDefineException("自定义参数赋值不能为空");
            }
            BatchTaskParam batchTaskParam = new BatchTaskParam();
            batchTaskParam.setTaskId(taskId);
            batchTaskParam.setType(tmp.getType());
            batchTaskParam.setParamName(tmp.getParamName());
            batchTaskParam.setParamCommand(tmp.getParamCommand());
            saves.add(this.addOrUpdate(batchTaskParam));
        }

        return saves;
    }

    /**
     * 将SQL中的系统参数和自定义参数对象转换为 BatchTaskParamShade 对象
     * @param params
     * @return
     * @throws Exception
     */
    public List<BatchTaskParamShade> convertShade(final List<BatchTaskParam> params) throws Exception {
        final List<BatchTaskParamShade> shades = Lists.newArrayList();
        if (params != null) {
            for (final BatchTaskParam param : params) {
                shades.add(PublicUtil.objectToObject(param, BatchTaskParamShade.class));
            }
        }
        return shades;
    }

    /**
     * 将 系统参数和自定义参数 的DTO对象转换为PO对象
     * @param paramDTOs
     * @return
     * @throws Exception
     */
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
            if (!EParamType.SYS_TYPE.getType().equals(taskParamShade.getType())) {
                continue;
            }
            // 将 command 属性设置为系统表的 command
            BatchSysParameter sysParameter = batchSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParams;
    }

}

