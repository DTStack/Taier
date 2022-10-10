package com.dtstack.taier.datasource.plugin.es.pool;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:19 2020/8/3
 * @Description：ElasticSearch 连接池配置类
 */
@Data
public class ElasticSearchPoolConfig extends GenericObjectPoolConfig {

    private String clusterName;

    Set<String> nodes = new HashSet<String>();

    private String username;

    private String password;

}
