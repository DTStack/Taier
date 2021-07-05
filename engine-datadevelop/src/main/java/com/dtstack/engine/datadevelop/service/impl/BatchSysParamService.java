package com.dtstack.engine.datadevelop.service.impl;

import com.dtstack.batch.dao.BatchSysParamDao;
import com.dtstack.batch.domain.BatchSysParameter;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统参数,用于离线任务中的参数替换
 * eg:${bdp.system.bizdate}：yyyyMMdd-1
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class BatchSysParamService {

    @Autowired
    private BatchSysParamDao batchSysParamDao;

    private Map<String, BatchSysParameter> cache = null;

    public Collection<BatchSysParameter> listSystemParam(){
        if (cache == null){
            loadSystemParam();
        }
        return cache.values();
    }

    @Forbidden
    public void loadSystemParam(){
        cache = Maps.newHashMap();
        List<BatchSysParameter> sysParamList = batchSysParamDao.listAll();
        for(BatchSysParameter tmp : sysParamList){
            cache.put(tmp.getParamName(), tmp);
        }
    }

    @Forbidden
    public BatchSysParameter getBatchSysParamByName(String name){

        if(cache == null){
            loadSystemParam();
        }

        return cache.get(name);
    }

}
