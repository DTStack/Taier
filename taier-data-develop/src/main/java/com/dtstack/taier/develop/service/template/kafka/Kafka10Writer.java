package com.dtstack.taier.develop.service.template.kafka;


import com.dtstack.taier.develop.service.template.PluginName;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class Kafka10Writer extends KafkaBaseWriter {

    @Override
    public String pluginName() {
        return PluginName.KAFKA_10_W;
    }
}
