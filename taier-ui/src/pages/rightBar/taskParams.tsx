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

import { useMemo } from 'react';
import molecule from '@dtinsight/molecule/esm';
import { Collapse, Form, Input, Tooltip } from 'antd';

import { formItemLayout, PARAMS_ENUM } from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import HelpDoc from '../../components/helpDoc';
import './taskParams.scss';

const FormItem = Form.Item;
const { Panel } = Collapse;

// 匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
// 原来的正则：/(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i;
const paramsRegPattern = /^\$[\{\[\(](\S+\((.*)\)|.+)[\}\]\)]$|^(?!\$)\S+$/i;

export default function TaskParams({ current }: IRightBarComponentProps) {
    const [form] = Form.useForm();

    const handleFormChanged = (changed: Record<string, string>, tabData: IOfflineTaskProps) => {
        const nextTaskVariables = (tabData.taskVariables || []).concat();
        Object.keys(changed).forEach((key) => {
            const target = nextTaskVariables.find((v) => v.paramName === key)!;
            target.paramCommand = changed[key];
        });
        // update the tab's values
        molecule.editor.updateTab({
            ...current!.tab!,
            data: {
                ...current!.tab!.data,
                taskVariables: nextTaskVariables,
            },
        });
    };

    const renderNothing = (text: string) => {
        return (
            <p
                style={{
                    textAlign: 'center',
                    fontSize: '14px',
                    color: '#a1a1a1',
                }}
            >
                {text || '无参数'}
            </p>
        );
    };

    const systemParams = useMemo(() => {
        return (
            (current?.tab?.data as IOfflineTaskProps | undefined)?.taskVariables?.filter(
                (p) => p.type === PARAMS_ENUM.SYSTEM
            ) || []
        );
    }, [current?.tab?.data?.taskVariables]);

    const customParams = useMemo(() => {
        return (
            (current?.tab?.data as IOfflineTaskProps | undefined)?.taskVariables?.filter(
                (p) => p.type === PARAMS_ENUM.CUSTOM
            ) || []
        );
    }, [current?.tab?.data?.taskVariables]);

    const tabData = current!.tab!.data!;

    return (
        <molecule.component.Scrollbar>
            <Form form={form} onValuesChange={(changed) => handleFormChanged(changed, tabData)} className="taskParams">
                <Collapse className="bg-transparent" bordered={false} defaultActiveKey={['1', '2']}>
                    <Panel
                        key="1"
                        header={
                            <span>
                                系统参数配置
                                <HelpDoc doc="customSystemParams" />
                            </span>
                        }
                    >
                        {systemParams.length
                            ? systemParams.map((param) => (
                                  <FormItem
                                      key={param.paramName}
                                      {...formItemLayout}
                                      label={
                                          <Tooltip className="taskParams__ellipsis" title={param.paramName}>
                                              {param.paramName}
                                          </Tooltip>
                                      }
                                      name={param.paramName}
                                      rules={[
                                          {
                                              pattern: paramsRegPattern,
                                              message: '参数格式不正确',
                                          },
                                      ]}
                                      initialValue={param.paramCommand}
                                  >
                                      <Input disabled />
                                  </FormItem>
                              ))
                            : renderNothing('无系统参数')}
                    </Panel>
                    <Panel
                        key="2"
                        header={
                            <span>
                                自定义参数配置
                                <HelpDoc doc="customParams" />
                            </span>
                        }
                    >
                        {customParams.length
                            ? customParams.map((param) => (
                                  <FormItem
                                      key={param.paramName}
                                      {...formItemLayout}
                                      label={param.paramName}
                                      name={param.paramName}
                                      rules={[
                                          {
                                              pattern: paramsRegPattern,
                                              message: '参数格式不正确',
                                          },
                                      ]}
                                      initialValue={param.paramCommand}
                                  >
                                      <Input />
                                  </FormItem>
                              ))
                            : renderNothing('无自定义参数')}
                    </Panel>
                </Collapse>
            </Form>
        </molecule.component.Scrollbar>
    );
}
