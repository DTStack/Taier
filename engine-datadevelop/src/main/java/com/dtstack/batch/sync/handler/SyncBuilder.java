package com.dtstack.batch.sync.handler;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.dtcenter.loader.source.DataSourceType;

import java.util.List;
import java.util.Map;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface SyncBuilder {


    void setReaderJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos);

    void setWriterJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos);

    Reader syncReaderBuild(Map<String, Object> sourceMap, List<Long> sourceIds);

    Writer syncWriterBuild(List<Long> targetIds, Map<String, Object> targetMap, Reader reader);

    DataSourceType getDataSourceType();

    default  <T> T objToObject(Object params,Class<T> clazz)  {
        if(params ==null) {return null;}
        return  JSON.parseObject(JSON.toJSONString(params), clazz);
    }
}
