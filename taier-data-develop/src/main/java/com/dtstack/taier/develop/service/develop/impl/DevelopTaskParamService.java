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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EParamType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.DevelopSysParameter;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.mapper.DevelopTaskParamMapper;
import com.dtstack.taier.develop.dto.devlop.DevelopParamDTO;
import com.dtstack.taier.develop.utils.develop.common.SqlUtils;
import com.dtstack.taier.develop.utils.develop.sync.job.SyncJob;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class DevelopTaskParamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopTaskParamService.class);

    @Autowired
    private DevelopTaskParamMapper developTaskParamDao;

    @Autowired
    private DevelopSysParamService developSysParamService;


    private static final String PARAM_REGEX = "\\$\\{.*?\\}";

    private static final Pattern PARAM_REGEX_PATTERN = Pattern.compile(PARAM_REGEX);

    /**
     * kerberos认证配置中包含${}
     */
    private static final String[] KERBEROS_IGNORE_KEYS = {"hadoopConfig"};

    public void addOrUpdateTaskParam(final List<Map<String, Object>> taskVariables, Long id) {
        List<DevelopParamDTO> parameterSet = this.paramResolver(taskVariables);
        this.saveTaskParams(id, parameterSet);
    }

    /**
     * 校验任务中的 系统参数 和 自定义参数
     *
     * @param jobContent   SQL内容
     * @param parameterSet 任务参数
     */
    public void checkParams(final String jobContent, final List parameterSet) {
        //校验任务参数不能为空参数
        if (CollectionUtils.isNotEmpty(parameterSet)) {
            for (Object paramObj : parameterSet) {
                DevelopTaskParam developTaskParam = PublicUtil.objectToObject(paramObj, DevelopTaskParam.class);
                if (developTaskParam != null) {
                    if (StringUtils.isBlank(developTaskParam.getParamCommand()) || "$[]".equalsIgnoreCase(developTaskParam.getParamCommand())) {
                        throw new TaierDefineException("自定义参数赋值不能为空");
                    }
                }
            }
        }

        String jobStr = jobContent;
        if (StringUtils.isBlank(jobStr)) {
            return;
        }

        //校验任务参数时，先清除sql中的注释
        String sqlWithoutComments = SqlUtils.removeComment(jobStr);
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
                throw new TaierDefineException(ErrorCode.TASK_PARAM_CONTENT_NOT_NULL);
            }
        }
    }

    private String removeConfig(String sql) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(sql);
            removeJsonConfig(jsonObject);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            return sql;
        }
    }

    private void removeJsonConfig(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = ((JSONObject) json);
            if (jsonObject.containsKey("hbaseConfig")) {
                jsonObject.remove("hbaseConfig");
            }
            if (jsonObject.containsKey("hadoopConfig")) {
                jsonObject.remove("hadoopConfig");
            }
            if (jsonObject.containsKey("kerberosConfig")) {
                jsonObject.remove("kerberosConfig");
            }
            for (String key : jsonObject.keySet()) {
                if (jsonObject.get(key) instanceof JSONObject) {
                    removeJsonConfig(jsonObject.get(key));
                } else if (jsonObject.get(key) instanceof JSONArray) {
                    removeJsonConfig(jsonObject.get(key));
                } else if (jsonObject.get(key) instanceof String) {
                    jsonObject.put(key, removeConfig(jsonObject.getString(key)));
                }
            }
        } else if (json instanceof JSONArray) {
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
     *
     * @param taskVariables
     * @return
     */
    public List<DevelopParamDTO> paramResolver(List<Map<String, Object>> taskVariables) {
        if (CollectionUtils.isEmpty(taskVariables)) {
            return Collections.emptyList();
        }

        List<DevelopParamDTO> parameters = new ArrayList<>(taskVariables.size());
        for (Map<String, Object> var : taskVariables) {
            DevelopParamDTO developParamDTO = new DevelopParamDTO(MathUtil.getIntegerVal(var.get("type")),
                    MathUtil.getString(var.get("paramName")), MathUtil.getString(var.get("paramCommand")));
            parameters.add(developParamDTO);
        }

        return parameters;
    }

    public List<DevelopTaskParam> saveTaskParams(final Long taskId, final List<DevelopParamDTO> developParamDTOS) {
        this.developTaskParamDao.delete(Wrappers.lambdaQuery(DevelopTaskParam.class).eq(DevelopTaskParam::getTaskId, taskId));
        return this.buildBatchTaskParams(taskId, developParamDTOS);
    }

    public DevelopTaskParam addOrUpdate(final DevelopTaskParam developTaskParam) {
        if (developTaskParam.getId() > 0) {
            developTaskParam.setGmtModified(new Timestamp(System.currentTimeMillis()));
            this.developTaskParamDao.updateById(developTaskParam);
        } else {
            developTaskParam.setIsDeleted(Deleted.NORMAL.getStatus());
            developTaskParam.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            developTaskParam.setGmtModified(new Timestamp(System.currentTimeMillis()));
            this.developTaskParamDao.insert(developTaskParam);
        }
        return developTaskParam;
    }

    public void deleteTaskParam(long taskId) {
        this.developTaskParamDao.delete(Wrappers.lambdaQuery(DevelopTaskParam.class).eq(DevelopTaskParam::getTaskId, taskId));
    }

    public List<DevelopTaskParam> buildBatchTaskParams(final long taskId, final List<DevelopParamDTO> developParamDTOS) {

        final List<DevelopTaskParam> saves = new ArrayList<>(developParamDTOS.size());

        for (final DevelopParamDTO tmp : developParamDTOS) {
            if (StringUtils.isBlank(tmp.getParamCommand())) {
                throw new TaierDefineException("自定义参数赋值不能为空");
            }
            DevelopTaskParam developTaskParam = new DevelopTaskParam();
            developTaskParam.setTaskId(taskId);
            developTaskParam.setType(tmp.getType());
            developTaskParam.setParamName(tmp.getParamName());
            developTaskParam.setParamCommand(tmp.getParamCommand());
            saves.add(this.addOrUpdate(developTaskParam));
        }

        return saves;
    }

    /**
     * 将SQL中的系统参数和自定义参数对象转换为 BatchTaskParamShade 对象
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DevelopTaskParamShade> convertShade(final List<DevelopTaskParam> params) {
        final List<DevelopTaskParamShade> shades = Lists.newArrayList();
        if (params != null) {
            for (final DevelopTaskParam param : params) {
                shades.add(PublicUtil.objectToObject(param, DevelopTaskParamShade.class));
            }
        }
        return shades;
    }


    public List<DevelopTaskParam> getTaskParam(final long taskId) {
        List<DevelopTaskParam> taskParams = developTaskParamDao.selectList(Wrappers.lambdaQuery(DevelopTaskParam.class)
                .eq(DevelopTaskParam::getTaskId, taskId)
                .eq(DevelopTaskParam::getIsDeleted, Deleted.NORMAL.getStatus()));
        // 特殊处理 TaskParam 系统参数
        for (DevelopTaskParam taskParamShade : taskParams) {
            if (!EParamType.SYS_TYPE.getType().equals(taskParamShade.getType())) {
                continue;
            }
            // 将 command 属性设置为系统表的 command
            DevelopSysParameter sysParameter = developSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParams;
    }
}

