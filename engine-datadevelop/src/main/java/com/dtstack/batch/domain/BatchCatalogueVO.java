package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchCatalogueVO extends BatchCatalogue {
    /**
     * 文件类型  如果是目录 是 folder  如果不是目录 就是file
     */
    private String type;

}
