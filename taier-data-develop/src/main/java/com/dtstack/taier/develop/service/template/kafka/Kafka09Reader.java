package com.dtstack.taier.develop.service.template.kafka;

import com.dtstack.taier.develop.service.template.PluginName;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class Kafka09Reader extends KafkaBaseReader {

    @Override
    public String pluginName() {
        return PluginName.KAFKA_09_R;
    }
}
