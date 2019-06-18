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
        onCancel, onChange,
        columnFamily
    } = props;

    let initialVal = '';
    const isNotHBase = sourceType !== DATA_SOURCE.HBASE;
    if (isNotHBase) {
        columns && columns.forEach(item => {
            const field = utils.checkExist(item.index) ? item.index : utils.checkExist(item.key) ? item.key : undefined;
            if (field !== undefined) initialVal += `${field}:${item.type},\n`;
        })
    } else {
        columns && columns.forEach(item => {
            const field = utils.checkExist(item.key) ? item.key : undefined;
            if (field !== undefined) initialVal += `${item.cf || '-'}:${field},\n`;
        })
    }
    return (
        <Modal
            title={title}
            onOk={onOk}
            onCancel={onCancel}
            visible={visible}>
            <div>
                { isNotHBase ? '批量导入的语法格式（index 从 0 开始）：' : '批量添加的语法格式:' }
                <b style={{ color: 'rgb(255, 102, 0)' }}>
                    {
                        Object.prototype.toString.call(desc).slice(8, -1) === 'String' ? (
                            desc.split(',').map(item => (
                                <p key={item}>{item}</p>
                            ))
                        ) : { desc }
                    }
                </b>
            </div>
            {
                isNotHBase ? columnFamily ? (
                    <div>
                        <p>常用数据类型（type)：
                            <span style={{ color: 'rgb(255, 102, 0)' }}>
                                {renderHDFSTypes()}
                            </span>
                        </p>
                        <p>已有列族：
                            <span style={{ color: 'rgb(255, 102, 0)' }}>
                                {columnFamily.map(col => `${col},`) }
                            </span>
                        </p>
                    </div>
                ) : (
                    <p>常用数据类型（type)：
                        <span style={{ color: 'rgb(255, 102, 0)' }}>
                            {renderHDFSTypes()}
                        </span>
                    </p>
                ) : null
            }
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
