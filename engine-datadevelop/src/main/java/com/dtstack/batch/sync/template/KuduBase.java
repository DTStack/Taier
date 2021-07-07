package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:25 2019-08-09
 */
@Data
public class KuduBase extends BaseSource {
    private String masterAddresses;

    private String otherParams;

    /**
     * 表名
     */
    protected String table;

    /**
     * 字段名
     */
    protected List<Map<String, Object>> column;

    /**
     * 写入模式
     */
    protected String writeMode;

    public void checkFormat(JSONObject data){
        data = data.getJSONObject("parameter");

        if(StringUtils.isEmpty(data.getString("hostPorts"))){
            throw new RdosDefineException("hostPorts 不能为空");
        }
    }
}
