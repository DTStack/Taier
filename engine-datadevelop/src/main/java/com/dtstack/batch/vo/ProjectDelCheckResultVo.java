package com.dtstack.batch.vo;

import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 删除项目预检查结果
 */
@Data
public class ProjectDelCheckResultVo {

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     *
     */
    private List<NotDeleteProjectVO> taskList = Lists.newArrayList();
}
