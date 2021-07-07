package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

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
        parameter.put("bucket", StringUtils.isNotBlank(this.getBucket()) ? this.getBucket() : "");
        parameter.put("objects", CollectionUtils.isNotEmpty(this.getObjects()) ? this.getObjects() : Lists.newArrayList());
        parameter.put("column", ColumnUtil.getColumns(getColumn(), PluginName.AWS_S3_R));
        parameter.put("encoding", StringUtils.isNotBlank(this.getEncoding()) ? this.getEncoding() : "");
        parameter.put("fieldDelimiter", StringUtils.isNotBlank(this.getFieldDelimiter()) ? this.getFieldDelimiter() : "");
        parameter.put("isFirstLineHeader", BooleanUtils.isTrue(this.getIsFirstLineHeader()) ? true : false);
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