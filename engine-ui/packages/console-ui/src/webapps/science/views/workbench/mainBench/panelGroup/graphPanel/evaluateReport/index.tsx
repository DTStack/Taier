
import * as React from 'react';
import { Tabs } from 'antd';
import { get } from 'lodash';

import DTModal from 'widgets/dt-modal';

import { EVALUATION_INDEX_TYPE, COMPONENT_TYPE, INPUT_TYPE } from '../../../../../../consts';
import api from '../../../../../../api/component';
import { regressionClassificationOptions, unionClassificationOptions } from './helper';

import ChartDetail from './chart';
import TableDetail from './table';
import SingleChart from './singleChart';
import MatrixGraph from './matrix';
import ConfusionTable from './confusionTable';

const TabPane = Tabs.TabPane;
export interface EvaluateReportModalProp {
    data: {
        id?: number;
        [propName: string]: any;
    };
    visible: boolean;
    onCancel: () => any;
    onOk: () => any;
}
class EvaluateReportModal extends React.Component<EvaluateReportModalProp, any> {
    renderPane (type: number) {
        const { data, visible } = this.props;
        switch (type) {
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
                return [<TabPane tab="图表曲线" key="pane-2">
                    <ChartDetail data={data} visible={visible} />
                </TabPane>,
                <TabPane tab="等宽详细数据" key="pane-3">
                    <div style={{ padding: 16 }}>
                        <TableDetail indexType={EVALUATION_INDEX_TYPE.WIDTH_DATA} data={data} visible={visible} />
                    </div>
                </TabPane>,
                <TabPane tab="等频详细数据" key="pane-4">
                    <div style={{ padding: 16 }}>
                        <TableDetail indexType={EVALUATION_INDEX_TYPE.FREQUENCY} data={data} visible={visible} />
                    </div>
                </TabPane>]
            }
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION: {
                return <TabPane tab="图表曲线" key="pane-2">
                    <SingleChart
                        key={data.id}
                        data={data}
                        visible={visible}
                        getData={() => { return api.getRegressionEvaluationGraph({ taskId: data.id }); }}
                        getOptions={regressionClassificationOptions}
                    />
                </TabPane>
            }
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION: {
                return <TabPane tab="图表曲线" key="pane-2">
                    <SingleChart
                        key={data.id}
                        data={data}
                        visible={visible}
                        getData={() => { return api.getClusterRegressionGraph({ taskId: data.id }); }}
                        getOptions={unionClassificationOptions}
                    />
                </TabPane>
            }
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX: {
                return [<TabPane tab="混淆矩阵" key="pane-2">
                    <MatrixGraph
                        key={data.id}
                        data={data}
                        visible={visible}
                        dataKey='confusionMatrix'
                        getData={() => { return api.getMissValueGraph({ taskId: data.id }) }}
                    />
                </TabPane>,
                <TabPane tab="比例矩阵" key="pane-3">
                    <MatrixGraph
                        key={data.id}
                        data={data}
                        visible={visible}
                        showColor={true}
                        dataKey='proportionMatrix'
                        getData={() => { return api.getMissValueGraph({ taskId: data.id }) }}
                    />
                </TabPane>,
                <TabPane tab="统计信息" key="pane-tj">
                    <ConfusionTable
                        visible={visible}
                        data={data}
                    />
                </TabPane>
                ]
            }
            default: {
                return null
            }
        }
    }
    getIndexType (type: number) {
        switch (type) {
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION: {
                return INPUT_TYPE.REGRESSION_OUTPUT_1;
            }
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION: {
                return INPUT_TYPE.NORMAL;
            }
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX: {
                return INPUT_TYPE.CONFUSION_MATRIX_OUTPUT_DATA;
            }
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
            default: {
                return EVALUATION_INDEX_TYPE.OVERALL;
            }
        }
    }
    render () {
        const { onOk, onCancel, visible, data } = this.props;
        const componentType: number = get(data, 'componentType');
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
                        {componentType != COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX && (
                            <TabPane tab="综合指标数据" key="pane-1">
                                <div style={{ padding: 16 }}>
                                    <TableDetail indexType={this.getIndexType(componentType)} data={data} visible={visible} />
                                </div>
                            </TabPane>
                        )}
                        {this.renderPane(componentType)}
                    </Tabs>
                </div>
            </DTModal>
        )
    }
}

export default EvaluateReportModal;
