import * as React from 'react';
import { Select } from 'antd';

const Option = Select.Option;

export default function EngineSelect (props: any) {
    const { disabledEngineTypes, tableTypes } = props;
    const engineOptionsList = tableTypes && tableTypes.map(
        (item: any) => (
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
