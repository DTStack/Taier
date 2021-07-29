package com.dtstack.engine.datasource.service.impl.datasource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsAppMappingMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAppMapping;
import com.dtstack.engine.datasource.service.impl.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsAppMappingService extends BaseService<DsAppMappingMapper, DsAppMapping> {

    /**
     * 通过产品类型获取数据源类型列表(去重)
     * @param appType
     * @return
     */
    public List<DsAppMapping> groupListByAppType(Integer appType) {
        return list(Wrappers.<DsAppMapping>query()
                .select("data_type").eq(Objects.nonNull(appType), "app_type", appType).last("group by data_type order by id asc"));
    }

}
