package com.dtstack.rdos.engine.execution.base.pojo;

/**
 * property key 常量定义
 * Date: 2017/3/1
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class PropertyConstant {

    /**计算资源**/
    public static final String SLOTS_KEY = "slots";

    public static final String FILE_TMP_PATH_KEY = "jarTmpDir";

    /*************fink check point**********************/
    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "flinkCheckpointInterval";

    public static final String FLINK_CHECKPOINT_MODE_KEY = "flinkCheckpointMode";

    public static final String FLINK_CHECKPOINT_TIMEOUT_KEY = "flinkCheckpointTimeout";

    public static final String FLINK_MAXCONCURRENTCHECKPOINTS_KEY = "maxConcurrentCheckpoints";

    public static final String FLINK_CHECKPOINT_CLEANUPMODE_KEY = "flinkCheckpointCleanupmode";

    public static final String FLINK_CHECKPOINT_DATAURI_KEY = "flinkCheckpointDataURI";

    /**************flink client init*********************/

    public static final String FLINK_JOBMGR_URL_KEY = "engineUrl";

    public static final String FLINK_ZKNAMESPACE_KEY = "engineZkAddress";

    public static final String FLINK_ZK_ROOT_KEY = "engineZkNamespace";

    public static final String FLINK_ZK_CLUSTERID_KEY = "engineClusterId";

    /**************flink submit job info***************/

    public static final String FLINK_JOB_JAR_PATH_KEY = "jobJarPath";

    public static final String FLINK_JOB_JAR_MAINCLASS_KEY = "jobJarMainClass";

    public static final String FLINK_JOB_PARALLELISM_KEY = "flinkJobParallelism";

    public static final String FLINK_JOB_FROMSAVEPOINT_KEY = "fromSavepoint";

    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    /**************kafka connect info*****************/

    public static final String KAFKA_BOOTSTRAPSERVERS_KEY = "bootstrapServers";

    public static final String KAFKA_TOPIC_KEY = "topic";

    public static final String KAFKA_OFFSETRESET_KEY = "offsetReset";

    /*************sql connect info**********************/

    public static final String SQL_BATCH_SIZE_KEY = "sqlBatchSize";

    public static final String SQL_DB_URL_KEY = "dbURL";

    public static final String SQL_DB_USERNAME_KEY = "userName";

    public static final String SQL_DB_password_KEY = "password";

    public static final String SQL_DB_tableName_KEY = "tableName";

    public static final String SQL_DB_SINK_PARALLELISM_KEY = "dbSinkParallelism";

}
