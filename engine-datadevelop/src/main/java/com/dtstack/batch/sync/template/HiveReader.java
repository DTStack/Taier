package com.dtstack.batch.sync.template;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Reader;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author jingzhen
 */
public class HiveReader extends HiveBase implements Reader {

    @Override
    public JSONObject toReaderJson() {
        try {
            inferHdfsParams();
            HDFSReader hdfsReader = new HDFSReader();
            hdfsReader.setHadoopConfig(hadoopConfig);
            hdfsReader.setFileType(fileType);
            hdfsReader.setFieldDelimiter(fieldDelimiter);
            hdfsReader.setColumn(column);
            hdfsReader.setDefaultFS(defaultFS);
            hdfsReader.setEncoding(encoding);
            hdfsReader.setExtralConfig(super.getExtralConfig());
            hdfsReader.setSourceIds(getSourceIds());
            if(StringUtils.isNotEmpty(partition)) {
                hdfsReader.setPartition(partition );
                hdfsReader.setFileName(partition);
            }

            if(StringUtils.isNotEmpty(partition)) {
                hdfsReader.setPartition(partition);
            }

            if (MapUtils.isNotEmpty(sftpConf)) {
                hdfsReader.setSftpConf(sftpConf);
            }
            if (StringUtils.isNotEmpty(remoteDir)) {
                hdfsReader.setRemoteDir(remoteDir);
            }

            if(StringUtils.isNotEmpty(table)) {
                hdfsReader.setTable(table);
            }

            if(StringUtils.isNotEmpty(jdbcUrl)) {
                hdfsReader.setJdbcUrl(jdbcUrl);
            }

            if(StringUtils.isNotEmpty(username)) {
                hdfsReader.setUsername(username);
            }

            if(StringUtils.isNotEmpty(password)) {
                hdfsReader.setPassword(password);
            }


            hdfsReader.setPath(path == null ? "" : path.trim());

            return hdfsReader.toReaderJson();
        } catch (Exception ex) {
            throw new RdosDefineException(ex.getCause().getMessage());
        }
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
