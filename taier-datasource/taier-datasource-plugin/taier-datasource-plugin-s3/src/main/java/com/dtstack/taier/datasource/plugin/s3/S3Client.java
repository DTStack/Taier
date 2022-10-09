package com.dtstack.taier.datasource.plugin.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 12:01 2020/9/29
 * @Description：S3 客户端
 */
@Slf4j
public class S3Client extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO source) {
        AmazonS3Client amazonS3Client = null;
        try {
            amazonS3Client = S3Utils.getClient(source, null);
            amazonS3Client.listBuckets();
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }finally {
            if (amazonS3Client != null) {
                amazonS3Client.shutdown();
            }
        }
        return true;
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        AmazonS3Client amazonS3Client = null;
        List<Bucket> buckets;
        try {
            amazonS3Client = S3Utils.getClient(source, null);
            buckets = amazonS3Client.listBuckets();
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }finally {
            if (amazonS3Client != null) {
                amazonS3Client.shutdown();
            }
        }
        if (CollectionUtils.isEmpty(buckets)) {
            return Collections.emptyList();
        }
        return buckets.stream().map(Bucket::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableList(source, queryDTO);
    }
}
