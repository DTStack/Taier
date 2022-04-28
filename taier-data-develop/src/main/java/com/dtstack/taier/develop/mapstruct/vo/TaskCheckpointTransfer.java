package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.develop.dto.devlop.CheckPointTimeRangeResultDTO;
import com.dtstack.taier.develop.dto.devlop.StreamTaskCheckpointVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckPointTimeRangeResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckpointListResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetSavePointResultVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:55 2021/1/6
 * @Description：数据源信息转化
 */
@Mapper(builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TaskCheckpointTransfer {
    TaskCheckpointTransfer INSTANCE = Mappers.getMapper(TaskCheckpointTransfer.class);

    GetSavePointResultVO getSavePointResult(StreamTaskCheckpointVO savePoint);

    GetCheckPointTimeRangeResultVO getCheckPointTimeRangeResult(CheckPointTimeRangeResultDTO dto);
}
