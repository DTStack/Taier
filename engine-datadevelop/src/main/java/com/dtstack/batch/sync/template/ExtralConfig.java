package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 1.数据同步高级配置,自定义参数
 * 2.json格式
 * 3.各类输入输出插件都需要
 *
 * @author sanyue
 * @date 2019/3/13
 */
public abstract class ExtralConfig {
    protected String extralConfig;

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    protected Map<String, Object> getExtralConfigMap() {
        try {
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNotBlank(getExtralConfig())) {
                JSONObject config = JSONObject.parseObject(getExtralConfig());
                if (config != null) {
                    for (String key : config.keySet()) {
                        map.put(key, config.get(key));
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new RdosDefineException("数据同步高级配置JSON格式错误", e);
        }
    }

    protected void checkExtralConfigIsJSON() {
        try {
            JSONObject.parseObject(getExtralConfig());
        } catch (Exception e) {
            throw new RdosDefineException("数据同步高级配置JSON格式错误", e);
        }
    }
}
