package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.Kafka2XWriter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/06/28
 * Company: www.dtstack.com
 * @author wangchuan
 */

@Component
public class Kafka2XWriterBuilder extends KafkaBaseWriterBuilder {

    @Override
    public Kafka2XWriter createKafkaWriter(Map<String, Object> sourceMap) {
        return new Kafka2XWriter();
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA_2X;
    }
}
