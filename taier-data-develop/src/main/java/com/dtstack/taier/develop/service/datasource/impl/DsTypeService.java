package com.dtstack.taier.develop.service.datasource.impl;


import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.DsType;
import com.dtstack.taier.dao.domain.DsVersion;
import com.dtstack.taier.dao.mapper.DsTypeMapper;
import com.dtstack.taier.develop.bo.datasource.DsTypeSearchParam;
import com.dtstack.taier.develop.mapstruct.datasource.DsTypeTransfer;
import com.dtstack.taier.develop.vo.datasource.DsTypeListVO;
import com.dtstack.taier.develop.vo.datasource.DsTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
public class DsTypeService {

    @Autowired
    private DsVersionService dsVersionService;

    @Autowired
    private DsTypeMapper dsTypeMapper;

    /**
     * 获取数据类型下拉列表
     *
     * @return
     */
    public List<DsTypeListVO> dsTypeList() {
        List<DsType> dsTypeList = dsTypeMapper.dsTypeList();
        return DsTypeTransfer.INSTANCE.toDsTypeListVOs(dsTypeList);
    }

    /**
     * 根据分类获取数据源类型
     *
     * @param searchParam
     * @return
     */
    public List<DsTypeVO> queryDsTypeByClassify(DsTypeSearchParam searchParam) {
        Long classifyId = searchParam.getClassifyId();
        String search = searchParam.getSearch();
        if (Strings.isNotBlank(search)) {
            classifyId = null;
            search = search.trim();
        }
        List<DsType> dsTypes = dsTypeMapper.queryDsTypeByClassify(classifyId, search.toLowerCase(Locale.ROOT));
        List<String> versionList = dsVersionService.listDsVersion().stream().map(DsVersion::getDataType)
                .collect(Collectors.toList());
        return dsTypes.stream().map(x -> {
            DsTypeVO dsTypeVO = DsTypeTransfer.INSTANCE.toInfoVO(x);
            dsTypeVO.setHaveVersion(versionList.contains(x.getDataType()));
            return dsTypeVO;
        }).collect(Collectors.toList());
    }

    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     *
     * @param dataType
     * @return
     */
    public Boolean plusDataTypeWeight(String dataType, Integer plusWeight) {
        Objects.requireNonNull(plusWeight);
        Objects.requireNonNull(dataType);
        return dsTypeMapper.plusDataTypeWeight(dataType, plusWeight) > 0;
    }


}
