package com.dtstack.batch.service.table;

import com.dtstack.batch.domain.BatchFunction;

/**
 * 函数操作相关
 * Date: 2019/6/17
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IFunctionService {

    /**
     * 添加函数
     * @param dtuicTenantId
     * @param dbName
     * @param funcName
     * @param className
     * @param resource
     * @throws Exception
     */
    void addFunction(Long dtuicTenantId, String dbName, String funcName, String className, String resource, Long projectId) throws Exception;

    /**
     * 删除函数
     * @param dtuicTenantId
     * @param dbName
     * @param functionName
     */
    void deleteFunction(Long dtuicTenantId, String dbName, String functionName, Long projectId) throws Exception;

    /**
     * 新增存储过程
     * @param dtuicTenantId
     * @param dbName
     * @param batchFunction
     */
    void addProcedure(Long dtuicTenantId, String dbName, BatchFunction batchFunction);
}
