/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.develop.filter;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
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
