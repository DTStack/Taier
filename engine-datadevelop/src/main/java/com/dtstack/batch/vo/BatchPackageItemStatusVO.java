package com.dtstack.batch.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchPackageItemStatusVO {

    private Integer count;

    private Integer successCount;

    private Integer failCount;

    private Integer waitPublishCount;
}
