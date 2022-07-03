package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.TaskDirtyDataManage;
import com.dtstack.taier.develop.enums.develop.SourceDTOType;
import com.dtstack.taier.dao.mapper.TaskDirtyDataManageMapper;
import com.dtstack.taier.develop.enums.develop.TaskDirtyDataManageParamEnum;
import com.dtstack.taier.develop.enums.develop.TaskDirtyOutPutTypeEnum;
import com.dtstack.taier.develop.mapstruct.vo.TaskDirtyDataManageTransfer;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.vo.develop.query.TaskDirtyDataManageVO;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: zhichen
 * @Date: 2022/06/14/2:52 PM
 */
public class TaskDirtyDataManageService extends ServiceImpl<TaskDirtyDataManageMapper, TaskDirtyDataManage> implements TaskDirtyDataManageIService<TaskDirtyDataManage>{

    private static final Logger logger = LoggerFactory.getLogger(TaskDirtyDataManageService.class);

    @Autowired
    private TaskDirtyDataManageIService taskDirtyDataIService;
    @Autowired
    private DsInfoService dsInfoService;
    @Autowired
    private DatasourceService datasourceService;

    public void deleteByTaskId(Long taskId) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("task_id", taskId);
        taskDirtyDataIService.removeByMap(columnMap);
    }

    /**
     * @param taskId
     * @return
     */
    public TaskDirtyDataManage getOneByTaskId(Long taskId) {
        QueryWrapper<TaskDirtyDataManage> queryWrapper = Wrappers.query();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq("is_deleted", Deleted.NORMAL);
        Object obj = taskDirtyDataIService.getOne(queryWrapper);
        return obj == null ? null : (TaskDirtyDataManage) obj;
    }

    /**
     * 添加或修改任务脏数据管理
     *
     * @param vo
     * @param tenantId        租户 id
     * @param taskId          任务 id
     */
    public void addOrUpdateDirtyDataManage(TaskDirtyDataManageVO vo, Long tenantId, Long taskId) {
        // 先删除原有的脏数据管理
        TaskDirtyDataManage taskDirtyDataManage = TaskDirtyDataManageTransfer.INSTANCE.taskDirtyDataManageVOToTaskDirtyDataManage(vo);
        deleteByTaskId(taskId);
        taskDirtyDataManage.setTaskId(taskId);
        taskDirtyDataManage.setTenantId(tenantId);
        taskDirtyDataManage.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        taskDirtyDataManage.setGmtModified(new Timestamp(System.currentTimeMillis()));
        if(Objects.equals(TaskDirtyOutPutTypeEnum.LOG.getValue(),taskDirtyDataManage.getOutputType())){
            taskDirtyDataManage.setLinkInfo("{}");
        }
        taskDirtyDataIService.save(taskDirtyDataManage);
    }


    public void buildTaskDirtyDataManageArgs(Integer taskType, Long taskId, JSONObject confProp) {
        if (Objects.equals(taskType, EScheduleJobType.DATA_ACQUISITION.getVal()) || Objects.equals(taskType, EScheduleJobType.SYNC.getVal())) {
            TaskDirtyDataManage byTaskId = getOneByTaskId(taskId);
            //开启脏数据管理需要在-confProp 设置参数
            if (byTaskId != null) {
                confProp.put(TaskDirtyDataManageParamEnum.OUTPUT_TYPE.getParam(), byTaskId.getOutputType());
                confProp.put(TaskDirtyDataManageParamEnum.MAX_ROWS.getParam(), byTaskId.getMaxRows());
                confProp.put(TaskDirtyDataManageParamEnum.MAX_COLLECT_FAILED_ROWS.getParam(), byTaskId.getMaxCollectFailedRows());
                confProp.put(TaskDirtyDataManageParamEnum.LOG_PRINT_INTERVAL.getParam(), byTaskId.getLogPrintInterval());
                if (Objects.equals(byTaskId.getOutputType(), "jdbc")) {
                    JSONObject dirtyDataJSON = JSONObject.parseObject(byTaskId.getLinkInfo());
                    Long srcId = Long.parseLong(dirtyDataJSON.getString("sourceId"));
                    DsInfo dsInfo = dsInfoService.getOneById(srcId);
                    JSONObject dataJson = JSON.parseObject(dsInfo.getDataJson());
                    confProp.put(TaskDirtyDataManageParamEnum.URL.getParam(), dataJson.getString("jdbcUrl"));
                    confProp.put(TaskDirtyDataManageParamEnum.USERNAME.getParam(), dataJson.getString("username"));
                    confProp.put(TaskDirtyDataManageParamEnum.PASSWORD.getParam(), dataJson.getString("password"));
                    String table = dirtyDataJSON.getString(TaskDirtyDataManageParamEnum.TABLE.name().toLowerCase());
                    if (StringUtils.isNotBlank(table)) {
                        confProp.put(TaskDirtyDataManageParamEnum.TABLE.getParam(), table);
                    } else {
                        //没有走默认表
                        createTable(Long.parseLong(dirtyDataJSON.getString("sourceId")));
                        confProp.put(TaskDirtyDataManageParamEnum.TABLE.getParam(), TaskDirtyDataManageParamEnum.TABLE.getDefaultValue());
                    }
                }
            }
        }
    }


    /**
     * 创建脏数据表
     */
    public void createTable(Long sourceId) {
        // 默认表名
        try {
            Map<String, Object> kerberosConfig = datasourceService.fillKerberosConfig(sourceId);
            DsInfo dataSource = dsInfoService.getOneById(sourceId);
            JSONObject dataJson = JSON.parseObject(dataSource.getDataJson());
            ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, dataSource.getDataTypeCode(), kerberosConfig, Maps.newHashMap());
            // 如果已经存在，则直接返回
            if (checkDirtyTableExist(sourceDTO)) {
                return;
            }
            // 创建表
            IClient client = ClientCache.getClient(sourceDTO.getSourceType());
            String[] createTableSqlParam = TaskDirtyDataManageParamEnum.CREATE_TABLE_SQL.getParam().split(";");
            client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(createTableSqlParam[0]).build());
            client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(createTableSqlParam[1]).build());
            client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(createTableSqlParam[2]).build());
        } catch (Exception e) {
            throw new RdosDefineException("创建脏数据表失败", e);
        }
    }

    /**
     * 检查脏数据表是否存在
     * （特殊情况-数据源切换）
     *
     * @param sourceDTO
     * @return
     */
    private boolean checkDirtyTableExist(ISourceDTO sourceDTO) {
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        try {
            client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(TaskDirtyDataManageParamEnum.TABLE.getDefaultValue()).build());
        } catch (Exception e) {
            if (e.getMessage().contains("doesn't exist")) {
                return false;
            }
            throw new RdosDefineException("创建脏数据表失败", e);
        }
        return true;
    }
}
