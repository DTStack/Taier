package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/5/14 3:04 下午
 */
@Data
public class AwsS3Writer extends AwsS3Base implements Writer  {

    private String object;

    @Override
    public JSONObject toWriterJson() {
        JSONObject result = new JSONObject();
        result.put("name", PluginName.AWS_S3_W);

        JSONObject parameter = new JSONObject();
        parameter.put("accessKey", getAccessKey());
        parameter.put("secretKey", getSecretKey());
        parameter.put("region", getRegion());
        parameter.put("endpoint", getEndpoint());
        parameter.put("bucket", StringUtils.isNotBlank(this.getBucket()) ? this.getBucket() : "");
        parameter.put("object", StringUtils.isNotBlank(this.getObject()) ? this.getObject() : "");
        parameter.put("column", ColumnUtil.getColumns(getColumn(), PluginName.AWS_S3_W));
        parameter.put("encoding", StringUtils.isNotBlank(this.getEncoding()) ? this.getEncoding() : "");
        parameter.put("fieldDelimiter", StringUtils.isNotBlank(this.getFieldDelimiter()) ? this.getFieldDelimiter() : "");
        parameter.put("isFirstLineHeader", getIsFirstLineHeader());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        result.put("parameter", parameter);
        return result;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
