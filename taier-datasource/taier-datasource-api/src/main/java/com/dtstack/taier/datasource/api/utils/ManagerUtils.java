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

    public static void setRCAndMF(AbstractManager abstractManager, RuntimeContext runtimeContext, ManagerFactory managerFactory) {
        abstractManager.setRuntimeContext(runtimeContext);
        abstractManager.setManagerFactory(managerFactory);
    }
}
