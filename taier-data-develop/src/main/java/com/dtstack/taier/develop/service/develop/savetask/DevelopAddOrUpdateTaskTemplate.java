package com.dtstack.taier.develop.service.develop.savetask;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleStatus;
import com.dtstack.taier.common.enums.TaskTemplateType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.BatchTaskParamService;
import com.dtstack.taier.develop.service.develop.impl.BatchTaskService;
import com.dtstack.taier.develop.service.develop.impl.BatchTaskTaskService;
import com.dtstack.taier.develop.service.task.TaskTemplateService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * @Author: zhichen
 * @Date: 2022/05/29/4:55 PM
 */
@Component
public abstract class DevelopAddOrUpdateTaskTemplate {
    private static final String DEFAULT_SCHEDULE_CONF = "{\"selfReliance\":0, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}";
    // 需要代码注释模版的任务类型
    private static final Set<Integer> shouldNoteSqlTypes = Sets.newHashSet(EScheduleJobType.SPARK_SQL.getVal(), EScheduleJobType.HIVE_SQL.getVal());

    @Autowired
    public BatchTaskService batchTaskService;
    @Autowired
    public DevelopTaskMapper developTaskMapper;
    @Autowired
    public BatchTaskParamService batchTaskParamService;
    @Autowired
    private ScheduleActionService actionService;
    @Autowired
    public TaskTemplateService taskTemplateService;
    @Autowired
    private BatchTaskTaskService batchTaskTaskService;
    @Autowired
    private UserService userService;
    public abstract TaskResourceParam handleParam(TaskResourceParam taskResourceParam);

    public abstract EScheduleJobType getEScheduleJobType();

    /**
     * 任务编辑添加入口
     *
     * @param taskResourceParam
     * @return
     */
    public TaskVO addOrUpdate(TaskResourceParam taskResourceParam) {
        handleParam(taskResourceParam);
        TaskVO taskVO = TaskMapstructTransfer.INSTANCE.TaskResourceParamToTaskVO(taskResourceParam);
        if (StringUtils.isBlank(taskVO.getName())) {
            throw new RdosDefineException("名称不能为空", ErrorCode.INVALID_PARAMETERS);
        }
        taskVO.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        Task task = developTaskMapper.selectOne(Wrappers.lambdaQuery(Task.class)
                .eq(Task::getName, taskVO.getName())
                .eq(Task::getTenantId, taskVO.getTenantId())
                .last("limit 1"));
        if (taskVO.getId() != null && taskVO.getId() > 0) {//update
            if (task != null && task.getName().equals(taskVO.getName()) && !task.getId().equals(taskVO.getId())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            batchTaskParamService.checkParams(taskVO.getSqlText(), taskVO.getTaskVariables());
            updateTask(taskVO);
        } else {
            if (task != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            addTask(taskVO);
        }
        // 添加任务依赖关系
        batchTaskTaskService.addOrUpdateTaskTask(taskVO.getId(), taskVO.getDependencyTasks());
        postProcessing(taskResourceParam);

        return taskVO;
    }

    public abstract void postProcessing(TaskResourceParam taskResourceParam) ;

    private void updateTask(TaskVO taskVO) {
        Task specialTask = developTaskMapper.selectById(taskVO.getId());
        if (specialTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 转换环境参数
        String convertParams = convertParams(FlinkVersion.getVersion(specialTask.getComponentVersion()),
                FlinkVersion.getVersion(taskVO.getComponentVersion()),
                taskVO.getTaskParams(), taskVO.getTaskType());
        taskVO.setTaskParams(convertParams);
//        //由于密码脱敏，脚本模式保存时密码变成"******"，进行按照原储存信息进行还原，依据是url+username todo 问月白要不要脱敏
//        if (Objects.nonNull(specialTask.getCreateModel())
//                && CREATE_MODEL_TEMPLATE == specialTask.getCreateModel()
//                && Objects.equals(specialTask.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
//            String sqlText = TaskUtils.resumeTemplatePwd(taskVO.getSqlText(), specialTask);
//            taskVO.setSqlText(sqlText);
//        }
        Task specialTask1 = new Task();
        TaskMapstructTransfer.INSTANCE.taskVOTOTask(taskVO, specialTask1);
        developTaskMapper.updateById(specialTask1);
    }
    /**
     * 新增任务
     *
     * @param taskVO 任务信息
     */
    private void addTask(TaskVO taskVO) {
        taskVO.setJobId(actionService.generateUniqueSign());
        taskVO.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        taskVO.setTaskParams(taskVO.getTaskParams() == null ? taskTemplateService.getTaskTemplate(TaskTemplateType.TASK_PARAMS.getType(), taskVO.getTaskType(), taskVO.getComponentVersion()).getContent() : taskVO.getTaskParams());
        taskVO.setTenantId(taskVO.getTenantId());
        taskVO.setScheduleStatus(EScheduleStatus.NORMAL.getVal());
        taskVO.setScheduleConf(StringUtils.isBlank(taskVO.getScheduleConf()) ? DEFAULT_SCHEDULE_CONF : taskVO.getScheduleConf());
        taskVO.setVersion(Objects.isNull(taskVO.getVersion()) ? 0 : taskVO.getVersion());
        taskVO.setSqlText(createAnnotationText(taskVO));
        taskVO.setMainClass(Objects.isNull(taskVO.getMainClass()) ? "" : taskVO.getMainClass());
        taskVO.setTaskDesc(Objects.isNull(taskVO.getTaskDesc()) ? "" : taskVO.getTaskDesc());
        taskVO.setSubmitStatus(0);
        developTaskMapper.insert(taskVO);
    }
    /**
     * 创建任务
     *
     * @param task
     * @return
     */
    private String createAnnotationText(TaskVO task) {
        if (StringUtils.isNotBlank(task.getSqlText())) {
            return task.getSqlText();
        }
        String ENTER = "\n";
        String NOTE_SIGN;

        StringBuilder sb = new StringBuilder();
        if (shouldNoteSqlTypes.contains(task.getTaskType())) {
            NOTE_SIGN = "-- ";
        } else {
            sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());
            return sb.toString();
        }
        String type = EScheduleJobType.getByTaskType(task.getTaskType()).getName();
        //包括任务名称、任务类型、作者、创建时间、描述；
        sb.append(NOTE_SIGN).append("name ").append(task.getName()).append(ENTER);
        sb.append(NOTE_SIGN).append("type ").append(type).append(ENTER);
        sb.append(NOTE_SIGN).append("author ").append(userService.getUserName(task.getCreateUserId())).append(ENTER);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(NOTE_SIGN).append("create time ").append(sdf.format(System.currentTimeMillis())).append(ENTER);
        sb.append(NOTE_SIGN).append("desc ").append(StringUtils.isBlank(task.getTaskDesc()) ? "" : task.getTaskDesc().replace(ENTER, " ")).append(ENTER);
        sb.append(StringUtils.isBlank(task.getSqlText()) ? "" : task.getSqlText());
        return sb.toString();
    }
    /**
     * 转化环境参数，不同版本之间切换需要刷新环境参数信息
     *
     * @param before       转化前的 flink 版本
     * @param after        转化后的 flink 版本
     * @param paramsBefore 环境参数
     * @return 转化后的环境参数
     */
    private String convertParams(FlinkVersion before, FlinkVersion after, String paramsBefore, Integer taskType) {
        // 版本一致不需要进行转换
        if (before.equals(after)) {
            return paramsBefore;
        }
        return taskTemplateService.getTaskTemplate(TaskTemplateType.TASK_PARAMS.getType(), taskType, after.getType()).getContent();
    }

}
