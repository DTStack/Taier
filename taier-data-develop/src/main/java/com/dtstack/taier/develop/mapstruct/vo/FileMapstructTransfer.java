package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.develop.vo.schedule.FileInfoVO;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FileMapstructTransfer {
    FileMapstructTransfer INSTANCE = Mappers.getMapper(FileMapstructTransfer.class);

    List<FileInfoVO> toInfoVO(List<FileResult> resultList);

}
