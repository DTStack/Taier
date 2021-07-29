package com.dtstack.engine.datasource.service.impl.datasource;

import com.dtstack.engine.datasource.common.utils.Dozers;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsClassifyMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsClassify;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.DsClassifyVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsClassifyService extends BaseService<DsClassifyMapper, DsClassify> {

    /**
     * 获取数据源分类类目列表
     *
     * @return
     */
    public List<DsClassifyVO> queryDsClassifyList() {
        return  lambdaQuery().orderByDesc(DsClassify::getSorted).list().stream()
                .map(x -> Dozers.convert(x, DsClassifyVO.class, (t, s, c) -> {
                    t.setClassifyId(s.getId());
                })).collect(Collectors.toList());
    }
}
