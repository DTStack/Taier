/// <reference types="react" />
import React from 'react';
import FormItem from 'antd/lib/form/FormItem';
export interface FormCreateOption<T> {
    onFieldsChange?: (props: T, fields: any[]) => void;
    onValuesChange?: (props: T, values: any) => void;
    mapPropsToFields?: (props: T) => void;
    withRef?: boolean;
}
export interface FormProps {
    layout?: 'horizontal' | 'inline' | 'vertical';
    horizontal?: boolean;
    inline?: boolean;
    vertical?: boolean;
    form?: WrappedFormUtils;
    onSubmit?: React.FormEventHandler<any>;
    style?: React.CSSProperties;
    className?: string;
    prefixCls?: string;
    hideRequiredMark?: boolean;
}
export interface ValidationRule {
    /** validation error message */
    message?: string;
    /** built-in validation type, available options: https://github.com/yiminghe/async-validator#type */
    type?: string;
    /** indicates whether field is required */
    required?: boolean;
    /** treat required fields that only contain whitespace as errors */
    whitespace?: boolean;
    /** validate the exact length of a field */
    len?: number;
    /** validate the min length of a field */
    min?: number;
    /** validate the max length of a field */
    max?: number;
    /** validate the value from a list of possible values */
    enum?: string | string[];
    /** validate from a regular expression */
    pattern?: RegExp;
    /** transform a value before validation */
    transform?: (value: any) => any;
    /** custom validate function (Note: callback must be called) */
    validator?: (rule: any, value: any, callback: any, source?: any, options?: any) => any;
}
export declare type ValidateCallback = (errors: any, values: any) => void;
export interface GetFieldDecoratorOptions {
    /** 子节点的值的属性，如 Checkbox 的是 'checked' */
    valuePropName?: string;
    /** 子节点的初始值，类型、可选值均由子节点决定 */
    initialValue?: any;
    /** 收集子节点的值的时机 */
    trigger?: string;
    /** 可以把 onChange 的参数转化为控件的值，例如 DatePicker 可设为：(date, dateString) => dateString */
    getValueFromEvent?: (...args: any[]) => any;
    /** 校验子节点值的时机 */
    validateTrigger?: string | string[];
    /** 校验规则，参见 [async-validator](https://github.com/yiminghe/async-validator) */
    rules?: ValidationRule[];
    /** 是否和其他控件互斥，特别用于 Radio 单选控件 */
    exclusive?: boolean;
    /** Normalize value to form component */
    normalize?: (value: any, prevValue: any, allValues: any) => any;
    /** Whether stop validate on first rule of error for this field. */
    validateFirst?: boolean;
}
export interface WrappedFormUtils {
    /** 获取一组输入控件的值，如不传入参数，则获取全部组件的值 */
    getFieldsValue(fieldNames?: string[]): Record<string, any>;
    /** 获取一个输入控件的值 */
    getFieldValue(fieldName: string): any;
    /** 设置一组输入控件的值 */
    setFieldsValue(obj: Record<string, any>): void;
    /** 设置一组输入控件的值 */
    setFields(obj: Record<string, any>): void;
    /** 校验并获取一组输入域的值与 Error */
    validateFields(fieldNames: string[], options: Record<string, any>, callback: ValidateCallback): any;
    validateFields(fieldNames: string[], callback: ValidateCallback): any;
    validateFields(options: Record<string, any>, callback: ValidateCallback): any;
    validateFields(callback: ValidateCallback): any;
    /** 与 `validateFields` 相似，但校验完后，如果校验不通过的菜单域不在可见范围内，则自动滚动进可见范围 */
    validateFieldsAndScroll(fieldNames?: string[], options?: Record<string, any>, callback?: ValidateCallback): void;
    validateFieldsAndScroll(fieldNames?: string[], callback?: ValidateCallback): void;
    validateFieldsAndScroll(options?: Record<string, any>, callback?: ValidateCallback): void;
    validateFieldsAndScroll(callback?: ValidateCallback): void;
    /** 获取某个输入控件的 Error */
    getFieldError(name: string): Record<string, any>[];
    getFieldsError(names?: string[]): Record<string, any>;
    /** 判断一个输入控件是否在校验状态 */
    isFieldValidating(name: string): boolean;
    isFieldTouched(name: string): boolean;
    isFieldsTouched(names?: string[]): boolean;
    /** 重置一组输入控件的值与状态，如不传入参数，则重置所有组件 */
    resetFields(names?: string[]): void;
    getFieldDecorator(id: string, options?: GetFieldDecoratorOptions): (node: React.ReactNode) => React.ReactNode;
}
export interface FormComponentProps {
    form: WrappedFormUtils;
}
export declare type Diff<T extends string | number | symbol, U extends string | number | symbol> = ({
    [P in T]: P;
} & {
    [P in U]: never;
} & {
    [x: string]: never;
})[T];
export declare type Omit<T, K extends keyof T> = Pick<T, Diff<keyof T, K>>;
export interface ComponentDecorator<TOwnProps> {
    <P extends FormComponentProps>(component: React.ComponentClass<P>): React.ComponentClass<Omit<P, keyof FormComponentProps> & TOwnProps>;
}
export default class Form extends React.Component<FormProps, any> {
    static defaultProps: {
        prefixCls: string;
        layout: string;
        hideRequiredMark: boolean;
        onSubmit(e: any): void;
    };
    static propTypes: {
        prefixCls: any;
        layout: any;
        children: any;
        onSubmit: any;
        hideRequiredMark: any;
    };
    static childContextTypes: {
        vertical: any;
    };
    static Item: typeof FormItem;
    static create: <TOwnProps>(options?: FormCreateOption<TOwnProps>) => ComponentDecorator<TOwnProps>;
    constructor(props: any);
    shouldComponentUpdate(...args: any[]): any;
    getChildContext(): {
        vertical: boolean | undefined;
    };
    render(): JSX.Element;
}
