package com.dtstack.engine.master.router;

import org.springframework.web.bind.annotation.RequestParam;
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
        Optional<RequestParam> dtRequestParam = context.resolvedMethodParameter().findAnnotation(RequestParam.class);
        if (dtRequestParam.isPresent()) {
            RequestParam annotation = dtRequestParam.get();
            context.parameterBuilder().name(emptyToNull(annotation.name()))
                    .description(emptyToNull(annotation.name()))
                    .required(annotation.required())
                    .parameterType(PARAMETER_TYPE)
                    .order(SWAGGER_PLUGIN_ORDER);
        }
    }
}
