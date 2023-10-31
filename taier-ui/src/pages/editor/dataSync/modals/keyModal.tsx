import type { ModalProps } from 'antd';
import { Form, Input, InputNumber, Modal, Select } from 'antd';

import { stringColumnFormat } from '@/components/helpDoc/docs';
import { DATA_SOURCE_ENUM, formItemLayout, HBASE_FIELD_TYPES, HDFS_FIELD_TYPES } from '@/constant';
import type { IDataColumnsProps } from '@/interface';
import { isValidFormatType } from '@/utils';

const FormItem = Form.Item;
const { Option } = Select;

interface IKeyModalProps extends Pick<ModalProps, 'title' | 'visible'> {
    onOk?: (values: any) => void;
    onCancel?: () => void;
    dataType?: DATA_SOURCE_ENUM;
    sourceColumnFamily?: string[];
    targetColumnFamily?: string[];
    keyModal?: {
        isReader?: boolean;
        editField?: IDataColumnsProps;
    };
}

export default function KeyModal({
    dataType,
    title,
    visible,
    keyModal = {},
    sourceColumnFamily,
    targetColumnFamily,
    onOk,
    onCancel,
}: IKeyModalProps) {
    const [form] = Form.useForm();

    const handleSubmit = () => {
        form.validateFields().then((values) => {
            onOk?.(values);
        });
    };

    const handleCancel = () => {
        onCancel?.();
    };

    const columnFamily = (data?: string[]) => {
        return (
            data?.map((item) => (
                <Option key={item} value={item}>
                    {item}
                </Option>
            )) || []
        );
    };

    const getHBaseTypeItem = (initialValue?: string) => {
        return (
            <FormItem
                name="type"
                label="选择类型"
                key="type"
                rules={[
                    {
                        required: true,
                    },
                ]}
                initialValue={initialValue || 'STRING'}
            >
                <Select placeholder="请选择类型" options={HBASE_FIELD_TYPES.map((t) => ({ label: t, value: t }))} />
            </FormItem>
        );
    };

    const renderFormItems = () => {
        const { isReader, editField } = keyModal;

        if (editField?.value) {
            // 常量额外处理
            return [
                <FormItem
                    name="key"
                    label="名称"
                    key="key"
                    rules={[
                        {
                            required: true,
                            type: 'string',
                        },
                    ]}
                    initialValue={editField.key}
                >
                    <Input style={{ width: '100%' }} disabled />
                </FormItem>,
                <FormItem
                    name="value"
                    label="值"
                    key="value"
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                    initialValue={editField.value}
                >
                    <Input style={{ width: '100%' }} disabled />
                </FormItem>,
                <FormItem
                    name="type"
                    label="类型"
                    key="type"
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                    initialValue={editField.type}
                >
                    <Input style={{ width: '100%' }} disabled />
                </FormItem>,
            ];
        }

        const initialKeyValue = editField?.index ?? editField?.key;

        if (isReader) {
            // 数据源
            switch (dataType) {
                case DATA_SOURCE_ENUM.FTP:
                case DATA_SOURCE_ENUM.HDFS:
                case DATA_SOURCE_ENUM.S3: {
                    return [
                        <FormItem
                            name="index"
                            label="索引值"
                            key="index"
                            rules={[
                                {
                                    required: true,
                                    type: 'integer',
                                    message: '请按要求填写索引值！',
                                },
                            ]}
                            initialValue={initialKeyValue}
                        >
                            <InputNumber placeholder="请输入索引值" style={{ width: '100%' }} min={0} />
                        </FormItem>,
                        <FormItem
                            name="type"
                            label="类型"
                            key="type"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={editField?.type || 'STRING'}
                        >
                            <Select
                                placeholder="请选择类型"
                                options={HDFS_FIELD_TYPES.map((t) => ({ label: t, value: t }))}
                                optionFilterProp="label"
                                showSearch
                            />
                        </FormItem>,
                    ];
                }
                case DATA_SOURCE_ENUM.HBASE: {
                    const disabledEdit = editField && editField.key === 'rowkey';
                    return [
                        <FormItem
                            name="key"
                            label="列名"
                            key="key"
                            rules={[
                                {
                                    required: true,
                                    type: 'string',
                                },
                            ]}
                            initialValue={editField?.key}
                        >
                            <Input placeholder="请输入列名" style={{ width: '100%' }} disabled={disabledEdit} />
                        </FormItem>,
                        <FormItem
                            name="cf"
                            label="列族"
                            key="cf"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={editField?.cf}
                        >
                            <Select placeholder="请选择列族" disabled={disabledEdit}>
                                {columnFamily(sourceColumnFamily)}
                            </Select>
                        </FormItem>,
                        getHBaseTypeItem(editField?.type),
                    ];
                }
                default: {
                    return [
                        <FormItem
                            name="key"
                            label="字段名"
                            key="key"
                            rules={[
                                {
                                    required: true,
                                    message: '请按要求填写字段名！',
                                },
                            ]}
                            initialValue={editField?.key}
                        >
                            <Input disabled placeholder="请输入字段名" style={{ width: '100%' }} />
                        </FormItem>,
                        <FormItem
                            name="type"
                            label="类型"
                            key="type"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={editField?.type || 'STRING'}
                        >
                            <Input disabled />
                        </FormItem>,
                    ];
                }
            }
        } else {
            // 目标表
            switch (dataType) {
                case DATA_SOURCE_ENUM.FTP:
                case DATA_SOURCE_ENUM.HDFS:
                case DATA_SOURCE_ENUM.S3: {
                    return [
                        <FormItem
                            name="key"
                            label="字段名"
                            key="keyName"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={initialKeyValue}
                        >
                            <Input placeholder="请输入字段名" />
                        </FormItem>,
                        <FormItem
                            name="type"
                            label="选择类型"
                            key="type"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={editField?.type || 'STRING'}
                        >
                            <Select
                                placeholder="请选择类型"
                                options={HDFS_FIELD_TYPES.map((t) => ({ label: t, value: t }))}
                                optionFilterProp="label"
                                showSearch
                            />
                        </FormItem>,
                    ];
                }
                case DATA_SOURCE_ENUM.HBASE: {
                    return [
                        <FormItem
                            name="key"
                            label="列名"
                            key="key"
                            rules={[
                                {
                                    required: true,
                                    type: 'string',
                                },
                            ]}
                            initialValue={editField?.key}
                        >
                            <Input placeholder="请输入列名" style={{ width: '100%' }} />
                        </FormItem>,
                        <FormItem
                            name="cf"
                            label="列族"
                            key="cf"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue={editField?.cf}
                        >
                            <Select placeholder="请选择列族">{columnFamily(targetColumnFamily)}</Select>
                        </FormItem>,
                        getHBaseTypeItem(editField?.type),
                    ];
                }
                default:
                    break;
            }
        }
        return [];
    };

    const { editField, isReader } = keyModal;
    const text = editField?.value ? '格式' : '格式化';

    return (
        <Modal title={title} visible={visible} destroyOnClose onOk={handleSubmit} onCancel={handleCancel}>
            <Form form={form} preserve={false} {...formItemLayout}>
                {renderFormItems()}
                {isReader && (
                    <FormItem noStyle dependencies={['type']}>
                        {({ getFieldValue }) =>
                            // 如果源数据类型为字符串，则支持字符串格式化
                            isValidFormatType(getFieldValue('type')) && (
                                <FormItem
                                    name="format"
                                    label={text}
                                    initialValue={editField?.format}
                                    tooltip={stringColumnFormat}
                                >
                                    <Input placeholder="格式化, 例如：yyyy-MM-dd" />
                                </FormItem>
                            )
                        }
                    </FormItem>
                )}
            </Form>
        </Modal>
    );
}
