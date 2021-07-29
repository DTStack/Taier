package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.enums.TableLocationType;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ImpalaHdfsWriter extends ImpalaHdfsBase implements Writer {
    private static String pluginName = PluginName.HDFS_W;



    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);

        parameter.put("path", this.getPath());
        parameter.put("defaultFS", this.getDefaultFS());
        parameter.put("column", ColumnUtil.getColumns(this.column, PluginName.HDFS_W));
        parameter.put("fileType", this.getFileType());
        parameter.put("fieldDelimiter", this.getFieldDelimiter());
        parameter.put("encoding", this.getEncoding());
        parameter.put("fileName",getFileName());
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("hadoopConfig", this.getHadoopConfig());
        parameter.put("sourceIds",getSourceIds());

        if(StringUtils.isNotEmpty(partition)) {
            parameter.put("partition", partition);
        }

        if(StringUtils.isNotEmpty(jdbcUrl)) {
            JSONObject connection = new JSONObject(2);
            connection.put("jdbcUrl", this.getJdbcUrl());
            connection.put("table", StringUtils.isNotBlank(this.getTable()) ? Lists.newArrayList(this.getTable()) : Lists.newArrayList());
            parameter.put("connection", Lists.newArrayList(connection));
        }

        parameter.putAll(super.getExtralConfigMap());
        parameter.put("sftpConf", this.getSftpConf());
        parameter.put("remoteDir", this.getRemoteDir());
        //仅用作区分表类型 同步的时候不会使用此参数
        parameter.put(TableLocationType.key(), TableLocationType.HIVE.getValue());
        JSONObject write = new JSONObject(true);
        write.put("name", pluginName);
        write.put("parameter", parameter);
        return write;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
