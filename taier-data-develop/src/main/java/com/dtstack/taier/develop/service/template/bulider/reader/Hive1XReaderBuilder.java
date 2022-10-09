package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.springframework.stereotype.Component;

@Component
public class Hive1XReaderBuilder extends Hive2XReaderBuilder {

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.HIVE1X;
    }

}
