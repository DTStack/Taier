package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:28 2019-08-09
 */
public class KuduWriter extends KuduBase implements Writer {

    private Map<String,Object> hadoopConfig;

    public Map<String, Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String, Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("table", this.getTable());
        parameter.put("column", this.convertColumn());
        parameter.put("masterAddresses", this.getMasterAddresses());
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("sourceIds",getSourceIds());
        if (MapUtils.isNotEmpty(this.getHadoopConfig())) {
            parameter.put("hadoopConfig", this.getHadoopConfig());
            parameter.put("authentication", "Kerberos");
        }
        parameter.putAll(super.getExtralConfigMap());
        JSONObject reader = new JSONObject(true);
        reader.put("name", PluginName.Kudu_W);
        reader.put("parameter", parameter);
        return reader;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");

        if (StringUtils.isBlank(data.getString("table"))) {
            throw new RdosDefineException("table 不能为空");
        }

        if (!data.containsKey("writeMode")) {
            throw new RdosDefineException("writeMode 不能为空");
        }
    }

    /**
     * kudu 需要有name参数
     *
     * @return
     */
    private List<Map<String, String>> convertColumn() {
        if (CollectionUtils.isNotEmpty(this.getColumn())) {
            for (Map<String, String> map : this.getColumn()) {
                if (!map.containsKey("name") && Objects.nonNull(map.get("key"))) {
                    map.put("name", map.get("key"));
                }
            }
            return this.getColumn();
        }
        return this.getColumn();
    }
}
