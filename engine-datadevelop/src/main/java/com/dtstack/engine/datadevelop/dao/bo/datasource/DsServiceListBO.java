package com.dtstack.pubsvc.dao.bo.datasource;

import lombok.Data;

import java.util.Date;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@Data
public class DsServiceListBO {

    /**
     * 数据源主键id
     */
    private Long dataInfoId;

    /**
     * 数据源名称
     */
    private String dataName;
    /**
     * 数据源类型加版本号
     */
    private String dataType;

    /**
     * 数据源版本
     */
    private String dataVersion;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 数据源连接信息 json
     */
    private String linkJson;

    /**数据愿配置信息**/
    private String dataJson;

    /**
     * 是否有meta标志 0-否 1-是
     */
    private Integer isMeta;

    /**
     * 连接状态 0-连接失败, 1-正常
     */
    private Integer status;

    /**
     * 最近修改时间
     */
    private Date gmtModified;

}
