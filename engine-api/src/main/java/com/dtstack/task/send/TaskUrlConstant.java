package com.dtstack.task.send;

/**
 * @author yuebai
 * @date 2019-10-28
 */
public class TaskUrlConstant {

    public static final String BATCH_JOB_GET_STATUS_COUNT = "/api/task/service/batchJob/getStatusCount";
    public static final String BATCH_JOB_GET_STATUS_JOB_LIST = "/api/task/service/batchJob/getStatusJobList";
    public static final String BATCH_JOB_JOBGRAPH = "/api/task/service/batchJob/getJobGraph";
    public static final String BATCH_JOB_ERROR_TOP_ORDER = "/api/task/service/batchJob/errorTopOrder";
    public static final String BATCH_JOB_RUNTIME_TOP_ORDER = "/api/task/service/batchJob/runTimeTopOrder";
    public static final String BATCH_JOB_QUERY_JOBS = "/api/task/service/batchJob/queryJobs";
    public static final String BATCH_JOB_QUERY_JOBS_STATUS_STATISTICS = "/api/task/service/batchJob/queryJobsStatusStatistics";
    public static final String BATCH_JOB_BATCH_STOP_JOBS = "/api/task/service/batchJob/batchStopJobs";
    public static final String BATCH_JOB_GET_FILL_DATAJOB_INFO_PREVIEW = "/api/task/service/batchJob/getFillDataJobInfoPreview";
    public static final String BATCH_JOB_GET_STOP_FILLDATA_JOBS = "/api/task/service/batchJob/stopFillDataJobs";
    public static final String BATCH_JOB_GET_FILL_DATA_DETAIL_INFO = "/api/task/service/batchJob/getFillDataDetailInfo";
    public static final String BATCH_JOB_STATISTICS_TASK_RECENTINFO = "/api/task/service/batchJob/statisticsTaskRecentInfo";
    public static final String BATCH_JOB_GENERAL_COUNT = "/api/task/service/batchJob/generalCount";
    public static final String BATCH_JOB_GENERAL_COUNT_WITHMINANDHOUR = "/api/task/service/batchJob/generalCountWithMinAndHour";
    public static final String BATCH_JOB_GENERAL_QUERY = "/api/task/service/batchJob/generalQuery";
    public static final String BATCH_JOB_GENERAL_QUERY_WITHMINANDHOUR = "/api/task/service/batchJob/generalQueryWithMinAndHour";
    public static final String BATCH_JOB_BATCH_UPDATE = "/api/task/service/batchJob/BatchJobsBatchUpdate";
    public static final String BATCH_JOB_GET_BY_ID = "/api/task/service/batchJob/getById";
    public static final String BATCH_JOB_GET_BY_IDS = "/api/task/service/batchJob/getByIds";
    public static final String BATCH_JOB_GET_BY_JOBS_ID = "/api/task/service/batchJob/getByJobId";
    public static final String BATCH_JOB_GET_ALL_CHILD_JOB_WITH_SAME_DAY = "/api/task/service/batchJob/getSameDayChildJob";
    public static final String BATCH_JOB_GET_LAST_SUCCESS_JOB = "/api/task/service/batchJob/getLastSuccessJob";
    public static final String BATCH_JOB_GET_FILL_TASK_DATA = "/api/task/service/batchJob/fillTaskData";
    public static final String BATCH_JOB_GET_SET_ALOGRITHM_LAB_LOG = "/api/task/service/batchJob/setAlogrithmLabLog";
    public static final String BATCH_JOB_GET_COUNT_SCIENCE_JOBSTATUS = "/api/task/service/batchJob/countScienceJobStatus";
    public static final String BATCH_JOB_GET_GET_RESTART_CHILDJOB = "/api/task/service/batchJob/getRestartChildJob";
    public static final String BATCH_JOB_STOP_JOB = "/api/task/service/batchJob/stopJob";
    public static final String BATCH_JOB_GET_GETRELATEDJOBS_FORFILLDATA = "/api/task/service/batchJob/getRelatedJobsForFillData";
    public static final String BATCH_JOB_GET_UPDATEJOB_STATUS_LOGINFO = "/api/task/service/batchJob/updateJobStatusAndLogInfo";
    public static final String BATCH_JOB_GET_GET_RELATEDJOBS = "/api/task/service/batchJob/getRelatedJobs";
    public static final String BATCH_JOB_GET_MINORHOUR_JOBQUERY = "/api/task/service/batchJob/minOrHourJobQuery";
    public static final String BATCH_JOB_GET_DISPLAY_PERIODS = "/api/task/service/batchJob/displayPeriods";
    public static final String BATCH_JOB_GET_GETLABTASKRELATIONMAP = "/api/task/service/batchJob/getLabTaskRelationMap";
    public static final String BATCH_JOB_GET_LISTJOBID_BYTASKNAMEANDSTATUS_LIST = "/api/task/service/batchJob/listJobIdByTaskNameAndStatusList";
    public static final String BATCH_JOB_GET_GETSCIENCEJOBGRAPH = "/api/task/service/batchJob/getScienceJobGraph";
    public static final String BATCH_JOB_JOBDETAIL = "/api/task/service/batchJob/jobDetail";
    public static final String BATCH_JOB_LIST_BY_BUSINESS_DATE_AND_PERIODTYPE_AND_STATUSLIST = "/api/task/service/batchJob/listByBusinessDateAndPeriodTypeAndStatusList";

