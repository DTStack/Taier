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

import React from 'react';
import { Form, Input } from 'antd';

import { binlogPortHelp } from '@/components/helpDoc/docs';

const FormItem = Form.Item;

export default () => {
    return (
        <React.Fragment>
            <FormItem name="macAndIp" label="主机名/IP">
                <Input disabled />
            </FormItem>
            <FormItem
                name="port"
                label="端口"
                rules={[
                    {
                        validator: (rule: any, value: any, callback: any) => {
                            if (value) {
                                if (parseInt(value)) {
                                    callback();
                                } else {
                                    const error = '请输入正确的端口';
                                    callback(error);
                                }
                            } else {
                                callback();
                            }
                        },
                    },
                ]}
                tooltip={binlogPortHelp}
            >
                <Input
                    // disabled={isEdit}
                    placeholder="请输入端口"
                    style={{ width: '100%' }}
                />
            </FormItem>
        </React.Fragment>
    );
};
