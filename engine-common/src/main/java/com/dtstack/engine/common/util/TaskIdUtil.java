package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.enums.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sishu.yss on 2017/5/15.
 */
public class TaskIdUtil {

    private static final Logger logger = LoggerFactory.getLogger(TaskIdUtil.class);

    private final static int MIN_LENGTH = 3;

    /**
     * 1: 表明是迁移到该节点的数据
     */
    private final static char MIGRATION_FLAG = '1';
    private final static char NO_MIGRATION_FLAG = '0';

    /**
     * computeType
     */
    private final static int ZERO_COMPUTETYPE = 0;
    /**
     * engineType
     */
    private final static int ONE_ENGINETYPE = 1;
    /**
     * MIGRATION
     */
    private final static int TWO_MIGRATION = 2;


    public static String getZkTaskId(int computeType, String engineType, String taskId) {
        EngineType type = EngineType.getEngineType(engineType);
        if (type == null) {
            throw new RdosException("engineType is not support", ErrorCode.INVALID_PARAMETERS);
        }
        return String.valueOf(computeType) + type.getVal() + NO_MIGRATION_FLAG + taskId;
    }

    public static String getTaskId(String zkTaskId) {
        if (zkTaskId.length() <= MIN_LENGTH) {
            logger.error("it's illegal zkTaskId {}.", zkTaskId);
            return "";
        }

        return zkTaskId.substring(MIN_LENGTH);
    }

    public static String convertToMigrationJob(String zkTaskId) {
        return changeJobMigrationStatus(zkTaskId, MIGRATION_FLAG);
    }

    public static String convertToNoMigrationJob(String zkTaskId) {
        return changeJobMigrationStatus(zkTaskId, NO_MIGRATION_FLAG);
    }

    public static String changeJobMigrationStatus(String zkTaskId, char status) {
        StringBuilder stringBuilder = new StringBuilder(zkTaskId);
        stringBuilder.setCharAt(TWO_MIGRATION, status);
        return stringBuilder.toString();
    }


    public static int getComputeType(String zkTaskId) {
        return Integer.parseInt(String.valueOf(zkTaskId.charAt(ZERO_COMPUTETYPE)));
    }

    public static String getEngineType(String zkTaskId) {
        char c = zkTaskId.charAt(ONE_ENGINETYPE);
        return EngineType.getEngineType(c).name().toLowerCase();
    }

    public static boolean isMigrationJob(String zkTaskId) {
        if (zkTaskId == null || zkTaskId.length() <= MIN_LENGTH ||
                NO_MIGRATION_FLAG == zkTaskId.charAt(TWO_MIGRATION)) {
            return false;
        }
        return true;
    }

}
