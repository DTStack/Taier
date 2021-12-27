package com.dtstack.batch.common.convert;

import com.dtstack.batch.bo.datasource.AddDataSourceParam;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.engine.common.lang.convert.Converter;
import com.dtstack.engine.common.util.DataSourceUtils;
import com.dtstack.engine.common.util.Strings;

/**
 * AddDataSourceParam 转 DataSourceVO
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
