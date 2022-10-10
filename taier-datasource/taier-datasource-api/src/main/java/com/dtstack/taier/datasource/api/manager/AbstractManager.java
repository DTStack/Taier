package com.dtstack.taier.datasource.api.manager;

import com.dtstack.taier.datasource.api.context.ManagerEnhance;
import com.dtstack.taier.datasource.api.context.RuntimeContext;
import com.dtstack.taier.datasource.api.exception.InitializeException;
import com.dtstack.taier.datasource.api.initialize.AbstractEnvInitialize;

/**
 * 初始化 manager
 *
 * @author ：wangchuan
 * date：Created in 19:56 2022/9/23
 * company: www.dtstack.com
 */
public abstract class AbstractManager extends AbstractEnvInitialize implements ManagerEnhance {

    private RuntimeContext runtimeContext;

    private ManagerFactory managerFactory;

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        if (this.runtimeContext != null) {
            return this.runtimeContext;
        } else {
            throw new InitializeException("The runtime context has not been initialized.");
        }
    }

    @Override
    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    @Override
    public ManagerFactory getManagerFactory() {
        if (this.managerFactory != null) {
            return this.managerFactory;
        } else {
            throw new InitializeException("The manager factory has not been initialized.");
        }
    }
}
