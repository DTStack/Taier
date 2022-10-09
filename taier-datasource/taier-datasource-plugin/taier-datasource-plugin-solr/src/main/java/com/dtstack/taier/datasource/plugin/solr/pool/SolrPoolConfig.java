package com.dtstack.taier.datasource.plugin.solr.pool;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 下午5:14 2021/5/7
 * @Description：
 */
@Data
public class SolrPoolConfig extends GenericObjectPoolConfig {

    private String zkHosts;

    private String chroot;

}
