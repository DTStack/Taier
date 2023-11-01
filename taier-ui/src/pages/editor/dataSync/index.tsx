import type { Reducer } from 'react';
import { useEffect, useReducer, useState } from 'react';
import molecule from '@dtinsight/molecule';
import { GlobalEvent } from '@dtinsight/molecule/esm/common/event';
import { Scrollbar } from '@dtinsight/molecule/esm/components';
import { EditorEvent } from '@dtinsight/molecule/esm/model';
import { connect } from '@dtinsight/molecule/esm/react';
import type { FormItemProps } from 'antd';
import { Collapse, Empty,Form, Input, InputNumber, Radio, Switch } from 'antd';
import type { Rule } from 'antd/lib/form';
import { debounce, get } from 'lodash';

import api from '@/api';
import notification from '@/components/notification';
import type { IOptionsFromRequest } from '@/components/scaffolds/task';
import {
    AutoCompleteWithRequest,
    InputWithColumns,
    SelectWithCreate,
    SelectWithPreviewer,
    SelectWithRequest,
} from '@/components/scaffolds/task';
import { formItemLayout } from '@/constant';
import { Context } from '@/context/dataSync';
import { useConstant } from '@/hooks';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IOfflineTaskProps } from '@/interface';
import { taskRenderService } from '@/services';
import taskSaveService from '@/services/taskSaveService';
import { convertObjToNamePath, getPlus, pickByTruly, visit } from '@/utils';
import KeyMap from './keyMap';
import './index.scss';

export const event = new (class extends GlobalEvent {})();
export enum EventKind {
    Changed = 'changed',
    SourceKeyChange = 'source_key_change',
    TargetKeyChange = 'target_key_change',
}

interface IWidget {
    widget?: 'select' | 'input' | 'radio' | 'inputNumber' | 'textarea' | 'autoComplete' | string;
    props?: any & IOptionsFromRequest;
}

interface IBasic {
    /**
     * 组件展示的 label
     */
    title: string;
    /**
     * 组件的健值
     */
    name: string;
    /**
     * 透传给 Form.Item 的 rules
     * @reference https://ant.design/components/form-cn/#Rule
     */
    rules?: FormItemProps['rules'];
    validator?: string;
    noStyle?: boolean;
    /**
     * 隐藏后仍然会收集值
     */
    hidden?:
        | boolean
        | {
              field: string;
              value: string | boolean | number;
              /**
               * 是否取反
               */
              isNot?: boolean;
          }[];
    /**
     * 是否必选
     */
    required?: boolean;
    /**
     * 初始值
     */
    initialValue?: any;

    /**
     * 当前值与某一个值强关联，例如 type 字段，强绑定于 sourceId 字段
     */
    bind?: {
        field: string;
        /**
         * 当 field 字段发生改变时，transformer 定义了如何基于 field 的值修改当前值
         * @example '{{ a.b#find.type }}' // equals with `a.b.find(i => i.value === field.value).type`
         */
        transformer: string;
    };

    /**
     * 当前组件获取值的行为同某值相关
     * @notice 当前值依赖于某个值后，若该值发生改变，会导致当前值重置
     */
    depends?: string[];
}

interface IObject extends IBasic {
    type: 'object';
    children: ISchema[];
}

interface INumber extends IWidget, IBasic {
    type: 'number';
}

interface IString extends IWidget, IBasic {
    type: 'string';
}

interface IBoolean extends IWidget, IBasic {
    type: 'boolean';
}

/**
 * Type Any is for the user-defined complex component like keyMap
 */
interface IAny {
    type: 'any';
    widget: string;

    [key: string]: any;
}

type ISchema = IObject | INumber | IString | IBoolean | IAny;

/**
 * Root Schema must be an object with type and children
 */
type Root = Pick<IObject, 'type' | 'children'>;

/**
 * 定义「转化」的工厂，用于 Select 中接口返回的数据需要再「转化」
 */
const transformerFactory: Record<string, (value: any, index: number, array: any[]) => any | undefined> = {
    sourceIdOnWriter: (item: IDataSourceUsedInSyncProps) => ({
        label: `${item.dataName}（${item.dataType}）`,
        value: item.dataInfoId,
        type: item.dataTypeCode,
        disabled: !taskRenderService.getState().supportSourceList.writers.includes(item.dataTypeCode),
    }),
    sourceIdOnReader: (item: IDataSourceUsedInSyncProps) => ({
        label: `${item.dataName}（${item.dataType}）`,
        value: item.dataInfoId,
        type: item.dataTypeCode,
        disabled: !taskRenderService.getState().supportSourceList.readers.includes(item.dataTypeCode),
    }),
    table: (item: string) => ({
        label: item === 'ROW_NUMBER()' ? 'ROW_NUMBER' : item,
        value: item,
    }),
    incrementColumn: (item: IDataColumnsProps) => ({
        label: `${item.key}(${item.type})`,
        value: item.key,
    }),
    split: (item: IDataColumnsProps) => ({
        label: item.key === 'ROW_NUMBER()' ? 'ROW_NUMBER' : item.key,
        value: item.key,
    }),
    restore: (item: { key: string; type: string }) => ({
        label: `${item.key}(${item.type})`,
        value: item.key,
    }),
};

