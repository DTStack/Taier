package com.dtstack.engine.datasource.converter.datasource;

import com.dtstack.engine.common.lang.convert.Converter;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.param.datasource.AddDataSourceParam;
import com.dtstack.engine.datasource.vo.datasource.DataSourceVO;
import org.apache.commons.lang3.StringUtils;

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
        returnVo.setDtuicTenantId(source.getTenantId());
        returnVo.setTenantId(source.getTenantId());
        returnVo.setDataName(source.getDataName());
        returnVo.setDataDesc(source.getDataDesc());
        returnVo.setDataType(source.getDataType());
        returnVo.setDataVersion(source.getDataVersion());
        returnVo.setDataJsonString(source.getDataJsonString());
        if (StringUtils.isNotBlank(source.getDataJsonString())) {
            returnVo.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        } else {
            returnVo.setDataJson(source.getDataJson());
        }
        returnVo.setAppTypeList(source.getAppTypeList());
        return returnVo;
    }
}
