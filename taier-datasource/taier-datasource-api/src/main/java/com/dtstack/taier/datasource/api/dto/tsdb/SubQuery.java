package com.dtstack.taier.datasource.api.dto.tsdb;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
