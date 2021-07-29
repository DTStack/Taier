package com.dtstack.batch.dto;

import lombok.Data;

@Data
public class BatchPackageItemQueryDTO {

    private Long tenantId;

    private Long projectId;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private Long packageId;

    private String sort = "desc";

}
