package com.dtstack.engine.master.impl;


import com.dtstack.engine.api.domain.TaskParamTemplate;
import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.dao.TaskParamTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:38 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class TaskParamTemplateService {

    @Autowired
    private TaskParamTemplateDao taskParamTemplateDao;

    public TaskTemplateResultVO getEngineParamTmplByComputeType(Integer engineType, Integer computeType, Integer taskType) {
        TaskParamTemplate taskParamTemplate = taskParamTemplateDao.getByEngineTypeAndComputeType(engineType, computeType, taskType == null ? 0 : taskType);
        if (taskParamTemplate != null) {
            TaskTemplateResultVO vo = new TaskTemplateResultVO();
            vo.setId(taskParamTemplate.getId());
            vo.setParams(taskParamTemplate.getParams());
            vo.setComputeType(taskParamTemplate.getComputeType());
            vo.setEngineType(taskParamTemplate.getEngineType());
            vo.setGmtModified(taskParamTemplate.getGmtModified());
            vo.setGmtCreate(taskParamTemplate.getGmtCreate());
            return vo;
        }
        return null;
    }
}
