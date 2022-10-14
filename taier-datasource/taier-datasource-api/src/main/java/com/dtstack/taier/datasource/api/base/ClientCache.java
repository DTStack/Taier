package com.dtstack.taier.datasource.api.base;

import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.client.IHdfsFile;
import com.dtstack.taier.datasource.api.client.IKafka;
import com.dtstack.taier.datasource.api.client.IKerberos;
import com.dtstack.taier.datasource.api.client.IRestful;
import com.dtstack.taier.datasource.api.client.ITable;
import com.dtstack.taier.datasource.api.client.IYarn;
import com.dtstack.taier.datasource.api.exception.InitializeException;
import com.dtstack.taier.datasource.api.manager.list.ClientManager;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * client cache
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Slf4j
public class ClientCache {

    /**
     * client manager
     */
    private static ClientManager clientManager;

    /**
     * 设置运行环境
     *
     * @param clientManager client manager
     */
    public static void setEnv(ClientManager clientManager) {
        if (Objects.isNull(ClientCache.clientManager)) {
            ClientCache.clientManager = clientManager;
        }
    }

    /**
     * 获取指定类型的 client
     *
     * @param clientType     clazz type
     * @param dataSourceType 数据源类型
     * @param <T>            client 类型
     * @return client
     */
    public static <T extends Client> T getClientByType(Class<T> clientType, Integer dataSourceType) {
        if (Objects.isNull(clientManager)) {
            throw new InitializeException("client manager can't be null.");
        }
        DataSourceType sourceType = DataSourceType.getSourceType(dataSourceType);
        return clientManager.registerClient(clientType, sourceType.getPluginName(), sourceType.getName(), null);
    }

    public static IClient getClient(Integer dataSourceType) {
        return getClientByType(IClient.class, dataSourceType);
    }

    /**
     * 获取 HDFS 文件客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return hdfs 文件客户端
     */
    public static IHdfsFile getHdfs(Integer dataSourceType) {
        return getClientByType(IHdfsFile.class, dataSourceType);
    }

    /**
     * 获取 KAFKA 客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return kafka 客户端
     */
    public static IKafka getKafka(Integer dataSourceType) {
        return getClientByType(IKafka.class, dataSourceType);
    }

    /**
     * 获取 Kerberos 服务客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return Kerberos 服务客户端
     */
    public static IKerberos getKerberos(Integer dataSourceType) {
        return getClientByType(IKerberos.class, dataSourceType);
    }

    /**
     * 获取 table Client 客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return table 客户端
     */
    public static ITable getTable(Integer dataSourceType) {
        return getClientByType(ITable.class, dataSourceType);
    }

    /**
     * 获取 restful Client 客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return restful Client 客户端
     */
    public static IRestful getRestful(Integer dataSourceType) {
        return getClientByType(IRestful.class, dataSourceType);
    }

    /**
     * 获取 yarn Client 客户端
     *
     * @param dataSourceType 数据源类型 val
     * @return yarn Client 客户端
     */
    public static IYarn getYarn(Integer dataSourceType) {
        return getClientByType(IYarn.class, dataSourceType);
    }
}
