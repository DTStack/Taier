package com.dtstack.rdos.engine.execution.flink130.constrant;


/**
 * 
 * @author sishu.yss
 *
 */
public class ConfigConstrant {
	
    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "sql.checkpoint.interval";

    public static final String FLINK_CHECKPOINT_MODE_KEY = "sql.checkpoint.mode";

    public static final String FLINK_CHECKPOINT_TIMEOUT_KEY = "sql.checkpoint.timeout";

    public static final String FLINK_MAXCONCURRENTCHECKPOINTS_KEY = "sql.max.concurrent.checkpoints";

    public static final String FLINK_CHECKPOINT_CLEANUPMODE_KEY = "sql.checkpoint.cleanup.mode";

    public static final String FLINK_CHECKPOINT_DATAURI_KEY = "flinkCheckpointDataURI";
    
    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";

    /**
     * 注意如果改了该变量的话,对应的链接的地方变量也需要更改
     * {@link com.dtstack.rdos.engine.execution.base.components.SlotsJudge#FLINK_SQL_MAX_ENV_PARALLELISM}
     */
    public static final String SQL_MAX_ENV_PARALLELISM = "sql.max.env.parallelism";
    
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    
    public static final String SQL_BUFFER_TIMEOUT_MILLIS = "sql.buffer.timeout.millis";

}
