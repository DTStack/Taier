import React from 'react';
import { Select } from 'antd';

import {
    ENGINE_SOURCE_TYPE_OPTIONS
} from '../../comm/const';

const Option = Select.Option;

export default function EngineSelect (props) {
    const { disabledEngineTypes } = props;
    const engineOptionsList = ENGINE_SOURCE_TYPE_OPTIONS.map(
        item => (
            <Option
                key={item.value}
                disabled={disabledEngineTypes && disabledEngineTypes.indexOf(item.value) > -1} // 禁用指定引擎类型选项
                value={item.value.toString()}
            >
                {item.name}
            </Option>
        )
    )

    return (
        <Select {...props}>
            { engineOptionsList }
        </Select>
    )
}
