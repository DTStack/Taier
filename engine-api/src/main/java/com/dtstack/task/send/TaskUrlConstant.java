package com.dtstack.task.send;

/**
 * @author yuemo
 * @date 2020-03-25
 */
public class TaskUrlConstant {

    private static final String ROOT = "/node";

    private static final String BATCH_JOB = String.format("%s/%s", ROOT, "batchJob");
    private static final String BATCH_TASK = String.format("%s/%s", ROOT, "batchTask");
    private static final String BATCH_TASK_SHADE = String.format("%s/%s", ROOT, "batchTaskShade");
    private static final String BATCH_TASK_TASK_SHADE = String.format("%s/%s", ROOT, "batchTaskTaskShade");
    private static final String BATCH_JOB_JOB = String.format("%s/%s", ROOT, "batchJobJob");
    private static final String BATCH_ALARM = String.format("%s/%s", ROOT, "batchAlarm");

    public static final String BATCH_JOB_GET_STATUS_COUNT = String.format("%s/%s", BATCH_JOB, "getStatusCount");
    public static final String BATCH_JOB_GET_STATUS_JOB_LIST = String.format("%s/%s", BATCH_JOB, "getStatusJobList");
    public static final String BATCH_JOB_JOBGRAPH = String.format("%s/%s", BATCH_JOB, "getJobGraph");
    public static final String BATCH_JOB_ERROR_TOP_ORDER = String.format("%s/%s", BATCH_JOB, "errorTopOrder");
    public static final String BATCH_JOB_RUNTIME_TOP_ORDER = String.format("%s/%s", BATCH_JOB, "runTimeTopOrder");
    public static final String BATCH_JOB_QUERY_JOBS = String.format("%s/%s", BATCH_JOB, "queryJobs");
    public static final String BATCH_JOB_QUERY_JOBS_STATUS_STATISTICS = String.format("%s/%s", BATCH_JOB, "queryJobsStatusStatistics");
    public static final String BATCH_JOB_BATCH_STOP_JOBS = String.format("%s/%s", BATCH_JOB, "batchStopJobs");
    public static final String BATCH_JOB_GET_FILL_DATAJOB_INFO_PREVIEW = String.format("%s/%s", BATCH_JOB, "getFillDataJobInfoPreview");
    public static final String BATCH_JOB_GET_STOP_FILLDATA_JOBS = String.format("%s/%s", BATCH_JOB, "stopFillDataJobs");
    public static final String BATCH_JOB_GET_FILL_DATA_DETAIL_INFO = String.format("%s/%s", BATCH_JOB, "getFillDataDetailInfo");
    public static final String BATCH_JOB_STATISTICS_TASK_RECENTINFO = String.format("%s/%s", BATCH_JOB, "statisticsTaskRecentInfo");
    public static final String BATCH_JOB_GENERAL_COUNT = String.format("%s/%s", BATCH_JOB, "generalCount");
    public static final String BATCH_JOB_GENERAL_COUNT_WITHMINANDHOUR = String.format("%s/%s", BATCH_JOB, "generalCountWithMinAndHour");
    public static final String BATCH_JOB_GENERAL_QUERY = String.format("%s/%s", BATCH_JOB, "generalQuery");
    public static final String BATCH_JOB_GENERAL_QUERY_WITHMINANDHOUR = String.format("%s/%s", BATCH_JOB, "generalQueryWithMinAndHour");
    public static final String BATCH_JOB_BATCH_UPDATE = String.format("%s/%s", BATCH_JOB, "BatchJobsBatchUpdate");
    public static final String BATCH_JOB_GET_BY_ID = String.format("%s/%s", BATCH_JOB, "getById");
    public static final String BATCH_JOB_GET_BY_IDS = String.format("%s/%s", BATCH_JOB, "getByIds");
    public static final String BATCH_JOB_GET_BY_JOBS_ID = String.format("%s/%s", BATCH_JOB, "getByJobId");
    public static final String BATCH_JOB_GET_ALL_CHILD_JOB_WITH_SAME_DAY = String.format("%s/%s", BATCH_JOB, "getSameDayChildJob");
    public static final String BATCH_JOB_GET_LAST_SUCCESS_JOB = String.format("%s/%s", BATCH_JOB, "getLastSuccessJob");
    public static final String BATCH_JOB_GET_FILL_TASK_DATA = String.format("%s/%s", BATCH_JOB, "fillTaskData");
    public static final String BATCH_JOB_GET_SET_ALOGRITHM_LAB_LOG = String.format("%s/%s", BATCH_JOB, "setAlogrithmLabLog");
    public static final String BATCH_JOB_GET_COUNT_SCIENCE_JOBSTATUS = String.format("%s/%s", BATCH_JOB, "countScienceJobStatus");
    public static final String BATCH_JOB_GET_GET_RESTART_CHILDJOB = String.format("%s/%s", BATCH_JOB, "getRestartChildJob");
    public static final String BATCH_JOB_STOP_JOB = String.format("%s/%s", BATCH_JOB, "stopJob");
    public static final String BATCH_JOB_GET_GETRELATEDJOBS_FORFILLDATA = String.format("%s/%s", BATCH_JOB, "getRelatedJobsForFillData");
    public static final String BATCH_JOB_GET_UPDATEJOB_STATUS_LOGINFO = String.format("%s/%s", BATCH_JOB, "updateJobStatusAndLogInfo");
    public static final String BATCH_JOB_GET_GET_RELATEDJOBS = String.format("%s/%s", BATCH_JOB, "getRelatedJobs");
    public static final String BATCH_JOB_GET_MINORHOUR_JOBQUERY = String.format("%s/%s", BATCH_JOB, "minOrHourJobQuery");
    public static final String BATCH_JOB_GET_DISPLAY_PERIODS = String.format("%s/%s", BATCH_JOB, "displayPeriods");
    public static final String BATCH_JOB_GET_GETLABTASKRELATIONMAP = String.format("%s/%s", BATCH_JOB, "getLabTaskRelationMap");
    public static final String BATCH_JOB_GET_LISTJOBID_BYTASKNAMEANDSTATUS_LIST = String.format("%s/%s", BATCH_JOB, "listJobIdByTaskNameAndStatusList");
    public static final String BATCH_JOB_GET_GETSCIENCEJOBGRAPH = String.format("%s/%s", BATCH_JOB, "getScienceJobGraph");
    public static final String BATCH_JOB_JOBDETAIL = String.format("%s/%s", BATCH_JOB, "jobDetail");
    public static final String BATCH_JOB_LIST_BY_BUSINESS_DATE_AND_PERIODTYPE_AND_STATUSLIST = String.format("%s/%s", BATCH_JOB, "listByBusinessDateAndPeriodTypeAndStatusList");

