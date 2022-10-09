package com.dtstack.taier.datasource.plugin.hdfs3.client;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.hdfs3.HdfsConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 16:53 2021/10/26
 * @Description：hdfs 客户端
 */
public class HdfsClient extends AbsNoSqlClient {
    private HdfsConnFactory hdfsConnFactory = new HdfsConnFactory();

    @Override
    public Boolean testCon(ISourceDTO source) {
        return hdfsConnFactory.testConn(source);
    }
}