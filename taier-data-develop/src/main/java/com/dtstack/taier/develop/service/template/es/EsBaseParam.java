package com.dtstack.taier.develop.service.template.es;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author daojin
 */
public abstract class EsBaseParam {

    private String index;
    private List column;
    private Integer sourceId;
    private List<Long> sourceIds;
    private String extralConfig;

    public void checkArray(JSONObject parameter, String param){
        if (StringUtils.isBlank(parameter.getString(param))){
            throw new RdosDefineException(param + "不能为空");
        }

        if(!(parameter.get(param) instanceof JSONArray)){
            throw new RdosDefineException(param + "必须为数组格式");
        }

        JSONArray column = parameter.getJSONArray(param);
        if(column.isEmpty()){
            throw new RdosDefineException(param + "不能为空");
        }

        for (Object o : column) {
            if(!(o instanceof JSONObject)){
                throw new RdosDefineException(param + "必须为对象数组格式");
            }
        }
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }
}
