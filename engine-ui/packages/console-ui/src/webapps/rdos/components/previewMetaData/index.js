import React, { Component } from 'react';
import { Modal, Table } from 'antd';
export default class PreviewMetaData extends Component {
    /* eslint-disable */
    UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.props.dbName && nextProps.visible && nextProps.visible != this.props.visible) {
            // 获取数据库详情
        }
    }
    render () {
        const { visible, onCancel } = this.props;
        return (
            <Modal
                title='预览元数据'
                visible={visible}
                onCancel={onCancel}
            >
                <Table></Table>
            </Modal>
        )
    }
}
