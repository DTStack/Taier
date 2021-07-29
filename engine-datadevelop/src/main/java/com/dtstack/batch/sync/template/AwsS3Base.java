package com.dtstack.batch.sync.template;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/5/14 11:53 上午
 */
@Data
public class AwsS3Base extends BaseSource{

    protected String accessKey;

    protected String secretKey;

    protected String region;

    protected String endpoint;

    protected String bucket;

    protected List column;

    protected String encoding;

    protected String fieldDelimiter;

    protected Boolean isFirstLineHeader;
}
