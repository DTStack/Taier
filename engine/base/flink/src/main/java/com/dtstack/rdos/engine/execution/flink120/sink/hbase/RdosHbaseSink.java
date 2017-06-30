package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;
import org.apache.flink.util.Preconditions;

import java.util.Properties;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public class RdosHbaseSink extends HbaseSink implements IStreamSinkGener<RdosHbaseSink>{


    @Override
    public RdosHbaseSink genStreamSink(CreateResultOperator operator) {
        Properties properties = operator.getProperties();
        this.host = Preconditions.checkNotNull(properties.getProperty(HBASE_ZOOKEEPER_QUORUM),
                "Should speficy hbase zookeeper quorum as host");
        this.port = Preconditions.checkNotNull(properties.getProperty(HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT),
                "Should specify hbase zookeeper property clientport as port");
        this.parent = properties.getProperty(ZOOKEEPER_ZNODE_PARENT);
        this.tableName = Preconditions.checkNotNull(properties.getProperty("not null"),
                "Should specify hbase tableName");


        return null;
    }

}
