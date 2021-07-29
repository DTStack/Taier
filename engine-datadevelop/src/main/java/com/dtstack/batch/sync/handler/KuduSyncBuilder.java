package com.dtstack.batch.sync.handler;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.template.KuduReader;
import com.dtstack.batch.sync.template.KuduWriter;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.dtstack.batch.service.datasource.impl.BatchDataSourceService.JDBC_HOSTPORTS;

/**
 * kudu数据同步构造，用于构造reader和writer参数
 *
 * @author ：wangchuan
 * @since ：Created in 上午10:20 2020/10/22
 */
@Slf4j
@Component
public class KuduSyncBuilder implements SyncBuilder {

    @Override
    public void setReaderJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos) {
        if (log.isDebugEnabled()) {
            log.debug("set read json DataSourceType: Kudu \nsourceMap :{} \n datasourceJson :{}", JSON.toJSONString(map), JSON.toJSONString(dataSource));
        }
        map.put("masterAddresses", MapUtils.getString(dataSource, JDBC_HOSTPORTS, ""));
        map.put("others", MapUtils.getString(dataSource, "others", ""));
    }

    @Override
    public void setWriterJson(Map<String, Object> map, Map<String, Object> dataSource,Map<String,Object> kerberos) {
        if (log.isDebugEnabled()) {
            log.debug("setWriterJson DataSourceType: Kudu \nsourceMap :{} \n datasourceJson :{}", JSON.toJSONString(map), JSON.toJSONString(dataSource));
        }
        map.put("masterAddresses", MapUtils.getString(dataSource, JDBC_HOSTPORTS, ""));
        map.put("others", MapUtils.getString(dataSource, "others", ""));
    }

    @Override
    public Reader syncReaderBuild(Map<String, Object> sourceMap, List<Long> sourceIds) {
        return objToObject(sourceMap, KuduReader.class);
    }

    @Override
    public Writer syncWriterBuild(List<Long> targetIds, Map<String, Object> targetMap, Reader reader) {
        return objToObject(targetMap, KuduWriter.class);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Kudu;
    }

}
