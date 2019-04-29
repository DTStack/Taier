
import React, { Component } from 'react';
import { Modal, Tabs } from 'antd';

import PaneOne from './paneOne';
import PaneTwo from './paneTwo';
import PaneThree from './paneThree';
import PaneFour from './paneFour';

const TabPane = Tabs.TabPane;

class EvaluateReportModal extends Component {
    render () {
        const { onOk, onCancel, visible } = this.props;
        return (
            <Modal
                onOk={onOk}
                visible={visible}
                onCancel={onCancel}
                title="评估报告">
                <Tabs className="m-tabs">
                    <TabPane tab="综合指标数据" key="pane-1">
                        <PaneOne />
                    </TabPane>
                    <TabPane tab="图表曲线" key="pane-2">
                        <PaneTwo />
                    </TabPane>
                    <TabPane tab="等宽详细数据" key="pane-3">
                        <PaneThree />
                    </TabPane>
                    <TabPane tab="等频详细数据" key="pane-4">
                        <PaneFour />
                    </TabPane>
                </Tabs>
            </Modal>
        )
    }
}

export default EvaluateReportModal;
