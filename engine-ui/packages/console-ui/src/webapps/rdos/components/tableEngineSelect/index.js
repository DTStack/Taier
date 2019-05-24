import React from 'react';
import { Select } from 'antd';

const Option = Select.Option;

export default function TableEngineSelect (props) {
    return (
        <Select {...props}>
            <Option value="hive">Hive</Option>
            <Option value="hawq">Hawq</Option>
            <Option value="libra">LibrA</Option>
        </Select>
    )
}
