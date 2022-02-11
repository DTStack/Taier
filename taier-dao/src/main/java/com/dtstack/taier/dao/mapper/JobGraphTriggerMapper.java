package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 6:59 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface JobGraphTriggerMapper extends BaseMapper<JobGraphTrigger> {

    /**
     * 按照时间查询JobGraphTrigger
     *
     * @param timestamp JobGraphTrigger的生成具体时间
     * @param triggerType JobGraph类型： 周期，立即，补数据
     * @return JobGraphTrigger
     */
    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);
}
