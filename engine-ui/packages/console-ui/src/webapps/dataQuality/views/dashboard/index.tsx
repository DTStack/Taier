import * as React from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { Table, Card, Row, Col } from 'antd';
import moment from 'moment';
import Resize from 'widgets/resize';

import {
    lineAreaChartOptions,
    alarmDateFilter,
    TASK_STATUS
} from '../../consts';
import { dashBoardActions } from '../../actions/dashBoard';
import DBApi from '../../api/dashBoard';

import echarts from 'echarts/lib/echarts';
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const mapStateToProps = (state: any) => {
    const { dashBoard, project } = state;
    return { dashBoard, project };
};

const mapDispatchToProps = (dispatch: any) => ({
    getTopRecord (params: any) {
        dispatch(dashBoardActions.getTopRecord(params));
    },
    getAlarmSum (params: any) {
        dispatch(dashBoardActions.getAlarmSum(params));
    },
    getUsage (params: any) {
        dispatch(dashBoardActions.getUsage(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
class DashBoard extends React.Component<any, any> {
    state: any = {
        currentDate: '1'
    };

    componentDidMount () {
        this.props.getUsage();
        this.props.getAlarmSum();
        this.props.getTopRecord({ date: this.state.currentDate });
        DBApi.getAlarmTrend().then((res: any) => {
            if (res.code === 1) {
                this.initLineChart(res.data);
            }
        });
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.getUsage();
            this.props.getAlarmSum();
            this.props.getTopRecord({ date: this.state.currentDate });
            DBApi.getAlarmTrend().then((res: any) => {
                if (res.code === 1) {
                    this.initLineChart(res.data);
                }
            });
        }
    }

    resize = () => {
        if (this.state.lineChart) {
            this.state.lineChart.resize();
        }
    };

    alarmTitle = (type: any) => {
        switch (type) {
            case '1':
                return '今天';
            case '30':
                return '最近30天';
            default:
                return '最近7天';
        }
    };

    jumpToTaskQuery (date: any) {
        // eslint-disable-next-line
        const endTime = moment();
        const startTime = moment(
            moment(endTime)
                .subtract(date - 1, 'days')
                .format('YYYY-MM-DD')
        ); // 获取n天前的日期，顺便取整

        hashHistory.push({
            pathname: '/dq/taskQuery',
            query: {
                startTime: startTime.valueOf(),
                endTime: endTime.valueOf(),
                statusFilter: [TASK_STATUS.FAIL, TASK_STATUS.UNPASS].join(',')
            }
        });
    }

    // table设置
    initColumns = () => {
        const { currentDate } = this.state;

        return [
            {
                title: '排名',
                dataIndex: 'rank',
                key: 'rank',
                render (text: any, record: any, index: any) {
                    return <div className="rank-number">{index + 1}</div>;
                },
                width: '10%'
            },
            {
                title: '类型',
                dataIndex: 'dataSourceName',
                key: 'dataSourceName',
                width: '40%'
            },
            {
                title: '表名',
                dataIndex: 'tableName',
                key: 'tableName',
                render: (text: any, record: any) => (
                    <Link
                        to={`/dq/taskQuery?tb=${text}&source=${
                            record.dataSourceType
                        }`}
                    >
                        {text}
                    </Link>
                ),
                width: '35%'
            },
            {
                title: '告警数',
                dataIndex: 'countByDate',
                key: 'countByDate',
                filters: alarmDateFilter,
                filteredValue: [currentDate],
                filterMultiple: false,
                render: (text: any) => (
                    <span style={{ color: '#EF5350' }}>{text}</span>
                ),
                width: '15%'
            }
        ];
    };

    initLineChart (chartData: any) {
        let myChart = echarts.init(document.getElementById('alarm-trend') as HTMLDivElement);

        let option: any = { ...lineAreaChartOptions };

        let xData = Object.keys(chartData).map((item: any) =>
            moment(item).format('YYYY-MM-DD')
        );

        let yData = Object.values(chartData);

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
        };
        option.yAxis[0].axisLabel.formatter = '{value}';
        option.yAxis[0].minInterval = 1;
        option.series = [
            {
                name: '告警数',
                symbol: 'none',
                type: 'line',
                data: yData
            }
        ];

        myChart.setOption(option);
        this.setState({ lineChart: myChart });
    }
    onTableChange = (page: any, filter: any, sorter: any) => {
        let date = filter.countByDate[0] || '7';
        this.props.getTopRecord({ date });
        this.setState({ currentDate: date });
    };

    render () {
        const {
            topRecords,
            alarmSum,
            usage,
            loading
        } = this.props.dashBoard;
        const { currentDate } = this.state;

        let extra = <Link to="/dq/taskQuery">查看更多</Link>;
        let marginTop: any = { marginTop: 20 };

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
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            今日告警数
                                        </span>
                                        <a
                                            onClick={this.jumpToTaskQuery.bind(
                                                this,
                                                1
                                            )}
                                            className="m-count-content font-red"
                                        >
                                            {alarmSum.countToday}
                                        </a>
                                    </section>
                                </Col>

                                <Col span={8}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            最近7天告警数
                                        </span>
                                        <a
                                            onClick={this.jumpToTaskQuery.bind(
                                                this,
                                                7
                                            )}
                                            className="m-count-content font-red"
                                        >
                                            {alarmSum.countWeek}
                                        </a>
                                    </section>
                                </Col>

                                <Col span={8}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            最近30天告警数
                                        </span>
                                        <a
                                            onClick={this.jumpToTaskQuery.bind(
                                                this,
                                                30
                                            )}
                                            className="m-count-content font-red"
                                        >
                                            {alarmSum.countMonth}
                                        </a>
                                    </section>
                                </Col>
                            </Row>
                        </Card>
                    </Row>

                    <Row
                        style={marginTop}
                        className="m-card shadow m-card-small"
                    >
                        <Card
                            noHovering
                            bordered={false}
                            loading={false}
                            title="告警趋势(最近30天)"
                        >
                            <Resize onResize={this.resize}>
                                <article
                                    id="alarm-trend"
                                    style={{ width: '100%', height: '317px' }}
                                />
                            </Resize>
                        </Card>
                    </Row>

                    <Row
                        style={marginTop}
                        className="m-card shadow m-card-small"
                    >
                        <Card
                            noHovering
                            bordered={false}
                            loading={false}
                            title="使用情况"
                        >
                            <Row className="m-count">
                                <Col span={6}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            已配置表数
                                        </span>
                                        <span className="m-count-content font-black">
                                            {usage.tableCount}
                                        </span>
                                    </section>
                                </Col>

                                <Col span={6}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            已配置规则数
                                        </span>
                                        <span className="m-count-content font-black">
                                            {usage.ruleCount}
                                        </span>
                                    </section>
                                </Col>

                                <Col span={6}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            昨日新增表数
                                        </span>
                                        <span className="m-count-content font-black">
                                            {usage.lastTableCount}
                                        </span>
                                    </section>
                                </Col>

                                <Col span={6}>
                                    <section
                                        className="m-count-section"
                                        style={{ width: 100 }}
                                    >
                                        <span className="m-count-title">
                                            昨日新增规则数
                                        </span>
                                        <span className="m-count-content font-black">
                                            {usage.lastRuleCount}
                                        </span>
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
        );
    }
}
export default DashBoard;
