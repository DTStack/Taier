package com.dtstack.taier.datasource.plugin.hdfs3_cdp;

import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.StringUtils;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:53 2020/2/27
 * @Description：HDFS 连接工厂
 */
public class HdfsConnFactory {

    public Boolean testConn(ISourceDTO iSource) {
        Hdfs3SourceDTO hdfsSourceDTO = (Hdfs3SourceDTO) iSource;
        if (StringUtils.isBlank(hdfsSourceDTO.getDefaultFS())) {
            throw new SourceException("defaultFS incorrect format");
        }

        return HdfsOperator.checkConnection(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
    }
}
