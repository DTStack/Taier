package com.dtstack.taier.develop.service.template;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder;
import com.dtstack.taier.develop.service.template.bulider.writer.DaWriterBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuebai
 * @date 2022/9/20
 */
@Component
public class SyncBuilderFactory implements ApplicationContextAware {

    public Map<DataSourceType, DaWriterBuilder> writerBuilderMap = new HashMap<>();
    public Map<DataSourceType, DaReaderBuilder> readBuilderMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DaWriterBuilder> writerBuilderBeanMap = applicationContext.getBeansOfType(DaWriterBuilder.class);
        writerBuilderBeanMap.forEach((t, service) -> {
            DataSourceType dataSourceType = service.getDataSourceType();
            writerBuilderMap.put(dataSourceType, service);
        });

        Map<String, DaReaderBuilder> readBuilderBeanMap = applicationContext.getBeansOfType(DaReaderBuilder.class);
        readBuilderBeanMap.forEach((t, service) -> {
            DataSourceType dataSourceType = service.getDataSourceType();
            readBuilderMap.put(dataSourceType, service);
        });

        //针对polardb兼容
        readBuilderMap.put(DataSourceType.Polardb_For_MySQL, readBuilderMap.get(DataSourceType.MySQL));
        readBuilderMap.put(DataSourceType.SQLServer, readBuilderMap.get(DataSourceType.SQLSERVER_2017_LATER));
    }

    public DaWriterBuilder getWriterBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        DaWriterBuilder daWriterBuilder = writerBuilderMap.get(dataSourceType);
        if (daWriterBuilder == null) {
            throw new RdosDefineException(String.format("not support datasourceType %s writer ", dataSourceType.getName()));
        }
        return daWriterBuilder;
    }

    public DaReaderBuilder getReadBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        DaReaderBuilder readerBuilder = readBuilderMap.get(dataSourceType);
        if (readerBuilder == null) {
            throw new RdosDefineException(String.format("not support datasourceType %s reader ", dataSourceType.getName()));
        }
        return readerBuilder;
    }
}
