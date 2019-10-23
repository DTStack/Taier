import * as React from 'react';
import { isNumber } from 'lodash';

import { Form, InputNumber } from 'antd';

import { formItemLayout } from './index';

const FormItem = Form.Item;

const inputStyle: any = {
    width: '100%'
}

export function renderNumberFormItem (options: {
    label: React.ReactNode;
    key: string;
    initialValue?: number;
    min?: number;
    excludeMin?: boolean;
    max?: number;
    excludeMax?: boolean;
    step?: number;
    isInt?: boolean;
    isRequired?: boolean;
    handleSubmit: (key: string, value: any) => any;
}, getFieldDecorator: any) {
    return <FormItem
        colon={false}
        label={<div style={{ display: 'inline-block' }}>{options.label}{(<span className="supplementary">{options.excludeMin ? '(' : '['}{options.min || 0},{options.max ? options.max : options.isInt ? '+n' : '+inf'}{options.excludeMax ? ')' : ']'}, {options.isInt ? '正整数' : 'float型'}</span>)}</div>}
        {...formItemLayout}
    >
        {getFieldDecorator(options.key, {
            initialValue: options.initialValue,
            rules: [
                { required: !!options.isRequired },
                { min: options.min || 0, max: options.max, message: `${options.label}的取值范围为${options.excludeMin ? '(' : '['}${options.min || 0},${options.max ? options.max : options.isInt ? '+n' : '+inf'}${options.excludeMax ? ')' : ']'}`, type: 'number' }
            ].filter(Boolean)
        })(
            <InputNumber
                {...{
                    onBlur: (e: any) => options.handleSubmit(options.key, e.target.value)
                }}
                step={options.step}
                formatter={options.isInt ? (value: any) => { return isNumber(value) ? ~~value : value; } : undefined}
                style={inputStyle}
            />
        )}
    </FormItem>
}
