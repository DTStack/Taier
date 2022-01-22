package com.dtstack.taiga.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taiga.dao.domain.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 6:59 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface JobGraphTriggerMapper extends BaseMapper<JobGraphTrigger> {


    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);

    /**
     * 根据触发时间查询关联最小任务ID
     */
    String  getMinJobIdByTriggerTime(@Param("triggerStartTime")String triggerStartTime,@Param("triggerEndTime")String triggerEndTime);
}
