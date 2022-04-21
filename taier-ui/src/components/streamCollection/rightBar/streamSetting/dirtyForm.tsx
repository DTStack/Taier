import React from 'react';
import { Form, FormProps } from 'antd';
import DirtyDataLog from './dirtyDataLog';

interface IProps extends FormProps {
    formItemLayout: any;
    data: any;
    isDirtyDataManage: boolean;
    onChange: any;
}
export default function DirtyForm(props: IProps) {
    const [form] = Form.useForm();
    return (
        <Form form={form} {...props.formItemLayout}>
            <DirtyDataLog {...props} form={form} />
        </Form>
    )
}
