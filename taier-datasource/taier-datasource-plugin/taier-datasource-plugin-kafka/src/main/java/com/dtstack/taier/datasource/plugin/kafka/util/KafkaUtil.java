package com.dtstack.taier.datasource.plugin.kafka.util;

import com.dtstack.taier.datasource.api.dto.KafkaConsumerDTO;
import com.dtstack.taier.datasource.api.dto.KafkaOffsetDTO;
import com.dtstack.taier.datasource.api.dto.KafkaPartitionDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.api.enums.KafkaAuthenticationType;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.TelUtil;
import com.dtstack.taier.datasource.plugin.kafka.KafkaConsistent;
import com.dtstack.taier.datasource.plugin.kafka.enums.EConsumeType;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.JaasUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kafka.cluster.Broker;
import kafka.cluster.EndPoint;
import kafka.coordinator.group.GroupOverview;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.JaasUtils;
import scala.collection.JavaConversions;
import sun.security.krb5.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:46 2020/2/26
 * @Description：Kafka 工具类
 */
@Slf4j
public class KafkaUtil {

    public static final String EARLIEST = "earliest";
    private static final int MAX_POOL_RECORDS = 5;

    // 开启 kerberos 默认 sasl.kerberos.service.name
    private static final String DEFAULT_KERBEROS_NAME = "kafka";

    private static final String KEY_PARTITIONS = "partitions";

    private static final String KEY_REPLICAS = "replicas";

