package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchFunctionResource extends TenantProjectEntity {

	private Long functionId;

	private Long resourceId;

    private Long resource_Id;

}
