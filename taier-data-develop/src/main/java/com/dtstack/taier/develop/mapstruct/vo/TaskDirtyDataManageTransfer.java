package com.dtstack.taier.develop.mapstruct.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.dao.domain.TaskDirtyDataManage;
import com.dtstack.taier.develop.vo.develop.query.TaskDirtyDataManageVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author zhiChen
 * @date 2021/5/12 17:31
 */
@Mapper( builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskDirtyDataManageTransfer {
    TaskDirtyDataManageTransfer INSTANCE = Mappers.getMapper(TaskDirtyDataManageTransfer.class);

    TaskDirtyDataManageVO taskDirtyDataManageToTaskDirtyDataManageVO(TaskDirtyDataManage dto);

    TaskDirtyDataManage taskDirtyDataManageVOToTaskDirtyDataManage(TaskDirtyDataManageVO dto);

    default String linkInfoJsonToString(JSONObject src) {
        return src.toString();
    }

    default JSONObject linkInfoStringToJSON(String src) {
        return JSONObject.parseObject(src);
    }
}
