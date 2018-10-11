package com.dtstack.rdos.engine.service;

import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
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

    /**
     * 查询stream job
     */
    public List<RdosEngineStreamJob> getEngineStreamJob(@Param("taskIds") List<String> taskIds){
        return rdosEngineStreamJobDAO.getRdosTaskByTaskIds(taskIds);
    }
}
