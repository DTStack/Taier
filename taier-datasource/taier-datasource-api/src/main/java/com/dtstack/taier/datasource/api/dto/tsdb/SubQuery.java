package com.dtstack.taier.datasource.api.dto.tsdb;

import java.util.*;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubQuery {
    private int index;
    private String aggregator;
    private String metric;
    private String downsample;
    private Boolean rate;
    private RateOptions rateOptions;
    private Boolean delta;
    private DeltaOptions deltaOptions;

    private Map<String, String> tags;
    private String granularity;
    private Boolean explicitTags;
    private Integer realTimeSeconds;
    private Integer limit;
    private Integer globalLimit;
    private Integer offset;
    private String dpValue;
    private String preDpValue;
    private List<Filter> filters;
    private Map<String, Map<String, Integer>> hint;
}
