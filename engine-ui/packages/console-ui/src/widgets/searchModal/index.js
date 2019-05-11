import React from 'react';

import {
    Modal, Select
} from 'antd';

const Option = Select.Option;

export default function SearchModal (props) {
    const { visible, id, style, searchResult, onCancel, onSelect, onChange, placeholder } = props;

    const options = searchResult && searchResult.map(d => {
        return <Option key={d.id} data={d.id} value={d.name}>{d.name}</Option>
    })

    const styleValue = Object.assign({
        width: '400px',
        height: '80px',
        top: '150px',
        left: '100px'
    }, style);

    return (
        <Modal
            closable={false}
            mask={false}
            style={styleValue}
            bodyStyle={{
                padding: '10px'
            }}
            visible={visible}
            onCancel={onCancel}
            footer={null}
        >
            <Select
                id={id}
                mode="combobox"
                showSearch
                style={{ width: '100%' }}
                placeholder={placeholder}
                notFoundContent="没有发现相关内容"
                defaultActiveFirstOption={false}
                showArrow={false}
                filterOption={false}
                autoComplete="off"
                onChange={onChange}
                onSelect={onSelect}
            >
                {options}
            </Select>
        </Modal>
    )
}
