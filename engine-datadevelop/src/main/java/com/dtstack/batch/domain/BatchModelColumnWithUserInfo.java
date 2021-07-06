package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class BatchModelColumnWithUserInfo extends BatchModelColumn {
    /**
     * 模糊查询
     */
    private String name;

    private String userName;

}
