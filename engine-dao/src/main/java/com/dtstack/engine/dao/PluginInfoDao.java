package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.PluginInfo;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface PluginInfoDao {

    Long replaceInto(PluginInfo pluginInfo);

    PluginInfo getByKey(@Param("pluginKey") String pluginKey);

    String getPluginInfo(@Param("id") Long id);
}
