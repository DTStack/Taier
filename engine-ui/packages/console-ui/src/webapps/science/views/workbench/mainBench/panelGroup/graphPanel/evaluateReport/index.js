
import React, { Component } from 'react';
import { Modal, Tabs } from 'antd';

import PaneOne from './paneOne';
import PaneTwo from './paneTwo';
import PaneThree from './paneThree';
import PaneFour from './paneFour';

const TabPane = Tabs.TabPane;

class EvaluateReportModal extends Component {
    render () {
        const { onOk, onCancel, visible, data } = this.props;
        return (
            <Modal
                onOk={onOk}
                visible={visible}
                onCancel={onCancel}
                className="nopadding-modal"
                title="评估报告">
                <div className="m-tabs">
                    <Tabs>
                        <TabPane tab="综合指标数据" key="pane-1">
                            <PaneOne data={data}/>
                        </TabPane>
                        <TabPane tab="图表曲线" key="pane-2">
                            <PaneTwo data={data}/>
                        </TabPane>
                        <TabPane tab="等宽详细数据" key="pane-3">
                            <PaneThree data={data}/>
                        </TabPane>
                        <TabPane tab="等频详细数据" key="pane-4">
                            <PaneFour data={data}/>
                        </TabPane>
                    </Tabs>
                </div>
            </Modal>
        )
    }
}

export default EvaluateReportModal;