    public static final String BATCH_TASK_DISPLAY_OFFSPRING = String.format("%s/%s", BATCH_TASK, "displayOffSpring");

    public static final String BATCH_TASK_QUERY_TASKS = String.format("%s/%s", BATCH_TASK_SHADE, "queryTasks");
    public static final String BATCH_TASK_FILL_TASK_DATA = String.format("%s/%s", BATCH_TASK_SHADE, "fillTaskData");
    public static final String BATCH_TASK_FROZEN_TASK = String.format("%s/%s", BATCH_TASK_SHADE, "frozenTask");
    public static final String BATCH_TASK_SHADE_BY_ID = String.format("%s/%s", BATCH_TASK_SHADE, "findId");
    public static final String BATCH_TASK_SHADE_BY_TASK_ID = String.format("%s/%s", BATCH_TASK_SHADE, "findTaskId");
    public static final String BATCH_TASK_SHADE_BY_TASK_IDS = String.format("%s/%s", BATCH_TASK_SHADE, "findTaskIds");
    public static final String BATCH_TASK_SHADE_DEAL_FLOW_WORK_TASK = String.format("%s/%s", BATCH_TASK_SHADE, "dealFlowWorkTask");
    public static final String BATCH_TASK_SHADE_GETTASK_BYIDS = String.format("%s/%s", BATCH_TASK_SHADE, "getTaskByIds");
    public static final String BATCH_TASK_SHADE_DELETE_TASK = String.format("%s/%s", BATCH_TASK_SHADE, "deleteTask");
    public static final String BATCH_TASK_SHADE_GET_TASKS_BY_NAME = String.format("%s/%s", BATCH_TASK_SHADE, "getTasksByName");
    public static final String BATCH_TASK_SHADE_GET_FLOWWORK_SUBTASKS = String.format("%s/%s", BATCH_TASK_SHADE, "getFlowWorkSubTasks");
    public static final String BATCH_TASK_SHADE_UPDATE_TASK_NAME = String.format("%s/%s", BATCH_TASK_SHADE, "updateTaskName");
    public static final String BATCH_TASK_SHADE_ADD_OR_UPDATE = String.format("%s/%s", BATCH_TASK_SHADE, "addOrUpdate");
    public static final String BATCH_TASK_SHADE_PAGEQUERY = String.format("%s/%s", BATCH_TASK_SHADE, "pageQuery");
    public static final String BATCH_TASK_SHADE_LIST_DEPENDENCY_TASK = String.format("%s/%s", BATCH_TASK_SHADE, "listDependencyTask");
    public static final String BATCH_TASK_SHADE_LIST_BYTASKIDS_NOTIN = String.format("%s/%s", BATCH_TASK_SHADE, "listByTaskIdsNotIn");


