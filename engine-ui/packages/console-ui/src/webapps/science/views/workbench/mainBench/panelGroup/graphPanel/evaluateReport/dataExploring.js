
import React, { Component } from 'react';
import { Modal, Icon } from 'antd';
import { get } from 'lodash';

import FullScreen from 'widgets/fullscreen';

import TableDetail from './table';

class DataExploringModal extends Component {
    render () {
        const { onOk, onCancel, visible, data } = this.props;
        return (
            <Modal
                bodyStyle={{
                    padding: '0 0 0 0',
                    position: 'relative'
                }}
                title={'数据探查（展示前100条数据）'}
                width={800}
                style={{ height: '560px' }}
                wrapClassName="vertical-center-modal m-log-modal"
                visible={visible}
                onOk={onOk}
                onCancel={onCancel}
            >
                <FullScreen
                    style={{
                        position: 'absolute',
                        right: '48px',
                        top: '-30px'
                    }}
                    target="JS_evaluate_tab"
                    fullIcon={<Icon className="alt" type="arrows-alt" />}
                    exitFullIcon={<Icon className="alt" type="shrink" />}
                    isShowTitle={false}
                />
                <TableDetail
                    data={data}
                    indexType={get(data, 'inputType', 0)}
                />
            </Modal>
        )
    }
}

export default DataExploringModal;
