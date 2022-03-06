package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.Kafka09Reader;
import com.dtstack.taier.develop.service.template.kafka.KafkaBaseReader;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class Kafka09ReaderBuilder extends KafkaBaseReaderBuilder {
    @Override
    public KafkaBaseReader createKafkaReader(Map<String, Object> sourceMap) {
        return new Kafka09Reader();
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA_09;
    }
}
