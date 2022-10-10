package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.enums.RedisMode;
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
 * @Date ：Created in 17:26 2020/5/22
 * @Description：Redis 数据源信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RedisSourceDTO extends AbstractSourceDTO {
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
     * 是否缓存
     */
    @Builder.Default
    protected Boolean isCache = false;

    /**
     * 端口号
     */
    private String hostPort;

    /**
     * 模式即 DBName
     */
    private String schema;

    /**
     * Redis 部署模式
     */
    private RedisMode redisMode;

    /**
     * 如果为 master slave 的则为 master 的地址
     */
    private String master;


    @Override
    public Integer getSourceType() {
        return DataSourceType.REDIS.getVal();
    }
}
