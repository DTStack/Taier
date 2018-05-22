import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Icon, Card, Row, Col } from 'antd';
import moment from 'moment';

const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
import Resize from 'widgets/resize';

import { lineAreaChartOptions, alarmDateFilter } from '../../consts';
import { dashBoardActions } from '../../actions/dashBoard';
import DBApi from '../../api/dashBoard';

const mapStateToProps = state => {
    const { dashBoard } = state;
    return { dashBoard }
};

const mapDispatchToProps = dispatch => ({
    getTopRecord(params) {
        dispatch(dashBoardActions.getTopRecord(params));
    },
    getAlarmSum(params) {
        dispatch(dashBoardActions.getAlarmSum(params));
    },
    getUsage(params) {
        dispatch(dashBoardActions.getUsage(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
export default class DashBoard extends Component {

    state = {
        currentDate: '7'
    }

    componentDidMount() {
        this.props.getUsage();
        this.props.getAlarmSum();
        this.props.getTopRecord({ date: 7 });
        DBApi.getAlarmTrend().then((res) => {
            if (res.code === 1) {
                this.initLineChart(res.data);
            }
        });
    }

    resize = () => {
        if (this.state.lineChart) {
            this.state.lineChart.resize();
        }
    }

    alarmTitle = (type) => {
        switch (type) {
            case '1':
                return '今天';
            case '30':
                return '最近30天';
            default:
                return '最近7天';
        }
    }

    jumpToTaskQuery(date){
        const endTime=new moment();
        const startTime=moment(moment(endTime).subtract(date,"days").format("YYYY-MM-DD"));//获取n天前的日期，顺便取整

        this.props.router.push({
            pathname:"/dq/taskQuery",
            query:{
                startTime:startTime.valueOf(),
                endTime:endTime.valueOf(),
            }
        })
    }

    // table设置
    initColumns = () => {
        const { currentDate } = this.state;

        return [{
            title: '排名',
            dataIndex: 'rank',
            key: 'rank',
            render(text, record, index) {
                return <div className="rank-number">{index+1}</div>
            },
            width: '10%'
        }, {
            title: '类型',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            width: '40%'
        }, {
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <Link to={`/dq/taskQuery?tb=${text}&source=${record.dataSourceType}`}>{text}</Link>
            ),
            width: '35%'
        }, {
            title: '告警数',
            dataIndex: 'countByDate',
            key: 'countByDate',
            filters: alarmDateFilter,
            filteredValue: [currentDate],
            filterMultiple: false,
            render: (text => <span style={{ color: '#EF5350' }}>{text}</span>),
            width: '15%'
        }]
    }

    initLineChart(chartData) {
        let myChart = echarts.init(document.getElementById('alarm-trend')),
            option  = {...lineAreaChartOptions},
            xData   = Object.keys(chartData).map(item => moment(item).format('YYYY-MM-DD')),
            yData   = Object.values(chartData);

        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';
        option.legend.data = ['告警数'];
        option.xAxis[0].axisLabel.formatter = '{value}';
        option.xAxis[0].data = chartData && xData ? xData : [];
        option.grid = {
            left: '3%',
            right: '4%',
            bottom: '8%',
            containLabel: true
        },
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
        let date = filter.countByDate[0] || '7' ;
        this.props.getTopRecord({ date });
        this.setState({ currentDate: date });
    }

    render() {
        const { topRecords, alarmTrend, alarmSum, usage, loading } = this.props.dashBoard;
        const { currentDate } = this.state;

        let extra = (
            <Link to="/dq/taskQuery">
                查看更多
            </Link>
        )
        let marginTop = { marginTop: 20 };

        return (
            <Row style={{ margin: 20 }}>
                <Col span={12} style={{ paddingRight: 10 }}>
                    <Row className="m-card shadow m-card-small">
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
                                        <a onClick={this.jumpToTaskQuery.bind(this,1)} className="m-count-content font-red">{alarmSum.countToday}</a>
                                    </section>
                                </Col>

                                <Col span={8}>
                                    <section className="m-count-section" style={{ width: 100 }}>
                                        <span className="m-count-title">最近7天告警数</span>
                                        <a onClick={this.jumpToTaskQuery.bind(this,7)} className="m-count-content font-red">{alarmSum.countWeek}</a>
                                    </section>
                                </Col>

                                <Col span={8}>
                                    <section className="m-count-section" style={{ width: 100 }}>
                                        <span className="m-count-title">最近30天告警数</span>
                                        <a onClick={this.jumpToTaskQuery.bind(this,30)} className="m-count-content font-red">{alarmSum.countMonth}</a>
                                    </section>
                                </Col>
                            </Row>
                        </Card>
                    </Row>

                    <Row style={marginTop} className="m-card shadow m-card-small">
                        <Card
                            noHovering
                            bordered={false}
                            loading={false} 
                            title="告警趋势(最近30天)"
                        >
                            <Resize onResize={this.resize}>
                                <article id="alarm-trend" style={{ width: '100%', height: '317px' }}/>
                            </Resize>
                        </Card>
                    </Row>

                    <Row style={marginTop} className="m-card shadow m-card-small">
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

                <Col span={12} style={{ paddingLeft: 10 }}>
                    <Row className="m-card shadow m-card-small">
                        <Card
                            noHovering
                            bordered={false}
                            loading={false} 
                            title={`${this.alarmTitle(currentDate)}告警TOP20`}
                        >
                            <Table 
                                rowKey="monitorId"
                                className="m-table trend-table"
                                // style={{ marginTop: 2 }}
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
        )
    }
}