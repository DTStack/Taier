package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;

import java.util.Map;

/**
 * Date: 2020/1/8
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface DaWriterBuilder {

    void setWriterJson(TaskResourceParam param);

    Writer daWriterBuild(TaskResourceParam param) throws Exception;

    Map<String, Object> getParserTargetMap(Map<String, Object> sourceMap);

    DataSourceType getDataSourceType();

}
