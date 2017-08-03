package com.dtstack.rdos.engine.execution.flink120.sink.kafka;

import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;

import org.apache.flink.streaming.connectors.kafka.Kafka09JsonTableSink;
import org.apache.flink.streaming.connectors.kafka.partitioner.KafkaPartitioner;
import org.apache.flink.types.Row;

import java.util.Properties;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosKafka09Sink implements IStreamSinkGener<Kafka09JsonTableSink> {

    @Override
    public Kafka09JsonTableSink genStreamSink(StreamCreateResultOperator resultOperator) {
        Properties properties = resultOperator.getProperties();
        String topic = properties.getProperty("topic");
        String bootstrapSvrs = properties.getProperty("bootstrapServers");
        KafkaPartitioner<Row> partitioner = new KafkaCustomPartitioner();

        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", bootstrapSvrs);
        Kafka09JsonTableSink tableSink = new Kafka09JsonTableSink(topic, kafkaProps, partitioner);
        return tableSink;
    }
}
