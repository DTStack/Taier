package com.dtstack.taier.develop.service.develop.savetask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Setting;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.TaskDirtyDataManageService;
import com.dtstack.taier.develop.service.template.DefaultSetting;
import com.dtstack.taier.develop.service.template.FlinkxJobTemplate;
import com.dtstack.taier.develop.service.template.Restoration;
import com.dtstack.taier.develop.service.template.bulider.nameMapping.NameMappingBuilder;
import com.dtstack.taier.develop.service.template.bulider.nameMapping.NameMappingBuilderFactory;
import com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder;
import com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilderFactory;
import com.dtstack.taier.develop.service.template.bulider.writer.DaWriterBuilder;
import com.dtstack.taier.develop.service.template.bulider.writer.DaWriterBuilderFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_GUIDE;
import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_TEMPLATE;

/**
 * @Author: zhichen
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class DevelopAddOrUpdateSyncTask extends DevelopAddOrUpdateTaskTemplate {

    public static Logger LOGGER = LoggerFactory.getLogger(DevelopAddOrUpdateSyncTask.class);

    @Autowired
    private DaReaderBuilderFactory daReaderBuilderFactory;

    @Autowired
    private DaWriterBuilderFactory daWriterBuilderFactory;

    @Autowired
    private NameMappingBuilderFactory nameMappingBuilderFactory;

    @Autowired
    private TaskDirtyDataManageService taskDirtyDataManageService;


    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        // 校验任务信息,主资源不能为空
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        if (taskResourceParam.getUpdateSource()) {
            taskVO.setSourceStr(taskResourceParam.getSourceMap() == null ? "" : JSON.toJSONString(taskResourceParam.getSourceMap()));
            taskVO.setTargetStr(taskResourceParam.getTargetMap() == null ? "" : JSON.toJSONString(taskResourceParam.getTargetMap()));
            taskVO.setSettingStr(taskResourceParam.getSettingMap() == null ? "" : JSON.toJSONString(taskResourceParam.getSettingMap()));
        } else {
            Task task = developTaskService.getOne(taskVO.getId());
            taskVO.setSourceStr(task.getSourceStr());
            taskVO.setTargetStr(task.getTargetStr());
            taskVO.setSettingStr(task.getSettingStr());
        }
//        //todo 检查密码回填操作
//        this.checkFillPassword(taskResourceParam);
//        if (EScheduleJobType.SYNC.getType().equals(taskVO.getTaskType()) || EScheduleJobType.DATA_ACQUISITION.getType().equals(taskVO.getTaskType())) {
            setSqlTextByCreateModel(taskResourceParam, taskVO);
//        }
        addParam(taskResourceParam);
        return taskResourceParam;
    }

    /**
     * 断点续传
     * @param param
     */
    private void addParam(TaskResourceParam param) {
        if (!EScheduleJobType.SYNC.getType().equals(param.getTaskType())
                || Objects.isNull(param.getSettingMap())
                || !(param.getSettingMap().containsKey("isRestore")
                && (Boolean) param.getSettingMap().get("isRestore"))) {
            return;
        }
        Map<String, Object> map = param.getTargetMap();
        map.put("semantic", "exactly-once");
        param.setTargetMap(map);
    }

    /**
     * 任务根据操作模式生成sqlText
     *
     * @param taskResourceParam
     * @param task
     */
    private void setSqlTextByCreateModel(TaskResourceParam taskResourceParam, Task task) {
        if (taskResourceParam.getSourceMap() != null && DataSourceType.Polardb_For_MySQL.getVal().equals(MapUtils.getInteger(taskResourceParam.getSourceMap(), "type"))) {
            taskResourceParam.getSourceMap().put("type", DataSourceType.MySQL.getVal());
        }
        String sqlText = taskResourceParam.getSqlText();
        Integer createModel = taskResourceParam.getCreateModel();
        if (CREATE_MODEL_TEMPLATE == createModel) {
            if (StringUtils.isNotBlank(sqlText)) {
                try {
                    JSONObject sqlJSON = JSON.parseObject(sqlText);
                    if (!sqlJSON.containsKey(createModel)) {
                        JSONObject sql = new JSONObject(2);
                        sql.put("job", sqlText);
                        sql.put("createModel", CREATE_MODEL_TEMPLATE);
                        sqlText = sql.toJSONString();
                    }
                } catch (Exception e) {
                    throw new RdosDefineException("Job是不是JSON格式,异常: " + e.getMessage());
                }
                if (Objects.equals(taskResourceParam.getTaskType(), EScheduleJobType.SYNC.getVal())) {
                    developTaskParamService.checkParams(sqlText, taskResourceParam.getTaskVariables());
                }
                task.setSqlText(sqlText);
            } else {
                JSONObject sql = new JSONObject(2);
                sql.put("job", sqlText);
                sql.put("createModel", CREATE_MODEL_TEMPLATE);
                task.setSqlText(sql.toJSONString());
            }

        } else if (CREATE_MODEL_GUIDE == createModel) {
            String daSqlText;
            if (taskResourceParam.isPreSave()) {
                dealWithTaskParam(taskResourceParam);
                daSqlText = getDASqlText(taskResourceParam);
                task.setSqlText(daSqlText);
            }
        } else {
            throw new RdosDefineException("createModel incorrect parameter", ErrorCode.INVALID_PARAMETERS);
        }
    }
    private static void dealWithTaskParam(TaskResourceParam task){
        if(task.getSettingMap() != null){
            if (Objects.equals(task.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                //实时任务参数处理
                task.getSettingMap().put("isStream",true);
                task.getSettingMap().put("isRestore",true);
            } else if (Objects.equals(task.getTaskType(), EScheduleJobType.SYNC.getVal())) {
                //离线任务参数处理
                task.getSettingMap().put("isStream",false);
                //离线任务FTP源处理，开启断点续传时必须指定恢复字段
                Integer type = Integer.parseInt(String.valueOf(task.getSourceMap().get("type")));
                if( Objects.equals(task.getSettingMap().get("isRestore") ,true) && Objects.equals(DataSourceType.FTP.getVal(),type)){
                    task.getSettingMap().put("restoreColumnName","");
                }
            }
        }

    }
    /**
     * 获取sqlText
     *
     * @param param
     * @return
     */
    private String getDASqlText(TaskResourceParam param) {
        try {
            //格式化入参(前端会在sourceMap传入很多无效参数，需要格式化入参获取真正需要的参数)
            int sourceType = Integer.parseInt(String.valueOf(param.getSourceMap().get("type")));
            DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
            DaReaderBuilder daReaderBuilder = daReaderBuilderFactory.getDaReaderBuilder(dataSourceType);
            if (daReaderBuilder == null) {
                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_INPUT);
            }
            //来源集合
            Map<String, Object> sourceMap = daReaderBuilder.getParserSourceMap(param.getSourceMap());
            param.setSourceMap(sourceMap);
            //前端入参 需要保存
            Map<String, Object> sourceParamMap = new HashMap<>(sourceMap);
            Reader reader = daReaderBuilder.daReaderBuild(param);

            //目标集合
            Map<String, Object> targetMap = param.getTargetMap();
            //流控、错误集合
            Map<String, Object> settingMap = param.getSettingMap();
            Writer writer = null;
            Setting setting = null;
            Restoration restoration = null;
            JSONObject nameMappingJson = null;
            Integer targetType = Integer.parseInt(String.valueOf(targetMap.get("type")));
            DataSourceType targetDataSourceType = DataSourceType.getSourceType(targetType);
            DaWriterBuilder daWriterBuilder = daWriterBuilderFactory.getDaWriterBuilder(targetDataSourceType);
            writer = daWriterBuilder.daWriterBuild(param);
            setting = PublicUtil.objectToObject(settingMap, DefaultSetting.class);

            NameMappingBuilder mysqlNameMappingBuilder = nameMappingBuilderFactory.getDaReaderBuilder(dataSourceType);
            if (mysqlNameMappingBuilder != null) {
                nameMappingJson = mysqlNameMappingBuilder.daReaderBuild(param);
            }

            //转脚本模式直接返回
            if (CREATE_MODEL_TEMPLATE == param.getCreateModel()) {
                String jobText = getJobText( reader, writer,setting, nameMappingJson, restoration, param);
                return jobText;
            }

            //获得数据同步job.xml的配置
            String jobXml = getJobText(reader, writer, setting, nameMappingJson, restoration, param);
            String parserXml = getParserText(sourceParamMap, targetMap, settingMap);

            JSONObject sql = new JSONObject(3);
            sql.put("job", jobXml);
            sql.put("parser", parserXml);
            sql.put("createModel", CREATE_MODEL_GUIDE);
            return sql.toJSONString();
        } catch (Exception e) {
            LOGGER.error("解析任务失败: " + e.getMessage(), ErrorCode.SERVER_EXCEPTION, e);
            throw new RdosDefineException("解析任务失败: " + e.getMessage(), ErrorCode.SERVER_EXCEPTION, e);
        }
    }

    private String getParserText(final Map<String, Object> sourceParamMap,
                                 final Map<String, Object> targetMap,
                                 final Map<String, Object> settingMap) {
        JSONObject parser = new JSONObject(4);
        parser.put("sourceMap", sourceParamMap);
        parser.put("targetMap", getTargetMap(targetMap));
        parser.put("setting", settingMap);
        return parser.toJSONString();
    }

    public Map<String, Object> getTargetMap(Map<String, Object> targetMap) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("type", targetMap.get("type"));
        map.put("sourceId", targetMap.get("sourceId"));
        map.put("name", targetMap.get("name"));
        return map;
    }

    private String getJobText(final Reader reader,
                              final Writer writer, final Setting setting, final JSONObject nameMapping, final Restoration restoration, final TaskResourceParam param) {
        FlinkxJobTemplate flinkxJobTemplate = new FlinkxJobTemplate() {
            @Override
            public Setting newSetting() {
                return setting;
            }

            @Override
            public JSONObject nameMapping() {
                return nameMapping;
            }

            @Override
            public Restoration restoration() {
                return restoration;
            }

            @Override
            public Reader newReader() {
                return reader;
            }

            @Override
            public Writer newWrite() {
                return writer;
            }
        };
        return flinkxJobTemplate.toJobJsonString(param);
    }
    @Override
    public EScheduleJobType getEScheduleJobType() {
        return EScheduleJobType.SYNC;
    }

    @Override
    public void afterProcessing(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        //脏数据管理
        if (BooleanUtils.isTrue(taskResourceParam.getOpenDirtyDataManage())) {
            taskDirtyDataManageService.addOrUpdateDirtyDataManage(taskResourceParam.getTaskDirtyDataManageVO(), taskResourceParam.getTenantId(), taskResourceParam.getId());
        } else {
            taskDirtyDataManageService.deleteByTaskId(taskResourceParam.getId());
        }
    }
}
