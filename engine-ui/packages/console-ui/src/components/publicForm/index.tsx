import React from 'react';
import { Form } from 'antd';
const FormItem = Form.Item

export interface ValidationRule {
    message?: React.ReactNode;
    type?: string;
    required?: boolean;
    whitespace?: boolean;
    len?: number;
    min?: number;
    max?: number;
    enum?: string | string[];
    pattern?: RegExp;
    transform?: (value: any) => any;
    validator?: (rule: any, value: any, callback?: any, source?: any, options?: any) => any;
}

interface ItemType {
    item?: {
        label?: React.ReactNode;
        key: string | number;
        required?: boolean;
        component?: React.ReactNode;
        options: {
            className?: string;
        };
        rules?: ValidationRule[];
    };
    layout?: {};
    getFieldDecorator: any;
    children: React.ReactNode;
    topSolt?: React.ReactNode;
    bottomSolt?: React.ReactNode;
    [propName: string]: any;
}

export default function RenderFormItem (props: ItemType) {
    const { fieldDecoratorOptions, name, formOptions, getFieldDecorator, topSolt, bottomSolt, children } = props
    return (
    <>
      <FormItem colon {...formOptions} >
          {topSolt}
          {getFieldDecorator(name || 'default', {
              ...fieldDecoratorOptions
          })(children)}
          {bottomSolt}
      </FormItem>
    </>
    )
}