const validatorFactory: Record<string, (rule: any, value: string) => Promise<void>> = {
    json: (_: any, value: string) => {
        let msg = '';
        try {
            if (value) {
                const t = JSON.parse(value);
                if (typeof t !== 'object') {
                    msg = '请填写正确的JSON';
                }
            }
        } catch (e) {
            msg = '请检查JSON格式，确认无中英文符号混用！';
        }

        if (msg) {
            return Promise.reject(new Error(msg));
        }
        return Promise.resolve();
    },
};

/**
 * 默认的 Widget
 */
const defaultWidget: Record<string, ((props: any) => JSX.Element) | undefined> = {
    number: (props: any) => <InputNumber style={{ width: '100%' }} {...props} />,
    string: (props: any) => <Input {...props} />,
    boolean: (props: any) => <Switch {...props} />,
    input: (props: any) => <Input {...props} />,
    select: (props: any) => <SelectWithRequest {...props} />,
    radio: (props: any) => <Radio.Group {...props} />,
    inputNumber: (props: any) => <InputNumber style={{ width: '100%' }} {...props} />,
    textarea: (props: any) => <Input.TextArea {...props} />,
    autoComplete: (props: any) => <AutoCompleteWithRequest {...props} />,
    // User-Defined Widget
    SelectWithCreate: (props: any) => <SelectWithCreate {...props} />,
    SelectWithPreviewer: (props: any) => <SelectWithPreviewer {...props} />,
    InputWithColumns: (props: any) => <InputWithColumns {...props} />,
};

/**
 * 类型为 Any 的 Widget
 */
const registerWidget: Record<string, (props: any) => JSX.Element> = {
    KeyMap: (props: any) => <KeyMap {...props} />,
};

export const updateValuesInData = debounce((data) => {
    const { current } = molecule.editor.getState();
    if (current?.tab) {
        molecule.editor.updateTab({
            id: current?.tab?.id,
            data: {
                ...current.tab.data,
                ...data,
            },
        });
        const groupId = molecule.editor.getGroupIdByTab(current.tab.id)!;
        const tab = molecule.editor.getTabById(current.tab.id, groupId);
        molecule.editor.emit(EditorEvent.OnUpdateTab, tab);
    }
}, 300);

