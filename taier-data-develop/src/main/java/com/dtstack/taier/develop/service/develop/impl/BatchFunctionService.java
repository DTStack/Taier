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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.constant.PatternConstant;
import com.dtstack.taier.common.enums.CatalogueType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.common.enums.FuncType;
import com.dtstack.taier.common.enums.FunctionType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.BatchFunction;
import com.dtstack.taier.dao.domain.BatchFunctionResource;
import com.dtstack.taier.dao.domain.BatchResource;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopFunctionDao;
import com.dtstack.taier.develop.dto.devlop.BatchFunctionVO;
import com.dtstack.taier.develop.dto.devlop.TaskCatalogueVO;
import com.dtstack.taier.develop.enums.develop.FlinkUDFType;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.sql.SqlParserImpl;
import com.dtstack.taier.develop.sql.parse.SqlParserFactory;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jiangbo on 2017/5/8 0008.
 */
@Service
public class BatchFunctionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFunctionService.class);

    @Autowired
    private DevelopFunctionDao developFunctionDao;

    @Autowired
    private BatchFunctionResourceService batchFunctionResourceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchTaskService batchTaskService;


    @Autowired
    private StreamSqlFormatService streamSqlFormatService;

    public static final List<String> ADD_FUNCTION_NAME = new ArrayList<>();
    static {
        ADD_FUNCTION_NAME.add("TIMETOSECOND");
        ADD_FUNCTION_NAME.add("TIMETOMILLISECOND");
    }

    private SqlParserFactory parserFactory = SqlParserFactory.getInstance();

    /**
     * 系统函数缓存
     */
    private Cache<String, Map<String, BatchFunction>> systemFunctions = CacheBuilder.newBuilder().expireAfterWrite(30 * (long)60, TimeUnit.SECONDS).maximumSize(10).build();


    private static String SYSTEM_FUNCTIONS = "systemFunctions";

    /**
     * 创建临时函数
     */
    private static final String CREATE_TEMP_FUNCTION = "create temporary function %s as '%s' using jar '%s';";

    private static String CUSTOM_FUNCTION_TEMPLATE = "CREATE %s FUNCTION %s WITH %s";


    /**
     * 启动服务时，就初始化系统函数到缓存中
     */
    @PostConstruct
    public void init() {
        Map<String, BatchFunction> batchFunctionMap = systemFunctions.getIfPresent(SYSTEM_FUNCTIONS);
        if (batchFunctionMap == null || batchFunctionMap.size() == 0) {
            List<BatchFunction> listSystemFunction = developFunctionDao.listSystemFunction(null);
            batchFunctionMap = Maps.newConcurrentMap();
            for (BatchFunction systemFunction : listSystemFunction) {
                batchFunctionMap.put(systemFunction.getName(), systemFunction);
            }
            systemFunctions.put(SYSTEM_FUNCTIONS, batchFunctionMap);
        }
        systemFunctions.put(SYSTEM_FUNCTIONS, batchFunctionMap);
    }


    /**
     * 根据id获取函数
     * @param functionId
     * @return
     */
    public BatchFunctionVO getFunction(Long functionId) {
        BatchFunction batchFunction = developFunctionDao.selectOne(Wrappers.lambdaQuery(BatchFunction.class)
                .eq(BatchFunction::getId,functionId)
                .eq(BatchFunction::getIsDeleted,Deleted.NORMAL.getStatus()));
        if (Objects.isNull(batchFunction)) {
            return new BatchFunctionVO();
        }
        BatchFunctionVO vo = BatchFunctionVO.toVO(batchFunction);
        //如果函数有资源，则设置函数的资源
        BatchFunctionResource resourceFunctionByFunctionId = batchFunctionResourceService.getResourceFunctionByFunctionId(batchFunction.getId());
        if (Objects.nonNull(resourceFunctionByFunctionId)){
            vo.setResources(resourceFunctionByFunctionId.getResourceId());
        }
        vo.setCreateUser(userService.getById(batchFunction.getCreateUserId()));
        vo.setModifyUser(userService.getById(batchFunction.getModifyUserId()));
        return vo;
    }


    /**
     * 添加函数
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskCatalogueVO addOrUpdateFunction(BatchFunction batchFunction, Long resourceId, Long tenantId, Long userId) {
        if (!PublicUtil.matcher(batchFunction.getName(), PatternConstant.FUNCTION_PATTERN)) {
            throw new RdosDefineException("注意名称只允许存在字母、数字、下划线、横线，hive函数不支持大写字母", ErrorCode.NAME_FORMAT_ERROR);
        }
        AssertUtils.notNull(resourceId, "新增函数必须添加资源");
        checkResourceType(resourceId, batchFunction.getTaskType());

        // id小于0走新增逻辑
        if (Objects.isNull(batchFunction.getId()) || batchFunction.getId() < 1) {
            //名称重复校验
            batchTaskService.checkName(batchFunction.getName(), CatalogueType.CUSTOM_FUNCTION.name(), null, 1, 0L);
            batchFunction.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }

        batchFunction.setType(FuncType.CUSTOM.getType());
        addOrUpdate(batchFunction, userId);
        addOrUpdateFunctionResource(batchFunction, resourceId);
        // 添加类目关系
        TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO();
        taskCatalogueVO.setId(batchFunction.getId());
        taskCatalogueVO.setName(batchFunction.getName());
        taskCatalogueVO.setType("file");
        taskCatalogueVO.setLevel(null);
        taskCatalogueVO.setChildren(null);
        taskCatalogueVO.setParentId(batchFunction.getNodePid());

        String username = userService.getUserName(batchFunction.getCreateUserId());
        taskCatalogueVO.setCreateUser(username);
        return taskCatalogueVO;
    }

    /**
     * 添加或更新函数资源关联关系
     *
     * @param function
     * @param resourceId
     */
    private void addOrUpdateFunctionResource(BatchFunction function, Long resourceId) {
        BatchFunctionResource batchFunctionResource = new BatchFunctionResource();
        batchFunctionResource.setFunctionId(function.getId());
        batchFunctionResource.setTenantId(function.getTenantId());
        batchFunctionResource.setResourceId(resourceId);
        BatchFunctionResource resourceFunctionByFunctionId = getResourceFunctionByFunctionId(function.getId());
        if (Objects.isNull(resourceFunctionByFunctionId)) {
            batchFunctionResource.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            batchFunctionResource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            batchFunctionResourceService.insert(batchFunctionResource);
        }else {
            batchFunctionResource.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            batchFunctionResourceService.updateByFunctionId(batchFunctionResource);
        }
    }

    /**
     * 根据函数id获取资源函数关系
     * @param functionId
     * @return
     */
    private BatchFunctionResource getResourceFunctionByFunctionId(Long functionId) {
        return batchFunctionResourceService.getResourceFunctionByFunctionId(functionId);
    }


    /**
     * 校验资源是否存在
     * @param resourceId
     */
    private void checkResourceType(Long resourceId, Integer taskType) {
        BatchResource resource = batchResourceService.getResource(resourceId);
        if (Objects.isNull(resource)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
        }
        //任务类型是flinkSql ,函数饮用的资源必须是上传sftp
        if (ComputeType.STREAM.getType().equals(resource.getComputeType())) {
            AssertUtils.isTrue(EScheduleJobType.SQL.getType().equals(taskType), "sparkSQL 任务只能引用上传到hdfs的资源");
        }
        if (ComputeType.BATCH.getType().equals(resource.getComputeType())) {
            AssertUtils.isTrue(EScheduleJobType.SPARK_SQL.getType().equals(taskType), "flinkSQL 任务只能引用上传到sftp的资源");
        }
    }

    /**
     * 新增、更新 函数信息
     *
     * @param batchFunction 函数信息
     * @param userId        用户ID
     * @return
     */
    private BatchFunction addOrUpdate(BatchFunction batchFunction, Long userId) {
        if (batchFunction.getId() > 0) {
            batchFunction.setModifyUserId(userId);
            batchFunction.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            developFunctionDao.updateById(batchFunction);
        } else {
            batchFunction.setCreateUserId(userId);
            batchFunction.setModifyUserId(userId);
            batchFunction.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            batchFunction.setIsDeleted(Deleted.NORMAL.getStatus());
            developFunctionDao.insert(batchFunction);
        }
        return batchFunction;
    }

    /**
     * 移动函数
     * @param userId
     * @param functionId
     * @param nodePid
     */
    public void moveFunction(Long userId, Long functionId, Long nodePid) {
        BatchFunction bf = developFunctionDao.selectOne(Wrappers.lambdaQuery(BatchFunction.class)
                .eq(BatchFunction::getId,functionId)
                .eq(BatchFunction::getIsDeleted,Deleted.NORMAL.getStatus()));
        if (Objects.isNull(bf)) {
            throw new RdosDefineException(ErrorCode.FUNCTION_CAN_NOT_FIND);
        }
        if (FuncType.SYSTEM.getType().equals(bf.getType())) {
            throw new RdosDefineException(ErrorCode.SYSTEM_FUNCTION_CAN_NOT_MODIFY);
        }
        bf = new BatchFunction();
        bf.setId(functionId);
        bf.setNodePid(nodePid);
        addOrUpdate(bf, userId);
    }

    /**
     * 删除函数
     * @param userId
     * @param functionId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFunction(Long userId, Long functionId) {
        BatchFunction batchFunction = developFunctionDao.selectOne(Wrappers.lambdaQuery(BatchFunction.class)
                .eq(BatchFunction::getId,functionId)
                .eq(BatchFunction::getIsDeleted,Deleted.NORMAL.getStatus()));
        if (Objects.isNull(batchFunction)) {
            throw new RdosDefineException(ErrorCode.FUNCTION_CAN_NOT_FIND);
        }
        if (FuncType.SYSTEM.getType().equals(batchFunction.getType())) {
            throw new RdosDefineException(ErrorCode.SYSTEM_FUNCTION_CAN_NOT_MODIFY);
        }

        batchFunctionResourceService.deleteByFunctionId(functionId);
        batchFunction = new BatchFunction();
        batchFunction.setId(functionId);
        batchFunction.setIsDeleted(Deleted.DELETED.getStatus());
        addOrUpdate(batchFunction, userId);
    }


    /**
     * 获取任务类型的所有函数
     * @param tenantId
     * @param taskType
     * @return
     */
    public List<String> getAllFunctionName(Long tenantId, Integer taskType) {
        List<String> nameList = developFunctionDao.listNameByTenantId(tenantId, taskType);
        List<BatchFunction> systemFunction = developFunctionDao.listSystemFunction(taskType);
        List<String> systemNames = systemFunction.stream().map(BatchFunction::getName).collect(Collectors.toList());
        systemNames.addAll(nameList);
        return systemNames;
    }


    /**
     * 根据 租户、函数类型、引擎类型 获取函数列表
     * @param tenantId
     * @param functionType
     * @param taskType
     * @return
     */
    public List<BatchFunction> listTenantFunction(Long tenantId, Integer functionType, Integer taskType) {
        return developFunctionDao.listTenantFunction(tenantId, functionType, taskType);
    }


    /**
     * 处理并返回 sql 自定义函数的 SQL
     *
     * @param sql
     * @param tenantId
     * @return
     */
    public List<String> buildContainFunctions(String sql, Long tenantId, Integer taskType) {
        String buildContainFunction = buildContainFunction(sql, tenantId, taskType);
        if (StringUtils.isNotBlank(buildContainFunction)) {
            return Arrays.asList(buildContainFunction.split(";"));
        }
        return Lists.newArrayList();
    }


    /**
     * 判断sql是否包含自定义函数
     *
     * @param sql
     * @param tenantId
     * @return
     */
    public String buildContainFunction(String sql, Long tenantId, Integer taskType) {
        StringBuilder sb = new StringBuilder();
        sql = SqlFormatUtil.formatSql(sql).toLowerCase();
        // sql中的自定义函数
        SqlParserImpl sqlParser = parserFactory.getSqlParser(ETableType.HIVE);
        Set<String> sqlFunctionNames = sqlParser.parseFunction(sql);
        if (CollectionUtils.isEmpty(sqlFunctionNames)) {
            return StringUtils.EMPTY;
        }
        // 获取此项目下的自定义函数名称
        List<BatchFunction> tenantCustomFunctions = listTenantFunction(tenantId, FunctionType.USER.getType(), taskType);
        if (CollectionUtils.isEmpty(tenantCustomFunctions)) {
            return StringUtils.EMPTY;
        }
        List<String> customFunctionNames = tenantCustomFunctions.stream().map(BatchFunction::getName).collect(Collectors.toList());
        // 循环sql中的函数判断是否是项目中的名称
        for (String sqlFunctionName : sqlFunctionNames) {
            // 如果sql中的函数存在于此项目下
            if (customFunctionNames.contains(sqlFunctionName)) {
                BatchFunction byNameAndTenantId = developFunctionDao.getByNameAndTenantId(tenantId, sqlFunctionName);
                sb.append(createTempUDF(byNameAndTenantId));
            }
        }
        return sb.toString();
    }


    /**
     * 创建临时函数
     *
     * @param batchFunction
     * @return
     */
    private String createTempUDF(BatchFunction batchFunction) {
        String funcName = batchFunction.getName();
        String className = batchFunction.getClassName();
        // 获取资源路径
        String resourceURL = batchResourceService.getResourceURLByFunctionId(batchFunction.getId());
        if (StringUtils.isNotBlank(resourceURL)) {
            return String.format(CREATE_TEMP_FUNCTION, funcName, className, resourceURL);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 根据 租户、父目录id 查询
     * @param tenantId
     * @param nodePid
     * @return
     */
    public List<BatchFunction> listByNodePidAndTenantId(Long tenantId, Long nodePid){
        return developFunctionDao.listByNodePidAndTenantId(tenantId, nodePid);
    }

    public List<BatchFunction> getFlinkFunctions(Set<String> funcNameSet, Long tenantId) {
        if (CollectionUtils.isEmpty(funcNameSet)) {
            return Lists.newArrayList();
        }
        List<BatchFunction> streamFunctionList = developFunctionDao.listTenantByFunction(tenantId, EScheduleJobType.SQL.getType());
        return streamFunctionList.stream().filter(f-> funcNameSet.contains(f.getName().toUpperCase())).collect(Collectors.toList());
    }

    public String createRegisterFlinkFuncSQL(BatchFunction function) {
        FlinkUDFType udfType = FlinkUDFType.fromTypeValue(function.getUdfType());
        return String.format(CUSTOM_FUNCTION_TEMPLATE, udfType.getName(), function.getName(), function.getClassName());
    }

    /**
     * 自定义函数区分大小写
     *
     * @param funcNameSet      函数集合
     * @return create xxx function sql
     */
    public List<String> generateFuncSql(Set<String> funcNameSet, Long tenantId) {
        List<BatchFunction> functionList = getFlinkFunctions(funcNameSet, tenantId);
        List<String> result = Lists.newArrayList();
        List<Long> resourceIds = new ArrayList<>();
        for (BatchFunction function : functionList) {
            BatchFunctionResource batchFunctionResource = batchFunctionResourceService.getResourceFunctionByFunctionId(function.getId());
            AssertUtils.notNull(batchFunctionResource, "函数资源为null");
            resourceIds.add(batchFunctionResource.getResourceId());
        }
        //add jar
        resourceIds.forEach(resourceId -> result.add(streamSqlFormatService.generateAddJarSQL(resourceId, null)));
        // register function
        functionList.forEach(streamFunction -> result.add(createRegisterFlinkFuncSQL(streamFunction)));
        return result;
    }



    public Set<String> getFuncSet(Task task, Boolean isFilterSys) {
        //sql 任务需要解析出关联的资源(eg:自定义function)
        Set<String> funcSet = streamSqlFormatService.getFuncName(task.getSqlText());
        Set<String> sourceFuncs = streamSqlFormatService.getFuncName(task.getSourceStr());
        Set<String> sidFuncs = streamSqlFormatService.getFuncName(task.getTargetStr());
        Set<String> sideFuncs = streamSqlFormatService.getFuncName(task.getSideStr());
        funcSet.addAll(sourceFuncs);
        funcSet.addAll(sidFuncs);
        funcSet.addAll(sideFuncs);
        if (!isFilterSys) {
            return funcSet;
        }
        List<BatchFunction> batchFunctions = developFunctionDao.listSystemFunction(EScheduleJobType.SQL.getType());
        List<String> sysFuncNames = batchFunctions.stream().map(BatchFunction::getName).collect(Collectors.toList());

        //FIXME 区分大小写
        for (String sysFuncName :  sysFuncNames) {
            if (funcSet.contains(sysFuncName) && !ADD_FUNCTION_NAME.contains(sysFuncName)) {
                funcSet.remove(sysFuncName);
                if (CollectionUtils.isEmpty(funcSet)) {
                    break;
                }
            }
        }
        return funcSet;
    }


    /**
     * 校验是否包含了函数
     *
     * @param sql
     * @param tenantId
     * @param functionType
     * @return
     */
    public boolean validContainSelfFunction(String sql, Long tenantId, Integer functionType, Integer taskType) {
        if (StringUtils.isBlank(sql) || Objects.isNull(tenantId)) {
            return false;
        }
        sql = SqlFormatUtil.formatSql(sql).toLowerCase();
        // sql中的自定义函数
        SqlParserImpl sqlParser = parserFactory.getSqlParser(ETableType.HIVE);
        Set<String> sqlFunctionNames = sqlParser.parseFunction(sql);
        if (CollectionUtils.isEmpty(sqlFunctionNames)) {
            return false;
        }
        // 获取此项目下的自定义函数名称
        List<BatchFunction> projectFunctions = listTenantFunction(tenantId, functionType, taskType);
        if (CollectionUtils.isEmpty(projectFunctions)) {
            return false;
        }
        List<String> projectFunctionNames = projectFunctions.stream().map(BatchFunction::getName).collect(Collectors.toList());
        // 循环sql中的函数判断是否是项目中的名称
        for (String sqlFunctionName : sqlFunctionNames) {
            // 如果sql中的函数存在于此项目下
            if (projectFunctionNames.contains(sqlFunctionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据 租户、名称、类型 查询
     *
     * @param tenantId 租户ID
     * @param name     名称
     * @param type     类型
     * @return
     */
    public List<BatchFunction> listByNameAndTenantId(Long tenantId, String name, Integer type){
        return developFunctionDao.listByNameAndTenantId(tenantId, name, type);
    }

}
