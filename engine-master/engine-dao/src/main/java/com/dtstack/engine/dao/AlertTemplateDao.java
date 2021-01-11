package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.po.AlertTemplatePO;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/16
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface AlertTemplateDao {

    int update(AlertTemplatePO alertTemplatePO);

    int insert(AlertTemplatePO alertTemplatePO);

    AlertTemplatePO getByTemplateTypeAndSource(@Param("templateType")int templateType, @Param("sourceName") String sourceName);

    int deleteByTemplateTypeAndSource(@Param("templateType")int templateType, @Param("sourceName") String sourceName);
}
