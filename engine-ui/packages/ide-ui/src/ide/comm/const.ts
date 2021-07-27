/**
 * 存储项目ID的 key 名称
 */
export const PROJECT_KEY = "project_id";

// 发布的item类别
export const publishType = {
  TASK: 0,
  TABLE: 1,
  RESOURCE: 2,
  FUNCTION: 3,
  PRODUCER: 4,
};

export const TASK_TYPE = {
  // 任务类型
  VIRTUAL_NODE: -1,
  /**
   * SparkSQL
   */
  SQL: 0,
  MR: 1,
  SYNC: 2,
  PYTHON: 3,
  R: 4,
  DEEP_LEARNING: 5,
  PYTHON_23: 6,
  SHELL: 7,
  ML: 8,
  HAHDOOPMR: 9,
  WORKFLOW: 10, // 工作流
  DATA_COLLECTION: 11, // 实时采集
  CARBONSQL: 12, // CarbonSQL
  NOTEBOOK: 13,
  EXPERIMENT: 14,
  LIBRASQL: 15,
  CUBE_KYLIN: 16,
  HIVESQL: 17,
  IMPALA_SQL: 18, // ImpalaSQL
  TI_DB_SQL: 19,
  ORACLE_SQL: 20,
  GREEN_PLUM_SQL: 21,
};

export const HELP_DOC_URL = {
  INDEX: "/public/helpSite/batch/v3.0/Summary.html",
  DATA_SOURCE: "/public/helpSite/batch/v3.0/DataIntegration/Overview.html",
  DATA_SYNC: "/public/helpSite/batch/v3.0/DataIntegration/JobConfig.html",
  TASKPARAMS:
    "/public/helpSite/batch/v3.0/DataDevelop/ScheduleConfig.html#ParamConfig",
};
