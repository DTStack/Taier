package com.dtstack.taier.develop.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.DsFormField;
import com.dtstack.taier.dao.mapper.DsFormFieldMapper;
import com.dtstack.taier.develop.bo.datasource.DsTypeVersionParam;
import com.dtstack.taier.develop.vo.datasource.DsFormFieldVO;
import com.dtstack.taier.develop.vo.datasource.DsFormTemplateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
@Service
public class DsFormFieldService extends BaseService<DsFormFieldMapper, DsFormField> {

    private static String COMMON = "common";


    /**
     * 根据数据库类型和版本查找表单模版
     * @param param
     * @return
     */
    public DsFormTemplateVO findTemplateByTypeVersion(DsTypeVersionParam param) {
        DsFormTemplateVO returnVo = new DsFormTemplateVO();
        String typeVersion = param.getDataType();
        if (Strings.isNotBlank(param.getDataVersion())) {
            typeVersion = param.getDataType() + "-" + param.getDataVersion();
        }
        List<DsFormField> formFieldList = this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).
                or().eq("type_version", COMMON));
        List<DsFormFieldVO> formFieldVos = new ArrayList<>();
        for (DsFormField dsFormField : formFieldList) {
            DsFormFieldVO dsFormFieldVo = new DsFormFieldVO();
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

    /**
     * 根据数据源类型和版本获取具有连接性质的属性列表
     * @param dataType
     * @param dataVersion
     * @return
     */
    public List<DsFormField> findLinkFieldByTypeVersion(String dataType, String dataVersion) {
        String typeVersion = dataType;
        if (Strings.isNotBlank(dataVersion)) {
            typeVersion = dataType + "-" + dataVersion;
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).eq("is_link", 1));
    }
}
