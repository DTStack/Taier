package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:27 2020/5/22
 * @Description：Kafka 数据源信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaSourceDTO extends AbstractSourceDTO {
    /**
     * ZK 的地址
     */
    private String url;


    /**
     * kafka Brokers 的地址
     */
    private String brokerUrls;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据源类型
     */
    protected Integer sourceType;


    /**
     * 认证类型
     */
    private String authentication;

    /**
     * 是否缓存
     */
    @Builder.Default
    protected Boolean isCache = false;

    @Override
    public Integer getSourceType() {
        return DataSourceType.KAFKA.getVal();
    }
}
