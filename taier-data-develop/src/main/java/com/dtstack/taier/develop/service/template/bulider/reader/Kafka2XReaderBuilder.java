package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.Kafka2XReader;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/06/28
 * Company: www.dtstack.com
 *
 * @author wangchuan
 */

@Component
public class Kafka2XReaderBuilder extends KafkaBaseReaderBuilder {

    @Override
    public Kafka2XReader createKafkaReader(Map<String, Object> sourceMap) {
        return JSONObject.parseObject(JSONObject.toJSONString(sourceMap), Kafka2XReader.class);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA_2X;
    }
}
