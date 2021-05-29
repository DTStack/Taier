package com.dtstack.engine.imp.datasource;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.dao.mapper.datasource.DsFormFieldMapper;
import com.dtstack.engine.dao.po.datasource.DsFormField;
import com.dtstack.engine.imp.BaseService;
import com.dtstack.engine.param.datasource.DsTypeVersionParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian，quanyue
 * create: 2021/5/10
 */
@Service
public class DsFormFieldService extends BaseService<DsFormFieldMapper, DsFormField> {

    private static String COMMON = "common";

    /**
     * 根据数据源类型和版本获取表单属性列表
     *
     * @param param
     * @return
     */
    public List<DsFormField> findFieldByTypeVersion(DsTypeVersionParam param) {
        String typeVersion = param.getDataType();
        if (StringUtils.isNotBlank(param.getDataVersion())) {
            typeVersion = param.getDataType() + "-" + param.getDataVersion();
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).or().eq("type_version", COMMON));
    }

    /**
     * 根据数据源类型和版本获取具有连接性质的属性列表
     *
     * @param dataType
     * @param dataVersion
     * @return
     */
    public List<DsFormField> findLinkFieldByTypeVersion(String dataType, String dataVersion) {
        String typeVersion = dataType;
        if (StringUtils.isNotBlank(dataVersion)) {
            typeVersion = dataType + "-" + dataVersion;
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).eq("is_link", 1));
    }
}
