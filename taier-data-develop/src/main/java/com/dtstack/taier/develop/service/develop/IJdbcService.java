package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/12
 */
public interface IJdbcService {

    /**
     * 执行查询，带前缀信息
     *
     * @param taskParam
     * @return
     */
    List<List<Object>> executeQuery(ISourceDTO sourceDTO, List<String> sqls, String taskParam, Integer limit);

    /**
     * 执行sql 忽略查询结果
     *
     * @return
     */
    Boolean executeQueryWithoutResult(ISourceDTO sourceDTO, String sql);

    /**
     * 获取所有的databases
     *
     * @return
     */
    List<String> getAllDataBases(ISourceDTO sourceDTO);

}
