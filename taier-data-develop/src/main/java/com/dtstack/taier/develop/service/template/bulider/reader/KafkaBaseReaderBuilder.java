package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.kafka.KafkaBaseReader;
import com.dtstack.taier.develop.service.template.kafka.KafkaReaderParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public abstract class KafkaBaseReaderBuilder implements DaReaderBuilder {

    @Autowired
    DsInfoService dsInfoService;

    @Override
    public void setReaderJson(TaskResourceParam param) {
        Map<String, Object> map = param.getSourceMap();
        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        DsInfo source = dsInfoService.getOneById(sourceId);
        map.put("source", source);
        map.put("type", source.getDataTypeCode());
        map.put("dataName", source.getDataName());
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        setReaderJson(param);
        Map<String, Object> map = param.getSourceMap();
        KafkaReaderParam readerParam = JSON.parseObject(JSON.toJSONString(map), KafkaReaderParam.class);
        DsInfo dataSource = (DsInfo) map.get("source");
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
        Map<String, Object> consumerSettings = new HashMap<>();
        KafkaBaseReader reader = createKafkaReader(map);
        //目前默认为null
        reader.setCodec(readerParam.getCodec());
        reader.setEncoding(null);
        //设置其他参数
        reader.setTopic(readerParam.getTopic());
        reader.setConsumerSettings(consumerSettings);
        String address = DataSourceUtils.getAddress(dataSourceJson);
        consumerSettings.put("zookeeper.connect", StringUtils.isEmpty(address) ? "" : address);
        consumerSettings.put("auto.commit.interval.ms", "1000");

        Boolean isKafka9 = PluginName.KAFKA_09_R.equals(reader.pluginName());
        String groupId = "default";

        if (isKafka9) {
            consumerSettings.put("group.id", groupId);
            consumerSettings.put("auto.offset.reset", "smallest");

        } else {
            reader.setGroupId(groupId);
            consumerSettings.put("auto.offset.reset", "latest");
            String bootstrapServers = DataSourceUtils.getBootStrapServers(dataSourceJson);
            consumerSettings.put("bootstrap.servers", StringUtils.isBlank(bootstrapServers) ? "" : bootstrapServers);
        }
        String kerberosCnf = dataSourceJson.getString("kerberosConfig");
        //如果开启Kerberos 需要设置
        if (BooleanUtils.isTrue(dataSourceJson.getBoolean("openKerberos")) && StringUtils.isNotBlank(kerberosCnf)) {
            Map<String, Object> kerberosConfig = JsonUtils.strToMap(kerberosCnf);
            consumerSettings.put("security.protocol", "SASL_PLAINTEXT");
            consumerSettings.put("sasl.mechansim", "GSSAPI");
            consumerSettings.put("sasl.kerberos.service.name", MapUtils.getString(kerberosConfig, "sasl.kerberos.service.name", "kafka"));
        }

        // 如果开启 SASL_PLAINTEXT(PLAIN) 认证需要设置
        Map<String, String> plainParams = DataSourceUtils.initKafkaPlainIfOpen(dataSourceJson.getString("username"), dataSourceJson.getString("password"), null);
        if (MapUtils.isNotEmpty(plainParams)) {
            consumerSettings.putAll(plainParams);
        }
        reader.setExtralConfig(readerParam.getExtralConfig());
        return reader;
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        KafkaReaderParam readerParam = JSON.parseObject(JSON.toJSONString(sourceMap), KafkaReaderParam.class);
        return JSON.parseObject(JSON.toJSONString(readerParam));
    }

    public abstract KafkaBaseReader createKafkaReader(Map<String, Object> sourceMap);
}
