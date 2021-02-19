package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentTemplateDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yuebai
 * @date 2021-02-19
 */
@Service
public class ComponentTemplateService {

    @Autowired
    private ComponentTemplateDao componentTemplateDao;

    public String loadComponentTemplateFromDB(String typeName) {
        if (StringUtils.isBlank(typeName)) {
            throw new RdosDefineException("组件类型不能为空");
        }
        String template = componentTemplateDao.getByTypeName(typeName);
        if (StringUtils.isBlank(template)) {
            throw new RdosDefineException("组件配置信息为空");
        }
        return template;
    }
}
