package com.dtstack.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.po.CountFillDataJobStatusPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 3:05 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

    /**
     * 获得补数据实例运行的全部状态
     *
     * @param fillId   补数据id
     * @return
     */
    List<CountFillDataJobStatusPO> countByFillIdGetAllStatus(@Param("fillId") Set<Long> fillId);
}