    public static final String BATCH_TASK_TASK_SHADE_DISPLAY_OFF_SPRING = String.format("%s/%s", BATCH_TASK_TASK_SHADE, "displayOffSpring");
    public static final String BATCH_TASK_TASK_SHADE_SAVE_TASK_LIST = String.format("%s/%s", BATCH_TASK_TASK_SHADE, "saveTaskTaskList");
    public static final String BATCH_TASK_TASK_SHADE_CLEAR_DATA_BY_TASKID = String.format("%s/%s", BATCH_TASK_TASK_SHADE, "clearDataByTaskId");
    public static final String BATCH_TASK_TASK_SHADE_GET_ALL_PARENT_TASK = String.format("%s/%s", BATCH_TASK_TASK_SHADE, "getAllParentTask");
    public static final String BATCH_TASK_TASK_SHADE_GET_ALL_FLOWSUBTASKS = String.format("%s/%s", BATCH_TASK_TASK_SHADE, "getAllFlowSubTasks");

    public static final String SEND_TASK_INFO = String.format("%s/%s", BATCH_TASK_SHADE, "info");

    public static final String BATCH_JOB_JOB_DISPLAY_FOREFATHERS = String.format("%s/%s", BATCH_JOB_JOB, "displayForefathers");
    public static final String BATCH_JOB_JOB_DISPLAY_OFFSPRING = String.format("%s/%s", BATCH_JOB_JOB, "displayOffSpring");
    public static final String BATCH_JOB_JOB_DISPLAYOFF_SPRINGWORK_FLOW = String.format("%s/%s", BATCH_JOB_JOB, "displayOffSpringWorkFlow");

    public static final String BATCH_ALARM_CREARE = String.format("%s/%s", BATCH_ALARM, "createAlarm");
    public static final String BATCH_ALARM_DELETE = String.format("%s/%s", BATCH_ALARM, "deleteAlarm");
    public static final String BATCH_ALARM_GET_BY_ID = String.format("%s/%s", BATCH_ALARM, "getById");
    public static final String BATCH_ALARM_UPDATE_ALARM = String.format("%s/%s", BATCH_ALARM, "updateAlarm");
    public static final String BATCH_ALARM_UPDATE = String.format("%s/%s", BATCH_ALARM, "update");
    public static final String BATCH_ALARM_GET_ALARM_LIST = String.format("%s/%s", BATCH_ALARM, "getAlarmList");
    public static final String BATCH_ALARM_GET_ALARM_RECORD_LIST = String.format("%s/%s", BATCH_ALARM, "getAlarmRecordList");
    public static final String BATCH_ALARM_COUNT_ALRAM = String.format("%s/%s", BATCH_ALARM, "countAlarm");
    public static final String BATCH_ALARM_SAVE_BATCHJOB_ALARM = String.format("%s/%s", BATCH_ALARM, "saveBatchJobAlarm");
    public static final String BATCH_ALARM_LIST_BY_TASK_ID = String.format("%s/%s", BATCH_ALARM, "listByTaskId");
    public static final String BATCH_ALARM_DELETE_ALARM_BYTASK = String.format("%s/%s", BATCH_ALARM, "deleteAlarmByTask");
    public static final String BATCH_ALARM_GETBYNAME_AND_PROJECTID = String.format("%s/%s", BATCH_ALARM, "getByNameAndProjectId");
    public static final String BATCH_ALARM_FORMAT_RECORD_USER = String.format("%s/%s", BATCH_ALARM, "formatRecordUser");


    /**
     * -----extraInfo中json key------
     **/
    public static final String INFO = "info";
    public static final String ALARM_USERS = "alarmUser";


    /**
     * jobId 占位标识符
     */
    public static final String JOB_ID = "${jobId}";
    public static final String UPLOADPATH = "${uploadPath}";


}
