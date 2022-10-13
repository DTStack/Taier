package com.dtstack.taier.datasource.plugin.kafka;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.kafka.util.KafkaUtil;

/**
 * t
 *
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午4:35 2020/6/2
 * @Description：Kafka 客户端 支持 Kafka 0.9、0.10、0.11、1.x版本
 */
public class KafkaClient extends AbsNoSqlClient {
    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        return KafkaUtil.checkConnection(kafkaSourceDTO);
    }
}
