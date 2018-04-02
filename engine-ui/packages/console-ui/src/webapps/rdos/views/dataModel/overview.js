import React, { Component } from 'react';

import {
    Row, Col, Card
} from 'antd';

import utils from 'utils';
import Resize from 'widgets/resize';

import { lineAreaChartOptions, defaultBarOption } from '../../comm/const';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
require('echarts/lib/chart/pie');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

export default class Overview extends Component {

    state = {
        data: '',
    }

    componentDidMount() {
    }

    resizeChart = () => {
        if (this.chart1) {
            this.chart1.resize()
            this.chart2.resize()
            this.chart3.resize()
        }
    }

    render() {
        const { data } = this.state;
        const flex = {
            flexGrow: 1,
            flex: 1
        };

        const countWidth = {
            width: '100px',
        }
        return (
            <Resize onResize={this.resizeChart}>
            <div className="data-model-overview" style={{ background: '#f2f7fa' }}>
                    <Row style={{marginTop: '10px'}}>
                        <h1 className="box-title box-title-bolder">
                            模型汇总信息
                        </h1>
                        <div className="box-4 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false} 
                                title="今日任务完成情况"
                            >
                                <Row className="m-count" style={{display: 'flex'}}>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日新增模型</span>
                                            <span className="m-count-content font-black">{data.ALL || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日新增指标</span>
                                            <span className="m-count-content font-red">{data.FAILED || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日不规范模型</span>
                                            <span className="m-count-content font-organge">{data.RUNNING || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">今日不规范指标</span>
                                            <span className="m-count-content font-green">{data.FINISHED || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">累计不规范模型</span>
                                            <span className="m-count-content font-gray">{data.UNSUBMIT || 0}</span>
                                        </section>
                                    </Col>
                                    <Col style={flex}>
                                        <section style={countWidth} className="m-count-section">
                                            <span className="m-count-title">累计不规范指标</span>
                                            <span className="m-count-content font-organge">{data.SUBMITTING || 0}</span>
                                        </section>
                                    </Col>
                                </Row>
                            </Card>
                        </div>
                    </Row>
                    <Row className="box-card" style={{marginTop: '20px'}}>
                        <Col span={12} className="m-card m-card-small" style={{paddingRight: '10px'}}>
                            <Card 
                                noHovering
                                bordered={false}
                                loading={false}  
                                className="shadow"
                                title="模型不规范原因分布"
                            >
                                <div id="Chart1"></div>
                            </Card>
                        </Col>
                        <Col span={12} className="m-card m-card-small" style={{paddingLeft: '10px'}}>
                            <Card
                                noHovering
                                bordered={false}
                                loading={false}  
                                className="shadow"
                                title="模型不规范趋势分析"
                            >
                                <div id="Chart2"></div>
                            </Card>
                        </Col>
                    </Row>
                    <Row className="box-card">
                        <Col span={12} className="m-card m-card-small" style={{paddingRight: '10px'}}>
                            <Card
                                noHovering
                                bordered={false}
                                loading={false}  
                                className="shadow"
                                title="字段不规范原因分布"
                            >
                                <div id="Chart3"></div>
                            </Card>
                        </Col>
                        <Col span={12} className="m-card m-card-small" style={{paddingLeft: '10px'}}>
                            <Card
                                noHovering
                                bordered={false}
                                loading={false}  
                                className="shadow"
                                title="字段不规范趋势分析"
                            >
                                <div id="Chart4"></div>
                            </Card>
                        </Col>
                    </Row>
            </div>
            </Resize>
        )
    }
}