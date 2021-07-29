package com.dtstack.batch.sync.template;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import com.dtstack.dtcenter.common.enums.SftpAuthType;
import com.dtstack.dtcenter.common.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FtpReader extends FtpBase implements Reader {

    private boolean isFirstLineHeader;

    private Object path;

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);

        parameter.put("protocol", this.getProtocol());

        if(path != null){
            if(path instanceof String){
                parameter.put("path", path.toString());
            } else if(path instanceof List){
                try {
                    parameter.put("path", StringUtils.join(PublicUtil.objectToObject(path,List.class),","));
                } catch (Exception e){
                    throw new RdosDefineException(String.format("解析ftp路径出错，报错原因: %s", e.getMessage()), e);
                }
            }
        } else {
            throw new RdosDefineException("FTP路径不能为空");
        }
        if (auth != null && Integer.valueOf(auth).equals(SftpAuthType.RSA.getType())) {
            //免登录 私钥路径
            parameter.put("privateKeyPath", rsaPath);
        } else {
            parameter.put("password", this.getPassword());
        }

        parameter.put("host", this.getHost());
        parameter.put("port", this.getPort());
        parameter.put("fileName", this.getFileName());
        parameter.put("username", this.getUsername());
        parameter.put("fieldDelimiter", this.fieldDelimiter);
        parameter.put("connectPattern", this.getConnectPattern());
        parameter.put("isFirstLineHeader", isFirstLineHeader);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.FTP_R));
        parameter.put("encoding", this.getEncoding());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);

        reader.put("name", PluginName.FTP_R);
        reader.put("parameter", parameter);

        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    public boolean getFirstLineHeader() {
        return isFirstLineHeader;
    }

    public void setFirstLineHeader(boolean firstLineHeader) {
        isFirstLineHeader = firstLineHeader;
    }

    public Object getPath() {
        return path;
    }

    public void setPath(Object path) {
        this.path = path;
    }

}
