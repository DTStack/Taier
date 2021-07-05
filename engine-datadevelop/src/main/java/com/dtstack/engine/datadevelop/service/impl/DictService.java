package com.dtstack.engine.datadevelop.service.impl;

import com.dtstack.batch.dao.DictDao;
import com.dtstack.batch.domain.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 字典表相关
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/7/18
 */
@Service
public class DictService {

    @Autowired
    private DictDao dictDao;

    public List<Dict> getDictByType(int type) {
        return dictDao.listByType(type);
    }

}