    public static final String BATCH_TASK_DISPLAY_OFFSPRING = "/api/task/service/batchTask/displayOffSpring";


    public static final String BATCH_TASK_QUERY_TASKS = "/api/task/service/batchTaskShade/queryTasks";
    public static final String BATCH_TASK_FILL_TASK_DATA = "/api/task/service/batchTaskShade/fillTaskData";
    public static final String BATCH_TASK_FROZEN_TASK = "/api/task/service/batchTaskShade/frozenTask";
    public static final String BATCH_TASK_SHADE_BY_ID = "/api/task/service/batchTaskShade/findId";
    public static final String BATCH_TASK_SHADE_BY_TASK_ID = "/api/task/service/batchTaskShade/findTaskId";
    public static final String BATCH_TASK_SHADE_BY_TASK_IDS = "/api/task/service/batchTaskShade/findTaskIds";
    public static final String BATCH_TASK_SHADE_DEAL_FLOW_WORK_TASK = "/api/task/service/batchTaskShade/dealFlowWorkTask";
    public static final String BATCH_TASK_SHADE_GETTASK_BYIDS = "/api/task/service/batchTaskShade/getTaskByIds";
    public static final String BATCH_TASK_SHADE_DELETE_TASK = "/api/task/service/batchTaskShade/deleteTask";
    public static final String BATCH_TASK_SHADE_GET_TASKS_BY_NAME = "/api/task/service/batchTaskShade/getTasksByName";
    public static final String BATCH_TASK_SHADE_GET_FLOWWORK_SUBTASKS = "/api/task/service/batchTaskShade/getFlowWorkSubTasks";
    public static final String BATCH_TASK_SHADE_UPDATE_TASK_NAME = "/api/task/service/batchTaskShade/updateTaskName";
    public static final String BATCH_TASK_SHADE_ADD_OR_UPDATE = "/api/task/service/batchTaskShade/addOrUpdate";
    public static final String BATCH_TASK_SHADE_PAGEQUERY = "/api/task/service/batchTaskShade/pageQuery";
    public static final String BATCH_TASK_SHADE_LIST_DEPENDENCY_TASK = "/api/task/service/batchTaskShade/listDependencyTask";
    public static final String BATCH_TASK_SHADE_LIST_BYTASKIDS_NOTIN = "/api/task/service/batchTaskShade/listByTaskIdsNotIn";


    public static final String BATCH_TASK_TASK_SHADE_DISPLAY_OFF_SPRING = "/api/task/service/batchTaskTaskShade/displayOffSpring";
    public static final String BATCH_TASK_TASK_SHADE_SAVE_TASK_LIST = "/api/task/service/batchTaskTaskShade/saveTaskTaskList";
    public static final String BATCH_TASK_TASK_SHADE_CLEAR_DATA_BY_TASKID = "/api/task/service/batchTaskTaskShade/clearDataByTaskId";
    public static final String BATCH_TASK_TASK_SHADE_GET_ALL_PARENT_TASK = "/api/task/service/batchTaskTaskShade/getAllParentTask";
    public static final String BATCH_TASK_TASK_SHADE_GET_ALL_FLOWSUBTASKS = "/api/task/service/batchTaskTaskShade/getAllFlowSubTasks";

    public static final String SEND_TASK_INFO = "/api/task/service/batchTaskShade/info";


    public static final String BATCH_JOB_JOB_DISPLAY_FOREFATHERS = "/api/task/service/batchJobJob/displayForefathers";
    public static final String BATCH_JOB_JOB_DISPLAY_OFFSPRING = "/api/task/service/batchJobJob/displayOffSpring";
    public static final String BATCH_JOB_JOB_DISPLAYOFF_SPRINGWORK_FLOW = "/api/task/service/batchJobJob/displayOffSpringWorkFlow";



    public static final String BATCH_ALARM_CREARE = "/api/task/service/batchAlarm/createAlarm";
    public static final String BATCH_ALARM_DELETE = "/api/task/service/batchAlarm/deleteAlarm";
    public static final String BATCH_ALARM_GET_BY_ID = "/api/task/service/batchAlarm/getById";
    public static final String BATCH_ALARM_UPDATE_ALARM = "/api/task/service/batchAlarm/updateAlarm";
    public static final String BATCH_ALARM_UPDATE = "/api/task/service/batchAlarm/update";
    public static final String BATCH_ALARM_GET_ALARM_LIST = "/api/task/service/batchAlarm/getAlarmList";
    public static final String BATCH_ALARM_GET_ALARM_RECORD_LIST = "/api/task/service/batchAlarm/getAlarmRecordList";
    public static final String BATCH_ALARM_COUNT_ALRAM = "/api/task/service/batchAlarm/countAlarm";
    public static final String BATCH_ALARM_SAVE_BATCHJOB_ALARM = "/api/task/service/batchAlarm/saveBatchJobAlarm";
    public static final String BATCH_ALARM_LIST_BY_TASK_ID = "/api/task/service/batchAlarm/listByTaskId";
    public static final String BATCH_ALARM_DELETE_ALARM_BYTASK = "/api/task/service/batchAlarm/deleteAlarmByTask";
    public static final String BATCH_ALARM_GETBYNAME_AND_PROJECTID = "/api/task/service/batchAlarm/getByNameAndProjectId";
    public static final String BATCH_ALARM_FORMAT_RECORD_USER = "/api/task/service/batchAlarm/formatRecordUser";


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
