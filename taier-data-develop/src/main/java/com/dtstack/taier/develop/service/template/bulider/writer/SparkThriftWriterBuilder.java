package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.stereotype.Component;

@Component
public class SparkThriftWriterBuilder extends Hive2XWriterBuilder {

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.SparkThrift2_1;
    }

}
