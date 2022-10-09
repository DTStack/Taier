package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.springframework.stereotype.Component;

@Component
public class SparkThriftReaderBuilder extends Hive2XReaderBuilder {

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.SparkThrift2_1;
    }

}
