package com.dtstack.engine.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuemo
 * @company www.dtstack.com
 * @Date 2020-03-29
 */
public class ApiUrlUtil {

    private static Map<String, String> apiUrlMap;

    public static String getApiUrl(String urlKey) {
        return apiUrlMap.get(urlKey);
    }

    static {
        apiUrlMap = new ConcurrentHashMap<String, String>(){{
            put("BatchJob_getStatusCount", ApiURL.BATCH_JOB_GET_STATUS_COUNT);
            put("BatchJob_getStatusJobList", ApiURL.BATCH_JOB_GET_STATUS_JOB_LIST);
            put("BatchJob_getJobGraph", ApiURL.BATCH_JOB_JOBGRAPH);
            put("BatchJob_errorTopOrder", ApiURL.BATCH_JOB_ERROR_TOP_ORDER);
            put("BatchJob_runTimeTopOrder", ApiURL.BATCH_JOB_RUNTIME_TOP_ORDER);
            put("BatchJob_queryJobs", ApiURL.BATCH_JOB_QUERY_JOBS);
            put("BatchJob_queryJobsStatusStatistics", ApiURL.BATCH_JOB_QUERY_JOBS_STATUS_STATISTICS);
            put("BatchJob_batchStopJobs", ApiURL.BATCH_JOB_BATCH_STOP_JOBS);
            put("BatchJob_getFillDataJobInfoPreview", ApiURL.BATCH_JOB_GET_FILL_DATAJOB_INFO_PREVIEW);
            put("BatchJob_stopFillDataJobs", ApiURL.BATCH_JOB_GET_STOP_FILLDATA_JOBS);
            put("BatchJob_getFillDataDetailInfo", ApiURL.BATCH_JOB_GET_FILL_DATA_DETAIL_INFO);
            put("BatchJob_statisticsTaskRecentInfo", ApiURL.BATCH_JOB_STATISTICS_TASK_RECENTINFO);
            put("BatchJob_generalCount", ApiURL.BATCH_JOB_GENERAL_COUNT);
            put("BatchJob_generalCountWithMinAndHour", ApiURL.BATCH_JOB_GENERAL_COUNT_WITHMINANDHOUR);
            put("BatchJob_generalQuery", ApiURL.BATCH_JOB_GENERAL_QUERY);
            put("BatchJob_generalQueryWithMinAndHour", ApiURL.BATCH_JOB_GENERAL_QUERY_WITHMINANDHOUR);
            put("BatchJob_batchJobsBatchUpdate", ApiURL.BATCH_JOB_BATCH_UPDATE);
            put("BatchJob_getById", ApiURL.BATCH_JOB_GET_BY_ID);
            put("BatchJob_getByIds", ApiURL.BATCH_JOB_GET_BY_IDS);
            put("BatchJob_getByJobId", ApiURL.BATCH_JOB_GET_BY_JOBS_ID);
            put("BatchJob_getSameDayChildJob", ApiURL.BATCH_JOB_GET_ALL_CHILD_JOB_WITH_SAME_DAY);
            put("BatchJob_getLastSuccessJob", ApiURL.BATCH_JOB_GET_LAST_SUCCESS_JOB);
            put("BatchJob_fillTaskData", ApiURL.BATCH_JOB_GET_FILL_TASK_DATA);
            put("BatchJob_setAlogrithmLabLog", ApiURL.BATCH_JOB_GET_SET_ALOGRITHM_LAB_LOG);
            put("BatchJob_countScienceJobStatus", ApiURL.BATCH_JOB_GET_COUNT_SCIENCE_JOBSTATUS);
            put("BatchJob_getRestartChildJob", ApiURL.BATCH_JOB_GET_GET_RESTART_CHILDJOB);
            put("BatchJob_stopJob", ApiURL.BATCH_JOB_STOP_JOB);
            put("BatchJob_getRelatedJobsForFillData", ApiURL.BATCH_JOB_GET_GETRELATEDJOBS_FORFILLDATA);
            put("BatchJob_updateJobStatusAndLogInfo", ApiURL.BATCH_JOB_GET_UPDATEJOB_STATUS_LOGINFO);
            put("BatchJob_getRelatedJobs", ApiURL.BATCH_JOB_GET_GET_RELATEDJOBS);
            put("BatchJob_minOrHourJobQuery", ApiURL.BATCH_JOB_GET_MINORHOUR_JOBQUERY);
            put("BatchJob_displayPeriods", ApiURL.BATCH_JOB_GET_DISPLAY_PERIODS);
            put("BatchJob_getLabTaskRelationMap", ApiURL.BATCH_JOB_GET_GETLABTASKRELATIONMAP);
            put("BatchJob_listJobIdByTaskNameAndStatusList", ApiURL.BATCH_JOB_GET_LISTJOBID_BYTASKNAMEANDSTATUS_LIST);
            put("BatchJob_getScienceJobGraph", ApiURL.BATCH_JOB_GET_GETSCIENCEJOBGRAPH);
            put("BatchJob_jobDetail", ApiURL.BATCH_JOB_JOBDETAIL);
            put("BatchJob_listByBusinessDateAndPeriodTypeAndStatusList", ApiURL.BATCH_JOB_LIST_BY_BUSINESS_DATE_AND_PERIODTYPE_AND_STATUSLIST);

            put("BatchTask_displayOffSpring", ApiURL.BATCH_TASK_DISPLAY_OFFSPRING);

            put("BatchTaskShade_queryTasks", ApiURL.BATCH_TASK_QUERY_TASKS);
            put("BatchTaskShade_fillTaskData", ApiURL.BATCH_TASK_FILL_TASK_DATA);
            put("BatchTaskShade_frozenTask", ApiURL.BATCH_TASK_FROZEN_TASK);
            put("BatchTaskShade_findId", ApiURL.BATCH_TASK_SHADE_BY_ID);
            put("BatchTaskShade_findTaskId", ApiURL.BATCH_TASK_SHADE_BY_TASK_ID);
            put("BatchTaskShade_findTaskIds", ApiURL.BATCH_TASK_SHADE_BY_TASK_IDS);
            put("BatchTaskShade_dealFlowWorkTask", ApiURL.BATCH_TASK_SHADE_DEAL_FLOW_WORK_TASK);
            put("BatchTaskShade_getTaskByIds", ApiURL.BATCH_TASK_SHADE_GETTASK_BYIDS);
            put("BatchTaskShade_deleteTask", ApiURL.BATCH_TASK_SHADE_DELETE_TASK);
            put("BatchTaskShade_getTasksByName", ApiURL.BATCH_TASK_SHADE_GET_TASKS_BY_NAME);
            put("BatchTaskShade_getFlowWorkSubTasks", ApiURL.BATCH_TASK_SHADE_GET_FLOWWORK_SUBTASKS);
            put("BatchTaskShade_updateTaskName", ApiURL.BATCH_TASK_SHADE_UPDATE_TASK_NAME);
            put("BatchTaskShade_addOrUpdate", ApiURL.BATCH_TASK_SHADE_ADD_OR_UPDATE);
            put("BatchTaskShade_pageQuery", ApiURL.BATCH_TASK_SHADE_PAGEQUERY);
            put("BatchTaskShade_listDependencyTask", ApiURL.BATCH_TASK_SHADE_LIST_DEPENDENCY_TASK);
            put("BatchTaskShade_listByTaskIdsNotIn", ApiURL.BATCH_TASK_SHADE_LIST_BYTASKIDS_NOTIN);

            put("BatchTaskTaskShade_displayOffSpring", ApiURL.BATCH_TASK_TASK_SHADE_DISPLAY_OFF_SPRING);
            put("BatchTaskTaskShade_saveTaskTaskList", ApiURL.BATCH_TASK_TASK_SHADE_SAVE_TASK_LIST);
            put("BatchTaskTaskShade_clearDataByTaskId", ApiURL.BATCH_TASK_TASK_SHADE_CLEAR_DATA_BY_TASKID);
            put("BatchTaskTaskShade_getAllParentTask", ApiURL.BATCH_TASK_TASK_SHADE_GET_ALL_PARENT_TASK);
            put("BatchTaskTaskShade_getAllFlowSubTasks", ApiURL.BATCH_TASK_TASK_SHADE_GET_ALL_FLOWSUBTASKS);

            put("BatchTaskShade_info", ApiURL.SEND_TASK_INFO);

            put("BatchJobJob_displayForefathers", ApiURL.BATCH_JOB_JOB_DISPLAY_FOREFATHERS);
            put("BatchJobJob_displayOffSpring", ApiURL.BATCH_JOB_JOB_DISPLAY_OFFSPRING);
            put("BatchJobJob_displayOffSpringWorkFlow", ApiURL.BATCH_JOB_JOB_DISPLAYOFF_SPRINGWORK_FLOW);

            put("Project_updateSchedule", ApiURL.PROJECT_SCHEDULE);
        }};
    }

}
