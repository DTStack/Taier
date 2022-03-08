package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/2/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class DaReaderBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(DaReaderBuilderFactory.class);

    @Autowired
    private List<DaReaderBuilder> daReaderBuilderList;

    private final Map<DataSourceType, DaReaderBuilder> daReaderBuilderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        if (CollectionUtils.isEmpty(daReaderBuilderList)) {
            throw new RuntimeException("no daReaderBuilderList in spring context!!");
        }
        new Thread(()-> {
            for (DaReaderBuilder daReaderBuilder : daReaderBuilderList) {
                daReaderBuilderMap.put(daReaderBuilder.getDataSourceType(), daReaderBuilder);
            }
            //针对polardb兼容
            daReaderBuilderMap.put(DataSourceType.Polardb_For_MySQL, daReaderBuilderMap.get(DataSourceType.MySQL));
            daReaderBuilderMap.put(DataSourceType.SQLServer, daReaderBuilderMap.get(DataSourceType.SQLSERVER_2017_LATER));
            logger.info("init DaReaderBuilderFactory success...");
        }, "DaReaderBuilderFactoryInitThread").start();
    }


    public DaReaderBuilder getDaReaderBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }

        DaReaderBuilder daReaderBuilder = daReaderBuilderMap.get(dataSourceType);
        if (daReaderBuilder ==null) {
            throw new RdosDefineException("暂不支持该源端");
        }
        return daReaderBuilder;
    }
}
