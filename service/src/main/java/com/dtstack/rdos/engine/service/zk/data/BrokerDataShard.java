package com.dtstack.rdos.engine.service.zk.data;

/**
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class BrokerDataShard {

    private BrokerDataTreeMap metas;
    private long version;

    public BrokerDataTreeMap getMetas() {
        return metas;
    }

    public void setMetas(BrokerDataTreeMap metas) {
        this.metas = metas;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
