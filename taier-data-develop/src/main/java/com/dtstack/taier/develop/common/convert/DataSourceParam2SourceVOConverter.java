package com.dtstack.taier.develop.common.convert;

import com.dtstack.taier.common.lang.convert.Converter;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.develop.bo.datasource.AddDataSourceParam;
import com.dtstack.taier.develop.dto.devlop.DataSourceVO;

/**
 * AddDataSourceParam 转 DataSourceVO
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/24
 */
public class DataSourceParam2SourceVOConverter extends Converter<AddDataSourceParam, DataSourceVO> {

    @Override
    protected DataSourceVO doConvert(AddDataSourceParam source) {
        DataSourceVO returnVo = new DataSourceVO();
        returnVo.setId(source.getId());
        returnVo.setUserId(source.getUserId());
        returnVo.setTenantId(source.getTenantId());
        returnVo.setTenantId(source.getTenantId());
        returnVo.setDataName(source.getDataName());
        returnVo.setDataDesc(source.getDataDesc());
        returnVo.setDataType(source.getDataType());
        returnVo.setDataVersion(source.getDataVersion());
        returnVo.setDataJsonString(source.getDataJsonString());
        if (Strings.isNotBlank(source.getDataJsonString())) {
            returnVo.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        }
        return returnVo;
    }
}
