package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleTaskShadeInfo;
import com.dtstack.taier.dao.mapper.ScheduleTaskShadeInfoMapper;
import org.springframework.stereotype.Service;


@Service
public class ScheduleTaskShadeInfoService extends ServiceImpl<ScheduleTaskShadeInfoMapper, ScheduleTaskShadeInfo> {


    public void update(ScheduleTaskShadeInfo scheduleTaskShadeInfo,Long taskId){
        getBaseMapper().update(scheduleTaskShadeInfo,
                Wrappers.lambdaQuery(ScheduleTaskShadeInfo.class)
                        .eq(ScheduleTaskShadeInfo::getTaskId,taskId));
    }

    public void insert(ScheduleTaskShadeInfo scheduleTaskShadeInfo){
        getBaseMapper().insert(scheduleTaskShadeInfo);
    }

    public JSONObject getInfoJSON(Long taskId) {
        ScheduleTaskShadeInfo scheduleTaskShadeInfo = getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleTaskShadeInfo.class)
                .eq(ScheduleTaskShadeInfo::getTaskId, taskId));
        if(null == scheduleTaskShadeInfo){
            return null;
        }
        return JSONObject.parseObject(scheduleTaskShadeInfo.getInfo());
    }
}
