package com.dtstack.taier.datasource.api.dto.tsdb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * OpenTSDB 查询条件
 *
 * @author ：wangchuan
 * date：Created in 上午10:52 2021/6/24
 * company: www.dtstack.com
 */
@Data
@Builder
public class TsdbQuery {

    private Long start;

    private Long end;

    private Boolean msResolution;

    private Boolean delete;

    private List<SubQuery> queries;

    @JSONField(serialize = false)
    private boolean showType;

    @JSONField(serialize = false)
    private Class<?> type;

    private Map<String, Map<String, Integer>> hint;

    @JSONField(name = "type")
    private String queryType;
}
