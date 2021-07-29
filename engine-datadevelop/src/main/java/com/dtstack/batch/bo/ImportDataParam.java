package com.dtstack.batch.bo;

import lombok.Data;

/**
 * 本地数据导入参数
 * Date: 2019/5/22
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class ImportDataParam {

    private Long dtuicTenantId;

    private Long projectId;

    private Long tenantId;

    private Long userId;

    private String separator;

    private String oriCharSet;

    private Integer startLine;

    private Boolean topLineIsTitl;

    private Integer matchType;

    private Long tableId;

    private String partitionStr;

    private String keyRefStr;

    private Integer overwriteFlag;

    private String oriFileName;

    private String tmpFilePath;

}
