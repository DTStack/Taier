package com.dtstack.taier.datasource.plugin.es5.pool;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Set;

/**
 * 连接吃配置类
 *
 * @author ：wangchuan
 * date：Created in 下午3:04 2021/12/8
 * company: www.dtstack.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ElasticSearchPoolConfig extends GenericObjectPoolConfig {

    private String clusterName;

    Set<String> nodes = Sets.newHashSet();

    private String username;

    private String password;

}
