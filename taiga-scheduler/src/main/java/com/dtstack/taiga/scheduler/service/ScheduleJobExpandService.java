package com.dtstack.taiga.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taiga.common.enums.IsDeletedEnum;
import com.dtstack.taiga.dao.domain.ScheduleJobExpand;
import com.dtstack.taiga.dao.mapper.ScheduleJobExpandMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:54 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobExpandService extends ServiceImpl<ScheduleJobExpandMapper, ScheduleJobExpand> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobExpandService.class);

    public Integer clearData(Set<String> jobIds) {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            return this.baseMapper.updateLogByJobIds(jobIds, IsDeletedEnum.NOT_DELETE.getType(),"","");
        }
        return 0;
    }
}
