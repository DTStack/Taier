package com.dtstack.rdos.engine.service.zk.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class BrokerDataShard {

    private BrokerDataTreeMap metas;
    private AtomicLong version;

    public BrokerDataTreeMap getMetas() {
        return metas;
    }

    public void setMetas(BrokerDataTreeMap metas) {
        this.metas = metas;
    }

    public AtomicLong getVersion() {
        return version;
    }

    public void setVersion(AtomicLong version) {
        this.version = version;
    }

    public Byte put(String key, Byte value) {
        version.incrementAndGet();
        return metas.put(key, value);
    }

    public Byte remove(String key) {
        version.incrementAndGet();
        return metas.remove(key);
    }

    public boolean containsKey(String key) {
        return metas.containsKey(key);
    }

    public int metaSize() {
        return metas.size();
    }

    public static void copy(BrokerDataShard source, BrokerDataShard target, boolean isCover) {
        if (source.getMetas() != null) {
            if (isCover) {
                target.setMetas(source.getMetas());
            } else {
                target.getMetas().putAll(source.getMetas());
            }
        }
    }

    public static BrokerDataShard initBrokerDataShard() {
        BrokerDataShard brokerNode = new BrokerDataShard();
        brokerNode.setMetas(new BrokerDataTreeMap());
        return brokerNode;
    }

    public static BrokerDataShard initNullBrokerShard() {
        BrokerDataShard brokerDataShard = new BrokerDataShard();
        return brokerDataShard;
    }
}
