package com.dtstack.taiga.develop.service.datasource.impl;


import com.dtstack.taiga.dao.domain.DsVersion;
import com.dtstack.taiga.dao.mapper.DsVersionMapper;
import com.dtstack.taiga.develop.bo.datasource.DsVersionSearchParam;
import com.dtstack.taiga.develop.mapstruct.datasource.DsVersionTransfer;
import com.dtstack.taiga.develop.vo.datasource.DsVersionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
public class DsVersionService {

    @Autowired
    private DsVersionMapper dsVersionMapper;

    /**
     * 根据数据源类型获取版本列表
     *
     * @param searchParam
     * @return
     */
    public List<DsVersionVO> queryDsVersionByType(DsVersionSearchParam searchParam) {
        List<DsVersion> dsVersions = dsVersionMapper.queryDsVersionByType(searchParam.getDataType());
        return DsVersionTransfer.INSTANCE.toInfoVOList(dsVersions);
    }

    /**
     * 获取版本列表
     * @return
     */
    public List<DsVersion> listDsVersion() {
        return dsVersionMapper.listDsVersion();
    }
}
