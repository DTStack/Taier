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

package com.dtstack.taier.develop.model.exception;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class BaseComponentException extends RdosDefineException {

    private static final String MESSAGE_TEMPLATE = "Error on component of type %s. %s";
    private static final String MESSAGE_TEMPLATE_ZH = "组件 %s 错误 %s";

    private final EComponentType type;
    private final String msg;

    public BaseComponentException(EComponentType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return String.format(MESSAGE_TEMPLATE_ZH, this.type, this.msg);
        } else {
            return String.format(MESSAGE_TEMPLATE, this.type, this.msg);
        }
    }

}
