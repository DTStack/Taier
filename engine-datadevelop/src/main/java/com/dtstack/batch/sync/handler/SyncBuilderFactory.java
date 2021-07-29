package com.dtstack.batch.sync.handler;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用于获取数据源类型对应的syncBuilder.
 *
 * @author ：wangchuan
 * @since ：Created in 上午10:20 2020/10/22
 */
@Component
@Slf4j
public class SyncBuilderFactory {

    @Autowired
    private List<SyncBuilder> syncBuilders;

    private Map<Integer, SyncBuilder> syncBuilderMap = Maps.newHashMap();

    /**
     * 用于初始化map集合 syncBuilderMap
     */
    @Async
    @PostConstruct
    public void init () {
        if (CollectionUtils.isEmpty(syncBuilders)) {
            throw new RdosDefineException("List syncBuilders is empty, No SyncBuilder was found in the spring container");
        }
        syncBuilders.forEach(syncBuilder -> syncBuilderMap.put(syncBuilder.getDataSourceType().getVal(), syncBuilder));
        log.info("init SyncBuilderFactory is success...");
    }

    /**
     * 根据数据源类型获取对应的syncBuilder，数据源类型见：{@link com.dtstack.dtcenter.loader.source.DataSourceType}
     * @param dataSourceType 数据源类型
     * @return {@link SyncBuilder} 数据同步reader、writer构造类
     */
    public SyncBuilder getSyncBuilder (Integer dataSourceType) {
        SyncBuilder syncBuilder = syncBuilderMap.get(dataSourceType);
        if (Objects.isNull(syncBuilder)) {
            throw new RdosDefineException("找不到该数据源类型对应的syncBuilder...");
        }
        return syncBuilder;
    }
}
