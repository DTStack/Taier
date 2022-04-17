package com.dtstack.taier.develop.service.template.hive;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.PluginName;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：K
 * @Date ：Created in 11:18 2019-07-04
 */
public class Hive2XReader extends HiveReaderBase {

    private String msg = "";
    protected List connection;
    private List<String> fullColumnName = new ArrayList<>();
    private List<String> fullColumnType = new ArrayList<>();
    protected String remoteDir;
    protected Map<String, Object> sftpConf;
    protected String encoding = "utf-8";
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");
        if (StringUtils.isEmpty(data.getString("jdbcUrl"))) {
            throw new RdosDefineException("jdbcUrl 不能为空");
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List getConnection() {
        return connection;
    }

    public void setConnection(List connection) {
        this.connection = connection;
    }

    public List<String> getFullColumnName() {
        return fullColumnName;
    }

    public void setFullColumnName(List<String> fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public List<String> getFullColumnType() {
        return fullColumnType;
    }

    public void setFullColumnType(List<String> fullColumnType) {
        this.fullColumnType = fullColumnType;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public Map<String, Object> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, Object> sftpConf) {
        this.sftpConf = sftpConf;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String pluginName() {
        if (CollectionUtils.isNotEmpty(connection)) {
            return PluginName.HDFS_R;
        }else {
            return PluginName.Hive_R;
        }
    }

}
