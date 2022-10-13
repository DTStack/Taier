package com.dtstack.taier.datasource.plugin.hdfs;

import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import org.apache.commons.lang3.StringUtils;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:53 2020/2/27
 * @Description：HDFS 连接工厂
 */
public class HdfsConnFactory {

    public Boolean testConn(ISourceDTO iSource) {
        HdfsSourceDTO hdfsSourceDTO = (HdfsSourceDTO) iSource;
        if (StringUtils.isBlank(hdfsSourceDTO.getDefaultFS())) {
            throw new SourceException("defaultFS incorrect format");
        }

        return HdfsOperator.checkConnection(hdfsSourceDTO.getDefaultFS(), hdfsSourceDTO.getConfig(), hdfsSourceDTO.getKerberosConfig());
    }
}
