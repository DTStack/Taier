package com.dtstack.batch.service.datasource.impl;

import com.dtstack.batch.mapstruct.datasource.DsClassifyTransfer;
import com.dtstack.batch.vo.datasource.DsClassifyVO;
import com.dtstack.engine.domain.DsClassify;
import com.dtstack.engine.mapper.DsClassifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
public class DsClassifyService {


    @Autowired
    private DsClassifyMapper dsClassifyMapper;

    /**
     * 获取数据源分类类目列表
     *
     * @return
     */
    public List<DsClassifyVO> queryDsClassifyList() {
        List<DsClassify> dsClassifyList = dsClassifyMapper.queryDsClassifyList();
        return dsClassifyList.stream().map
                (DsClassifyTransfer.INSTANCE::toInfoVO).collect(Collectors.toList());
    }
}
