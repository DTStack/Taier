package com.dtstack.taier.datasource.api.utils;

import com.dtstack.taier.datasource.api.context.RuntimeContext;
import com.dtstack.taier.datasource.api.manager.AbstractManager;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;

/**
 * manager utils
 *
 * @author ：wangchuan
 * date：Created in 11:24 2022/9/23
 * company: www.dtstack.com
 */
public class ManagerUtils {

    /**
     * 为 datasource 中的 manager 设置上下文和 ManagerFactory
     *
     * @param abstractManager 需要处理的 manager
     * @param runtimeContext  上下文信息
     * @param managerFactory  ManagerFactory
     */
    public static void setRCAndMF(AbstractManager abstractManager, RuntimeContext runtimeContext, ManagerFactory managerFactory) {
        abstractManager.setRuntimeContext(runtimeContext);
        abstractManager.setManagerFactory(managerFactory);
    }
}
