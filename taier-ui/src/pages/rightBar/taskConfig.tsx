import { useContext, useMemo } from 'react';
import molecule from '@dtinsight/molecule';
import type { FormInstance } from 'antd';
import { Checkbox, Collapse,Form, Input, InputNumber, Select } from 'antd';

import { dirtyFailRecord, dirtyMaxRecord, dirtySaveType, logPrintTimes } from '@/components/helpDoc/docs';
import { DATA_SOURCE_ENUM, DIRTY_DATA_SAVE, formItemLayout } from '@/constant';
import { dataSourceService } from '@/services';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import { FormContext } from '@/services/rightBarService';

const { Panel } = Collapse;

interface IFormFieldProps {
    openDirtyDataManage: boolean;
    maxRows?: number;
    maxCollectFailedRows?: number;
    outputType?: DIRTY_DATA_SAVE;
    linkInfo?: {
        sourceId: number;
    };
    tableName?: string;
    logPrintInterval?: number;
}

export default function TaskConfig({ current }: IRightBarComponentProps) {
    const { form } = useContext(FormContext) as { form?: FormInstance<IFormFieldProps> };

    const handleFormValuesChange = () => {
        setTimeout(() => {
            const { openDirtyDataManage, ...restValues } = form?.getFieldsValue() || {};

            molecule.editor.updateTab({
                ...current!.tab!,
                data: {
                    ...current!.tab!.data,
                    openDirtyDataManage,
                    taskDirtyDataManageVO: {
                        ...current!.tab!.data!.taskDirtyDataManageVO,
                        ...restValues,
                    },
                },
            });
        }, 0);
    };

    const initialValues = useMemo<IFormFieldProps>(() => {
        if (current?.tab?.data) {
            const { maxRows, maxCollectFailedRows, outputType, linkInfo, tableName, logPrintInterval } = (current.tab
                .data.taskDirtyDataManageVO || {}) as IFormFieldProps;

            return {
                openDirtyDataManage: current.tab.data.openDirtyDataManage,
                maxRows,
                maxCollectFailedRows,
                outputType,
                linkInfo,
                tableName,
                logPrintInterval,
            };
        }
        return {
            openDirtyDataManage: false,
        };
    }, [current?.activeTab]);

    const dataSourceList = useMemo(
        () =>
            dataSourceService
                .getDataSource()
                .filter((d) => d.dataTypeCode === DATA_SOURCE_ENUM.MYSQL)
                .map((i) => ({ label: i.dataName, value: i.dataInfoId })),
        []
    );

    return (
        <molecule.component.Scrollbar>
            <Collapse bordered={false} ghost defaultActiveKey={['1']}>
                <Panel key="1" header="脏数据管理">
                    <Form
                        form={form}
                        initialValues={initialValues}
                        preserve={false}
                        onValuesChange={handleFormValuesChange}
                        {...formItemLayout}
                    >
                        <Form.Item label="脏数据记录" name="openDirtyDataManage" valuePropName="checked">
                            <Checkbox> 开启 </Checkbox>
                        </Form.Item>
                        <Form.Item dependencies={['openDirtyDataManage']} noStyle>
                            {({ getFieldValue }) =>
                                getFieldValue('openDirtyDataManage') && (
                                    <>
                                        <Form.Item
                                            label="脏数据最大值"
                                            name="maxRows"
                                            tooltip={dirtyMaxRecord}
                                            initialValue={100000}
                                        >
                                            <InputNumber
                                                style={{ width: '100%' }}
                                                addonAfter="条"
                                                max={1000000}
                                                min={-1}
                                            />
                                        </Form.Item>
                                        <Form.Item
                                            label="失败条数"
                                            name="maxCollectFailedRows"
                                            tooltip={dirtyFailRecord}
                                            initialValue={100000}
                                        >
                                            <InputNumber
                                                style={{ width: '100%' }}
                                                addonAfter="条"
                                                max={1000000}
                                                min={-1}
                                            />
                                        </Form.Item>
                                        <Form.Item
                                            label="脏数据保存"
                                            name="outputType"
                                            tooltip={dirtySaveType}
                                            initialValue={DIRTY_DATA_SAVE.NO_SAVE}
                                        >
                                            <Select>
                                                <Select.Option value={DIRTY_DATA_SAVE.NO_SAVE}>
                                                    不保存，仅日志输出
                                                </Select.Option>
                                                <Select.Option value={DIRTY_DATA_SAVE.BY_MYSQL}>
                                                    保存至MySQL
                                                </Select.Option>
                                            </Select>
                                        </Form.Item>
                                        <Form.Item dependencies={['outputType']} noStyle>
                                            {({ getFieldValue: getOutputType }) =>
                                                getOutputType('outputType') === DIRTY_DATA_SAVE.BY_MYSQL && (
                                                    <>
                                                        <Form.Item
                                                            label="脏数据写入库"
                                                            name={['linkInfo', 'sourceId']}
                                                            rules={[
                                                                {
                                                                    required: true,
                                                                    message: '脏数据写入库为必填项',
                                                                },
                                                            ]}
                                                        >
                                                            <Select
                                                                placeholder="请选择脏数据写入的MySQL库"
                                                                allowClear
                                                                showSearch
                                                                options={dataSourceList}
                                                                filterOption={(input, option) =>
                                                                    option!
                                                                        .label!.toLowerCase()
                                                                        .includes(input.toLowerCase())
                                                                }
                                                            />
                                                        </Form.Item>
                                                        <Form.Item
                                                            label="脏数据写入表"
                                                            name="tableName"
                                                            initialValue="flinkx_dirty_data"
                                                        >
                                                            <Input disabled />
                                                        </Form.Item>
                                                    </>
                                                )
                                            }
                                        </Form.Item>
                                        <Form.Item dependencies={['outputType', 'maxRows']} noStyle>
                                            {({ getFieldValue: innerGetFieldValue }) =>
                                                innerGetFieldValue('outputType') === DIRTY_DATA_SAVE.NO_SAVE && (
                                                    <Form.Item
                                                        label="日志打印频率"
                                                        name="logPrintInterval"
                                                        tooltip={logPrintTimes}
                                                        initialValue={1}
                                                    >
                                                        <InputNumber
                                                            style={{ width: '100%' }}
                                                            addonAfter="条/次"
                                                            max={
                                                                typeof innerGetFieldValue('maxRows') === 'number'
                                                                    ? innerGetFieldValue('maxRows') + 1
                                                                    : 1000000
                                                            }
                                                            min={0}
                                                        />
                                                    </Form.Item>
                                                )
                                            }
                                        </Form.Item>
                                    </>
                                )
                            }
                        </Form.Item>
                    </Form>
                </Panel>
            </Collapse>
        </molecule.component.Scrollbar>
    );
}
