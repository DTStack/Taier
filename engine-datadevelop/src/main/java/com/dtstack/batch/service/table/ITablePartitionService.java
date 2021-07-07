package com.dtstack.batch.service.table;

import com.dtstack.batch.dto.HivePatitionSearchVO;
import com.dtstack.batch.engine.rdbms.common.dto.PartitionDTO;
import com.dtstack.batch.web.pager.PageResult;

import java.util.List;

/**
 * 表分区相关
 * Date: 2019/5/14
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITablePartitionService {

    PageResult<List<PartitionDTO>> getPartitions(HivePatitionSearchVO search) throws Exception;

    boolean isPartitionExist(Long dtuicTenantId, String partitionVal, String tableName, String db, Long projectId, Integer tableType);
}
