const RDOS_BASE_URL = '/api/rdos'

export default {
    // 使用离线的接口
    GET_TASK_JOB_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchJobJob/displayOffSpringWorkFlow`, // 获取工作流节点
    GET_WORKFLOW_FILLDATA_RELATED_JOBS: `/api/rdos/batch/batchJob/getRelatedJobsForFillData`, // 补数据工作流子节点
    GET_TASK_WORKFLOW_NODES: `${RDOS_BASE_URL}/batch/batchTaskTaskShade/getAllFlowSubTasks` // 获取工作流节点
}
