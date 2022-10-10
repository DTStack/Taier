package com.dtstack.taier.datasource.plugin.csp_s3;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.CspS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.ObjectListing;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * csp s3 Client
 *
 * @author ：wangchuan
 * date：Created in 上午9:46 2021/12/6
 * company: www.dtstack.com
 */
public class CspS3Client extends AbsNoSqlClient {

    /**
     * s3 object 前置查询需要以 .* 结尾
     */
    private static final String SEARCH_PREFIX_SING = ".*";

    @Override
    public Boolean testCon(ISourceDTO source) {
        CspS3SourceDTO sourceDTO = CspS3Util.convertSourceDTO(source);
        COSClient cosClient = null;
        try {
            cosClient = CspS3Util.getClient(sourceDTO);
            cosClient.listBuckets();
        } catch (Exception e) {
            throw new SourceException(String.format("csp s3 connection failed : %s", e.getMessage()), e);
        } finally {
            CspS3Util.closeAmazonS3(cosClient);
        }
        return true;
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        CspS3SourceDTO sourceDTO = CspS3Util.convertSourceDTO(source);
        String bucket = queryDTO.getSchema();
        if (StringUtils.isBlank(bucket)) {
            throw new SourceException("bucket cannot be blank....");
        }
        String tableNamePattern = queryDTO.getTableNamePattern();
        // 是否匹配查询
        boolean isPattern = StringUtils.isNotBlank(tableNamePattern);
        // 仅支持前置匹配
        boolean isPrefix = isPattern && tableNamePattern.endsWith(SEARCH_PREFIX_SING);
        COSClient cosClient = null;
        List<String> objectList;
        try {
            cosClient = CspS3Util.getClient(sourceDTO);
            ObjectListing objectListing;
            if (!isPattern) {
                objectListing = cosClient.listObjects(bucket);
            } else {
                objectListing = cosClient.listObjects(bucket, isPrefix ? tableNamePattern.substring(0, tableNamePattern.length() - 2) : tableNamePattern);
            }
            if (Objects.isNull(objectListing)) {
                return Lists.newArrayList();
            }
            List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (CollectionUtils.isEmpty(objectSummaries)) {
                return Lists.newArrayList();
            }
            objectList = objectSummaries.stream().map(COSObjectSummary::getKey).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("csp s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            CspS3Util.closeAmazonS3(cosClient);
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
        CspS3SourceDTO sourceDTO = CspS3Util.convertSourceDTO(source);
        COSClient amazonS3 = null;
        List<String> result;
        try {
            amazonS3 = CspS3Util.getClient(sourceDTO);
            List<Bucket> buckets = amazonS3.listBuckets();
            result = buckets.stream().map(Bucket::getName).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("csp s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            CspS3Util.closeAmazonS3(amazonS3);
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
