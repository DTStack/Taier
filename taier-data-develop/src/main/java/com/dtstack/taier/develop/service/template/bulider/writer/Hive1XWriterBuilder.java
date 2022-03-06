package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.stereotype.Component;

@Component
public class Hive1XWriterBuilder extends Hive2XWriterBuilder {

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.HIVE1X;
    }

}
