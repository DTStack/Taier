package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;
import org.apache.flink.util.Preconditions;

import java.util.Properties;

/**
 * author: jingzhen@dtstack.com
 * date: 2017-6-30
 */
public class RdosHbaseSink extends HbaseSink implements IStreamSinkGener<RdosHbaseSink>{

    @Override
    public RdosHbaseSink genStreamSink(StreamCreateResultOperator operator) {
        Properties properties = operator.getProperties();
        this.host = Preconditions.checkNotNull(properties.getProperty(HBASE_ZOOKEEPER_QUORUM),
                "Should speficy hbase zookeeper quorum as host");

        this.port = Preconditions.checkNotNull(properties.getProperty(HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT),
                "Should specify hbase zookeeper property clientport as port");

        this.parent = properties.getProperty(ZOOKEEPER_ZNODE_PARENT);

        this.tableName = Preconditions.checkNotNull(operator.getName(),
                "Should specify table name");

        this.fullFieldNames = Preconditions.checkNotNull(operator.getFields(),
                "Should specify full field names");

        this.fullFieldTypes = Preconditions.checkNotNull(operator.getFieldTypes(),
                "Should specify full field types");

        this.columnFamily = Preconditions.checkNotNull(properties.getProperty(HBASE_COLUMN_FAMILY),
                "Should spedify columnFamily");

        this.rowkey = Preconditions.checkNotNull(properties.getProperty(HBASE_ROWKEY),
                "Should specify rowKey");

        return this;
    }

}
