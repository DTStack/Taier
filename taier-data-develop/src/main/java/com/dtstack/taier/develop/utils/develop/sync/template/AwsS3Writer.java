/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
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
