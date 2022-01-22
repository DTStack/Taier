package com.dtstack.taiga.dao.domain.po;

import lombok.Data;

import java.util.Date;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
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

    private String schemaName;


}
