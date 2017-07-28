package com.dtstack.rdos.engine.execution.flink130.sink.stream.kafka;

import org.apache.flink.streaming.connectors.kafka.partitioner.KafkaPartitioner;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.util.Random;

/**
 * kafka 自定义 指定partitions index
 * FIXME 暂时只是做了简单的random
 * Date: 2017/3/8
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class KafkaCustomPartitioner extends KafkaPartitioner<Row> implements Serializable{

    private Random random = new Random();

    @Override
    public int partition(Row next, byte[] serializedKey, byte[] serializedValue, int numPartitions) {
        return random.nextInt(numPartitions);
    }
}
