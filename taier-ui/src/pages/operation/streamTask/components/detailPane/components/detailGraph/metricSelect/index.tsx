import React, { useEffect, useState } from 'react';
import { Button, Select, Tooltip } from 'antd';
import classNames from 'classnames';

import './style.scss';

const { Option } = Select;

interface IProps {
    value?: string;
    defaultValue?: string;
    options: any[];
    enterButton?: string | React.ReactNode;
    placeholder?: string;
    className?: string;
    style?: React.CSSProperties;
    onOk?: (value: any, option: any) => void;
    onChange?: (value: any) => void;
}

const MetricSelect: React.FC<IProps> = (props) => {
    const { enterButton, placeholder, value, defaultValue, options, onChange, onOk, className, ...rest } = props;
    const [selectValue, setSelectValue] = useState<any>(defaultValue);

    useEffect(() => {
        setSelectValue(value);
    }, [value]);

    // 选择metric
    const handleSelectChange = (value: any) => {
        if (onChange) {
            onChange(value);
        }
        if (value === undefined) {
            setSelectValue(value);
        }
    };

    // 确认添加
    const handleClick = () => {
        if (onOk) {
            const option = options.find((item: any) => item.value === selectValue);
            onOk(selectValue, option);
        }
    };

    return (
        <div className={classNames('c-check-select', className)} {...rest}>
            <Select
                showSearch
                placeholder={placeholder}
                style={{ width: 400 }}
                showArrow={false}
                value={selectValue}
                onChange={handleSelectChange}
            >
                {Array.isArray(options) &&
                    options.map((item: any) => (
                        <Option key={item.value} value={item.value}>
                            <Tooltip title={item.text}>{item.text}</Tooltip>
                        </Option>
                    ))}
            </Select>
            <Button type="primary" onClick={handleClick}>
                {enterButton || 'Add'}
            </Button>
        </div>
    );
};
export default MetricSelect;
