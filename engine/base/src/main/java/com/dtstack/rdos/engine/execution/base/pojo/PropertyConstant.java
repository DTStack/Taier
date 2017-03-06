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
}
