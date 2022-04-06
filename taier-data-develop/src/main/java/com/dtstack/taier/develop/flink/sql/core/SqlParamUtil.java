package com.dtstack.taier.develop.flink.sql.core;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * sql 参数工具
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class SqlParamUtil {

    /**
     * 获得对应版本的key
     *
     * @param flinkVersion      flink 版本
     * @param sqlParamEnumBases 参数枚举集合
     * @return 前端 key 对应的 flinkX 需要的 key
     */
    public static Map<String, String> getFrontFlinkXKeyMap(FlinkVersion flinkVersion, ISqlParamEnum[] sqlParamEnumBases) {
        Map<String, String> frontFlinkXKeyMap = Maps.newHashMap();
        if (ArrayUtils.isEmpty(sqlParamEnumBases)) {
            return frontFlinkXKeyMap;
        }
        switch (flinkVersion) {
            case FLINK_112:
                for (ISqlParamEnum paramEnumBase : sqlParamEnumBases) {
                    MapUtil.putIfValueNotBlank(frontFlinkXKeyMap, paramEnumBase.getFront(), paramEnumBase.getFlink112());
                }
                break;
            case FLINK_110:
            case FLINK_180:
                for (ISqlParamEnum paramEnumBase : sqlParamEnumBases) {
                    MapUtil.putIfValueNotBlank(frontFlinkXKeyMap, paramEnumBase.getFront(), paramEnumBase.getFlink110());
                }
                break;
            default:
                throw new DtCenterDefException(String.format("不支持flink版本: %s", flinkVersion.getType()));
        }
        return frontFlinkXKeyMap;
    }

    /**
     * 转换参数 value 信息
     *
     * @param allParam          所有参数信息
     * @param frontKey          前端入参 key
     * @param flinkVersion      flink 版本
     * @param sqlParamEnumBases 参数枚举
     * @param isDelete          找不到对应 value 时是否清除
     */
    public static void convertParamValue(Map<String, Object> allParam, String frontKey, FlinkVersion flinkVersion,
                                         ISqlParamEnum[] sqlParamEnumBases, boolean isDelete) {
        String value = MapUtils.getString(allParam, frontKey);
        if (StringUtils.isEmpty(value)) {
            return;
        }
        for (ISqlParamEnum sqlParamEnumBase : sqlParamEnumBases) {
            if (sqlParamEnumBase.getFront().equals(value)) {
                String defaultValue;
                switch (flinkVersion) {
                    case FLINK_180:
                    case FLINK_110:
                        defaultValue = sqlParamEnumBase.getFlink110();
                        break;
                    default:
                        defaultValue = sqlParamEnumBase.getFlink112();
                        break;
                }
                allParam.put(frontKey, defaultValue);
                return;
            }
        }
        // value 不符合条件时清除
        if (isDelete) {
            allParam.remove(frontKey);
        }
    }
}
