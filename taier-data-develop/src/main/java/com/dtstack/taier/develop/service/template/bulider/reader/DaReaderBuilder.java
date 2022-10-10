package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;

import java.util.Map;

/**
 * Date: 2020/1/8
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface DaReaderBuilder {

    String JDBC_USERNAME = "username";

    String JDBC_PASSWORD = "password";

    String RDBMS_DA_TYPE = "rdbmsDaType";

    String JDBC_URL = "jdbcUrl";

    /**
     * 参数预处理
     * @param param
     */
    void setReaderJson(TaskResourceParam param);

    Reader daReaderBuild(TaskResourceParam param) throws Exception;

    /**
     * 基础的参数 例如 sourceId
     * @param sourceMap
     * @return
     */
    Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap);

    DataSourceType getDataSourceType();

}
