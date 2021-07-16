package com.dtstack.engine.datasource.controller.datasource;

import com.dtstack.engine.common.exception.BizException;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.facade.datasource.FormTemplateFacade;
import com.dtstack.engine.datasource.param.datasource.DsTypeVersionParam;
import com.dtstack.engine.datasource.vo.datasource.form.DsFormTemplateVo;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Api(tags = {"数据源中心-数据源表单模版化"})
@RestController
@RequestMapping(value = "/api/publicService/dsForm")
public class DatasourceFormController {

    @Autowired
    private FormTemplateFacade formTemplateFacade;

    @ApiOperation("根据数据库类型和版本查找表单模版")
    @PostMapping("/findFormByTypeVersion")
    public R<DsFormTemplateVo> findTemplateByTypeVersion(@RequestBody DsTypeVersionParam param) {
        return new APITemplate<DsFormTemplateVo>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(param.getDataType(), "数据源类型不能为空!");
            }

            @Override
            protected DsFormTemplateVo process() throws BizException {
                return formTemplateFacade.findTemplateByTypeVersion(param);
            }
        }.execute();
    }


}
