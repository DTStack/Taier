package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.KafkaWriter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class KafkaWriterBuilder extends KafkaBaseWriterBuilder {
    @Override
    public KafkaWriter createKafkaWriter(Map<String, Object> sourceMap) {
        return new KafkaWriter();
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA;
    }
}
