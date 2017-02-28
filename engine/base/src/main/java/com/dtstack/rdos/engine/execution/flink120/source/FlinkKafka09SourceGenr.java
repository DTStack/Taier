package com.dtstack.rdos.engine.execution.flink120.source;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.com.google.common.base.Preconditions;
import org.apache.flink.streaming.connectors.kafka.Kafka09JsonTableSource;
import org.apache.flink.table.sources.StreamTableSource;

import java.util.Properties;

/**
 * flink kafka 关联数据源
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FlinkKafka09SourceGenr implements IStreamSourceGener<StreamTableSource> {


    /**
     * 获取kafka数据源,需要提供数据字段名称,数据类型
     * FIXME 还未做字段校验
     * 如果不指定auto.offset.reset的话,默认 从kafka 的latest开始读取数据,
     * 后续调用方式 tableEnvironment.registerTableSource("kafka-source", kafkaTableSource);
     * 参数不合法会抛出{@link IllegalStateException}
     * @param params
     * @return
     */
    public StreamTableSource genStreamSource(Properties params, String[] fieldNames, Class[] fieldTypes) {

        Preconditions.checkState(fieldNames.length == fieldTypes.length, "");

        String topicName = (String)params.get("topicname");
        String zkNamespace = (String)params.get("zknamespace");
        String groupId = (String) params.get("consumegroup");
        String boostrapSrvs = (String) params.get("bootstrapservers");
        Object offsetReset = params.get("offsetreset");//latest, earliest

        offsetReset = offsetReset == null ? "latest" : "earliest";

        Properties props = new Properties();
        props.setProperty("zookeeer.connect", zkNamespace);
        props.setProperty("group.id", groupId);
        props.setProperty("auto.offset.reset", (String) offsetReset);
        props.setProperty("bootstrap.servers", boostrapSrvs);

        TypeInformation[] types = new TypeInformation[fieldTypes.length];
        for(int i=0; i<fieldTypes.length; i++){
            types[i] = TypeInformation.of(fieldTypes[i]);
        }

        Kafka09JsonTableSource source = new Kafka09JsonTableSource(topicName, props, fieldNames, types);
        return source;
    }
}
