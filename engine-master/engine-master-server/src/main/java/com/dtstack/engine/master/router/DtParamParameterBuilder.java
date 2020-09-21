package com.dtstack.engine.master.router;

import com.dtstack.engine.master.router.DtRequestParam;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.readers.parameter.ApiParamParameterBuilder;

import static com.google.common.base.Strings.emptyToNull;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

@Component
@Order
public class DtParamParameterBuilder extends ApiParamParameterBuilder {
    private static final String PARAMETER_TYPE = "query";

    @Autowired
    public DtParamParameterBuilder(DescriptionResolver descriptions, EnumTypeDeterminer enumTypeDeterminer) {
        super(descriptions, enumTypeDeterminer);
    }

    @Override
    public void apply(ParameterContext context) {
        Optional<DtRequestParam> dtRequestParam = context.resolvedMethodParameter().findAnnotation(DtRequestParam.class);
        if (dtRequestParam.isPresent()) {
            DtRequestParam annotation = dtRequestParam.get();
            context.parameterBuilder().name(emptyToNull(annotation.name()))
                    .description(emptyToNull(annotation.description().equals("") ? annotation.name() : annotation.description()))
                    .required(annotation.required())
                    .parameterType(PARAMETER_TYPE)
                    .order(SWAGGER_PLUGIN_ORDER);
        }
    }
}
