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

package com.dtstack.taier.datasource.plugin.aws_s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.AwsS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * aws s3 Client
 *
 * @author ：wangchuan
 * date：Created in 上午9:46 2021/5/6
 * company: www.dtstack.com
 */
public class AwsS3Client extends AbsNoSqlClient {

    /**
     * s3 object 前置查询需要以 .* 结尾
     */
    private static final String SEARCH_PREFIX_SING = ".*";

    @Override
    public Boolean testCon(ISourceDTO source) {
        AwsS3SourceDTO sourceDTO = AwsS3Util.convertSourceDTO(source);
        AmazonS3 amazonS3 = null;
        try {
            amazonS3 = AwsS3Util.getClient(sourceDTO);
            amazonS3.listBuckets();
        } catch (Exception e) {
            throw new SourceException(String.format("aws s3 connection failed : %s", e.getMessage()), e);
        } finally {
            AwsS3Util.closeAmazonS3(amazonS3);
        }
        return true;
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        AwsS3SourceDTO sourceDTO = AwsS3Util.convertSourceDTO(source);
        String bucket = queryDTO.getSchema();
        if (StringUtils.isBlank(bucket)) {
            throw new SourceException("bucket cannot be blank....");
        }
        String tableNamePattern = queryDTO.getTableNamePattern();
        // 是否匹配查询
        boolean isPattern = StringUtils.isNotBlank(tableNamePattern);
        // 仅支持前置匹配
        boolean isPrefix = isPattern && tableNamePattern.endsWith(SEARCH_PREFIX_SING);
        AmazonS3 amazonS3 = null;
        List<String> objectList;
        try {
            amazonS3 = AwsS3Util.getClient(sourceDTO);
            ObjectListing objectListing;
            if (!isPattern) {
                objectListing = amazonS3.listObjects(bucket);
            } else {
                objectListing = amazonS3.listObjects(bucket, isPrefix ? tableNamePattern.substring(0, tableNamePattern.length() - 2) : tableNamePattern);
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
            throw new SourceException(String.format("aws s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            AwsS3Util.closeAmazonS3(amazonS3);
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
        AwsS3SourceDTO sourceDTO = AwsS3Util.convertSourceDTO(source);
        AmazonS3 amazonS3 = null;
        List<String> result;
        try {
            amazonS3 = AwsS3Util.getClient(sourceDTO);
            List<Bucket> buckets = amazonS3.listBuckets();
            result = buckets.stream().map(Bucket::getName).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("aws s3 get buckets failed : %s", e.getMessage()), e);
        } finally {
            AwsS3Util.closeAmazonS3(amazonS3);
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
