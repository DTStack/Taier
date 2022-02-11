package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.dao.mapper.ScheduleJobOperatorRecordMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobOperatorRecordService extends ServiceImpl<ScheduleJobOperatorRecordMapper, ScheduleJobOperatorRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobOperatorRecordService.class);

    /**
     * 扫描操作记录
     *
     * @param startSort 开始位置
     * @param nodeAddress 节点
     * @param type 操作类型
     * @param isEq 是否查询开始位置
     * @return 操作记录
     */
    public List<ScheduleJobOperatorRecord> listOperatorRecord(Long startSort, String nodeAddress, Integer type, Boolean isEq) {
        if (startSort != null && startSort >= 0) {
            return this.baseMapper.listOperatorRecord(startSort,nodeAddress,type,isEq);
        }
        return Lists.newArrayList();
    }

    public Integer updateOperatorExpiredVersion(Long id, Timestamp operatorExpired, Integer version) {
        if (id != null && id > 0 && version != null) {
            return this.baseMapper.updateOperatorExpiredVersion(id,operatorExpired,version);
        }
        return 0;
    }


}
