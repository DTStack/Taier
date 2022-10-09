package com.dtstack.taier.datasource.api.manager;

import com.dtstack.taier.datasource.api.context.RuntimeContext;
import com.dtstack.taier.datasource.api.initialize.AbstractEnvInitialize;
import com.dtstack.taier.datasource.api.manager.list.ClassloaderManager;
import com.dtstack.taier.datasource.api.manager.list.ClientManager;
import com.dtstack.taier.datasource.api.manager.list.ProxyThreadPoolManager;
import com.dtstack.taier.datasource.api.utils.ManagerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * manager 工厂, 负责创建、初始化、销毁 manager 等, 确保每种 manager 只初始化一次
 *
 * @author ：wangchuan
 * date：Created in 19:04 2022/9/23
 * company: www.dtstack.com
 */
public class ManagerFactory {

    // 等待初始化的 manager
    List<AbstractManager> waitInitManagerList = new ArrayList<>();

    /**
     * manager 缓存
     */
    private final Map<Class<?>, AbstractManager> managerCache = new ConcurrentHashMap<>();

    /**
     * 根据 type 获取 manager
     *
     * @param managerType manager 类型
     * @param <T>         类型范型
     * @return manager
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractManager> T getManager(Class<T> managerType) {
        return (T) managerCache.get(managerType);
    }

    /**
     * 逐步初始化所有 manager
     *
     * @param runtimeContext 运行上下文
     */
    public void init(RuntimeContext runtimeContext) {

        // 后期自动注入, 通过注解方式实现, 支持依赖判断
        Collections.addAll(
                waitInitManagerList,
                new ClassloaderManager(),
                new ClientManager(),
                new ProxyThreadPoolManager()
        );

        // 依次完成初始化
        waitInitManagerList.forEach(manager -> {
            ManagerUtils.setRCAndMF(manager, runtimeContext, this);
            manager.init(runtimeContext.getConfig());
            managerCache.put(manager.getClass(), manager);
        });
    }

    /**
     * 关闭并清理所有的资源
     */
    public void close() {
        waitInitManagerList.forEach(AbstractEnvInitialize::destroy);
    }
}
