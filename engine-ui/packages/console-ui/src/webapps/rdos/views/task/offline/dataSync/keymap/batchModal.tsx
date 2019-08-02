import * as React from 'react';
import {
    Modal, Input
} from 'antd';

import {
    DATA_SOURCE,
    hdfsFieldTypes
} from '../../../../../comm/const';

const renderHDFSTypes = () => {
    const types = hdfsFieldTypes.map((type: any) => <b key={type}>{type}, </b>)
    return <span style={{ wordBreak: 'break-all' }}>
        {types}
    </span>
}

export default function BatchModal (props: any) {
    const {
        title, desc,
        visible, onOk,
        placeholder, value, sourceType,
        onCancel, onChange,
        columnFamily
    } = props;
    const rowsFix = { rows: 6 }
    const isNotHBase = sourceType !== DATA_SOURCE.HBASE;
    return (
        <Modal
            title={title}
            onOk={onOk}
            onCancel={onCancel}
            maskClosable={false}
            visible={visible}>
            <div>
                { isNotHBase ? '批量导入的语法格式（index 从 0 开始）：' : '批量添加的语法格式:' }
                <b style={{ color: 'rgb(255, 102, 0)' }}>
                    {
                        Object.prototype.toString.call(desc).slice(8, -1) === 'String' ? (
                            desc.split(',').map((item: any) => (
                                <p key={item}>{item}</p>
                            ))
                        ) : { desc }
                    }
                </b>
                <p>常用数据类型（type)：
                    <span style={{ color: 'rgb(255, 102, 0)' }}>
                        {renderHDFSTypes()}
                    </span>
                </p>
                {columnFamily ? <p>已有列族：
                    <span style={{ color: 'rgb(255, 102, 0)' }}>
                        {columnFamily.map((col: any) => `${col},`) }
                    </span>
                </p> : ''}
            </div>
            <br/>
            <Input
                type="textarea"
                {...rowsFix}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
            />
        </Modal>
    )
}
