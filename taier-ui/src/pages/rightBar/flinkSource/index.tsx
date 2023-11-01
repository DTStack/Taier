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
import { useContext, useEffect, useMemo, useRef,useState } from 'react';
import { DeleteOutlined,PlusOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import type { FormInstance } from 'antd';
import { Button, Collapse, Form, Popconfirm } from 'antd';
import type { DefaultOptionType } from 'antd/lib/cascader';
import classNames from 'classnames';
import moment from 'moment';

import stream from '@/api';
import { CODE_TYPE, DATA_SOURCE_ENUM, FLINK_VERSIONS, formItemLayout, KAFKA_DATA_TYPE } from '@/constant';
import type { IDataSourceUsedInSyncProps, IFlinkSourceProps } from '@/interface';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import { FormContext } from '@/services/rightBarService';
import { getColumnsByColumnsText } from '@/utils';
import { isAvro, isKafka } from '@/utils/is';
import SourceForm from './form';
import { getTimeZoneList } from './panelData';
import './index.scss';

const { Panel } = Collapse;
const DEFAULT_TYPE = DATA_SOURCE_ENUM.KAFKA_2X;

/**
 * 创建源表的默认输入内容
 */
const DEFAULT_INPUT_VALUE: IFormFieldProps[typeof NAME_FIELD][number] = {
    type: DEFAULT_TYPE,
    sourceId: undefined,
    topic: undefined,
    charset: CODE_TYPE.UTF_8,
    table: undefined,
    timeType: 1,
    timeTypeArr: [1],
    timeZone: ['Asia', 'Shanghai'], // 默认时区值
    offset: 0,
    offsetUnit: 'SECOND',
    columnsText: undefined,
    parallelism: 1,
    offsetReset: 'latest',
    sourceDataType: KAFKA_DATA_TYPE.TYPE_JSON,
};

/**
 * 表单收集的字段
 */
export const NAME_FIELD = 'panelColumn';

interface IFormFieldProps {
    [NAME_FIELD]: Partial<
        Omit<IFlinkSourceProps, 'timeZone' | 'timestampOffset'> & {
            timeZone?: string[];
            timestampOffset?: moment.Moment;
        }
    >[];
}

export default function FlinkSourcePanel({ current }: IRightBarComponentProps) {
    const currentPage = current?.tab?.data || {};

    const { form } = useContext(FormContext) as { form?: FormInstance<IFormFieldProps> };
    const [panelActiveKey, setPanelActiveKey] = useState<string[]>([]);
    const [originOptionType, setOriginOptionType] = useState<Record<number, IDataSourceUsedInSyncProps[]>>({});
    const [topicOptionType, setTopicOptionType] = useState<Record<number, string[]>>({});
    // 时区数据
    const [timeZoneData, setTimeZoneData] = useState<DefaultOptionType[]>([]);
    const isAddOrRemove = useRef(false);

    const initTimeZoneList = async () => {
        const list = await getTimeZoneList();
        setTimeZoneData(list);
    };

    // 获取数据源
    const getTypeOriginData = async (type?: DATA_SOURCE_ENUM) => {
        if (type !== undefined) {
            const existData = originOptionType[type];
            if (existData) {
                return;
            }
            const res = await stream.getTypeOriginData({ type });
            if (res.code === 1) {
                // 没有新建对象来 setState，当有多个源表同时请求数据源的话，新建对象的话会导致旧对象会被新对象覆盖掉
                setOriginOptionType((options) => ({ ...options, [type]: res.data }));
            }
        }
    };

    const getTopicType = async (sourceId?: number) => {
        if (sourceId !== undefined) {
            // improve the performance
            const existTopic = topicOptionType[sourceId];
            if (existTopic) {
                return;
            }
            const res = await stream.getTopicType({ sourceId });
            if (res.code === 1) {
                setTopicOptionType((options) => ({
                    ...options,
                    [sourceId]: res.data,
                }));
            }
        }
    };

    /**
     * 添加或删除源表
     * @param panelKey 删除的时候需要带上 panelKey
     */
    const handlePanelChanged = (type: 'add' | 'delete', panelKey?: string) => {
        if (type === 'add') {
            getTypeOriginData(DEFAULT_INPUT_VALUE.type);
            getTopicType(DEFAULT_INPUT_VALUE.sourceId);
        } else {
            setPanelActiveKey((keys) => keys.filter((key) => panelKey !== key));
        }

        // 记录下是否触发了添加或删除方法，用来在 handleFormValuesChange 进行标记
        isAddOrRemove.current = true;
    };

    const handleSyncFormToTab = () => {
        const source = form?.getFieldsValue()[NAME_FIELD];
        // 需要额外处理部分字段
        const nextSource = source?.map((s) => {
            const next: Partial<IFlinkSourceProps> = {
                ...s,
                timeZone: s.timeZone?.join('/'),
                timestampOffset: s.timestampOffset?.valueOf(),
            };
            return next;
        });
        // 将表单的值保存至 tab 中
        molecule.editor.updateTab({
            id: current!.tab!.id,
            data: {
                ...current!.tab!.data,
                source: nextSource,
            },
        });
    };

    const handleFormValuesChange = (changedValues: IFormFieldProps, values: IFormFieldProps) => {
        if (isAddOrRemove.current) {
            isAddOrRemove.current = false;
            // 如果是添加或者删除整个数据项同样触发该方法，但是我们仅仅希望这里只触发修改的方法
            handleSyncFormToTab();
            return;
        }

        // 当前正在修改的数据索引
        const changeIndex = changedValues[NAME_FIELD].findIndex((col) => col);
        const changeKeys = Object.keys(changedValues[NAME_FIELD][changeIndex]);

        if (changeKeys.includes('type')) {
            const value = changedValues[NAME_FIELD][changeIndex].type;
            getTypeOriginData(value);

            const nextValues = { ...values };
            nextValues[NAME_FIELD][changeIndex] = { ...DEFAULT_INPUT_VALUE, type: value };
            if (isKafka(value)) {
                nextValues[NAME_FIELD][changeIndex].sourceDataType =
                    value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT
                        ? KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT
                        : KAFKA_DATA_TYPE.TYPE_JSON;
            }
            form?.setFieldsValue(nextValues);
        }

        if (changeKeys.includes('sourceId')) {
            const value = changedValues[NAME_FIELD][changeIndex].sourceId;
            getTopicType(value);

            const nextValues = { ...values };
            nextValues[NAME_FIELD][changeIndex] = {
                ...DEFAULT_INPUT_VALUE,
                type: nextValues[NAME_FIELD][changeIndex].type,
                sourceDataType: nextValues[NAME_FIELD][changeIndex].sourceDataType,
                sourceId: value,
            };

            form?.setFieldsValue(nextValues);
        }

        if (changeKeys.includes('columnsText')) {
            const value = changedValues[NAME_FIELD][changeIndex].columnsText;
            const cols = getColumnsByColumnsText(value);

            // timeColumn 是否需要重置
            const timeColumnCheck = (columns: typeof cols) => {
                if (values[NAME_FIELD][changeIndex].timeColumn) {
                    if (!columns.find((c) => c.field === values[NAME_FIELD][changeIndex].timeColumn)) {
                        return undefined;
                    }
                }
                return values[NAME_FIELD][changeIndex].timeColumn;
            };

            const nextValues = { ...values };
            nextValues[NAME_FIELD][changeIndex].timeColumn = timeColumnCheck(cols);
            form?.setFieldsValue(nextValues);
        }

        if (changeKeys.includes('sourceDataType')) {
            const value = changedValues[NAME_FIELD][changeIndex].sourceDataType;
            if (!isAvro(value)) {
                const nextValues = { ...values };
                nextValues[NAME_FIELD][changeIndex].schemaInfo = undefined;
                form?.setFieldsValue(nextValues);
            }
        }

        if (changeKeys.includes('timeTypeArr')) {
            let value = changedValues[NAME_FIELD][changeIndex].timeTypeArr || [];

            // timeTypeArr 这个字段只有前端用，根据 timeTypeArr ，清空相应字段
            // 不勾选 ProcTime，不传 procTime 名称字段
            // 不勾选 EventTime，不传时间列、最大延迟时间字段
            if (currentPage.componentVersion === FLINK_VERSIONS.FLINK_1_12) {
                const nextValues = { ...values };
                const panel = nextValues[NAME_FIELD][changeIndex];

                // 如果只勾选了 EventTime，则还需要同时勾选 ProcTime
                if (value.length === 1 && value[0] === 2) {
                    value = [1, 2];
                    panel.timeTypeArr = [1, 2];
                }

                if (!value.includes(1)) {
                    panel.procTime = undefined;
                }
                if (!value.includes(2)) {
                    panel.timeColumn = undefined;
                    panel.offset = undefined;
                }

                form?.setFieldsValue(nextValues);
            }
        }

        if (changeKeys.includes('offsetReset')) {
            const nextValues = { ...values };
            const panel = nextValues[NAME_FIELD][changeIndex];

            panel.timestampOffset = undefined;
            panel.offsetValue = '';
            form?.setFieldsValue(nextValues);
        }

        handleSyncFormToTab();
    };

    useEffect(() => {
        initTimeZoneList();
    }, []);

    useEffect(() => {
        currentPage?.source?.forEach((s: IFlinkSourceProps) => {
            getTypeOriginData(s.type);
            getTopicType(s.sourceId);
        });
    }, [current]);

    const initialValues = useMemo<IFormFieldProps>(() => {
        return {
            [NAME_FIELD]:
                (currentPage?.source as IFlinkSourceProps[]).map((s) => ({
                    ...s,
                    timeZone: s.timeZone?.split('/'),
                    timestampOffset: s.timestampOffset ? moment(s.timestampOffset) : undefined,
                })) || [],
        };
    }, []);

    return (
        <molecule.component.Scrollbar>
            <div className="panel-content">
                <Form<IFormFieldProps>
                    {...formItemLayout}
                    form={form}
                    labelWrap
                    onValuesChange={handleFormValuesChange}
                    initialValues={initialValues}
                >
                    <Form.List name={NAME_FIELD}>
                        {(fields, { add, remove }) => (
                            <>
                                <Collapse
                                    activeKey={panelActiveKey}
                                    bordered={false}
                                    onChange={(key) => setPanelActiveKey(key as string[])}
                                    destroyInactivePanel
                                >
                                    {fields.map((field, index) => {
                                        const { sourceId, type, table } = form?.getFieldValue(NAME_FIELD)[index] || {};
                                        return (
                                            <Panel
                                                header={
                                                    <div className="input-panel-title">
                                                        <span>{` 源表 ${index + 1} ${table ? `(${table})` : ''}`}</span>
                                                    </div>
                                                }
                                                key={field.key.toString()}
                                                extra={
                                                    <Popconfirm
                                                        placement="topLeft"
                                                        title="你确定要删除此源表吗？"
                                                        onConfirm={() => {
                                                            handlePanelChanged('delete', field.key.toString());
                                                            remove(field.name);
                                                        }}
                                                        {...{
                                                            onClick: (e: any) => {
                                                                e.stopPropagation();
                                                            },
                                                        }}
                                                    >
                                                        <DeleteOutlined className={classNames('title-icon')} />
                                                    </Popconfirm>
                                                }
                                                style={{ position: 'relative' }}
                                                className="input-panel"
                                            >
                                                <SourceForm
                                                    index={index}
                                                    componentVersion={FLINK_VERSIONS.FLINK_1_12}
                                                    topicOptionType={topicOptionType[sourceId || -1] || []}
                                                    originOptionType={originOptionType[type || -1] || []}
                                                    timeZoneData={timeZoneData}
                                                />
                                            </Panel>
                                        );
                                    })}
                                </Collapse>
                                <Button
                                    size="large"
                                    block
                                    onClick={() => {
                                        handlePanelChanged('add');
                                        add({ ...DEFAULT_INPUT_VALUE });
                                    }}
                                    icon={<PlusOutlined />}
                                >
                                    <span>添加源表</span>
                                </Button>
                            </>
                        )}
                    </Form.List>
                </Form>
            </div>
        </molecule.component.Scrollbar>
    );
}
