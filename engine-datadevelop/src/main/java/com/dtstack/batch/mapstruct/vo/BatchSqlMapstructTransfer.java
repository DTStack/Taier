package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteDataResultVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteRunLogResultVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteStatusResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchSqlMapstructTransfer {

    BatchSqlMapstructTransfer INSTANCE = Mappers.getMapper(BatchSqlMapstructTransfer.class);

    /**
     * ExecuteResultVO -> BatchExecuteDataResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteDataResultVO executeResultVOToBatchExecuteDataResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> BatchExecuteRunLogResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteRunLogResultVO executeResultVOToBatchExecuteRunLogResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> BatchExecuteStatusResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteStatusResultVO executeResultVOToBatchExecuteStatusResultVO(ExecuteResultVO executeResultVO);

}
