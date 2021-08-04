const RDOS_BASE_URL = '/api/rdos'

export default {
    // ===== 运维中心模块 ===== //
    QUERY_TASKS: `/node/scheduleTaskShade/queryTasks`, // 任务管理 - 搜索-x
    GET_TASK_CHILDREN: `/node/scheduleTaskTaskShade/displayOffSpring`, // 获取任务自己节点-x
    FIND_TASK_RULE_JOB: `/node/scheduleJob/findTaskRuleJob`, // 获取补数据、周期 hover 信息-x
    FIND_TASK_RULE_TASK: `/node/scheduleTaskShade/findTaskRuleTask`, // 获取任务hover信息-x
    GET_TASK_LOG: `/node/action/log/unite`, // 获取任务告警日志-x
    GET_TASK_TYPESX: `/node/component/getSupportJobTypes`, // 获取任务类型-x新
    FROZEN_TASK: `/node/scheduleTaskShade/frozenTask`, // 冻结/解冻任务-x
    QUERY_JOBS: `/node/scheduleJob/queryJobs`, // 任务运维 - 补数据搜索
    GET_JOB_GRAPH: `/node/scheduleJob/getJobGraph`, // 今天、昨天、月平均折线图数据-x
    GET_JOB_TOP_TIME: `/node/scheduleJob/runTimeTopOrder`, // 离线任务运行时长top排序-x
    GET_JOB_TOP_ERROR: `/node/scheduleJob/errorTopOrder`, // 离线任务错误top排序-x
    PATCH_TASK_DATA: `/node/scheduleJob/fillTaskData`, // 补数据-x
    STOP_JOB: `/node/scheduleJob/stopJob`, // 停止任务-x
    BATCH_STOP_JOBS: `/node/scheduleJob/batchStopJobs`, // 停止任务-x
    BATCH_STOP_JOBS_BY_DATE: `/node/scheduleJob/stopJobByCondition`, // 按照业务日期杀任务-x
    RESTART_AND_RESUME_JOB: `/node/scheduleJob/syncRestartJob`, // 重启并恢复任务-x
    BATCH_RESTART_AND_RESUME_JOB: `/node/scheduleJob/restartJobAndResume`, // 批量重启-x
    GET_FILL_DATA: `/node/scheduleJob/getFillDataJobInfoPreview`, // 获取补数据-x
    GET_FILL_DATA_DETAIL: `/node/scheduleJob/getJobGetFillDataDetailInfo`, // 获取补数据详情-x
    GET_JOB_CHILDREN: `/node/scheduleJobJob/displayOffSpring`, // 获取子job-x
    GET_TASK_PERIODS: `/node/scheduleJob/displayPeriods`, // 转到前后周期实例-x
    GET_JOB_PARENT: `/node/scheduleJobJob/displayForefathers`, // 获取父节点-x
    QUERY_JOB_STATISTICS: `/node/scheduleJob/queryJobsStatusStatistics`, // 查询Job统计-x
    STOP_FILL_DATA_JOBS: `/node/scheduleJob/stopFillDataJobs`, // 停止补数据任务-x
    GET_RESTART_JOBS: `/node/scheduleJob/getRestartChildJob`, // 获取restart job列表-x
    GET_APPTYPE: `/node/action/appType`,
    GET_WORKFLOW_RELATED_TASKS: `node/scheduleTaskShade/dealFlowWorkTask`, // 获取工作流的子任务-x
    GET_WORKFLOW_RELATED_JOBS: `/node/scheduleJob/getRelatedJobs`, // 获取工作流实例的子任务-x
    GET_PROJECT_LIST: `/node/project/findFuzzyProjectByProjectAlias`, // 根据别名模糊查询项目名称

    // 202105运维中心新增接口
    USER_QUERYUSER: `/node/user/queryUser`, // 获取负责人

    // 使用离线的接口
    GET_WORKFLOW_FILLDATA_RELATED_JOBS: `/api/rdos/batch/batchJob/getRelatedJobsForFillData`, // 补数据工作流子节点
    GET_TASK: `${RDOS_BASE_URL}/batch/batchTask/getTaskById`, // 获取任务通过任务ID
    GET_TASK_JOB_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchJobJob/displayOffSpringWorkFlow`, // 获取工作流节点
    GET_TASK_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchTaskTaskShade/getAllFlowSubTasks`, // 获取工作流节点
    STATISTICS_TASK_RUNTIME: `${RDOS_BASE_URL}/batch/batchJob/statisticsTaskRecentInfo`, // 统计任务运行信息
    OPERA_RECORD_DATA: `${RDOS_BASE_URL}/batch/batchTaskRecord/queryRecords` // 操作记录
}
