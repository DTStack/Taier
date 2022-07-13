package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.utils.develop.common.IDownload;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2022/7/13
 */
public abstract class HadoopTaskRunner extends JdbcTaskRunner {

    @Override
    public List<EScheduleJobType> support() {
        return null;
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String sql, Task task, String jobId) throws Exception {
        return null;
    }

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception {

    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        return null;
    }

    @Override
    public ExecuteResultVO runLogShow(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum, String logType) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        return null;
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType) {
        return null;
    }
}
