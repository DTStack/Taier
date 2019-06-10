
import React, { Component } from 'react';
import { Tabs } from 'antd';

import DTModal from 'widgets/dt-modal';

import { EVALUATION_INDEX_TYPE } from '../../../../../../consts';

import ChartDetail from './chart';
import TableDetail from './table';

const TabPane = Tabs.TabPane;

class EvaluateReportModal extends Component {
    render () {
        const { onOk, onCancel, visible, data } = this.props;
        return (
            <DTModal
                bodyStyle={{
                    padding: '0 0 0 0',
                    position: 'relative'
                }}
                title="评估报告"
                width={800}
                maskClosable={false}
                style={{ height: '560px' }}
                wrapClassName="vertical-center-modal m-log-modal"
                visible={visible}
                onOk={onOk}
                onCancel={onCancel}
            >
                <div className="m-tabs">
                    <Tabs>
                        <TabPane tab="综合指标数据" key="pane-1">
                            <div style={{ padding: 16 }}>
                                <TableDetail indexType={EVALUATION_INDEX_TYPE.OVERALL} data={data} visible={visible}/>
                            </div>
                        </TabPane>
                        <TabPane tab="图表曲线" key="pane-2">
                            <ChartDetail data={data} visible={visible}/>
                        </TabPane>
                        <TabPane tab="等宽详细数据" key="pane-3">
                            <div style={{ padding: 16 }}>
                                <TableDetail indexType={EVALUATION_INDEX_TYPE.WIDTH_DATA} data={data} visible={visible}/>
                            </div>
                        </TabPane>
                        <TabPane tab="等频详细数据" key="pane-4">
                            <div style={{ padding: 16 }}>
                                <TableDetail indexType={EVALUATION_INDEX_TYPE.FREQUENCY} data={data} visible={visible}/>
                            </div>
                        </TabPane>
                    </Tabs>
                </div>
            </DTModal>
        )
    }
}

export default EvaluateReportModal;
