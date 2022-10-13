package com.dtstack.taier.develop.service.template.bulider.nameMapping;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;

/**
 * @author zhiChen
 * @date 2022/1/12 11:43
 */
public interface NameMappingBuilder {

    JSONObject daReaderBuild(TaskResourceParam param) throws Exception;

    DataSourceType getDataSourceType();
}
