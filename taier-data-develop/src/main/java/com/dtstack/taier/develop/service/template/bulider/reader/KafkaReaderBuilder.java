package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.service.template.kafka.KafkaReader;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class KafkaReaderBuilder extends KafkaBaseReaderBuilder {
    @Override
    public KafkaReader createKafkaReader(Map<String, Object> sourceMap) {
        return JSONObject.parseObject(JSONObject.toJSONString(sourceMap), KafkaReader.class);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA;
    }
}
