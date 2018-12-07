import React from 'react';
import {
    Modal, Input
} from 'antd';
import utils from 'utils';

import {
    DATA_SOURCE
} from '../../../../../comm/const';

const renderHDFSTypes = () => {
    return <span>
        <b>STRING</b>,
        <b> LONG</b>,
        <b> DOUBLE</b>,
        <b> BOOLEAN</b>,
        <b> DATE</b>
    </span>
}

export default function BatchModal (props) {
    const {
        title, desc,
        visible, onOk,
        placeholder, value, columns, sourceType,
        onCancel, onChange, columnFamily
    } = props

    let initialVal = '';
    if (sourceType !== DATA_SOURCE.HBASE) {
        columns && columns.forEach(item => {
            const field = utils.checkExist(item.index) ? item.index : utils.checkExist(item.key) ? item.key : undefined;
            if (field !== undefined) initialVal += `${field}:${item.type},\n`;
        })
    } else {
        columns && columns.forEach(item => {
            const field = utils.checkExist(item.key) ? item.key : undefined;
            if (field !== undefined) initialVal += `${item.cf || '-'}:${field}:${item.type},\n`;
        })
    }

    return (
        <Modal
            title={title}
            onOk={onOk}
            onCancel={onCancel}
            visible={visible}>
            <p>
                批量导入的语法格式（index 从 0 开始）：
                <b style={{ color: 'rgb(255, 102, 0)' }}>
                    {desc}
                </b>
            </p>
            <p>
            </p>
            <p>常用数据类型（type)：
                <span style={{ color: 'rgb(255, 102, 0)' }}>
                    {renderHDFSTypes()}
                </span>
            </p>
            {columnFamily ? <p>已有列族：
                <span style={{ color: 'rgb(255, 102, 0)' }}>
                    {columnFamily.map(col => `${col},`) }
                </span>
            </p> : ''}

            <br/>
            <Input
                type="textarea"
                rows={6}
                value={value || initialVal}
                onChange={onChange}
                placeholder={placeholder}
            />
        </Modal>
    )
}
