
import React, { Component } from 'react';
import { get } from 'lodash';

import DTModal from 'widgets/dt-modal';

import TableDetail from './table';

class DataExploringModal extends Component {
    render () {
        const { onOk, onCancel, visible, data = {} } = this.props;
        return (
            <DTModal
                bodyStyle={{
                    padding: '16px',
                    position: 'relative'
                }}
                title={'数据探查（展示前100条数据）'}
                width={800}
                style={{ height: '400px' }}
                wrapClassName="vertical-center-modal m-log-modal"
                visible={visible}
                onOk={onOk}
                onCancel={onCancel}
            >
                {visible && <TableDetail
                    visible={visible}
                    data={data}
                    indexType={get(data, 'inputType', 0)}
                />}
            </DTModal>
        )
    }
}

export default DataExploringModal;
