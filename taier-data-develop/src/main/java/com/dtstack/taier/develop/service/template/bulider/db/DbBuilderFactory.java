package com.dtstack.taier.develop.service.template.bulider.db;

import com.dtstack.taier.datasource.api.source.DataSourceType;
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
 * @Author ：gengxin
 * @Date ：Created in 上午10:28 2020/7/16
 * @Description：数据库工具创建工厂
 */
@Component
public class DbBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(DbBuilderFactory.class);

    @Autowired
    private List<DbBuilder> dbBuilders;

    private final Map<Integer, DbBuilder> dbBuilderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        if (CollectionUtils.isEmpty(dbBuilders)) {
            throw new RuntimeException("no rdbmsDbBuilders in spring context!!");
        }
        for (DbBuilder dbBuilder : dbBuilders) {
            dbBuilderMap.put(dbBuilder.getDataSourceType().getVal(), dbBuilder);
        }
        dbBuilderMap.put(DataSourceType.ES6.getVal(), dbBuilderMap.get(DataSourceType.ES.getVal()));
        dbBuilderMap.put(DataSourceType.ES7.getVal(), dbBuilderMap.get(DataSourceType.ES7.getVal()));
        dbBuilderMap.put(DataSourceType.SQLServer.getVal(), dbBuilderMap.get(DataSourceType.SQLSERVER_2017_LATER.getVal()));
        logger.info("init DbBuilderFactory success...");
    }

    public DbBuilder getDbBuilder(Integer dataSourceType) {
        if (dataSourceType == null) {
            throw new RuntimeException("dataSourceType should not be null !");
        }
        return dbBuilderMap.get(dataSourceType);
    }

}
