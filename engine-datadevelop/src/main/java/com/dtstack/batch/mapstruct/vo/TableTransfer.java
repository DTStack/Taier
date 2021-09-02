package com.dtstack.batch.mapstruct.vo;


import com.dtstack.batch.vo.*;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TableTransfer {

    TableTransfer INSTANCE = Mappers.getMapper(TableTransfer.class);


    /**
     * column -> BatchTableColumnMetaInfoDTO
     * @param column
     * @return
     */
    BatchTableColumnMetaInfoDTO columnMetaDTOToBatchTableColumnMetaInfoDTO(ColumnMetaDTO column);

    /**
     * table -> BatchTableMetaInfoDTO
     *
     * @param table
     * @return
     */
    BatchTableMetaInfoDTO tableToBatchTableMetaInfoDTO(com.dtstack.dtcenter.loader.dto.Table table);
}
