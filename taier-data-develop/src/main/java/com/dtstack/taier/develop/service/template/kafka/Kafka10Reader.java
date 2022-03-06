package com.dtstack.taier.develop.service.template.kafka;

import com.dtstack.taier.develop.service.template.PluginName;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class Kafka10Reader extends KafkaBaseReader {

    @Override
    public String pluginName() {
        return PluginName.KAFKA_10_R;
    }
}
