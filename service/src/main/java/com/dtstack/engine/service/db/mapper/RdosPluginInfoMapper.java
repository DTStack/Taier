package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosPluginInfo;
import org.apache.ibatis.annotations.Param;

/**
 * Reason:
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosPluginInfoMapper {

    Integer replaceInto(RdosPluginInfo rdosPluginInfo);

    RdosPluginInfo getByKey(@Param("pluginKey") String pluginKey);

    String getPluginInfo(@Param("id") Long id);
}
