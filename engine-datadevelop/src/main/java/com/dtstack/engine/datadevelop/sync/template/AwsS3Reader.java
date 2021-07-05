package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/5/14 11:56 上午
 */
@Data
public class AwsS3Reader extends AwsS3Base implements Reader {

    private List<String> objects;

    @Override
    public JSONObject toReaderJson() {
        JSONObject result = new JSONObject();
        result.put("name", PluginName.AWS_S3_R);

        JSONObject parameter = new JSONObject();
        parameter.put("accessKey", getAccessKey());
        parameter.put("secretKey", getSecretKey());
        parameter.put("region", getRegion());
        parameter.put("endpoint", getEndpoint());
        parameter.put("bucket", getBucket());
        parameter.put("objects", getObjects());
        parameter.put("column", ColumnUtil.getColumns(getColumn(), PluginName.AWS_S3_R));
        parameter.put("encoding", getEncoding());
        parameter.put("fieldDelimiter", getFieldDelimiter());
        parameter.put("isFirstLineHeader", getIsFirstLineHeader());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        result.put("parameter", parameter);
        return result;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

}