package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosPluginInfo;
import org.apache.ibatis.annotations.Param;

/**
 * Reason:
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosPluginInfoMapper {

    Long replaceInto(@Param("pluginKey") String pluginKey, @Param("pluginInfo") String pluginInfo, @Param("type") int type);

    RdosPluginInfo getByKey(@Param("pluginKey") String pluginKey);

    String getPluginInfo(@Param("id") Long id);
}
