package com.dtstack.taier.datasource.api.context.enhance;


import com.dtstack.taier.datasource.api.manager.ManagerFactory;

/**
 * manager rich context
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/9/23
 * company: www.dtstack.com
 */
public interface ManagerEnhance extends Enhance {

    void setManagerFactory(ManagerFactory managerFactory);

    ManagerFactory getManagerFactory();
}
