package com.dtstack.batch.engine.greenplum.service;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.service.table.IFunctionService;
import com.dtstack.dtcenter.common.enums.EJobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reason:
 * Date: 2019/6/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchGreenplumFunctionService implements IFunctionService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchGreenplumFunctionService.class);

    @Autowired
    private IJdbcService jdbcServiceImpl;


    @Override
    public void addFunction(Long dtuicTenantId, String dbName, String funcName, String sqlText, String resource, Long projectId) throws Exception {

    }

    @Override
    public void deleteFunction(Long dtuicTenantId, String dbName, String sql, Long projectId) throws Exception {
        try{
            jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, EJobType.GREENPLUM_SQL, dbName, sql);
        } catch (Exception e) {
            LOG.error("{}", e);
            throw new RdosDefineException("删除失败，" + e.getMessage());
        }
    }

    @Override
    public void addProcedure(Long dtuicTenantId, String dbName, BatchFunction batchFunction) {
        try {
            jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, EJobType.GREENPLUM_SQL, dbName, batchFunction.getSqlText());
        } catch (Exception e) {
            LOG.error("{}", e);
            throw new RdosDefineException("新增失败，" + e.getMessage());
        }
    }
}
