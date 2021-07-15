package com.dtstack.batch.web.task.vo.result;

import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import com.dtstack.batch.web.task.vo.result.BatchResourceResultVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskVersionDetailResultVO;
import com.dtstack.batch.web.task.vo.result.ReadWriteLockResultVO;
import com.dtstack.batch.web.user.vo.result.BatchUserGetTaskByIdResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("任务信息")
public class BatchTaskGetTaskByIdResultVO {

    @ApiModelProperty(value = "任务周期 ID", example = "1")
    protected Integer taskPeriodId;

    @ApiModelProperty(value = "任务周期类别", example = "2")
    protected String taskPeriodType;

    @ApiModelProperty(value = "节点名称", example = "数据开发")
    private String nodePName;

    @ApiModelProperty(value = "用户 ID", example = "2")
    private Long userId;

    @ApiModelProperty(value = "锁版本", example = "11")
    private Integer lockVersion;

    @ApiModelProperty(value = "任务参数")
    private List<Map> taskVariables;

    @ApiModelProperty(value = "是否覆盖更新", example = "true")
    private Boolean forceUpdate = false;

    @ApiModelProperty(value = "数据源 ID", example = "2")
    private Long dataSourceId;

    @ApiModelProperty(value = "任务信息")
    private BatchScheduleTaskResultVO subNodes;

    @ApiModelProperty(value = "任务信息")
    private List<BatchScheduleTaskResultVO> relatedTasks;

    @ApiModelProperty(value = "租户名称", example = "dev租户")
    private String tenantName;

    @ApiModelProperty(value = "项目名称", example = "dev开发")
    private String projectName;

    @ApiModelProperty(value = "创建模式 0-向导模式，1-脚本模式", example = "0")
    private Integer createModel = 0;

    @ApiModelProperty(value = "操作模式 0-资源模式，1-编辑模式", example = "1")
    private Integer operateModel = 0;

    @ApiModelProperty(value = "python版本 2-python2.x,3-python3.x式", example = "2")
    private Integer pythonVersion = 0;

    @ApiModelProperty(value = "0-TensorFlow,1-MXNet", example = "1")
    private Integer learningType = 0;

    @ApiModelProperty(value = "输入数据文件的路径", example = "/usr/opt/a")
    private String input;

    @ApiModelProperty(value = "输出模型的路径", example = "/usr/opt/a")
    private String output;

    @ApiModelProperty(value = "脚本的命令行参数", example = "")
    private String options;

    @ApiModelProperty(value = "工作流名称", example = "数据同步test")
    private String flowName;

    @ApiModelProperty(value = "同步模式", example = "2")
    private Integer syncModel = 0;

    @ApiModelProperty(value = "自增字段")
    private String increColumn;

    @ApiModelProperty(value = "是否是当前项目", example = "true")
    private Boolean currentProject = false;

    @ApiModelProperty(value = "任务信息")
    private List<BatchScheduleTaskResultVO> taskVOS;

    @ApiModelProperty(value = "任务信息")
    private List<BatchScheduleTaskResultVO> subTaskVOS;

    @ApiModelProperty(value = "定时周期表达式", example = "* 0/1 * * * *")
    protected String cron;

    @ApiModelProperty(value = "是否发布到了生产环境", example = "1")
    private Long isPublishToProduce;

    @ApiModelProperty(value = "扩展信息")
    private String extraInfo;

    @ApiModelProperty(value = "任务 ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "batchJob执行的时候的vesion版本", example = "23")
    private Integer versionId;

    @ApiModelProperty(value = "任务名称(name)必填", example = "dev_test")
    private String name;

    @ApiModelProperty(value = "任务类型(taskType)必填", example = "0")
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "job.executor:1")
    private String taskParams;

    @ApiModelProperty(value = "调度配置")
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "1")
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "2")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "启动:0 停止:1", example = "0")
    private Integer projectScheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "0")
    private Integer submitStatus;

    @ApiModelProperty(value = "最后修改task的用户", example = "5")
    private Long modifyUserId;

    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "负责人id", example = "3")
    private Long ownerUserId;

    @ApiModelProperty(value = "节点 id", example = "13")
    private Long nodePid;

    @ApiModelProperty(value = "任务描述", example = "测试任务")
    private String taskDesc;

    @ApiModelProperty(value = "入口类")
    private String mainClass;

    @ApiModelProperty(value = "启动参数")
    private String exeArgs;

    @ApiModelProperty(value = "所属工作流id", example = "32")
    private Long flowId;

    @ApiModelProperty(value = "是否过期", example = "1")
    private Integer isExpire;

    @ApiModelProperty(value = "uic 租户 ID", example = "11")
    private Long dtuicTenantId = 0L;

    @ApiModelProperty(value = "平台类型", example = "11")
    private Integer appType;

    @ApiModelProperty(value = "租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "5")
    private Long projectId;

    @ApiModelProperty(value = "主键 ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "")
    private  String EMPYT = "";

    @ApiModelProperty(value = "组件版本", example = "1.8")
    private String componentVersion;

    @ApiModelProperty(value = "")
    private List<BatchResourceResultVO> resourceList;

    @ApiModelProperty(value = "")
    private List<BatchResourceResultVO> refResourceList;

    @ApiModelProperty(value = "任务版本信息")
    private List<BatchTaskVersionDetailResultVO> taskVersions;

    @ApiModelProperty(value = "工作流父任务版本号 ")
    private Integer parentReadWriteLockVersion ;

    @ApiModelProperty(value = "读写锁信息")
    private ReadWriteLockResultVO readWriteLockVO;

    @ApiModelProperty(value = "task 版本")
    private Integer version;

    @ApiModelProperty(value = "任务创建人信息")
    private BatchUserGetTaskByIdResultVO createUser;

    @ApiModelProperty(value = "任务修改人信息")
    private BatchUserGetTaskByIdResultVO modifyUser;

    @ApiModelProperty(value = "任务责任人信息")
    private BatchUserGetTaskByIdResultVO ownerUser;

}
