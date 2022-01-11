package com.dtstack.engine.master.service;

import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.DtJobIdWorker;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 2:09 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleActionService {

    private DtJobIdWorker jobIdWorker;

    /**
     * 生成jobId
     * @return jobId
     */
    public String generateUniqueSign() {
        if (null == jobIdWorker) {
            String[] split = AddressUtil.getOneIp().split("\\.");
            jobIdWorker = DtJobIdWorker.getInstance(split.length >= 4 ? Integer.parseInt(split[3]) : 0, 0);
        }
        return jobIdWorker.nextJobId();
    }
}
