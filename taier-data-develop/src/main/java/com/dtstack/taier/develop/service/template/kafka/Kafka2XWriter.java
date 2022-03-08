package com.dtstack.taier.develop.service.template.kafka;


import com.dtstack.taier.develop.service.template.PluginName;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class Kafka2XWriter extends KafkaBaseWriter {

    @Override
    public String pluginName() {
        return PluginName.KAFKA_2X_W;
    }
}
