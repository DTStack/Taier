package com.dtstack.taier.datasource.plugin.kylinRestful.http;

public interface HttpAPI {

    /**
     * 认证
     */
    String AUTH = "/kylin/api/user/authentication";

    /**
     * 获取project
     */
    String PROJECT_LIST = "/kylin/api/projects/readable";

    /**
     * 获取kylin 表
     */
    String HIVE_TABLES = "/kylin/api/tables?ext=true&project=%s";

    /**
     * 获取hive 表详情 {project}/{tableName}
     */
    String HIVE_A_TABLE = "/kylin/api/tables/%s/%s";

    /**
     * Show databases in hive
     */
    String HIVE_LIST_ALL_DB = "/kylin/api/tables/hive?project=%s";

    /**
     * databases && project
     */
    String HIVE_LIST_ALL_TABLES = "/kylin/api/tables/hive/%s?project=%s";

    /**
     * 获取job list
     */
    String GET_JOB_LIST = "/kylin/api/jobs?cubeName=%s&limit=%s&offset=0&projectName=%s&timeFilter=1";

    /**
     * 取消job
     */
    String DISCARD_JOB = "/kylin/api/jobs/%s/cancel";

    /**
     * 新建job
     */
    String BUILD_CUBE = "/kylin/api/cubes/%s/build";

    /**
     * 恢复job
     */
    String RESUME_JOB = "/kylin/api/jobs/%s/resume";

    /**
     * 获取job的状态
     */
    String GET_STATUS = "/kylin/api/jobs/%s";

    /**
     * 获取执行步骤输出
     */
    String GET_STEP_OUTPUT = "/kylin/api/jobs/%s/steps/%s/output";
}