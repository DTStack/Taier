package com.dtstack.engine.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseParam implements Serializable {

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Boolean isAdmin;

    @ApiModelProperty(hidden = true)
    private Long createUserId;

    @ApiModelProperty(hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(hidden = true)
    private Boolean isOwner;

    /**
     * 是否删除
     */
    @ApiModelProperty(hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(hidden = true)
    private String productCode;
}
