package com.dtstack.batch.sync.util;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.sqlparser.common.client.domain.Twins;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: jingzhen
 * create: 2017/5/17
 */
public class HdfsOrcUtil {


    public static List<Twins<String, String>> getColumnList(final String tableName, final String defaultFS, final String hadoopConfig,Map<String,Object> kerberosConf) {
        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(defaultFS)
                .kerberosConfig(kerberosConf)
                .config(hadoopConfig).build();
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                .tableName(tableName)
                .build();
        List<ColumnMetaDTO> columnMetaData = hdfsClient.getColumnList(hdfsSourceDTO, sqlQueryDTO, FileFormat.ORC.getVal());
        if (CollectionUtils.isEmpty(columnMetaData)){
            return Lists.newArrayList();
        }
        return columnMetaData.stream().map(c->{
            Twins<String, String> twins = new Twins<>(c.getKey(),c.getType());
            return twins;
        }).collect(Collectors.toList());
    }
}
