import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import moment from 'moment';
import { isEmpty, cloneDeep } from 'lodash';
import { Table, Icon, Card, Row, Col } from 'antd';
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

import Resize from 'widgets/resize';
import { lineAreaChartOptions } from '../../consts';
import { dashBoardActions } from '../../actions/dashBoard';
import DBApi from '../../api/dashBoard';
// import '../../../styles/views/dashBoard.scss';

const mapStateToProps = state => {
    const { dashBoard, common } = state;
    return { dashBoard, common }
};

const mapDispatchToProps = dispatch => ({
    getTopRecord(params) {
        dispatch(dashBoardActions.getTopRecord(params));
    },
    getAlarmSum(params) {
        dispatch(dashBoardActions.getAlarmSum(params));
    },
    getAlarmTrend(params) {
        dispatch(dashBoardActions.getAlarmTrend(params));
    },
    getUsage(params) {
        dispatch(dashBoardActions.getUsage(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
export default class DashBoard extends Component {

    state = {}

    componentDidMount() {
        this.props.getUsage();
        // this.props.getAlarmTrend();
        this.props.getAlarmSum();
        this.props.getTopRecord({ date: 1 });
        DBApi.getAlarmTrend().then((res) => {
            if (res.code === 1) {
                this.initLineChart(res.data);
            }
        });
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.dashBoard.alarmTrend,
            newData = nextProps.dashBoard.alarmTrend;

        if (isEmpty(oldData) && !isEmpty(newData)) {
            this.initLineChart(newData)
        }
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }

    // table设置
    initColumns = () => {
        const { dataSourceType } = this.props.common.allDict;
        return [{
            title: '类型',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            width: '40%'
        }, {
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a>{text}</a>
            ),
            width: '30%'
        },
        {
            title: '告警数',
            dataIndex: 'countByDate',
            key: 'countByDate',
            filters: [{
                text: '今日告警数',
                value: '1',
            }, {
                text: '最近7天告警数',
                value: '7',
            }, {
                text: '最近30天告警数',
                value: '30',
            }],
            filterMultiple: false,
            width: '30%'
        }]
    }

    initLineChart(chartData) {
        let myChart = echarts.init(document.getElementById('AlarmTrend')),
            option  = cloneDeep(lineAreaChartOptions),
            xData   = Object.keys(chartData).map(item => moment(item).format('YYYY-MM-DD')),
            yData   = Object.values(chartData);

        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';
        option.legend.data = ['告警数'];
        option.xAxis[0].axisLabel.formatter = '{value}';
        option.xAxis[0].data = chartData && xData ? xData : [];

        option.yAxis[0].axisLabel.formatter = '{value}';
        option.yAxis[0].minInterval = 1;
        option.series = [{
            name: '告警数',
            symbol: 'none',
            type:'line',
            data: yData,
        }];

        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }

    onTableChange = (page, filter, sorter) => {
        let date = filter.countByDate[0] || 1;
        this.props.getTopRecord({ date });
    }

    render() {
        const { topRecords, alarmTrend, alarmSum, usage, loading } = this.props.dashBoard;

        let extra = (
            <Link to="/dq/taskQuery">
                查看更多
            </Link>
        )

        return (
            <div className="dashboard">
                <Row>
                    <Col span={12}>
                        <Row className="box-1 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false} 
                                title="告警汇总"
                                extra={extra}
                            >
                                <Row className="m-count">
                                    <Col span={8}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">今日告警数</span>
                                            <span className="m-count-content font-red">{alarmSum.countToday}</span>
                                        </section>
                                    </Col>
                                    <Col span={8}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">最近7天告警数</span>
                                            <span className="m-count-content font-red">{alarmSum.countWeek}</span>
                                        </section>
                                    </Col>
                                    <Col span={8}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">最近30天告警数</span>
                                            <span className="m-count-content font-red">{alarmSum.countMonth}</span>
                                        </section>
                                    </Col>
                                </Row>
                            </Card>
                        </Row>

                        <Row className="box-1 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false} 
                                title="告警趋势(最近30天)"
                            >
                                <Resize onResize={this.resize}>
                                    <article id="AlarmTrend" style={{ width: '100%', height: '350px' }}/>
                                </Resize>
                            </Card>
                        </Row>

                        <Row className="box-1 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false} 
                                title="使用情况"
                            >
                                <Row className="m-count">
                                    <Col span={6}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">已配置表数</span>
                                            <span className="m-count-content font-black">{usage.tableCount}</span>
                                        </section>
                                    </Col>
                                    <Col span={6}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">已配置规则数</span>
                                            <span className="m-count-content font-black">{usage.ruleCount}</span>
                                        </section>
                                    </Col>
                                    <Col span={6}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">昨日新增表数</span>
                                            <span className="m-count-content font-black">{usage.lastTableCount}</span>
                                        </section>
                                    </Col>
                                    <Col span={6}>
                                        <section className="m-count-section" style={{ width: 100 }}>
                                            <span className="m-count-title">昨日新增规则数</span>
                                            <span className="m-count-content font-black">{usage.lastRuleCount}</span>
                                        </section>
                                    </Col>
                                </Row>
                            </Card>
                        </Row>

                    </Col>

                    <Col span={12}>
                        <Row className="box-1 m-card m-card-small">
                            <Card
                                noHovering
                                bordered={false}
                                loading={false} 
                                title="告警TOP20"
                            >
                                <Table 
                                    rowKey="id"
                                    className="m-table"
                                    style={{ marginTop: 2 }}
                                    columns={this.initColumns()} 
                                    loading={loading}
                                    pagination={false}
                                    dataSource={topRecords}
                                    onChange={this.onTableChange}
                                />
                            </Card>
                        </Row>
                    </Col>
                </Row>
            </div>
        )
    }
}