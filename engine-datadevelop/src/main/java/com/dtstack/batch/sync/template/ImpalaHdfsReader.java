package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.engine.pluginapi.pojo.Column;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ImpalaHdfsReader extends ImpalaHdfsBase implements Reader {

    @Override
    public JSONObject toReaderJson() {
        HDFSReader hdfsReader = new HDFSReader();
        hdfsReader.setHadoopConfig(hadoopConfig);
        hdfsReader.setFieldDelimiter(fieldDelimiter);
        //前端传入的column参数没有index hdfs读取需要此参数
        Map<String, Column> allColumnsMap = allColumns.stream().collect(Collectors.toMap(Column::getName, item -> item));

        for (Object col : column) {
            String name = (String) ((Map<String, Object>) col).get("key");
            ((Map<String, Object>) col).put("index", allColumnsMap.get(name).getIndex());
        }

        hdfsReader.setColumn(column);
        hdfsReader.setDefaultFS(defaultFS);
        hdfsReader.setEncoding(encoding);
        hdfsReader.setExtralConfig(super.getExtralConfig());
        hdfsReader.setFileType(fileType);

        if(StringUtils.isNotEmpty(partition)) {
            hdfsReader.setPath(path + "/" + partition);
        } else {
            hdfsReader.setPath(path);
        }
        if (MapUtils.isNotEmpty(sftpConf)) {
            hdfsReader.setSftpConf(sftpConf);
        }
        if (StringUtils.isNotEmpty(remoteDir)) {
            hdfsReader.setRemoteDir(remoteDir);
        }

        hdfsReader.setPath(hdfsReader.getPath().trim());
        return hdfsReader.toReaderJson();
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
