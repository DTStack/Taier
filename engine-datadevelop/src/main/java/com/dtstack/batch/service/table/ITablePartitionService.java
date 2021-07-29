package com.dtstack.batch.service.table;

/**
 * 表分区相关
 * Date: 2019/5/14
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITablePartitionService {

    boolean isPartitionExist(Long dtuicTenantId, String partitionVal, String tableName, String db, Long projectId, Integer tableType);
}
