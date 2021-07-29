package com.dtstack.batch.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchPackageCreateDTO {

    private Long tenantId;

    private Long projectId;

    private Long userId;

    private String packageName;

    private String packageDesc;

    private List<BatchPackageItemDTO> items;

}
