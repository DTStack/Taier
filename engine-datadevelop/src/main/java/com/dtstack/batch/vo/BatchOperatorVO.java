package com.dtstack.batch.vo;

import lombok.Data;

/**
 * 批量处理返回
 * Date: 2017/6/16
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
@Data
public class BatchOperatorVO<T> {

    private Integer successNum = 0;

    private Integer failNum = 0;

    private T detail;
}
