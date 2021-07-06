package com.dtstack.batch.enums;

/**
 * 数据同步写入模式
 *
 * @author sanyue
 * @date 2019/1/17
 */
public enum SyncWriteMode {

    /**
     * replace
     */
    REPLACE("replace"),

    /**
     * insert
     */
    INSERT("insert"),

    /**
     * overwrite
     */
    HIVE_OVERWRITE("overwrite"),

    /**
     * append
     */
    HIVE_APPEND("append");

    private String mode;

    SyncWriteMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static String tranferHiveMode(String writeMode) {
        if (writeMode != null) {
            if (writeMode.equalsIgnoreCase(SyncWriteMode.REPLACE.getMode())) {
                writeMode = HIVE_OVERWRITE.getMode();
            } else if (writeMode.equalsIgnoreCase(SyncWriteMode.INSERT.getMode())) {
                writeMode = HIVE_APPEND.getMode();
            }
        }
        return writeMode;
    }
}