    /**
     * 从 ZK 中获取所有的 Kafka broker 地址
     *
     * @param zkUrls zk 地址
     * @return kafka broker 地址
     */
    public static String getAllBrokersAddressFromZk(String zkUrls) {
        log.info("Obtain Kafka Broker address through ZK : {}", zkUrls);
        if (StringUtils.isBlank(zkUrls) || !TelUtil.checkTelnetAddr(zkUrls)) {
            throw new SourceException("Please configure the correct zookeeper address");
        }

        ZkUtils zkUtils = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            zkUtils = ZkUtils.apply(zkUrls, KafkaConsistent.SESSION_TIME_OUT,
                    KafkaConsistent.CONNECTION_TIME_OUT, JaasUtils.isZkSecurityEnabled());
            List<Broker> brokers = JavaConversions.seqAsJavaList(zkUtils.getAllBrokersInCluster());
            if (CollectionUtils.isNotEmpty(brokers)) {
                for (Broker broker : brokers) {
                    List<EndPoint> endPoints = JavaConversions.seqAsJavaList(broker.endPoints());
                    for (EndPoint endPoint : endPoints) {
                        String ip = endPoint.host();
                        int port = endPoint.port();
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",").append(ip).append(":").append(port);
                        } else {
                            stringBuilder.append(ip).append(":").append(port);
                        }
                    }
                }
            }
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 从 KAFKA 中获取 TOPIC 的信息
     *
     * @param kafkaSourceDTO kafka 数据源信息
     * @return topic 列表
     */
    public static List<String> getTopicList(KafkaSourceDTO kafkaSourceDTO) {
        Properties defaultKafkaConfig = initProperties(kafkaSourceDTO);
        List<String> results = Lists.newArrayList();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(defaultKafkaConfig)) {
            Map<String, List<PartitionInfo>> topics = consumer.listTopics();
            if (topics != null) {
                results.addAll(topics.keySet());
            }
        } catch (Exception e) {
            throw new SourceException(String.format("failed to get topics from broker. %s", e.getMessage()), e);
        } finally {
            destroyProperty();
        }
        return results;
    }

    /**
     * 通过 KAFKA 中创建 TOPIC 的信息
     *
     * @param sourceDTO         数据源信息
     * @param topicName         topic 名称
     * @param partitions        分区数量
     * @param replicationFactor 每个分区的副本数量
     */
    public static void createTopicFromBroker(KafkaSourceDTO sourceDTO, String topicName,
                                             Integer partitions, Short replicationFactor) {
        Properties defaultKafkaConfig = initProperties(sourceDTO);
        try (AdminClient client = AdminClient.create(defaultKafkaConfig);) {
            NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);
            client.createTopics(Collections.singleton(topic));
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    /**
     * 获取所有分区中最大最小的偏移量
     *
     * @param sourceDTO 数据源信息
     * @param topic     kafka topic
     * @return kafka 每个分区的最大最小 offset
     */
    public static List<KafkaOffsetDTO> getPartitionOffset(KafkaSourceDTO sourceDTO, String topic) {
        Properties defaultKafkaConfig = initProperties(sourceDTO);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(defaultKafkaConfig)) {
            List<TopicPartition> partitions = new ArrayList<>();
            List<PartitionInfo> allPartitionInfo = consumer.partitionsFor(topic);
            for (PartitionInfo partitionInfo : allPartitionInfo) {
                partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            }

            Map<Integer, KafkaOffsetDTO> kafkaOffsetDTOMap = new HashMap<>();
            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(partitions);
            for (Map.Entry<TopicPartition, Long> entry : beginningOffsets.entrySet()) {
                KafkaOffsetDTO offsetDTO = new KafkaOffsetDTO();
                offsetDTO.setPartition(entry.getKey().partition());
                offsetDTO.setFirstOffset(entry.getValue());
                offsetDTO.setLastOffset(entry.getValue());
                kafkaOffsetDTOMap.put(entry.getKey().partition(), offsetDTO);
            }

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
                KafkaOffsetDTO offsetDTO = kafkaOffsetDTOMap.getOrDefault(entry.getKey().partition(),
                        new KafkaOffsetDTO());
                offsetDTO.setPartition(entry.getKey().partition());
                offsetDTO.setFirstOffset(null == offsetDTO.getFirstOffset() ? entry.getValue() :
                        offsetDTO.getFirstOffset());
                offsetDTO.setLastOffset(entry.getValue());
                kafkaOffsetDTOMap.put(entry.getKey().partition(), offsetDTO);
            }

            return new ArrayList<>(kafkaOffsetDTOMap.values());
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            destroyProperty();
        }
    }

    /**
     * 根据 Kafka 地址 校验连接性
     *
     * @param sourceDTO 数据源信息
     * @return 是否连通
     */
    public static boolean checkConnection(KafkaSourceDTO sourceDTO) {
        Properties props = initProperties(sourceDTO);
        /* 定义consumer */
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.listTopics();
        } catch (Exception e) {
            throw new SourceException(String.format("connect kafka fail: %s", e.getMessage()), e);
        } finally {
            destroyProperty();
        }
        return true;
    }

    private static void destroyProperty() {
        System.clearProperty("java.security.auth.login.config");
        System.clearProperty("javax.security.auth.useSubjectCredsOnly");
    }

    /**
     * 获取 kafka broker 地址，如果 broker 填写为空则从 zookeeper 中获取
     *
     * @param sourceDTO kafka 数据源信息
     * @return kafka broker 地址
     */
    private static String getKafkaBroker(KafkaSourceDTO sourceDTO) {
        String brokerUrls = StringUtils.isEmpty(sourceDTO.getBrokerUrls()) ? getAllBrokersAddressFromZk(sourceDTO.getUrl()) : sourceDTO.getBrokerUrls();
        if (StringUtils.isBlank(brokerUrls)) {
            throw new SourceException("failed to get broker from zookeeper.");
        }
        return brokerUrls;
    }

    /**
     * 初始化 Kafka 配置信息
     *
     * @param sourceDTO 数据源信息
     * @return kafka 配置
     */
    private synchronized static Properties initProperties(KafkaSourceDTO sourceDTO) {
        try {
            String brokerUrls = getKafkaBroker(sourceDTO);
            log.info("Initialize Kafka configuration information, brokerUrls : {}, kerberosConfig : {}", brokerUrls, sourceDTO.getKerberosConfig());
            Properties props = new Properties();
            if (StringUtils.isBlank(brokerUrls)) {
                throw new SourceException("Kafka Broker address cannot be empty");
            }
            /* 定义kakfa 服务的地址，不需要将所有broker指定上 */
            props.put("bootstrap.servers", brokerUrls);
            /* 是否自动确认offset */
            props.put("enable.auto.commit", "true");
            /* 设置group id */
            props.put("group.id", KafkaConsistent.KAFKA_GROUP);
            /* 自动确认offset的时间间隔 */
            props.put("auto.commit.interval.ms", "1000");
            //heart beat 默认3s
            props.put("session.timeout.ms", "10000");
            //一次性的最大拉取条数
            props.put("max.poll.records", "5");
            props.put("auto.offset.reset", "earliest");
            /* key的序列化类 */
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            /* value的序列化类 */
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            /*设置超时时间*/
            props.put("request.timeout.ms", "10500");

            // username 和 password 都为空的时候走 SASL/PLAIN 认证逻辑
            if (StringUtils.isNotBlank(sourceDTO.getUsername()) && StringUtils.isNotBlank(sourceDTO.getPassword())) {
                // SASL/PLAIN 相关设置
                props.put("security.protocol", "SASL_PLAINTEXT");
                String saslType = ReflectUtil.getFieldValueNotThrow(String.class, sourceDTO, "authentication", KafkaAuthenticationType.SASL_PLAINTEXT.name());
                if (KafkaAuthenticationType.SASL_SCRAM.name().equalsIgnoreCase(saslType)) {
                    // scram
                    props.put("sasl.mechanism", KafkaAuthenticationType.SASL_SCRAM.getMechanism());
                    props.put("sasl.jaas.config", String.format(KafkaConsistent.KAFKA_SASL_SCRAM_CONTENT, sourceDTO.getUsername(), sourceDTO.getPassword()));
                } else if (KafkaAuthenticationType.SASL_SCRAM_512.name().equalsIgnoreCase(saslType)) {
                    // scram-512
                    props.put("sasl.mechanism", KafkaAuthenticationType.SASL_SCRAM_512.getMechanism());
                    props.put("sasl.jaas.config", String.format(KafkaConsistent.KAFKA_SASL_SCRAM_CONTENT, sourceDTO.getUsername(), sourceDTO.getPassword()));
                } else {
                    props.put("sasl.mechanism", KafkaAuthenticationType.SASL_PLAINTEXT.getMechanism());
                    props.put("sasl.jaas.config", String.format(KafkaConsistent.KAFKA_SASL_PLAIN_CONTENT, sourceDTO.getUsername(), sourceDTO.getPassword()));
                }
                return props;
            }

            if (MapUtils.isEmpty(sourceDTO.getKerberosConfig())) {
                //不满足kerberos条件 直接返回
                return props;
            }
            // 只需要认证的用户名
            String kafkaKbrServiceName = MapUtils.getString(sourceDTO.getKerberosConfig(), KerberosConstant.KAFKA_KERBEROS_SERVICE_NAME, DEFAULT_KERBEROS_NAME);
            kafkaKbrServiceName = kafkaKbrServiceName.split("/")[0];
            String kafkaLoginConf = JaasUtil.writeJaasConf(sourceDTO.getKerberosConfig(), JaasUtil.KAFKA_JAAS_CONTENT);

            // 刷新kerberos认证信息，在设置完java.security.krb5.conf后进行，否则会使用上次的krb5文件进行 refresh 导致认证失败
            try {
                Config.refresh();
                javax.security.auth.login.Configuration.setConfiguration(null);
            } catch (Exception e) {
                log.error("Kafka kerberos authentication information refresh failed!");
            }
            // kerberos 相关设置
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "GSSAPI");
            // kafka broker的启动配置
            props.put("sasl.kerberos.service.name", kafkaKbrServiceName);
            System.setProperty("java.security.auth.login.config", kafkaLoginConf);
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
            return props;
        } catch (Exception e) {
            destroyProperty();
            throw new SourceException("init kafka properties error", e);
        }

    }


    public static List<String> getRecordsFromKafka(KafkaSourceDTO sourceDTO, String topic, String autoReset) {
        List<String> result = new ArrayList<>();
        Properties props = initProperties(sourceDTO);
        /*去除超时时间*/
        props.remove("request.timeout.ms");
        props.put("max.poll.records", MAX_POOL_RECORDS);
        /* 定义consumer */
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);) {
            List<TopicPartition> partitions = new ArrayList<>();
            List<PartitionInfo> all = consumer.partitionsFor(topic);
            for (PartitionInfo partitionInfo : all) {
                partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            }

            consumer.assign(partitions);
            //如果消息没有被消费过，可能出现无法移动offset的情况导致报错
            //https://stackoverflow.com/questions/41008610/kafkaconsumer-0-10-java-api-error-message-no-current-assignment-for-partition
            //主动拉去一次消息
            consumer.poll(1000);

            //根据autoReset 设置位移
            if (EARLIEST.equals(autoReset)) {
                consumer.seekToBeginning(partitions);
            } else {
                Map<TopicPartition, Long> partitionLongMap = consumer.endOffsets(partitions);
                for (Map.Entry<TopicPartition, Long> entry : partitionLongMap.entrySet()) {
                    long offset = entry.getValue() - MAX_POOL_RECORDS;
                    offset = offset > 0 ? offset : 0;
                    consumer.seek(entry.getKey(), offset);
                }
            }

            /* 读取数据，读取超时时间为100ms */
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                if (result.size() >= MAX_POOL_RECORDS) {
                    break;
                }
                result.add(record.value());
            }
        } catch (Exception e) {
            throw new SourceException(String.format("consumption data from kafka error: %s", e.getMessage()), e);
        } finally {
            destroyProperty();
        }
        return result;
    }

    public static List<KafkaPartitionDTO> getPartitions(KafkaSourceDTO sourceDTO, String topic) {
        Properties defaultKafkaConfig = initProperties(sourceDTO);
        List<KafkaPartitionDTO> partitionDTOS = Lists.newArrayList();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(defaultKafkaConfig)) {
            // PartitionInfo没有实现序列化接口，不能使用 fastJson 进行拷贝
            List<PartitionInfo> partitions = consumer.partitionsFor(topic);
            if (CollectionUtils.isEmpty(partitions)) {
                return partitionDTOS;
            }
            for (PartitionInfo partition : partitions) {
                // 所有副本
                List<KafkaPartitionDTO.Node> replicas = Lists.newArrayList();
                for (Node node : partition.replicas()) {
                    replicas.add(buildKafkaPartitionNode(node));
                }
                // 在isr队列中的副本
                List<KafkaPartitionDTO.Node> inSyncReplicas = Lists.newArrayList();
                for (Node node : partition.inSyncReplicas()) {
                    inSyncReplicas.add(buildKafkaPartitionNode(node));
                }
                KafkaPartitionDTO kafkaPartitionDTO = KafkaPartitionDTO.builder()
                        .topic(partition.topic())
                        .partition(partition.partition())
                        .leader(buildKafkaPartitionNode(partition.leader()))
                        .replicas(replicas.toArray(new KafkaPartitionDTO.Node[]{}))
                        .inSyncReplicas(inSyncReplicas.toArray(new KafkaPartitionDTO.Node[]{}))
                        .build();
                partitionDTOS.add(kafkaPartitionDTO);
            }
            return partitionDTOS;
        } catch (Exception e) {
            throw new SourceException(String.format("Get topic: %s partition information is exception：%s", topic, e.getMessage()), e);
        }
    }

    /**
     * 构建kafka node
     *
     * @param node kafka副本信息
     * @return common-loader中定义的kafka副本信息
     */
    private static KafkaPartitionDTO.Node buildKafkaPartitionNode(Node node) {
        if (Objects.isNull(node)) {
            return KafkaPartitionDTO.Node.builder().build();
        }
        return KafkaPartitionDTO.Node.builder()
                .host(node.host())
                .id(node.id())
                .idString(node.idString())
                .port(node.port())
                .rack(node.rack())
                .build();
    }


    /**
     * 从 kafka 消费数据
     *
     * @param sourceDTO       kafka 数据源信息
     * @param topic           消费主题
     * @param collectNum      收集条数
     * @param offsetReset     消费方式
     * @param timestampOffset 按时间消费
     * @param maxTimeWait     最大等待时间
     * @return 消费到的数据
     */
    public static List<String> consumeData(KafkaSourceDTO sourceDTO, String topic, Integer collectNum,
                                           String offsetReset, Long timestampOffset, Integer maxTimeWait) {
        // 结果集
        List<String> result = new ArrayList<>();
        Properties prop = initProperties(sourceDTO);
        // 每次拉取最大条数
        prop.put("max.poll.records", MAX_POOL_RECORDS);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop)) {
            List<TopicPartition> partitions = Lists.newArrayList();
            // 获取所有的分区
            List<PartitionInfo> allPartitions = consumer.partitionsFor(topic);
            for (PartitionInfo partitionInfo : allPartitions) {
                partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            }
            consumer.assign(partitions);

            // 从最早位置开始消费
            if (EConsumeType.EARLIEST.name().toLowerCase().equals(offsetReset)) {
                consumer.seekToBeginning(partitions);
            } else if (EConsumeType.TIMESTAMP.name().toLowerCase().equals(offsetReset) && Objects.nonNull(timestampOffset)) {
                Map<TopicPartition, Long> timestampsToSearch = Maps.newHashMap();
                for (TopicPartition partition : partitions) {
                    timestampsToSearch.put(partition, timestampOffset);
                }
                Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);
                // 没有找到offset 则从当前时间开始消费
                if (MapUtils.isEmpty(offsetsForTimes)) {
                    consumer.seekToEnd(partitions);
                } else {
                    for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetsForTimes.entrySet()) {
                        consumer.seek(entry.getKey(), entry.getValue().offset());
                    }
                }
            } else {
                // 默认从最当前位置开始消费
                if (EConsumeType.LATEST.name().toLowerCase().equals(offsetReset)) {
                    consumer.seekToEnd(partitions);
                }
            }

            // 开始时间
            long start = System.currentTimeMillis();
            // 消费结束时间
            long endTime = start + maxTimeWait * 1000;
            while (true) {
                long nowTime = System.currentTimeMillis();
                if (nowTime >= endTime) {
                    break;
                }
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    result.add(value);
                    if (result.size() >= collectNum) {
                        break;
                    }
                }
                if (result.size() >= collectNum) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("consumption data from Kafka exception: %s", e.getMessage()), e);
        } finally {
            destroyProperty();
        }
        return result;
    }

    /**
     * 获取 kafka 消费者组列表
     *
     * @param sourceDTO kakfa 数据源信息
     * @param topic     kafka 主题
     * @return 消费者组列表
     */
    public static List<String> listConsumerGroup(KafkaSourceDTO sourceDTO, String topic) {
        List<String> consumerGroups = new ArrayList<>();
        Properties prop = initProperties(sourceDTO);
        // 获取kafka client
        kafka.admin.AdminClient adminClient = kafka.admin.AdminClient.create(prop);
        try {
            // scala seq 转 java list
            List<GroupOverview> groups = JavaConversions.seqAsJavaList(adminClient.listAllConsumerGroupsFlattened().toSeq());
            groups.forEach(group -> consumerGroups.add(group.groupId()));
            // 不指定topic 全部返回
            if (StringUtils.isBlank(topic)) {
                return consumerGroups;
            }
            List<String> consumerGroupsByTopic = Lists.newArrayList();
            for (String groupId : consumerGroups) {
                kafka.admin.AdminClient.ConsumerGroupSummary groupSummary = adminClient.describeConsumerGroup(groupId, 5000L);
                // 消费者组不存在的情况
                if (Objects.isNull(groupSummary) || "Dead".equals(groupSummary.state())) {
                    continue;
                }
                Map<TopicPartition, Object> offsets = JavaConversions.mapAsJavaMap(adminClient.listGroupOffsets(groupId));
                for (TopicPartition topicPartition : offsets.keySet()) {
                    if (topic.equals(topicPartition.topic())) {
                        consumerGroupsByTopic.add(groupId);
                        break;
                    }
                }
            }
            return consumerGroupsByTopic;
        } catch (Exception e) {
            log.error("listConsumerGroup error:{}", e.getMessage(), e);
        } finally {
            if (Objects.nonNull(adminClient)) {
                adminClient.close();
            }
            destroyProperty();
        }
        return Lists.newArrayList();
    }

    /**
     * 获取 kafka 消费者组详细信息
     *
     * @param sourceDTO kafka 数据源信息
     * @param groupId   消费者组
     * @param srcTopic  kafka 主题
     * @return 消费者组详细信息
     */
    public static List<KafkaConsumerDTO> getGroupInfoByGroupId(KafkaSourceDTO sourceDTO, String groupId, String srcTopic) {
        List<KafkaConsumerDTO> result = Lists.newArrayList();
        Properties prop = initProperties(sourceDTO);
        // 获取kafka client
        kafka.admin.AdminClient adminClient = kafka.admin.AdminClient.create(prop);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop)) {

            if (StringUtils.isNotBlank(groupId)) {
                kafka.admin.AdminClient.ConsumerGroupSummary groupSummary = adminClient.describeConsumerGroup(groupId, 5000L);
                // 消费者组不存在的情况
                if (Objects.isNull(groupSummary) || "Dead".equals(groupSummary.state())) {
                    return result;
                }
            } else {
                // groupId 为空的时候获取所有的分区
                List<PartitionInfo> allPartitions = consumer.partitionsFor(srcTopic);
                for (PartitionInfo partitionInfo : allPartitions) {
                    TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                    // 指定当前分区
                    consumer.assign(Lists.newArrayList(topicPartition));
                    consumer.seekToEnd(Lists.newArrayList(topicPartition));
                    long logEndOffset = consumer.position(topicPartition);
                    String brokerHost = Objects.isNull(partitionInfo.leader()) ? null : partitionInfo.leader().host();
                    // 组装kafka consumer 信息
                    KafkaConsumerDTO kafkaConsumerDTO = KafkaConsumerDTO.builder()
                            .groupId(groupId)
                            .topic(partitionInfo.topic())
                            .partition(partitionInfo.partition())
                            .logEndOffset(logEndOffset)
                            .brokerHost(brokerHost)
                            .build();
                    result.add(kafkaConsumerDTO);
                }
                return result;
            }

            Map<TopicPartition, Object> offsets = JavaConversions.mapAsJavaMap(adminClient.listGroupOffsets(groupId));
            for (TopicPartition topicPartition : offsets.keySet()) {
                String topic = topicPartition.topic();
                // 过滤指定topic 下的 partition
                if (StringUtils.isNotBlank(srcTopic) && !srcTopic.equals(topic)) {
                    continue;
                }
                int partition = topicPartition.partition();
                // 当前消费位置
                Long currentOffset = (Long) offsets.get(topicPartition);
                List<TopicPartition> singleTopicPartition = Lists.newArrayList(topicPartition);
                // 指定当前分区
                consumer.assign(singleTopicPartition);
                consumer.seekToEnd(singleTopicPartition);
                long logEndOffset = consumer.position(topicPartition);

                List<PartitionInfo> partitions = consumer.partitionsFor(topic);

                // 组装kafka consumer 信息
                KafkaConsumerDTO kafkaConsumerDTO = KafkaConsumerDTO.builder()
                        .groupId(groupId)
                        .topic(topic)
                        .partition(partition)
                        .currentOffset(currentOffset)
                        .logEndOffset(logEndOffset)
                        .lag(logEndOffset - currentOffset)
                        .build();

                // 查询当前分区 leader 所在机器的host
                for (PartitionInfo partitionInfo : partitions) {
                    if (partition == partitionInfo.partition() && Objects.nonNull(partitionInfo.leader())) {
                        kafkaConsumerDTO.setBrokerHost(partitionInfo.leader().host());
                        break;
                    }
                }
                result.add(kafkaConsumerDTO);
            }
        } catch (Exception e) {
            log.error("getGroupInfoByGroupId error:{}", e.getMessage(), e);
        } finally {
            if (Objects.nonNull(adminClient)) {
                adminClient.close();
            }
            destroyProperty();
        }
        return result;
    }

    /**
     * 获取topic的分区数和副本数
     *
     * @return 分区数和副本数
     */
    public static Map<String, Integer> getTopicPartitionCountAndReplicas(KafkaSourceDTO sourceDTO, String topic) throws Exception {
        Properties properties = initProperties(sourceDTO);
        Properties clientProp = removeExtraParam(properties);
        AdminClient client = AdminClient.create(clientProp);
        //存放结果
        Map<String, Integer> countAndReplicas = new HashMap<>();
        DescribeTopicsResult result = client.describeTopics(Collections.singletonList(topic));
        Map<String, KafkaFuture<TopicDescription>> values = result.values();
        KafkaFuture<TopicDescription> topicDescription = values.get(topic);
        int partitions, replicas;
        try {
            partitions = topicDescription.get().partitions().size();
            replicas = topicDescription.get().partitions().iterator().next().replicas().size();
        } catch (Exception e) {
            log.error("get topic partition count and replicas error:{}", e.getMessage(), e);
            throw new Exception(e);
        } finally {
            client.close();
        }
        countAndReplicas.put(KEY_PARTITIONS, partitions);
        countAndReplicas.put(KEY_REPLICAS, replicas);
        return countAndReplicas;
    }

    /**
     * 删除properties中kafka client 不需要的的参数
     *
     * @param properties properties
     * @return prop
     */
    private static Properties removeExtraParam(Properties properties) {
        Properties prop = new Properties();
        prop.putAll(properties);
        //以下这些参数kafka client不需要
        prop.remove("enable.auto.commit");
        prop.remove("auto.commit.interval.ms");
        prop.remove("session.timeout.ms");
        prop.remove("max.poll.records");
        prop.remove("auto.offset.reset");
        prop.remove("key.deserializer");
        prop.remove("value.deserializer");
        return prop;
    }
}
