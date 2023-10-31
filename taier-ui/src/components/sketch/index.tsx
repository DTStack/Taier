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

import { useEffect,useImperativeHandle, useRef, useState } from 'react';
import type { FormInstance, FormItemProps, PaginationProps } from 'antd';
import { Form, Pagination, Table } from 'antd';
import type { ColumnsType, TablePaginationConfig, TableProps } from 'antd/lib/table';
import type { FilterValue, SorterResult } from 'antd/lib/table/interface';
import classnames from 'classnames';

import { useCalcTableScroll } from '@/components/customHooks/index';
import { usePagination } from '@/hooks';
import {
    DatePickerItem,
    InputItem,
    InputWithConditionItem,
    OwnerItem,
    RadioItem,
    RangeItem,
    SelectItem,
} from './headerForm';
import './index.scss';

const FormItem = Form.Item;

export interface ISlotItemProps<P = any, T = any, Y = any> {
    /**
     * 透传给 Form.Item 组件的参数
     */
    formItemProps: FormItemProps<P>;
    /**
     * 透传给组件的参数
     */
    slotProps: T;
    /**
     * 特殊情况下，透传给组件的子组件的参数，如 inputWithCondition 组件
     */
    restProps: Y;
}

interface ISketchHeaderProps {
    // 内置组件的名称
    name: string;
    formItem?: Omit<FormItemProps<any>, 'name'>;
    renderFormItem?: FormItemProps['children'];
    props?: Partial<ISlotItemProps>;
}

export interface IActionRef {
    selectedRowKeys: React.Key[];
    selectedRows: any[];
    setSelectedKeys: React.Dispatch<React.SetStateAction<React.Key[]>>;
    submit: () => void;
    form: FormInstance<any>;
    getTableData: () => any[];
}

export interface ISketchProps<T, P> {
    /**
     * 通过 actionRef 获取内部值
     */
    actionRef?: React.RefObject<IActionRef>;
    /**
     * 设置 header 组件内容
     */
    header?: (string | ISketchHeaderProps)[];
    /**
     * 设置 className
     */
    className?: string;
    /**
     * 设置 header 容器的 className
     */
    headerClassName?: string;
    /**
     * 在条件满足的情况下，会执行该方法获取表格数据
     * @param values 当前表单域的值
     * @param pagination 当前分页数据
     * @param filters 当前表格过滤条件
     * @param sorter 当前表格排序条件
     * @returns polling 是否轮训查询表格数据, 默认轮训间隔 36000ms
     * @required
     */
    request: (
        values: P,
        pagination: { current: number; pageSize: number },
        filters: Record<string, FilterValue | null>,
        sorter?: SorterResult<any>
    ) => Promise<{ data: T[]; total: number; polling?: boolean | { delay?: number } } | void>;
    /**
     * 条件筛选的表单值改变函数，常用于表单值的联动
     */
    onFormFieldChange?: (field: keyof P, value: any, values: P, form: FormInstance<P>) => void;
    /**
     * 表格项选中事件
     */
    onTableSelect?: (rowKeys: React.Key[], rows: T[]) => void;
    /**
     * 表格的展开事件
     */
    onExpand?: (expanded: boolean, record: T) => Promise<T[]>;
    /**
     * 表格的列表项
     */
    columns?: ColumnsType<T>;
    /**
     * 表格的 Props，会透传给表格组件，除了 `columns` | `dataSource` | `scroll` 属性以外
     */
    tableProps?: Omit<Partial<TableProps<T>>, 'columns' | 'dataSource' | 'scroll'>;
    /**
     * 头部需要额外的组件，如添加按钮，刷新按钮等
     */
    extra?: React.ReactNode;
    /**
     * 表格的 footer 组件，如批量处理表格选中项等
     */
    tableFooter?: React.ReactNode;
    /**
     * 表格的头部组件。在头部组件的下方，表格组件的上方，常用于展示表格的一些总览标签。
     */
    headerTitle?: React.ReactNode;
    /**
     * 表格的头部组件的 className
     */
    headerTitleClassName?: string;
}

