
import React, { Component } from 'react';
import { Modal, Tabs, Icon } from 'antd';

import FullScreen from 'widgets/fullscreen';

import { EVALUATION_INDEX_TYPE } from '../../../../../../consts';

import ChartDetail from './chart';
import TableDetail from './table';

const TabPane = Tabs.TabPane;

class EvaluateReportModal extends Component {
    render () {
        const { onOk, onCancel, visible, data } = this.props;
        return (
            <Modal
                bodyStyle={{
                    padding: '0 0 0 0',
                    position: 'relative'
                }}
                title="评估报告"
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
                <div className="m-tabs" id="JS_evaluate_tab">
                    <Tabs>
                        <TabPane tab="综合指标数据" key="pane-1">
                            <TableDetail indexType={EVALUATION_INDEX_TYPE.OVERALL} data={data}/>
                        </TabPane>
                        <TabPane tab="图表曲线" key="pane-2">
                            <ChartDetail data={data}/>
                        </TabPane>
                        <TabPane tab="等宽详细数据" key="pane-3">
                            <TableDetail indexType={EVALUATION_INDEX_TYPE.WIDTH_DATA} data={data}/>
                        </TabPane>
                        <TabPane tab="等频详细数据" key="pane-4">
                            <TableDetail indexType={EVALUATION_INDEX_TYPE.FREQUENCY} data={data}/>
                        </TabPane>
                    </Tabs>
                </div>
            </Modal>
        )
    }
}

export default EvaluateReportModal;
