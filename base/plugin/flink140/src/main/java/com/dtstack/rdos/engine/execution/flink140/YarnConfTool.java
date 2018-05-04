package com.dtstack.rdos.engine.execution.flink140;

import avro.shaded.com.google.common.collect.Lists;
import com.dtstack.rdos.common.util.MathUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * FIXME 公共配置--之后抽到common
 * Date: 2018/5/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class YarnConfTool {

    public static final String YARN_RESOURCEMANAGER_HA_RM_IDS = "yarn.resourcemanager.ha.rm-ids";
    public static final String YARN_RESOURCEMANAGER_ADDRESS = "yarn.resourcemanager.address.%s";
    public static final String YARN_RESOURCEMANAGER_HA_ENABLED = "yarn.resourcemanager.ha.enabled";
    public static final String YARN_RESOURCEMANAGER_WEBAPP_ADDRESS = "yarn.resourcemanager.webapp.address.%s";

    public static String getYarnResourcemanagerHaRmIds(Map<String, Object> conf){
        String resourceManagerHaRmIds = MathUtil.getString(conf.get(YARN_RESOURCEMANAGER_HA_RM_IDS));
        Preconditions.checkNotNull(resourceManagerHaRmIds, YARN_RESOURCEMANAGER_HA_RM_IDS + "不能为空");
        return resourceManagerHaRmIds;
    }

    public static List<String> getYarnResourceManagerAddressKeys(Map<String, Object> conf){
        String haRmIds = getYarnResourcemanagerHaRmIds(conf);
        String[] haRmIdArr = haRmIds.split(",");

        List<String> rmAddressKeys = Lists.newArrayList();
        for(String rmId : haRmIdArr){
            String rmAddress = String.format(YARN_RESOURCEMANAGER_ADDRESS, rmId);
            rmAddressKeys.add(rmAddress);
        }

        return rmAddressKeys;
    }

    public static String getYarnResourceManagerAddressVal(Map<String, Object> conf, String key){
        String rmAddress = MathUtil.getString(conf.get(key));
        return rmAddress;
    }

    public static String getYarnResourcemanagerHaEnabled(Map<String, Object> conf){
        String haEnable = MathUtil.getString(conf.get(YARN_RESOURCEMANAGER_HA_ENABLED));
        if(StringUtils.isEmpty(haEnable)){
            return "true";
        }

        return haEnable;
    }

    public static List<String> getYarnResourceManagerWebAppAddressKeys(Map<String, Object> conf){
        String haRmIds = getYarnResourcemanagerHaRmIds(conf);
        String[] haRmIdArr = haRmIds.split(",");

        List<String> rmWebAppAddressKeys = Lists.newArrayList();
        for(String rmId : haRmIdArr){
            String rmWebAppAddress = String.format(YARN_RESOURCEMANAGER_WEBAPP_ADDRESS, rmId);
            rmWebAppAddressKeys.add(rmWebAppAddress);
        }

        return rmWebAppAddressKeys;
    }

}
