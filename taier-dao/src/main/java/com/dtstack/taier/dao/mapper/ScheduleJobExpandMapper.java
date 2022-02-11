package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 10:32 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobExpandMapper extends BaseMapper<ScheduleJobExpand> {

    Integer updateLogByJobIds(@Param("jobIds") Set<String> jobIds,
                              @Param("isDeleted") Integer isDeleted,
                              @Param("logInfo") String logInfo,
                              @Param("engineLog") String engineLog);
}
