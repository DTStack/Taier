package com.dtstack.engine.flink.constrant;


/**
 * 
 * @author sishu.yss
 *
 */
public class ConfigConstrant {

    public static final String SPLIT = "_";

    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    
    public static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";


    public final static String JOBMANAGER_MEMORY_MB = "jobmanager.memory.mb";
    public final static String TASKMANAGER_MEMORY_MB = "taskmanager.memory.mb";
    public final static String CONTAINER = "container";
    public final static String SLOTS = "slots";

    /**
     * the minimum memory should be higher than the min heap cutoff
     */
    public final static int MIN_JM_MEMORY = 1024;
    public final static int MIN_TM_MEMORY = 1024;
}
