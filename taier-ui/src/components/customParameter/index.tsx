import React, { useEffect, useState } from 'react';
import { DeleteOutlined, PlusSquareOutlined } from '@ant-design/icons';
import { Col, Input, Row, Space } from 'antd';

import './index.scss';

interface IComponentType {
    /**
     * 自定义组件类型
     */
    type: string;
    /**
     * 组件
     */
    Component: React.ComponentClass<any, any> | React.FC<any>;
}

interface ICustomItem extends IComponentType {
    /**
     * 自定义参数名
     */
    label: string;
    /**
     * 自定义参数值
     */
    value: string;
    /**
     * 自定义参数是否合法
     */
    status: boolean;
}

export type ICustomValue = Omit<ICustomItem, 'Component'>;

interface ICustomParameterProps {
    /**
     * 自定义参数名-value对应的key名
     */
    labelKey?: string;
    /**
     * 自定义参数值-value对应的key名
     */
    valueKey?: string;
    /**
     * 已存在的keys
     */
    existingKeys: string[];
    /**
     * 自定义参数列表
     */
    value?: Record<string, string>[];
    /**
     * 自定义参数改变触发函数
     * @param value
     * @returns
     */
    onChange?: (value: ICustomValue[]) => void;
}

const DEFAULT_FORM_ITEM: IComponentType = {
    type: 'INPUT',
    Component: Input,
};

export default function CustomParameter({
    labelKey = 'label',
    valueKey = 'value',
    existingKeys,
    value,
    onChange,
}: ICustomParameterProps) {
    const [customParamRows, setCustomParamRows] = useState<ICustomItem[]>([]);

    const handleCustomParameterAdd = () => {
        const customItem: ICustomItem = {
            type: DEFAULT_FORM_ITEM.type,
            Component: DEFAULT_FORM_ITEM.Component,
            label: '',
            value: '',
            status: false,
        };

        setCustomParamRows([...customParamRows, customItem]);
    };

    const handleCustomParameterDelete = (index: number) => {
        const paramRows = customParamRows.filter((_, i) => i !== index);
        setCustomParamRows(paramRows);
        onChange?.(
            paramRows?.map((item) => ({
                label: item.label,
                value: item.value,
                type: item.type,
                status: item.status,
            }))
        );
    };

    const handleIsSame = (item: ICustomItem) =>
        existingKeys.includes(item.label) || customParamRows.filter((cIt) => cIt.label === item.label).length > 1;

    console.log(existingKeys);

    useEffect(() => {
        setCustomParamRows(
            value?.map((item) => {
                return {
                    type: item.type || DEFAULT_FORM_ITEM.type,
                    label: item[labelKey],
                    value: item[valueKey],
                    Component: DEFAULT_FORM_ITEM.Component,
                    status: false,
                };
            }) || []
        );
    }, []);

    return (
        <div className="dtc-custom-parameter">
            {customParamRows.map((item, index) => (
                <Row className="dtc-custom-parameter__item" align={'middle'} key={index}>
                    <Col span={8}>
                        <Input
                            value={item.label}
                            style={{
                                width: 'calc(100% - 18px)',
                            }}
                            status={!item.label ? 'error' : ''}
                            onChange={(e: any) => {
                                const params = customParamRows.map((item, i) => {
                                    if (i === index) item.label = e.target.value;
                                    return item;
                                });
                                setCustomParamRows(params);
                                onChange?.(
                                    params?.map((item) => ({
                                        label: item.label,
                                        value: item.value,
                                        type: item.type,
                                        status: Boolean(item.label && item.value && !handleIsSame(item)),
                                    }))
                                );
                            }}
                        />
                        <span className="dtc-custom-parameter__gap">:</span>
                    </Col>
                    <Col span={12}>
                        <item.Component
                            value={item.value}
                            status={!item.value ? 'error' : ''}
                            onChange={(e: any) => {
                                const params = customParamRows.map((item, i) => {
                                    if (i === index) item.value = e.target.value;
                                    return item;
                                });
                                setCustomParamRows(params);
                                onChange?.(
                                    params?.map((item) => ({
                                        label: item.label,
                                        value: item.value,
                                        type: item.type,
                                        status: Boolean(item.label && item.value && !handleIsSame(item)),
                                    }))
                                );
                            }}
                        />
                    </Col>
                    <Col span={4}>
                        <div className="dtc-custom-parameter__delete">
                            <Space>
                                <DeleteOutlined
                                    style={{ fontSize: 18 }}
                                    onClick={() => handleCustomParameterDelete(index)}
                                />
                                {handleIsSame(item) ? (
                                    <span className="dtc-custom-parameter__exist">已存在</span>
                                ) : null}
                            </Space>
                        </div>
                    </Col>
                </Row>
            ))}
            <Col span={16} offset={8}>
                <Space className="dtc-custom-parameter__add" onClick={handleCustomParameterAdd}>
                    <PlusSquareOutlined />
                    <span>{'添加自定义参数'}</span>
                </Space>
            </Col>
        </div>
    );
}
