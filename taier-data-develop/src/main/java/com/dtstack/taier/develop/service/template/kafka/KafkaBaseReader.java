package com.dtstack.taier.develop.service.template.kafka;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Date: 2020/7/7
 * Company: www.dtstack.com
 *参考：https://dtstack.yuque.com/rd-center/sm6war/an7m9c
 * @author zhichen
 */
public abstract class KafkaBaseReader extends BaseReaderPlugin {
    private Map<String, Object> consumerSettings;
    private String codec;
    private String topic;

    /**
     *仅支持kafka0.9，因Kafka0.9平台不再支持，因此取消
     */
    private String encoding;
    private String groupId;

    /**
     * 仅tidbcdc需要 // todo 下个版本要去掉
     */
    private String deserialization;

    /**
     * 从ZK / Kafka brokers中指定的消费组已经提交的offset开始消费 默认值
     */
    private final static String GROUP_OFFSETS = "group-offsets";

    /**
     * 从最早的偏移量开始(如果可能)
     */
    private final static String EARLIEST_OFFSET = "earliest-offset";

    /**
     * 从最新的偏移量开始(如果可能)
     */
    private final static String LATEST_OFFSET = "latest-offset";

    /**
     * 从每个分区的指定的时间戳开始
     */
    private final static String TIMESTAMP = "timestamp";

    /**
     * 从每个分区的指定的特定偏移量开始
     */
    private final static String SPECIFIC_OFFSETS = "specific-offsets";

    public String getDeserialization() {
        return deserialization;
    }

    public void setDeserialization(String deserialization) {
        this.deserialization = deserialization;
    }

    public Map<String, Object> getConsumerSettings() {
        return consumerSettings;
    }

    public void setConsumerSettings(Map<String, Object> consumerSettings) {
        this.consumerSettings = consumerSettings;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void checkFormat(JSONObject data) {
        Boolean isKafka9 = PluginName.KAFKA_09_R.equals(pluginName());
        //如果是kafka 或者KAFKA_2X
        Boolean isKafkaORKafka2X = PluginName.KAFKA_2X_R.equals(pluginName());
        data = data.getJSONObject("parameter");
        JSONObject consumerSettings = data.getJSONObject("consumerSettings");
        if ((isKafka9 && StringUtils.isEmpty(consumerSettings.getString("group.id"))) || (!isKafka9 && StringUtils.isEmpty(data.getString("groupId")))) {
            throw new RdosDefineException("Kafka数据源 groupId不能为空");
        }
        String topic = data.getString("topic");
        String code = data.getString("codec");
        if (StringUtils.isBlank(topic)) {
            throw new RdosDefineException("Kafka数据源 topic不能为空");
        }
        if (!StringUtils.isBlank(code) && !Objects.equals(code, "json") && !Objects.equals(code, "text")) {
            throw new RdosDefineException("读取类型仅支持json、text, 不支持 " + code);
        }
        if (!isKafka9 && StringUtils.isEmpty(consumerSettings.getString("bootstrap.servers"))) {
            throw new RdosDefineException("Kafka数据源 bootstrap.servers 不能为空");
        }
        if (isKafkaORKafka2X){
            String mode = data.getString("mode");
            if(!StringUtils.isBlank(mode)){
                switch (mode) {
                    case TIMESTAMP:
                        if (StringUtils.isBlank(data.getString("timestamp"))) {
                            throw new RdosDefineException("timestamp为空");
                        }
                        break;
                    case SPECIFIC_OFFSETS:
                        if (StringUtils.isBlank(data.getString("offset"))) {
                            throw new RdosDefineException("自定义参数为空");
                        }
                        break;
                    case LATEST_OFFSET:
                        break;
                    case EARLIEST_OFFSET:
                        break;
                    case GROUP_OFFSETS:
                        break;
                    default:
                        throw new RdosDefineException("输入offset参数错误 " + mode);
                }
            }
        }
    }
}