const SLOT_ITEM = ['input', 'inputWithCondition', 'owner', 'rangeDate', 'datePicker', 'select', 'radioGroup'];

export const useSketchRef = () => {
    const ref = useRef<IActionRef>(null);
    return ref;
};

export default function Sketch<
    T extends Record<string, any> = Record<string, any>,
    P extends Record<string, any> = Record<string, any>
>({
    actionRef,
    header = [],
    extra,
    className,
    headerClassName,
    columns,
    tableProps = {},
    tableFooter,
    headerTitle,
    headerTitleClassName,
    request,
    onFormFieldChange,
    onTableSelect,
    onExpand,
}: ISketchProps<T, P>) {
    const [form] = Form.useForm<P>();
    const insenseKeys = useRef(new Set(['name', 'multipleName']));
    const { current, pageSize, total, setPagination } = usePagination({});
    const [dataSource, setDataSource] = useState<T[]>([]);
    const [loading, setLoading] = useState(false);
    const [selectedRowKeys, setSelectedKeys] = useState<React.Key[]>([]);
    const [selectedRows, setSelectedRows] = useState<T[]>([]);
    const timeout = useRef<number | undefined>(undefined);
    const { scroll: calcTableScroll } = useCalcTableScroll({ className: 'dt-sketch-table' });

    // we should save the filter and sorter from table
    const tableInfo = useRef<{
        filters?: Record<string, FilterValue | null>;
        sorter?: SorterResult<any> | SorterResult<any>[];
    }>({});

    useImperativeHandle(actionRef, () => ({
        selectedRowKeys,
        selectedRows,
        setSelectedKeys,
        submit: () => getDataSource({ current, pageSize }),
        form,
        getTableData: () => dataSource.concat(),
    }));

    const getDataSource = (
        { current: nextCurrent = 1, pageSize: nextPageSize = 20 }: TablePaginationConfig = {},
        filters?: Record<string, FilterValue | null>,
        sorter?: SorterResult<any>,
        silent = false
    ) => {
        if (!silent) {
            setLoading(true);
        }
        window.clearTimeout(timeout.current);
        request(
            form.getFieldsValue(),
            { current: nextCurrent, pageSize: nextPageSize },
            filters || tableInfo.current.filters || {},
            sorter || (tableInfo.current.sorter as SorterResult<any>)
        )
            .then((res) => {
                if (res) {
                    const { total: nextTotal, data = [], polling } = res;
                    if (polling) {
                        const delay = (typeof polling === 'object' && polling.delay) || 36000;
                        timeout.current = window.setTimeout(() => {
                            // 轮训需要静默请求
                            getDataSource(
                                { current: nextCurrent, pageSize: nextPageSize },
                                filters || tableInfo.current.filters || {},
                                sorter || (tableInfo.current.sorter as SorterResult<any>),
                                true
                            );
                        }, delay);
                    }

                    setPagination({
                        total: nextTotal,
                        current: nextCurrent,
                        pageSize: nextPageSize,
                    });
                    setDataSource(data);
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const handleFormValueChanged = (changedValues: Partial<P>, values: P) => {
        const keys = Object.keys(changedValues);
        const field = keys[0] as keyof P;
        onFormFieldChange?.(field, changedValues[field], values, form);
        if (!insenseKeys.current.has(field as string)) {
            getDataSource();
        }
    };

    const handleTableChange = (
        pagination: TablePaginationConfig,
        filters?: Record<string, FilterValue | null>,
        sorter?: SorterResult<any> | SorterResult<any>[]
    ) => {
        setSelectedKeys([]);
        // save it into ref
        tableInfo.current = { filters, sorter };
        getDataSource(pagination, filters, sorter as SorterResult<any>);
    };

    const handleExpand = (expanded: boolean, record: T) => {
        onExpand?.(expanded, record).then((children) => {
            setDataSource((d) => {
                const next = [...d];
                const idx = next.indexOf(record);
                if (idx > -1) {
                    next[idx] = { ...record, children };
                }
                return next;
            });
        });
    };

    const handleSelectedRowChanged = (rowKeys: React.Key[], rows: T[]) => {
        setSelectedKeys(rowKeys);
        setSelectedRows(rows);
        onTableSelect?.(rowKeys, rows);
    };

    useEffect(() => {
        getDataSource();

        return () => {
            window.clearTimeout(timeout.current);
        };
    }, []);

    const pagination: PaginationProps = {
        total,
        showQuickJumper: true,
        showSizeChanger: true,
        defaultPageSize: 20,
        pageSizeOptions: ['10', '20', '50', '100', '200'],
        current,
        pageSize,
        onChange: (page, nextPageSize) =>
            handleTableChange(
                { current: page, pageSize: nextPageSize },
                tableInfo.current.filters,
                tableInfo.current.sorter
            ),
        ...tableProps.pagination,
    };

    const { className: tableClassName, expandable, ...restTableProps } = tableProps;

    const renderFormItemByName = (name: string, props: Partial<ISlotItemProps> = {}) => {
        switch (name) {
            case 'input': {
                const { slotProps = {}, ...restProps } = props;
                return (
                    <InputItem
                        key={name}
                        slotProps={{
                            onPressEnter: () => getDataSource(),
                            ...slotProps,
                        }}
                        {...restProps}
                    />
                );
            }
            case 'inputWithCondition': {
                const { slotProps = {}, ...restProps } = props;
                return (
                    <InputWithConditionItem
                        key={name}
                        slotProps={{
                            onPressEnter: () => getDataSource(),
                            ...slotProps,
                        }}
                        {...restProps}
                    />
                );
            }
            case 'owner': {
                return <OwnerItem key={name} {...props} />;
            }
            case 'rangeDate': {
                const itemName = props.formItemProps?.name?.toString();
                return <RangeItem key={itemName || name} {...props} />;
            }
            case 'datePicker': {
                const itemName = props.formItemProps?.name?.toString();
                return <DatePickerItem key={itemName || name} {...props} />;
            }
            case 'select': {
                const itemName = props.formItemProps?.name?.toString();
                return <SelectItem key={itemName || name} {...props} />;
            }
            case 'radioGroup': {
                const itemName = props.formItemProps?.name?.toString();
                return <RadioItem key={itemName || name} {...props} />;
            }
            default:
                return null;
        }
    };

    return (
        <div className={classnames('dt-sketch', className)}>
            {(Boolean(header.length) || extra) && (
                <div className={classnames('dt-sketch-header', headerClassName)}>
                    <Form<P> form={form} layout="inline" autoComplete="off" onValuesChange={handleFormValueChanged}>
                        {header.map((headerForm) => {
                            if (typeof headerForm === 'string') {
                                return renderFormItemByName(headerForm);
                            }
                            const { name, formItem, renderFormItem, props = {} } = headerForm;

                            if (SLOT_ITEM.includes(name)) {
                                return renderFormItemByName(name, props);
                            }

                            return (
                                <FormItem key={name} name={name} {...formItem}>
                                    {renderFormItem}
                                </FormItem>
                            );
                        })}
                    </Form>
                    {extra && <div className="dt-sketch-extra">{extra}</div>}
                </div>
            )}
            <div className={classnames('dt-sketch-header-bar', headerTitleClassName)}>{headerTitle}</div>
            <Table<T>
                rowKey="id"
                pagination={false}
                rowSelection={{
                    selectedRowKeys,
                    onChange: handleSelectedRowChanged,
                }}
                expandable={{
                    onExpand: handleExpand,
                    ...expandable,
                }}
                className={classnames('dt-sketch-table', tableClassName)}
                scroll={{ ...calcTableScroll }}
                loading={loading}
                columns={columns}
                dataSource={dataSource}
                onChange={handleTableChange}
                footer={() => (
                    <div className="flex-between">
                        <div style={{ paddingLeft: '25px' }}>{tableFooter}</div>
                        <div>
                            <Pagination {...pagination} />
                        </div>
                    </div>
                )}
                {...restTableProps}
            />
        </div>
    );
}
