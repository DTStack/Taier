package com.dtstack.taier.datasource.api.dto.restful;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * http response
 *
 * @author ：wangchuan
 * date：Created in 下午4:39 2021/8/10
 * company: www.dtstack.com
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /**
     * 返回状态码
     */
    private Integer statusCode;

    /**
     * 具体的返回信息
     */
    private String content;

    /**
     * 调用失败时的异常信息
     */
    private String errorMsg;

}
