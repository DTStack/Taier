import React, { useEffect, useState } from 'react';
import { PlusSquareOutlined, DeleteOutlined } from '@ant-design/icons';
import { Input, Row, Col, Space } from 'antd';

import './index.scss';

interface IComponentType {
    type: string;
    Component: React.ComponentClass<any, any> | React.FC<any>;
}

interface ICustomItem extends IComponentType {
    label: string;
    value: string;
    status: boolean;
}

export type ICustomValue = Omit<ICustomItem, 'Component'>;

interface ICustomParameterProps {
    existingKeys: string[];
    value?: Omit<ICustomItem, 'Component'>[];
    onChange?: (value: ICustomValue[]) => void;
}

const DEFAULT_FORM_ITEM: IComponentType = {
    type: 'INPUT',
    Component: Input,
};

export default function CustomParameter({ existingKeys, value, onChange }: ICustomParameterProps) {
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

    console.log(existingKeys)

    useEffect(() => {
        setCustomParamRows(
            value?.map((item) => {
                return {
                    ...item,
                    Component: DEFAULT_FORM_ITEM.Component,
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
