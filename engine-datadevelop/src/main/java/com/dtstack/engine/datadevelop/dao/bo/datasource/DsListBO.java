package com.dtstack.engine.datadevelop.dao.bo.datasource;

import lombok.Data;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DsListBO {

    private Long dataInfoId;

    private String dataName;

    private String dataType;

    private String dataVersion;

    private String appNames;

    private String dataDesc;

    private String linkJson;

    private String dataJson;

    private Integer status;

    private Integer isMeta;

    private Date gmtModified;


}
