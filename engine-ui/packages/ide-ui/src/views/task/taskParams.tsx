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

import * as React from 'react';
import { Form, Collapse, Input } from 'antd';
import { debounce } from 'lodash';

import './styles.css';
import molecule from '@dtinsight/molecule/esm';
import HelpDoc from '../../components/helpDoc';
import {
    EDIT_TASK_PREFIX,
    EDIT_FOLDER_PREFIX,
    CREATE_TASK_PREFIX,
} from '../common/utils/const';

const FormItem = Form.Item;
const Panel = Collapse.Panel;

const formItemLayout: any = {
    // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};

// 匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
// 原来的正则：/(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i;
export const paramsRegPattern =
    /* eslint-disable-next-line */
    /^\$[\{\[\(](\S+\((.*)\)|.+)[\}\]\)]$|^(?!\$)\S+$/i;

class TaskParams extends React.Component<any, any> {
    onChange = (index: any, value: any) => {
        const { current } = this.props;
        const tabData = current.tab.data;

        if (!value || paramsRegPattern.test(value)) {
            const taskVariables: any = [...tabData.taskVariables];
            taskVariables[index].paramCommand = value;
            console.log('onChange:', { taskVariables });
            // onChange({ taskVariables });
        }
    };

    debounceChange = debounce(this.onChange, 300, { maxWait: 2000 });

    removeParams = (index: any) => {
        const { current, onChange } = this.props;
        const tabData = current.tab.data;
        const taskVariables: any = [...tabData.taskVariables];
        taskVariables.splice(index, 1);
        onChange({ taskVariables });
    };

    getFormItems = () => {
        const { current } = this.props;
        const { getFieldDecorator } = this.props.form;
        const tabData = current.tab.data;
        const { taskVariables } = tabData;
        const sysArr: any = [];
        const customArr: any = [];
        const getFormItem = (index: any, param: any) => (
            <FormItem
                key={param.paramName}
                {...formItemLayout}
                label={param.paramName}
            >
                {getFieldDecorator(param.paramName, {
                    rules: [
                        {
                            pattern: paramsRegPattern,
                            message: '参数格式不正确',
                        },
                    ],
                    initialValue: param.paramCommand,
                })(
                    <Input
                        disabled={param.type === 0}
                        onChange={(e: any) => {
                            this.debounceChange(index, e.target.value);
                        }}
                    />
                )}
            </FormItem>
        );
        if (taskVariables) {
            for (let i = 0; i < taskVariables.length; i++) {
                const param = taskVariables[i];
                const formItem = getFormItem(i, param);
                if (param.type === 0) {
                    // 系统参数
                    sysArr.push(formItem);
                } else if (param.type === 1) {
                    // 自定义参数
                    customArr.push(formItem);
                }
            }
        }
        return {
            sysItems: sysArr,
            customItems: customArr,
        };
    };

    renderNothing(text: any) {
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
    }

    render() {
        const { current } = this.props;
        if (
            !current ||
            !current.activeTab ||
            current.activeTab.includes(EDIT_TASK_PREFIX) ||
            current.activeTab.includes(EDIT_FOLDER_PREFIX) ||
            current.activeTab.includes(CREATE_TASK_PREFIX)
        ) {
            return (
                <div
                    style={{
                        marginTop: 10,
                        textAlign: 'center',
                    }}
                >
                    无法提供任务参数
                </div>
            );
        }
        const { tab } = current;
        const tabData = tab.data;
        const isLocked =
            tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock;
        const formItems = this.getFormItems();

        return (
            <molecule.component.Scrollable>
                <Form className="taskParams" style={{ position: 'relative' }}>
                    {isLocked ? <div className="cover-mask"></div> : null}
                    <Collapse
                        style={{ background: 'transparent' }}
                        bordered={false}
                        defaultActiveKey={['1', '2']}
                    >
                        <Panel
                            key="1"
                            header={
                                <span>
                                    系统参数配置{' '}
                                    <HelpDoc
                                        style={{ position: 'inherit' }}
                                        doc="customSystemParams"
                                    />
                                </span>
                            }
                        >
                            {formItems.sysItems.length
                                ? formItems.sysItems
                                : this.renderNothing('无系统参数')}
                        </Panel>
                        <Panel
                            key="2"
                            header={
                                <span>
                                    自定义参数配置{' '}
                                    <HelpDoc
                                        style={{ position: 'inherit' }}
                                        doc="customParams"
                                    />
                                </span>
                            }
                        >
                            {formItems.customItems.length
                                ? formItems.customItems
                                : this.renderNothing('无自定义参数')}
                        </Panel>
                    </Collapse>
                </Form>
            </molecule.component.Scrollable>
        );
    }
}

const FormWrapper = Form.create<any>()(TaskParams);

export default FormWrapper;
