package com.dtstack.taier.datasource.api.manager.list;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.config.SourceConfig;
import com.dtstack.taier.datasource.api.manager.AbstractManager;
import com.dtstack.taier.datasource.api.proxy.ClientExecuteProxy;
import com.dtstack.taier.datasource.api.utils.ClassUtils;
import com.dtstack.taier.datasource.api.utils.ClassloaderUtils;
import com.dtstack.taier.datasource.api.utils.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * client manager, client 获取入口
 *
 * @author ：wangchuan
 * date：Created in 16:09 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public class ClientManager extends AbstractManager {

    /**
     * client cache
     * - key -> pluginName
     * - - key -> clientType
     * - - - key -> connectorName
     */
    private Map<String, Map<String, Map<String, Client>>> clientCache = new ConcurrentHashMap<>();

    @Override
    public void open() {
        // ignore
    }

    @Override
    public void close() {
        clientCache.clear();
        clientCache = null;
    }

    /**
     * 注册并 proxy client 并返回
     *
     * @param clientType    client type
     * @param pluginName    plugin name
     * @param <T>           client 类型
     * @param connectorName connector name
     * @param sourceConfig  数据源配置信息
     * @return proxy client
     */
    public <T extends Client> T registerClient(Class<T> clientType, String pluginName, String connectorName, SourceConfig sourceConfig) {
        T cacheClient = getClient(clientType, pluginName, connectorName);
        if (null != cacheClient) {
            return cacheClient;
        }
        synchronized (this) {
            T interCacheClient = getClient(clientType, pluginName, connectorName);
            if (null != interCacheClient) {
                return interCacheClient;
            }
            ClassLoader classLoader = getManagerFactory().getManager(ClassloaderManager.class).getClassloaderByPluginName(pluginName);
            T proxyClient = ClassloaderUtils.executeAndReset(() -> {
                ServiceLoader<T> clientLoader = ServiceLoader.load(clientType);
                Iterator<T> clientLoaderIterator = clientLoader.iterator();
                if (!clientLoaderIterator.hasNext()) {
                    throw new RuntimeException(String.format("This plugin [%s] is not support.", pluginName));
                }
                T client = clientLoaderIterator.next();
                ClientUtils.setRuntimeContext(client, getRuntimeContext());
                // open
                client.open(sourceConfig);
                // 创建代理
                return ClientExecuteProxy.getProxyClient(client, clientType, getRuntimeContext().getConfig(), getManagerFactory());
            }, classLoader);
            // add cache
            putProxyClient(clientType, pluginName, connectorName, proxyClient);
            return proxyClient;
        }
    }

    /**
     * 根据插件名称销毁所有的 client
     *
     * @param pluginName 插件名称
     */
    public void destroyByPluginName(String pluginName) {
        Map<String, Map<String, Client>> clientTypeMap = clientCache.remove(pluginName);
        // null 直接返回
        if (null == clientTypeMap || clientTypeMap.isEmpty()) {
            return;
        }
        // 等待关闭的所有 client
        List<Client> waitDestroyClient = clientTypeMap
                .entrySet()
                .stream()
                .filter(entry -> null != entry.getValue())
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        log.info("wait destroy client num: {}, plugin name: {}", waitDestroyClient.size(), pluginName);
        for (Client client : waitDestroyClient) {
            try {
                client.close();
            } catch (Exception e) {
                // ignore error
                log.error("close client error, plugin name: {}", pluginName, e);
            }
        }
        log.info("destroy client success, plugin name: {}", pluginName);
    }

    /**
     * 直接从已注册 client 中获取指定 client
     *
     * @param clientType    client type
     * @param pluginName    插件名称
     * @param <T>           client 类型
     * @param connectorName connector name
     * @return proxy client
     */
    private <T extends Client> T getClient(Class<T> clientType, String pluginName, String connectorName) {
        Map<String, Map<String, Client>> pluginNameMap = clientCache.get(pluginName);
        if (MapUtils.isEmpty(pluginNameMap)) {
            return null;
        }
        Map<String, Client> clientTypeMap = pluginNameMap.get(clientType.getName());
        if (MapUtils.isEmpty(clientTypeMap)) {
            return null;
        }
        Client client = clientTypeMap.get(connectorName);
        return ClassUtils.castOrThrow(clientType, client);
    }

    /**
     * 缓存的代理 client
     *
     * @param clientType    client class
     * @param pluginName    插件名称
     * @param connectorName connect name
     * @param proxyClient   代理 client
     * @param <T>           client 类型
     */
    private <T extends Client> void putProxyClient(Class<T> clientType, String pluginName, String connectorName, T proxyClient) {
        clientCache
                .computeIfAbsent(pluginName, key -> new HashMap<>())
                .computeIfAbsent(clientType.getName(), key -> new HashMap<>())
                .put(connectorName, proxyClient);
    }
}
