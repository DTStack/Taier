package com.dtstack.engine.master.data;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class BrokerDataShard {

    private BrokerDataTreeMap<String, Byte> metas;
    private long version;

    @JsonIgnore
    private transient volatile AtomicLong newVersion;

    /**
     * 请使用 getView() 获取数据视图，不可修改！
     * 仅用于Zk中的数据迁移，方便管理任务
     */
    public BrokerDataTreeMap<String, Byte> getMetas() {
        return metas;
    }

    public void setMetas(BrokerDataTreeMap<String, Byte> metas) {
        this.metas = metas;
    }

    public long getVersion() {
        return version;
    }

    @JsonIgnore
    public AtomicLong getNewVersion() {
        return newVersion;
    }

    public void setVersion(long version) {
        this.version = version;
        this.newVersion = new AtomicLong(version);
    }

    public Byte put(String key, Byte value) {
        newVersion.incrementAndGet();
        return metas.put(key, value);
    }

    public Byte remove(String key) {
        newVersion.incrementAndGet();
        return metas.remove(key);
    }

    public boolean containsKey(String key) {
        return metas.containsKey(key);
    }

    public int metaSize() {
        return metas.size();
    }

    @JsonIgnore
    public NavigableMap<String, Byte> getView() {
        return Collections.unmodifiableNavigableMap(metas);
    }

    public static BrokerDataShard initBrokerDataShard() {
        BrokerDataShard brokerNode = new BrokerDataShard();
        brokerNode.setMetas(BrokerDataTreeMap.initBrokerDataTreeMap());
        brokerNode.setVersion(0);
        return brokerNode;
    }

}