export default connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
    const [form] = Form.useForm();
    const [templateSchema, setSchema] = useState<Root>({
        type: 'object',
        children: [],
    });
    /**
     * 全局的 state，用于存放接口返回的数据
     */
    const [optionCollections, dispatch] = useReducer<
        Reducer<
            Record<string, any[]>,
            {
                type: 'update';
                payload: {
                    field: string;
                    collection: any[];
                };
            }
        >
    >((state, action) => {
        switch (action.type) {
            case 'update':
                return {
                    ...state,
                    [action.payload.field]: action.payload.collection,
                };
            default:
                return state;
        }
    }, {});
    const optionCollectionsRef = useConstant(optionCollections);

    const handleValuesChanged = (changed: Record<string, any>) => {
        const [namePath, value] = convertObjToNamePath(changed);
        event.emit(EventKind.Changed, namePath, value);

        updateValuesInData(form.getFieldsValue());
    };

    const renderContent = (data: ISchema, prefix?: string[]) => {
        switch (data.type) {
            case 'object': {
                return (
                    <Collapse className="taier__dataSync__collapse" defaultActiveKey={[data.name]} key={data.title}>
                        <Collapse.Panel key={data.name} header={data.title}>
                            {data.children.map((child) =>
                                renderContent(child, prefix ? [...prefix, data.name] : [data.name])
                            )}
                        </Collapse.Panel>
                    </Collapse>
                );
            }
            case 'any': {
                const Widget = registerWidget[data.widget || data.type];
                if (Widget) {
                    return <Widget key={data.widget} />;
                }

                notification.error({
                    key: 'UNDEFINED_WIDGET',
                    message: `未找到 ${data.widget}`,
                });
                return <Empty key={data.widget} />;
            }
            case 'string':
            case 'boolean':
            case 'number': {
                const Widget = defaultWidget[data.widget || data.type];
                if (!Widget) {
                    notification.error({
                        key: 'UNDEFINED_WIDGET',
                        message: `未找到 ${data.name} 注册的 ${data.widget || data.type}`,
                    });
                    return <Empty key={data.name} />;
                }
                const dependencies = data.depends?.map((d) => d.split('.')) || [];
                return (
                    <Form.Item noStyle dependencies={dependencies} key={data.name}>
                        {({ getFieldsValue }) => {
                            const values = getFieldsValue();
                            const depValues = dependencies.reduce<string>(
                                (pre, cur) => `${pre}-${get(values, cur.join('.'))}`,
                                ''
                            );

                            const hidden = Array.isArray(data.hidden)
                                ? data.hidden
                                      .map((item) => {
                                          const value = get({ form: values }, item.field);
                                          const isEqual = item.value.toString()?.split(',').includes(`${value}`);

                                          return item.isNot ? !isEqual : isEqual;
                                      })
                                      .some(Boolean)
                                : data.hidden;

                            const rules: Rule[] = [];
                            if (data.required) {
                                rules.push({
                                    required: data.required,
                                });
                            }

                            if (data.rules) {
                                rules.push(...data.rules);
                            }

                            if (data.validator) {
                                rules.push({
                                    validator: validatorFactory[data.validator],
                                });
                            }

                            return hidden ? null : (
                                <Form.Item
                                    key={depValues}
                                    name={prefix ? [...prefix, data.name] : data.name}
                                    label={data.title}
                                    noStyle={data.noStyle}
                                    initialValue={data.initialValue}
                                    rules={rules}
                                    valuePropName={data.type === 'boolean' ? 'checked' : 'value'}
                                >
                                    {!data.noStyle && <Widget {...data.props} event={event} />}
                                </Form.Item>
                            );
                        }}
                    </Form.Item>
                );
            }
            default:
                return null;
        }
    };

    useEffect(() => {
        // 赋予初始值
        if (current?.tab?.data) {
            const data: IOfflineTaskProps = current?.tab?.data;
            const targetMap = data.targetMap ? pickByTruly(data.targetMap) : undefined;
            const settingMap = data.settingMap
                ? pickByTruly({
                      ...data.settingMap,
                      speed: data.settingMap?.speed === '-1' ? '不限制传输速率' : data.settingMap?.speed,
                  })
                : {};
            const sourceMap = pickByTruly(data.sourceMap);

            form.setFieldsValue({
                sourceMap,
                targetMap,
                settingMap,
            });
        }
    }, [templateSchema]);

    // 监听 form 的 values 修改事件
    useEffect(() => {
        function handler(field: string[], value: any) {
            visit<Root, ISchema>(
                templateSchema,
                (i) => !!(i.bind || i.depends),
                (item, vNode) => {
                    if (item.bind) {
                        if (field.join('.') === item.bind?.field) {
                            const val = getPlus(
                                {
                                    optionCollections: optionCollectionsRef.current,
                                },
                                item.bind.transformer,
                                value
                            );
                            form.setFieldValue(vNode.formName, val);
                            event.emit(EventKind.Changed, vNode.formName, val);
                        }
                    }

                    if (item.depends) {
                        if (item.depends.includes(field.join('.'))) {
                            form.setFieldValue(vNode.formName, undefined);
                            event.emit(EventKind.Changed, vNode.formName, undefined);
                        }
                    }
                }
            );
        }
        event.subscribe(EventKind.Changed, handler);

        return () => {
            event.unsubscribe(EventKind.Changed, handler);
        };
    }, [templateSchema]);

    // 监听保存事件
    useEffect(() => {
        const listener = (action: Parameters<Parameters<typeof taskSaveService['onBeforeSave']>[0]>[0]) => {
            form.validateFields()
                .then(() => {
                    action.continue();
                })
                .catch(() => {
                    action.stop();
                });
        };

        taskSaveService.onBeforeSave(listener);
        return () => {
            taskSaveService.unsubScribeOnBeforeSave(listener);
        };
    }, []);

    useEffect(() => {
        api.getSyncProperties({}).then((res) => {
            if (res.code === 1) {
                setSchema(res.data);
            }
        });
    }, []);

    return (
        <Scrollbar isShowShadow>
            <div className="taier__dataSync__container">
                <Context.Provider value={{ optionCollections, dispatch, transformerFactory }}>
                    <Form
                        {...formItemLayout}
                        validateMessages={{ required: '请选择${label}' }}
                        onValuesChange={handleValuesChanged}
                        form={form}
                        autoComplete="off"
                    >
                        {templateSchema.children.map((child) => renderContent(child))}
                    </Form>
                </Context.Provider>
            </div>
        </Scrollbar>
    );
});
