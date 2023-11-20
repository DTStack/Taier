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

import { useEffect, useState } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import { Alert, Form, Input, message,Modal, Radio, Select, Space, Table, Tooltip } from 'antd';
import moment from 'moment';

import stream from '@/api';
import { CHECK_TYPE_VALUE, formItemLayout } from '@/constant';
import { IStreamJobProps } from '@/interface';

const { Option } = Select;

interface IProps {
    data?: Pick<IStreamJobProps, 'jobId' | 'id'>;
    visible: boolean;
    onOk: () => void;
    onCancel: () => void;
}

interface ICheckPointList {
    jobId: string;
    execStartTime: number;
    execEndTime: number;
    engineJobId: string;
    applicationId: string;
}

interface IFormFieldProps {
    type: CHECK_TYPE_VALUE;
    filePath?: string;
    checkPoint?: string;
    checkPointPath?: string;
    pointType?: POINT_TYPE;
}

interface ICheckPointPathProps {
    path: string;
    modificationTime: number;
    blockSize: number;
    owner: string;
}

enum POINT_TYPE {
    CHECK_POINT,
    SAVE_POINT,
}

export default function GoOnTask({ visible, data, onOk, onCancel }: IProps) {
    const [form] = Form.useForm<IFormFieldProps>();
    const [checkPointList, setPointList] = useState<ICheckPointList[]>([]);
    const [checkPointPathList, setPathList] = useState<ICheckPointPathProps[]>([]);
    const [loading, setLoading] = useState(false);

    const getCheckPointList = () => {
        stream.getListHistory({ jobId: data?.jobId }).then((res) => {
            if (res.code === 1) {
                setPointList(res.data || []);
            }
        });
    };

    const renderContent = (type: CHECK_TYPE_VALUE) => {
        switch (type) {
            case CHECK_TYPE_VALUE.CHECK_POINT_FILE:
                return (
                    <Form.Item
                        name="filePath"
                        label="目录"
                        tooltip="例如：hdfs://"
                        rules={[
                            {
                                required: true,
                            },
                            {
                                validator: (_, value) => {
                                    if (!/^hdfs:\/\//.test(value)) {
                                        return Promise.reject(new Error('请输入以”hdfs://”开头的HDFS地址'));
                                    } else if (/\s/.test(value)) {
                                        return Promise.reject(new Error('文件路径不支持空格'));
                                    }
                                    return Promise.resolve();
                                },
                            },
                        ]}
                    >
                        <Input placeholder="请输入HDFS中CheckPoin文件完整路径" />
                    </Form.Item>
                );

            case CHECK_TYPE_VALUE.CHECK_POINT:
                return (
                    <>
                        <Form.Item name="pointType" label="选择类型" initialValue={POINT_TYPE.CHECK_POINT}>
                            <Radio.Group style={{ marginTop: 6 }}>
                                <Space direction="vertical">
                                    <Radio value={POINT_TYPE.CHECK_POINT}>CheckPoint</Radio>
                                    <Radio value={POINT_TYPE.SAVE_POINT}>SavePoint</Radio>
                                </Space>
                            </Radio.Group>
                        </Form.Item>
                        <Form.Item
                            name="checkPoint"
                            label="CheckPoint"
                            tooltip="只暂时近20条数据"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                        >
                            <Select<string>
                                placeholder="请选择 CheckPoint"
                                filterOption={(input, option) =>
                                    option?.props.children?.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                            >
                                {checkPointList.map((checkPoint) => {
                                    const label = `${checkPoint.applicationId}(
										${moment(checkPoint.execStartTime).format('YYYY-MM-DD HH:mm:ss')})`;
                                    return (
                                        <Option value={checkPoint.applicationId} key={checkPoint.applicationId}>
                                            <Tooltip title={label}>
                                                {label.slice(0, 20)}......{label.slice(-22)}
                                            </Tooltip>
                                        </Option>
                                    );
                                })}
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name="checkPointPath"
                            label="目录"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                        >
                            <PathTable loading={loading} data={checkPointPathList} />
                        </Form.Item>
                    </>
                );
            default:
                break;
        }
    };

    const handleValuesChanged = (checkedValue: Partial<IFormFieldProps>, values: IFormFieldProps) => {
        if (Object.keys(checkedValue).includes('type')) {
            if (checkedValue.type === CHECK_TYPE_VALUE.CHECK_POINT) {
                getCheckPointList();
            }

            // reset path list
            setPathList([]);
        }

        if (Object.keys(checkedValue).includes('checkPoint')) {
            setLoading(true);
            stream
                .listCheckPoint({
                    jobId: data?.jobId,
                    applicationId: checkedValue.checkPoint,
                    getSavePointPath: values.pointType === POINT_TYPE.SAVE_POINT,
                })
                .then((res) => {
                    if (res.code === 1) {
                        setPathList(res.data || []);
                    }
                })
                .finally(() => {
                    setLoading(false);
                });

            form.setFieldsValue({
                checkPointPath: undefined,
            });
        }

        // 选择类型切换重制 checkPoint
        if (Object.keys(checkedValue).includes('pointType')) {
            form.setFieldsValue({
                checkPoint: undefined,
                checkPointPath: undefined,
            });
            setPathList([]);
        }
    };

    const doGoOn = () => {
        form.validateFields().then((values) => {
            stream
                .startTask({
                    taskId: data?.id,
                    externalPath:
                        values.type === CHECK_TYPE_VALUE.CHECK_POINT ? values.checkPointPath : values.filePath,
                    isRestoration: 0,
                })
                .then((res) => {
                    if (res.code === 1) {
                        message.success('续跑操作成功！');
                        onOk?.();
                    }
                });
        });
    };

    return (
        <Modal
            title="续跑任务"
            visible={visible}
            okText="确认"
            onCancel={onCancel}
            onOk={doGoOn}
            cancelText="取消"
            maskClosable={false}
            destroyOnClose
        >
            <Alert message="续跑，任务将恢复至停止前的状态继续运行!" type="warning" showIcon />
            <Form<IFormFieldProps>
                {...formItemLayout}
                form={form}
                onValuesChange={handleValuesChanged}
                initialValues={{
                    type: CHECK_TYPE_VALUE.CHECK_POINT_FILE,
                }}
                preserve={false}
            >
                <Form.Item name="type" label="续跑方式">
                    <Radio.Group style={{ marginTop: 6 }}>
                        <Space direction="vertical">
                            <Radio value={CHECK_TYPE_VALUE.CHECK_POINT_FILE}>通过指定文件恢复并续跑</Radio>
                            <Radio value={CHECK_TYPE_VALUE.CHECK_POINT}>选择 CheckPoint 续跑</Radio>
                        </Space>
                    </Radio.Group>
                </Form.Item>
                <Form.Item noStyle dependencies={['type']}>
                    {({ getFieldValue }) => renderContent(getFieldValue('type'))}
                </Form.Item>
            </Form>
        </Modal>
    );
}

interface IPathTable {
    data: ICheckPointPathProps[];
    value?: ICheckPointPathProps['path'];
    loading?: boolean;
    onChange?: (data: ICheckPointPathProps['path']) => void;
}
function PathTable({ value, data, loading, onChange }: IPathTable) {
    const [search, setSearch] = useState<string>('');
    const [filterData, setFilterData] = useState<ICheckPointPathProps[]>(data);

    const handleChanged = (selectedRowKeys: React.Key[]) => {
        onChange?.(selectedRowKeys[0].toString());
    };

    const handleSearch = () => {
        setFilterData(data.filter((d) => d.path.toLowerCase().includes(search.toLowerCase())));
    };

    useEffect(() => {
        setFilterData(data);
        setSearch('');
    }, [data]);

    return (
        <Table<ICheckPointPathProps>
            dataSource={filterData}
            rowKey="path"
            size="small"
            loading={loading}
            pagination={false}
            scroll={{ y: 300 }}
            columns={[
                {
                    title: 'Path',
                    dataIndex: 'path',
                    key: 'path',
                    filterDropdown: ({ confirm }) => (
                        <Input
                            onPressEnter={() => {
                                handleSearch();
                                confirm();
                            }}
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            style={{ margin: 8, width: 'auto' }}
                        />
                    ),
                    filterIcon: () => <SearchOutlined style={{ color: search ? '#1890ff' : undefined }} />,
                    render: (text: string) => (
                        <Tooltip title={text}>
                            {text.slice(0, 20)}...{text.slice(-20)}
                        </Tooltip>
                    ),
                },
                {
                    title: '修改时间',
                    dataIndex: 'modificationTime',
                    width: 100,
                    key: 'modificationTime',
                    render: (text) => moment(text).format('YYYY-MM-DD HH:mm:ss'),
                },
            ]}
            onRow={(record) => ({ onClick: () => handleChanged([record.path]) })}
            rowSelection={{
                type: 'radio',
                onChange: handleChanged,
                selectedRowKeys: value ? [value] : [],
            }}
        />
    );
}
