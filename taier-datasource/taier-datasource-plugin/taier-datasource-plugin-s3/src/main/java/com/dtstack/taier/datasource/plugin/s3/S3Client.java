package com.dtstack.taier.datasource.plugin.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class S3Client extends AbsNoSqlClient {

    /**
     * s3 object 前置查询需要以 .* 结尾
     */
    private static final String SEARCH_PREFIX_SING = ".*";

    @Override
    public Boolean testCon(ISourceDTO source) {
        AmazonS3Client s3Client = null;
        try {
            s3Client = S3Utils.getClient(source, null);
            s3Client.listBuckets();
        } catch (Exception e) {
            throw new SourceException(String.format("s3 connection failed : %s", e.getMessage()), e);
        }finally {
            if (s3Client != null) {
                s3Client.shutdown();
            }
        }
        return true;
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        String bucket = queryDTO.getSchema();
        if (StringUtils.isBlank(bucket)) {
            throw new SourceException("bucket cannot be blank....");
        }
        String tableNamePattern = queryDTO.getTableNamePattern();
        // 是否匹配查询
        boolean isPattern = StringUtils.isNotBlank(tableNamePattern);
        // 仅支持前置匹配
        boolean isPrefix = isPattern && tableNamePattern.endsWith(SEARCH_PREFIX_SING);
        AmazonS3Client amazonS3Client = null;
        List<String> objectList;
        try {
            amazonS3Client = S3Utils.getClient(source, null);
            ObjectListing objectListing;
            if (!isPattern) {
                objectListing = amazonS3Client.listObjects(bucket);
            } else {
                objectListing = amazonS3Client.listObjects(bucket, isPrefix ? tableNamePattern.substring(0, tableNamePattern.length() - 2) : tableNamePattern);
            }
            if (Objects.isNull(objectListing)) {
                return Lists.newArrayList();
            }
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (CollectionUtils.isEmpty(objectSummaries)) {
                return Lists.newArrayList();
            }
            objectList = objectSummaries.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            if (amazonS3Client != null) {
                amazonS3Client.shutdown();
            }
        }
        if (isPattern && !isPrefix) {
            objectList = objectList.stream().filter(table -> StringUtils.equalsIgnoreCase(table, tableNamePattern)).collect(Collectors.toList());
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            objectList = objectList.stream().limit(queryDTO.getLimit()).collect(Collectors.toList());
        }
        return objectList;
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableList(source, queryDTO);
    }


    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        AmazonS3Client amazonS3Client = null;
        List<String> result;
        try {
            amazonS3Client = S3Utils.getClient(source, null);
            List<Bucket> buckets = amazonS3Client.listBuckets();
            result = buckets.stream().map(Bucket::getName).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            if (amazonS3Client != null) {
                amazonS3Client.shutdown();
            }
        }
        if (StringUtils.isNotBlank(queryDTO.getSchema())) {
            result = result.stream().filter(bucket -> StringUtils.containsIgnoreCase(bucket, queryDTO.getSchema().trim())).collect(Collectors.toList());
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            result = result.stream().limit(queryDTO.getLimit()).collect(Collectors.toList());
        }
        return result;
    }
}
