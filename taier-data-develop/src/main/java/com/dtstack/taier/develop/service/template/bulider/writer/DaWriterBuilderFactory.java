package com.dtstack.taier.develop.service.template.bulider.writer;

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
 * Date: 2020/2/25
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class DaWriterBuilderFactory {

    private static Logger logger = LoggerFactory.getLogger(DaWriterBuilderFactory.class);

    @Autowired
    private List<DaWriterBuilder> daWriterBuilderList;

    private static Map<DataSourceType, DaWriterBuilder> daWriterBuilderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        if (CollectionUtils.isEmpty(daWriterBuilderList)) {
            throw new RuntimeException("no daWriterBuilderList in spring context!!");
        }
        new Thread(() -> {
            for (DaWriterBuilder daWriterBuilder : daWriterBuilderList) {
                daWriterBuilderMap.put(daWriterBuilder.getDataSourceType(), daWriterBuilder);
            }
            logger.info("init DaWriterBuilderFactory success...");
        }, "DaWriterBuilderFactoryInitThread").start();
    }


    public DaWriterBuilder getDaWriterBuilder(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        DaWriterBuilder daWriterBuilder = daWriterBuilderMap.get(dataSourceType);
        if (daWriterBuilder ==null) {
            throw new RdosDefineException("暂不支持该目标端");
        }
        return daWriterBuilder;
    }
}
