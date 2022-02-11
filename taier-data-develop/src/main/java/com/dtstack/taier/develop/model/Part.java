package com.dtstack.taier.develop.model;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.ComponentConfig;

import java.util.List;

public interface Part {
    EComponentType getType();

    /**
     * 获取组件显示版本 对应的 具体版本值
     *
     * @return
     */
    String getVersionValue();

    /**
     * 加载组件模版
     *
     * @return
     */
    List<ComponentConfig> loadTemplate();

    /**
     * 获取组件对应pluginName
     *
     * @return
     */
    String getPluginName();

    /**
     * 获取组件依赖的资源组件
     *
     * @return
     */
    EComponentType getResourceType();

    /**
     * 获取组件版本适配参数
     * @return
     */
    Long getExtraVersionParameters();
}
