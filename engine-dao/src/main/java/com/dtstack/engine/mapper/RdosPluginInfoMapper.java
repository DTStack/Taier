package com.dtstack.engine.mapper;

import com.dtstack.engine.domain.RdosPluginInfo;
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
