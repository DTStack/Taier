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
    
    public static final String ENV_PARALLELISM = "env.parallelism";
    
    public static final String MAX_ENV_PARALLELISM = "max.env.parallelism";
    
    public static final String JOB_PARALLELISM = "job.parallelism";
    
    public static final String BUFFER_TIMEOUT_MILLIS = "buffer.timeout.millis";

}
