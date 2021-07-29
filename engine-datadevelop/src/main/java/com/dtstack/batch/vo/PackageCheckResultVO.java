package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class PackageCheckResultVO {

    private Boolean checkStatus;

    private String message;

    /**
     * 用于寻找上传的压缩包
     */
    private String fileKey;

    private String packageName;

}
