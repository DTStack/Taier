package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.dto.HivePatitionSearchVO;
import com.dtstack.batch.web.table.vo.query.BatchHivePartSearchInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchTablePartitionMapstructTransfer {

    BatchTablePartitionMapstructTransfer INSTANCE = Mappers.getMapper(BatchTablePartitionMapstructTransfer.class);


    /**
     * HivePartSearchInfoVO -> HivePartSearchVO
     * @param infoVO
     * @return
     */
    HivePatitionSearchVO HivePartSearchInfoVOToHivePartSearchVO(BatchHivePartSearchInfoVO infoVO);

}
