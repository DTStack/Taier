package com.dtstack.engine.datasource.facade.datasource;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.datasource.dao.po.datasource.DsFormField;
import com.dtstack.engine.datasource.param.datasource.DsTypeVersionParam;
import com.dtstack.engine.datasource.service.impl.datasource.DsFormFieldService;
import com.dtstack.engine.datasource.service.impl.datasource.DsTypeFieldRefService;
import com.dtstack.engine.datasource.vo.datasource.form.DsFormFieldVo;
import com.dtstack.engine.datasource.vo.datasource.form.DsFormTemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Slf4j
@Service
public class FormTemplateFacade {

    @Autowired
    private DsTypeFieldRefService typeFieldRefService;

    @Autowired
    private DsFormFieldService formFieldService;


    /**
     * 根据数据库类型和版本查找表单模版
     * @param param
     * @return
     */
    public DsFormTemplateVo findTemplateByTypeVersion(DsTypeVersionParam param) {
        DsFormTemplateVo returnVo = new DsFormTemplateVo();
        List<DsFormField> formFieldList = formFieldService.findFieldByTypeVersion(param);
        List<DsFormFieldVo> formFieldVos = new ArrayList<>();
        for (DsFormField dsFormField : formFieldList) {
            DsFormFieldVo dsFormFieldVo = new DsFormFieldVo();
            BeanUtils.copyProperties(dsFormField,dsFormFieldVo);
            if(StringUtils.isNotBlank(dsFormField.getOptions())){
                List<Map> optionList = JSON.parseArray(dsFormField.getOptions(), Map.class);
                dsFormFieldVo.setOptions(optionList);
            }
            formFieldVos.add(dsFormFieldVo);
        }
        returnVo.setDataType(param.getDataType());
        returnVo.setDataVersion(param.getDataVersion());
        returnVo.setFromFieldVoList(formFieldVos);
        return returnVo;
    }
}
