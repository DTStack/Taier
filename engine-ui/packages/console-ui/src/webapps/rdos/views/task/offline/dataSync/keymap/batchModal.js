import React from 'react';
import { 
    Modal, Input,
} from 'antd';

const renderHDFSTypes = () => {
    return <span>
        <b>STRING</b>,
        <b>VARCHAR</b>,
        <b>CHAR</b>,
        <b>TINYINT</b>,
        <b>SMALLINT</b>,
        <b>DECIMAL</b>,
        <br/>
        <b>INT</b>,
        <b>BIGINT</b>,
        <b>FLOAT</b>,
        <b>DOUBLE</b>,
        <b>TIMESTAMP</b>,
        <b>DATE</b>
    </span>
}

export default function BatchModal(props) {

    const { 
        title, desc,
        visible, onOk, 
        placeholder, value,
        onCancel, onChange, columnFamily,
    } = props

    return (
        <Modal 
            title={title}
            onOk={onOk}
            onCancel={onCancel}
            visible={visible}>
            <p>
                批量导入的语法格式：
                <b style={{color: 'rgb(255, 102, 0)'}}>
                   {desc}
                </b>
            </p>
            <p>常用数据类型（type）：
                <span style={{color: 'rgb(255, 102, 0)'}}>
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
                value={value}
                onChange={onChange}
                placeholder={placeholder}
            />
        </Modal>
    )
}