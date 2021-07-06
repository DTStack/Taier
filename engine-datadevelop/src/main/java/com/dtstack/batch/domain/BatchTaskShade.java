package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchTaskShade extends TenantProjectEntity {

    /**
     * '任务名称'
     */
    private String name;

    /**
     * '任务类型 0 sql，1 mr' 2 sync
     */
    private int taskType;

    /**
     * '计算类型 0实时，1 离线'
     */
    private int computeType;

    /**
     * '执行引擎类型 0 flink, 1 spark'
     */
    private int engineType;

    /**
     * 'sql 文本'
     */
    private String sqlText;

    /**
     * '任务参数'
     */
    private String taskParams;

    /**
     * 调度配置
     */
    private String scheduleConf;


    /**
     * 调度状态
     */
    private int scheduleStatus;

    private int submitStatus;

    /**
     * 最后修改task的用户
     */
    private long modifyUserId;

    /**
     * 新建task的用户
     */
    private Long createUserId;

    /**
     * 'task版本'
     */
    private int version;

    private long nodePid;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 入口类
     */
    private String mainClass;

    private String exeArgs;

    /**
     * 任务ID
     */
    private Long taskId;
}
