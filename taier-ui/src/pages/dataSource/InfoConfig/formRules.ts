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

import type { Rule } from 'antd/lib/form';

import { utf8to16 } from '@/utils';
import type { IFormFieldVoList } from '.';

export function getRules(item: IFormFieldVoList) {
    const ruleArr: Rule[] = [
        {
            required: item.required === 1,
            message: `${item.label}不能为空`,
        },
    ];

    if (item?.validInfo) {
        try {
            const validInfo = JSON.parse(item.validInfo);
            const validKeys = Object.keys(validInfo);

            if (validKeys.includes('length')) {
                ruleArr.push(validInfo.length);
            }
            if (validKeys.includes('regex')) {
                ruleArr.push({
                    pattern: item.regex ? RegExp(item.regex.substring(1, item.regex.length - 1)) : undefined,
                    message: validInfo?.regex?.message,
                });
            }
        } catch (error) {
            // don't handle this error
        }
    }

    if (item.label === 'JDBC URL') {
        ruleArr.push({
            pattern: /^[\S]+$/,
            message: 'JDBC URL不能包含空格',
        });
    }

    if (item.label === '数据源名称') {
        ruleArr.push({
            pattern: /^[\u4e00-\u9fa50-9A-Za-z_]+$/,
            message: '仅支持中文、数字、英文大小写、下划线',
        });
    }

    return {
        initialValue: utf8to16(item.initialValue),
        rules: ruleArr,
        validateFirst: true,
    };
}
