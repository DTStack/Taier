package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import com.dtstack.dtcenter.common.enums.SftpAuthType;

import java.util.Optional;

public class FtpWriter extends FtpBase implements Writer {

    private String writeMode = "overwrite";

    private String path;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);

        if (auth != null && Integer.valueOf(auth).equals(SftpAuthType.RSA.getType())) {
            //免登录 私钥路径
            parameter.put("privateKeyPath", rsaPath);
        } else {
            parameter.put("password", this.getPassword());
        }
        parameter.put("protocol", this.getProtocol());
        parameter.put("path", Optional.ofNullable(this.getPath()).orElse(""));
        parameter.put("host", this.getHost());
        parameter.put("port", this.getPort());
        parameter.put("fileName", this.getFileName());
        parameter.put("username", this.getUsername());
        parameter.put("fieldDelimiter", this.fieldDelimiter);
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("connectPattern", this.getConnectPattern());
        parameter.put("column", ColumnUtil.getColumns(this.column,PluginName.FTP_W));
        parameter.put("encoding", this.getEncoding());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject writer = new JSONObject(true);

        writer.put("name", PluginName.FTP_W);
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
