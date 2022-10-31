/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.solr.pool;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 下午5:14 2021/5/7
 * @Description：
 */
@Slf4j
public class SolrPoolFactory implements PooledObjectFactory<CloudSolrClient> {

    private String zkHosts;

    private String chroot;

    public SolrPoolFactory(SolrPoolConfig config) {
        this.zkHosts = config.getZkHosts();
        this.chroot = config.getChroot();
    }

    /**
     * 当对象池中没有多余的对象可以用的时候，调用此方法。
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<CloudSolrClient> makeObject() throws Exception {
        Optional<String> chrootOptional = StringUtils.isNotBlank(chroot) ? Optional.of(chroot) : Optional.empty();
        CloudSolrClient client = new CloudSolrClient.Builder(Collections.singletonList(zkHosts), chrootOptional).build();
        // 连接池中的连接池对象
        return new DefaultPooledObject<>(client);
    }

    /**
     * 销毁
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<CloudSolrClient> pooledObject) throws Exception {
        SolrClient client = pooledObject.getObject();
        if (Objects.nonNull(client)) {
            try {
                client.close();
            } catch (Exception e) {
                throw new SourceException("close client error", e);
            }
        }
    }

    /**
     * 功能描述：判断连接对象是否有效，有效返回 true，无效返回 false
     * 什么时候会调用此方法
     * 1：从连接池中获取连接的时候，参数 testOnBorrow 或者 testOnCreate 中有一个 配置 为 true 时，
     * 则调用 factory.validateObject() 方法.
     * 2：将连接返还给连接池的时候，参数 testOnReturn，配置为 true 时，调用此方法.
     * 3：连接回收线程，回收连接的时候，参数 testWhileIdle，配置为 true 时，调用此方法.
     *
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<CloudSolrClient> pooledObject) {
        CloudSolrClient client = pooledObject.getObject();
        try {
            client.connect();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 功能描述：激活资源对象
     * 什么时候会调用此方法
     * 1：从连接池中获取连接的时候
     * 2：连接回收线程，连接资源的时候，根据配置的 testWhileIdle 参数，
     * 判断 是否执行 factory.activateObject()方法，true 执行，false 不执行
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<CloudSolrClient> pooledObject) throws Exception {
        CloudSolrClient client = pooledObject.getObject();
        // connect 一下，使其没有空闲
        client.connect();
    }

    /**
     * 功能描述：钝化资源对象
     * 将连接返还给连接池时，调用此方法。
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<CloudSolrClient> pooledObject) throws Exception {
        // nothing
    }
}
