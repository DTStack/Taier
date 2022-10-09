package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.Kafka09Writer;
import com.dtstack.taier.develop.service.template.kafka.KafkaBaseWriter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class Kafka09WriterBuilder extends KafkaBaseWriterBuilder {
    @Override
    public KafkaBaseWriter createKafkaWriter(Map<String, Object> sourceMap) {
        return new Kafka09Writer();
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA_09;
    }
}
