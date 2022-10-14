package com.dtstack.taier.datasource.api.utils;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.context.enhance.Enhance;
import com.dtstack.taier.datasource.api.context.RuntimeContext;

/**
 * client utils
 *
 * @author ：wangchuan
 * date：Created in 11:24 2022/9/23
 * company: www.dtstack.com
 */
public class ClientUtils {

    /**
     * 设置 client 运行时上下文
     *
     * @param client         client
     * @param runtimeContext 运行时上下文
     */
    public static void setRuntimeContext(Client client, RuntimeContext runtimeContext) {
        if (client instanceof Enhance) {
            ((Enhance) client).setRuntimeContext(runtimeContext);
        }
    }
}
