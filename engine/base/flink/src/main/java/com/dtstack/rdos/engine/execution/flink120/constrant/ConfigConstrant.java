package com.dtstack.rdos.engine.execution.flink120.constrant;


/**
 * 
 * @author sishu.yss
 *
 */
public class ConfigConstrant {
	
    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "flinkCheckpointInterval";

    public static final String FLINK_CHECKPOINT_MODE_KEY = "flinkCheckpointMode";

    public static final String FLINK_CHECKPOINT_TIMEOUT_KEY = "flinkCheckpointTimeout";

    public static final String FLINK_MAXCONCURRENTCHECKPOINTS_KEY = "maxConcurrentCheckpoints";

    public static final String FLINK_CHECKPOINT_CLEANUPMODE_KEY = "flinkCheckpointCleanupmode";

    public static final String FLINK_CHECKPOINT_DATAURI_KEY = "flinkCheckpointDataURI";
    
    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";
    
    public static final String SQL_MAX_ENV_PARALLELISM = "sql.max.env.parallelism";
    
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    
    public static final String SQL_BUFFER_TIMEOUT_MILLIS = "sql.buffer.timeout.millis";

}
