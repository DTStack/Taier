package com.dtstack.rdos.engine.service;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Reason: 查询实时任务数据
 * Date: 2018/10/11
 * Company: www.dtstack.com
 * @author jiangbo
 */
public class StreamTaskServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(StreamTaskServiceImpl.class);

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDAO = new RdosEngineStreamJobDAO();

    /**
     * 查询checkPoint
     */
    public List<RdosStreamTaskCheckpoint> getCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd){
        return rdosStreamTaskCheckpointDAO.listByTaskIdAndRangeTime(taskId,triggerStart,triggerEnd);
    }

    public RdosStreamTaskCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("engineTaskId") String engineTaskId){
        return rdosStreamTaskCheckpointDAO.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    /**
     * 查询stream job
     */
    public List<RdosEngineStreamJob> getEngineStreamJob(@Param("taskIds") List<String> taskIds){
        return rdosEngineStreamJobDAO.getRdosTaskByTaskIds(taskIds);
    }

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus(@Param("status") Integer status){
        return rdosEngineStreamJobDAO.getTaskIdsByStatus(status);
    }

    /**
     * 获取任务的状态
     */
    public Byte getTaskStatus(@Param("taskId") String taskId){
        Byte status = null;
        if (StringUtils.isNotEmpty(taskId)){
            RdosEngineStreamJob streamJob = rdosEngineStreamJobDAO.getRdosTaskByTaskId(taskId);
            if (streamJob != null){
                status = streamJob.getStatus();
            }
        }

        return status;
    }

    public String getRunningTaskLogUrl(@Param("taskId") String taskId){

        Preconditions.checkState(StringUtils.isNotEmpty(taskId), "taskId can't be empty");

        RdosEngineStreamJob streamJob = rdosEngineStreamJobDAO.getRdosTaskByTaskId(taskId);
        Preconditions.checkNotNull(streamJob, "can't find record by taskId" + taskId);

        //只获取运行中的任务的log—url
        Byte status = streamJob.getStatus();
        Preconditions.checkState(RdosTaskStatus.RUNNING.getStatus().equals(status.intValue()), String.format("current task %s is not running now.", taskId));

        Preconditions.checkState(StringUtils.isNotEmpty(streamJob.getApplicationId()), String.format("current task %s don't have application id.", taskId));

        //如何获取url前缀


        return "";
    }
}
