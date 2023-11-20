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

import { useEffect, useMemo, useState } from 'react';
import { Form,Radio, Select } from 'antd';

import { ENGINE_SOURCE_TYPE_ENUM, PROJECT_CREATE_MODEL } from '@/constant';
import { isOracleEngine } from '@/utils/is';
import api from '../../api';

const { Option } = Select;
const RadioGroup = Radio.Group;
const FormItem = Form.Item;

interface IConfigItemProps {
    engineType: ENGINE_SOURCE_TYPE_ENUM;
    formParentField?: string;
    /**
     * 组件 id
     */
    metaComponent?: number;
    /**
     * 当前选中的集群 id
     */
    clusterId?: number;
    /**
     * 父组件控制 layout
     */
    formItemLayout: any;
    /**
     * 当引擎类型为 hadoop 的时候，需要指定 hadoop 的名称
     */
    hadoopName?: string;
    checked?: boolean;
}

/**
 * 引擎配置表单域
 */
export default ({
    engineType,
    formParentField,
    formItemLayout,
    checked,
    metaComponent,
    clusterId,
    hadoopName = 'Hive2.x',
}: IConfigItemProps) => {
    const [targetDb, setTargetDb] = useState<string[]>([]);

    const getRetainDBList = () => {
        api.getRetainDBList({ clusterId, componentTypeCode: metaComponent }).then((res) => {
            if (res.code === 1) {
                const data = res.data || [];
                setTargetDb([...data]);
            }
        });
    };

    const getEngineRadios = (
        type: number,
        engineTypeText: string
    ): {
        label: string;
        value: PROJECT_CREATE_MODEL;
        disabled?: boolean;
    }[] => {
        return [
            {
                label: '创建',
                value: PROJECT_CREATE_MODEL.NORMAL,
                disabled: type === ENGINE_SOURCE_TYPE_ENUM.ORACLE,
            },
            {
                label: `对接已有${engineTypeText}`,
                value: PROJECT_CREATE_MODEL.IMPORT,
            },
        ];
    };

    const changeType = (value: PROJECT_CREATE_MODEL) => {
        if (value === PROJECT_CREATE_MODEL.IMPORT) {
            getRetainDBList();
        }
    };

    const renderDbOptions = (dataBase: string[]) => {
        // 多引擎则是 Map 对象，需要根据引擎类型单独获取，否则就直取
        const dbList = Array.isArray(dataBase) ? dataBase : dataBase[engineType] || [];

        return dbList.map((item) => {
            return (
                <Option key={item} value={item}>
                    {item}
                </Option>
            );
        });
    };

    useEffect(() => {
        if (engineType === ENGINE_SOURCE_TYPE_ENUM.ORACLE) {
            getRetainDBList();
        }
    }, []);

    const parentField = formParentField ? `${formParentField}` : '';
    const createModelInitialValue = isOracleEngine(engineType)
        ? PROJECT_CREATE_MODEL.IMPORT
        : PROJECT_CREATE_MODEL.NORMAL;

    const createModelOptions = useMemo<
        {
            label: string;
            value: PROJECT_CREATE_MODEL;
            disabled?: boolean;
        }[]
    >(() => {
        switch (engineType) {
            case ENGINE_SOURCE_TYPE_ENUM.HADOOP: {
                return getEngineRadios(engineType, hadoopName);
            }
            case ENGINE_SOURCE_TYPE_ENUM.LIBRA: {
                return getEngineRadios(engineType, 'LibrA Schema');
            }
            case ENGINE_SOURCE_TYPE_ENUM.TI_DB: {
                return getEngineRadios(engineType, 'TiDB Schema');
            }
            case ENGINE_SOURCE_TYPE_ENUM.ORACLE: {
                return getEngineRadios(engineType, 'Oracle Schema');
            }
            case ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM: {
                return getEngineRadios(engineType, 'Greenplum Schema');
            }
            case ENGINE_SOURCE_TYPE_ENUM.ADB: {
                return getEngineRadios(engineType, 'AnalyticDB PostgreSQL Schema');
            }
            default:
                return [
                    {
                        label: '对接未知类型',
                        value: PROJECT_CREATE_MODEL.IMPORT,
                    },
                ];
        }
    }, [engineType, hadoopName]);

    return (
        <>
            <FormItem
                label="初始化方式"
                {...formItemLayout}
                name={`${parentField}.createModel`}
                initialValue={createModelInitialValue}
            >
                <RadioGroup onChange={(e) => changeType(e.target.value)} options={createModelOptions} />
            </FormItem>
            <FormItem
                noStyle
                shouldUpdate={(pre, cur) => pre[`${parentField}.createModel`] !== cur[`${parentField}.createModel`]}
            >
                {({ getFieldValue }) =>
                    getFieldValue(`${parentField}.createModel`) === PROJECT_CREATE_MODEL.IMPORT ||
                    isOracleEngine(engineType) ? (
                        <>
                            <FormItem {...formItemLayout} label="对接目标" required={checked}>
                                <FormItem
                                    noStyle
                                    name={`${parentField}.database`}
                                    rules={[
                                        {
                                            required: checked,
                                            message: '请选择对接目标',
                                        },
                                    ]}
                                >
                                    <Select style={{ width: '100%' }} placeholder="请选择对接目标">
                                        {renderDbOptions(targetDb)}
                                    </Select>
                                </FormItem>
                            </FormItem>
                        </>
                    ) : null
                }
            </FormItem>
        </>
    );
};
