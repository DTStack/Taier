package com.dtstack.engine.datasource.common.enums.datasource;

import com.dtstack.engine.datasource.common.utils.Collects;
import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.Strings;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 产品枚举类 对应 com.dtstack.dtcenter.common.enums.AppType
 * @description:
 * @author: liuxx
 * @date: 2021/3/11
 */
public enum AppTypeEnum {
    /**
     * 离线计算
     */
    RDOS(1, "batch", "离线开发", "rdos"),
    /**
     * 数据质量
     */
    DQ(2, "valid", "数据质量", "dataQuality"),
    /**
     * 数据api
     */
    API(3, "api", "数据服务", "dataApi"),
    /**
     * 标签引擎
     */
    TAG(4, "tag", "智能标签", "tagEngine"),
    /**
     *
     */
    MAP(5, "map", "数据地图", ""),
    /**
     * 控制台
     */
    CONSOLE(6, "console", "控制台", ""),
    /**
     * 实时
     */
    STREAM(7, "stream", "实时开发", "stream"),
    /**
     * 数据科学(算法)
     */
    DATASCIENCE(8, "ai", "算法开发", "science"),
    /**
     * 数据资产
     */
    DATAASSETS(9, "assets", "数据资产", "dataAssets"),
    /**
     * 指标平台
     */
    INDEX(10, "index", "指标平台", "index");


    AppTypeEnum(Integer type, String appCode, String name, String uicCode) {
        this.type = type;
        this.appCode = appCode;
        this.name = name;
        this.uicCode = uicCode;
    }

    /**
     * 匹配uicCode列表的枚举
     * @param uicCodeList
     * @return
     */
    public static List<AppTypeEnum> mappingUic(Collection<String> uicCodeList) {
        List<AppTypeEnum> returnList = Lists.newArrayList();
        if (Collects.isEmpty(uicCodeList)) {
            return returnList;
        }
        for (String uicCode : uicCodeList) {
            AppTypeEnum typeEnum = uicCodeAt(uicCode);
            if (Objects.nonNull(typeEnum)) {
                returnList.add(typeEnum);
            }
        }
        return returnList;
    }

    /**
     * 匹配appType列表的枚举
     * @param appTypeList
     * @return
     */
    public static List<AppTypeEnum> mappingType(Collection<Integer> appTypeList) {
        List<AppTypeEnum> returnList = Lists.newArrayList();
        if (Collects.isEmpty(appTypeList)) {
            return returnList;
        }
        for (Integer appType : appTypeList) {
            AppTypeEnum typeEnum = appTypeAt(appType);
            if (Objects.nonNull(typeEnum)) {
                returnList.add(typeEnum);
            }
        }
        return returnList;
    }



    /**
     * 根据uicCode匹配AppType
     * @param uicCode
     * @return
     */
    public static AppTypeEnum uicCodeAt(String uicCode) {
        if (Strings.isBlank(uicCode)) {
            return null;
        }
        for (AppTypeEnum value : AppTypeEnum.values()) {
            if (Objects.equals(value.getUicCode(), uicCode)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据appCode匹配AppType
     * @param appCode
     * @return
     */
    public static AppTypeEnum appCodeAt(String appCode) {
        if (Strings.isBlank(appCode)) {
            return null;
        }
        for (AppTypeEnum value : AppTypeEnum.values()) {
            if (Objects.equals(value.getAppCode(), appCode)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据appType匹配AppType
     * @param appType
     * @return
     */
    public static AppTypeEnum appTypeAt(Integer appType) {
        if (Objects.isNull(appType)) {
            return null;
        }
        for (AppTypeEnum value : AppTypeEnum.values()) {
            if (Objects.equals(value.getType(), appType)) {
                return value;
            }
        }
        return null;
    }


    public static Boolean containAppType(List<AppTypeEnum> appTypeEnumList, Integer appType) {
        if (Collects.isEmpty(appTypeEnumList) || Objects.isNull(appType)) {
            return false;
        }
        for (AppTypeEnum next : appTypeEnumList) {
            if (Objects.nonNull(next) && Objects.equals(next.getType(), appType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断type合法性
     */
    public static boolean valid(Integer type) {
        if (type == null) {
            return false;
        }
        AppTypeEnum[] values = values();
        for (AppTypeEnum value : values) {
            if (value.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static String name(Integer appType) {
        return Optional.ofNullable(appTypeAt(appType))
                .map(AppTypeEnum::getName)
                .orElse(null);
    }



    private Integer type;

    private String appCode;

    private String name;

    private String uicCode;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUicCode() {
        return uicCode;
    }

    public void setUicCode(String uicCode) {
        this.uicCode = uicCode;
    }
}
