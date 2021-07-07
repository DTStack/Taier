package com.dtstack.batch.service.task.impl;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTaskParamShadeDao;
import com.dtstack.batch.domain.BatchSysParameter;
import com.dtstack.batch.domain.BatchTaskParam;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.service.impl.BatchSysParamService;
import com.dtstack.dtcenter.common.enums.EParamType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Reason:
 * Date: 2017/8/23
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchTaskParamShadeService {

    @Autowired
    private BatchTaskParamShadeDao batchTaskParamShadeDao;

    @Autowired
    private BatchSysParamService batchSysParamService;

    public void clearDataByTaskId(Long taskId) {
        batchTaskParamShadeDao.deleteByTaskId(taskId);
    }

    public void saveTaskParam(List<BatchTaskParam> paramList) {
        for (BatchTaskParam batchTaskParam : paramList) {
            BatchTaskParamShade paramShade = new BatchTaskParamShade();
            BeanUtils.copyProperties(batchTaskParam, paramShade);
            addOrUpdate(paramShade);
        }
    }

    public void addOrUpdate(BatchTaskParamShade batchTaskParamShade) {
        if (StringUtils.isBlank(batchTaskParamShade.getParamCommand())) {
            throw new RdosDefineException("自定义参数赋值不能为空");
        }
        BatchTaskParamShade dbTaskParam = batchTaskParamShadeDao.getByTypeAndName(batchTaskParamShade.getTaskId(), batchTaskParamShade.getType(), batchTaskParamShade.getParamName());
        if (Objects.nonNull(dbTaskParam)) {
            dbTaskParam.setParamCommand(batchTaskParamShade.getParamCommand());
            dbTaskParam.setGmtModified(new Timestamp(System.currentTimeMillis()));
            batchTaskParamShadeDao.update(dbTaskParam);
        } else {
            batchTaskParamShadeDao.insert(batchTaskParamShade);
        }
    }

    public List<BatchTaskParamShade> getTaskParam(long taskId) {
        List<BatchTaskParamShade> taskParamShades = batchTaskParamShadeDao.listByTaskId(taskId);

        // 特殊处理 TaskParam 系统参数
        for (BatchTaskParamShade taskParamShade : taskParamShades) {
            if (EParamType.SYS_TYPE.getType() != taskParamShade.getType()) {
                continue;
            }

            // 将 command 属性设置为系统表的 command
            BatchSysParameter sysParameter = batchSysParamService.getBatchSysParamByName(taskParamShade.getParamName());
            taskParamShade.setParamCommand(sysParameter.getParamCommand());
        }
        return taskParamShades;
    }
}
