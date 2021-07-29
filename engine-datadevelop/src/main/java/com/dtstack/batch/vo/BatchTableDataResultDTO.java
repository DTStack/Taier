package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

@Data
public class BatchTableDataResultDTO {

    /**
     * 状态 true: 有权限  false：无权限
     */
    public Boolean status;

    /**
     * 数据
     */
    public List<Object> data;

    /**
     * 提示信息
     */
    public String msg;

    public BatchTableDataResultDTO(Boolean status, List<Object> data, String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

}
