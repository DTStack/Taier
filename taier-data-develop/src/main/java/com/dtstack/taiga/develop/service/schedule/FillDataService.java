package com.dtstack.taiga.develop.service.schedule;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taiga.dao.domain.ScheduleFillDataJob;
import com.dtstack.taiga.dao.mapper.ScheduleFillDataJobMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 4:20 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class FillDataService extends ServiceImpl<ScheduleFillDataJobMapper, ScheduleFillDataJob> {


    public boolean checkExistsName(String fillName) {

        return false;
    }
}
