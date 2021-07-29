package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/3 13:33
 */
@Data
public class MongoDbBase extends BaseSource{

    private String hostPorts = "localhost:27017";

    private String username = "";

    private String password = "";

    private String database = "";

    private String collectionName = "";

    private List column;

    public void checkFormat(JSONObject data){
        data = data.getJSONObject("parameter");

        if(StringUtils.isEmpty(data.getString("hostPorts"))){
            throw new RdosDefineException("hostPorts 不能为空");
        }

        if(StringUtils.isEmpty(data.getString("database"))){
            throw new RdosDefineException("database 不能为空");
        }

        if(StringUtils.isEmpty(data.getString("collectionName"))){
            throw new RdosDefineException("collectionName 不能为空");
        }

        if(data.get("column") == null){
            throw new RdosDefineException("column 不能为空");
        }

        if(!(data.get("column") instanceof JSONArray)){
            throw new RdosDefineException("column 必须为数组格式");
        }

        JSONArray column = data.getJSONArray("column");
        if(column.isEmpty()){
            throw new RdosDefineException("column 不能为空");
        }
    }
}
