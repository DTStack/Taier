package com.dtstack.taier.datasource.plugin.es.pool;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:15 2020/8/3
 * @Description：
 */
@Slf4j
public class ElasticSearchPoolFactory implements PooledObjectFactory<RestHighLevelClient> {

    private AtomicReference<Set<String>> nodesReference = new AtomicReference<>();

    private String clusterName;

    private String username;

    private String password;

    public ElasticSearchPoolFactory(ElasticSearchPoolConfig config) {
        this.clusterName = config.getClusterName();
        this.nodesReference.set(config.getNodes());
        this.username = config.getUsername();
        this.password = config.getPassword();
    }

    /**
     * 当对象池中没有多余的对象可以用的时候，调用此方法。
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
        // 构建es集群中的所有节点
        HttpHost[] nodes = new HttpHost[nodesReference.get().size()];
        nodes = nodesReference.get().stream()
                .map(hostPorts -> HttpHost.create(hostPorts.trim()))
                .collect(Collectors.toList()).toArray(nodes);

        RestClientBuilder builder = RestClient.builder(nodes);

        // 有密码就增加密码
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder = builder.setHttpClientConfigCallback(e -> e.setDefaultCredentialsProvider(credentialsProvider));
        }

        RestHighLevelClient client = new RestHighLevelClient(builder);
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
    public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        RestHighLevelClient client = pooledObject.getObject();
        if (Objects.nonNull(client) && client.ping(RequestOptions.DEFAULT)) {
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
    public boolean validateObject(PooledObject<RestHighLevelClient> pooledObject) {
        RestHighLevelClient client = pooledObject.getObject();
        try {
            return client.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            return false;
        }
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
    public void activateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        RestHighLevelClient client = pooledObject.getObject();
        // ping 一下，使其没有空闲
        client.ping(RequestOptions.DEFAULT);
    }

    /**
     * 功能描述：钝化资源对象
     * 将连接返还给连接池时，调用此方法。
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        // nothing
    }
}
