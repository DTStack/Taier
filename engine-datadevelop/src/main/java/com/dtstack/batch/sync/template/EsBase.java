package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public abstract class EsBase extends BaseSource{

    protected String address;

    protected List column;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

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
}
