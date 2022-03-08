package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONObject;

/**ddl相关配置
 * @author zhiChen
 * @date 2022/1/7 10:46
 */
public interface NameMapping {

    JSONObject toNameMappingJson();

    String toNameMappingString();
}
