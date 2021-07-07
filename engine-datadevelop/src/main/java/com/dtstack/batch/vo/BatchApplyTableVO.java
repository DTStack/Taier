package com.dtstack.batch.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.domain.BatchApply;
import lombok.Data;

import java.util.List;

/**
 * @author sanyue
 * @date 2018/11/20
 */
@Data
public class BatchApplyTableVO extends BatchApply {


    private Long tablePermission;

    private List<String> columnNames;

    private Boolean fullColumn;

    private List<JSONObject> fullColumnList;

}
