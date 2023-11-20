import { Utils } from '@dtinsight/dt-utils/lib';
import { Form,Input, Modal, Select } from 'antd';

import HelpDoc, { relativeStyle } from '@/components/helpDoc';
import { formItemLayout } from '@/constant';
import type { IDataColumnsProps } from '@/interface';
import './constModal.scss';

const { Option } = Select;
const FormItem = Form.Item;

interface IConstModalProps {
    visible?: boolean;
    onOk?: (values: IDataColumnsProps) => void;
    onCancel?: () => void;
}

interface IFormFieldProps {
    constName: string;
    constValue: string;
    type: string;
    constFormat?: string;
}

const systemVariable = [
    '${bdp.system.bizdate}',
    '${bdp.system.bizdate2}',
    '${bdp.system.cyctime}',
    '${bdp.system.premonth}',
    '${bdp.system.currmonth}',
    '${bdp.system.runtime}',
];

export default function ConstModal({ visible, onOk, onCancel }: IConstModalProps) {
    const [form] = Form.useForm<IFormFieldProps>();

    const handleSubmit = () => {
        form.validateFields().then((values) => {
            if (onOk) {
                onOk({
                    type: values.type,
                    key: values.constName,
                    value: values.constValue,
                    format: values.constFormat,
                });
            }
        });
    };

    return (
        <Modal title="添加常量" onOk={handleSubmit} onCancel={onCancel} visible={visible} destroyOnClose>
            <Form
                {...formItemLayout}
                autoComplete="off"
                form={form}
                initialValues={{ type: 'STRING' }}
                preserve={false}
            >
                <FormItem
                    name="constName"
                    label="名称"
                    rules={[
                        {
                            required: true,
                            message: '常量名称不可为空！',
                        },
                    ]}
                >
                    <Input placeholder="请输入常量名称" />
                </FormItem>
                <FormItem
                    name="constValue"
                    label="值"
                    rules={[
                        {
                            required: true,
                            message: '常量值不可为空！',
                        },
                        ({ getFieldValue }) => ({
                            validator(_, value) {
                                const valueWithoutBlank = Utils.trim(value);
                                if (
                                    systemVariable.includes(valueWithoutBlank) &&
                                    getFieldValue('type') === 'TIMESTAMP'
                                ) {
                                    return Promise.reject(new Error('常量的值中存在参数时类型不可选timestamp！'));
                                }

                                return Promise.resolve();
                            },
                        }),
                    ]}
                >
                    <Input placeholder="请输入常量值" />
                </FormItem>
                <FormItem name="type" label="类型">
                    <Select placeholder="请选择类型">
                        <Option value="STRING">STRING</Option>
                        <Option value="DATE">DATE</Option>
                        <Option value="TIMESTAMP">TIMESTAMP</Option>
                    </Select>
                </FormItem>
                <FormItem name="constFormat" label="格式">
                    <Input placeholder="格式化, 例如：yyyy-MM-dd" />
                </FormItem>
                <FormItem
                    wrapperCol={{
                        span: formItemLayout.wrapperCol.sm.span,
                        offset: formItemLayout.labelCol.sm.span,
                    }}
                >
                    <ol className="ant-form-text p-0">
                        <li>{`输入的常量值将会被英文单引号包括，如'abc'、'123'等`}</li>
                        <li>
                            可以配合调度参数使用，如 ${`{bdp.system.bizdate}`}等
                            <HelpDoc style={relativeStyle} doc="customSystemParams" />
                        </li>
                        <li>{`如果您输入的值无法解析，则类型显示为'未识别'`}</li>
                    </ol>
                </FormItem>
            </Form>
        </Modal>
    );
}
