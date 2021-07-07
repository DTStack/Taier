package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchTask;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/5
 */
@Data
public class TaskResourceParam extends BatchTask {

    private Long userId;

    private List<Long> resourceIdList;

    private List<Long> refResourceIdList;

    private boolean preSave;

    private Map<String, Object> sourceMap;

    private Map<String, Object> targetMap;

    private Map<String, Object> settingMap;

    private List<BatchTask> dependencyTasks;

    private String publishDesc;

    private int lockVersion;

    private List<Map> taskVariables;

    private Long dataSourceId;

    /**
     * 0-向导模式,1-脚本模式
     */
    private int createModel;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private int operateModel = 1;

    /**
     * 同步模式 0-无增量标识，1-有增量标识
     */
    private int syncModel;

    /**
     * 2-python2.x,3-python3.x
     */
    private int pythonVersion;

    /**
     * 0-TensorFlow,1-MXNet
     */
    private int learningType;

    /**
     * 输入数据文件的路径
     */
    private String input;

    /**
     * 输出模型的路径
     */
    private String output;

    /**
     * 脚本的命令行参数
     */
    private String options;

    /**
     * 任务流中待更新的子任务
     */
    private List<TaskResourceParam> toUpdateTasks;

    /**
     * 是否是右键编辑任务
     */
    private Boolean isEditBaseInfo = false;

    /**
     * 工作流父任务版本号  用于子任务获取父任务锁
     */
    private Integer parentReadWriteLockVersion ;

    private ReadWriteLockVO readWriteLockVO;


}
