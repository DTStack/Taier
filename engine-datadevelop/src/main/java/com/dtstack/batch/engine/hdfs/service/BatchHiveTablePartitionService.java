package com.dtstack.batch.engine.hdfs.service;


import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.dao.BatchHiveTablePartitionDao;
import com.dtstack.batch.domain.BatchHiveTablePartition;
import com.dtstack.batch.dto.HivePatitionSearchVO;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.engine.rdbms.common.dto.PartitionDTO;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.service.table.ITablePartitionService;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Hive表分区相关
 * Date: 2019/5/14
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHiveTablePartitionService implements ITablePartitionService {

    @Autowired
    public ITableService iTableServiceImpl;


    /**
     * 判断分区是否存在
     *
     * @param dtuicTenantId
     * @param partitionVal
     * @param tableName
     * @param db
     * @param projectId
     * @return
     */
    @Override
    public boolean isPartitionExist(Long dtuicTenantId, String partitionVal, String tableName, String db, Long projectId, Integer tableType) {
        return this.iTableServiceImpl.isPartitionExist(dtuicTenantId, null, partitionVal, db, ETableType.getTableType(tableType), tableName);
    }

}
