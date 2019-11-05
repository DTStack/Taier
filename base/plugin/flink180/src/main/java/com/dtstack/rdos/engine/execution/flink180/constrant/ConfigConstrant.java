package com.dtstack.rdos.engine.execution.flink180.constrant;


/**
 * 
 * @author sishu.yss
 *
 */
public class ConfigConstrant {

    public static final String SPLIT = "_";

    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "sql.checkpoint.interval";

    public static final String FLINK_CHECKPOINT_MODE_KEY = "sql.checkpoint.mode";

    public static final String FLINK_CHECKPOINT_TIMEOUT_KEY = "sql.checkpoint.timeout";

    public static final String FLINK_MAXCONCURRENTCHECKPOINTS_KEY = "sql.max.concurrent.checkpoints";

    public static final String FLINK_CHECKPOINT_CLEANUPMODE_KEY = "sql.checkpoint.cleanup.mode";

    public static final String FLINK_CHECKPOINT_DATAURI_KEY = "flinkCheckpointDataURI";

    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String SQL_MAX_ENV_PARALLELISM = "sql.max.env.parallelism";
    
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    
    public static final String SQL_BUFFER_TIMEOUT_MILLIS = "sql.buffer.timeout.millis";

    public static final String FLINK_TIME_CHARACTERISTIC_KEY = "time.characteristic";

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
    /**
     * plugin load by classpath or ship mode
     */
    public final static String FLINK_PLUGIN_LOAD_MODE = "pluginLoadMode";
    public static final String FLINK_PLUGIN_CLASSPATH_LOAD = "classpath";
    public static final String FLINK_PLUGIN_SHIPFILE_LOAD = "shipfile";
}
