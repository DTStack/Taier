import React, { Component } from 'react';
import { isEmpty, cloneDeep } from 'lodash';
import { Table, Card, Checkbox } from 'antd';
import moment from 'moment';

import Resize from 'widgets/resize';

import { lineAreaChartOptions } from '../../consts';
import { DetailCheckStatus } from '../../components/display';
import TQApi from '../../api/taskQuery';
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
require('echarts/lib/component/markLine');

export default class TaskDetailPane extends Component {
    constructor (props) {
        super(props);
        this.state = {
            lineChart: '',
            visible: false,
            taskDetail: [],
            currentRecord: {},
            showSnapshot: false
        };
    }

    componentWillReceiveProps (nextProps) {
        let oldData = this.props.data;

        let newData = nextProps.data;

        if (!isEmpty(newData) && oldData !== newData) {
            TQApi.getTaskDetail({
                recordId: newData.id,
                monitorId: newData.monitorId
            }).then((res) => {
                if (res.code === 1) {
                    this.setState({
                        taskDetail: res.data,
                        visible: false
                    });
                }
            });
        }

        if (this.props.currentTab !== nextProps.currentTab) {
            this.resize();
        }
    }

    isSnapshotChange (e) {
        this.setState({
            showSnapshot: e.target.checked
        });
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }

    initRulesColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            key: 'columnName',
            render: (text, record) => {
                const snapshotText = record.isSnapshot ? ' (已删除)' : '';
                let obj = {
                    children: (record.isCustomizeSql ? record.customizeSql : text) + snapshotText,
                    props: {
                        colSpan: record.isCustomizeSql ? 3 : 1
                    }
                };
                return obj;
            },
            width: '100px'
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => {
                let obj = {
                    children: record.functionName,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    }
                };
                return obj;
            },
            width: '100px'
        }, {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => {
                let obj = {
                    children: text,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    }
                };
                return obj;
            },
            width: '100px'
        }, {
            title: '校验方法',
            dataIndex: 'verifyTypeValue',
            key: 'verifyTypeValue',
            width: '10%'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: text => <DetailCheckStatus value={text} />,
            width: '8%'
        }, {
            title: '统计值',
            dataIndex: 'statistic',
            key: 'statistic',
            width: '8%'
        }, {
            title: '阈值',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => {
                if (record.isPercentage) {
                    return `${record.operator}  ${text}  %`;
                } else {
                    return `${record.operator}  ${text}`;
                }
            },
            width: '8%'
        }, {
            title: '最近修改人',
            key: 'modifyUser',
            dataIndex: 'modifyUser',
            width: '12%'
        }, {
            title: '最近修改时间',
            key: 'gmtModified',
            dataIndex: 'gmtModified',
            width: '12%',
            render: (text) => (moment(text).format('YYYY-MM-DD HH:mm'))
        }, {
            title: '操作',
            width: '8%',
            render: (text, record) => {
                return <a onClick={this.onCheckReport.bind(this, record)}>查看趋势</a>
            }
        }]
    }

    onCheckReport = (record) => {
        this.setState({ currentRecord: record });
        TQApi.getTaskAlarmNum({
            ruleId: record.id,
            monitorId: record.monitorId
        }).then((res) => {
            if (res.code === 1) {
                this.setState({ visible: true });
                this.initLineChart(res.data);
            }
        });
    }

    initLineChart (chartData) {
        const { currentRecord } = this.state;

        let myChart = echarts.init(document.getElementById('TaskTrend'));

        let option = cloneDeep(lineAreaChartOptions);

        let xData = Object.keys(chartData).map(item => moment(item).format('YYYY-MM-DD HH:mm'));

        let yData = Object.values(chartData);

        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';
        option.legend.data = ['统计值'];

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true
        }
        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = (value, index) => (moment(value).format('YYYY-MM-DD HH:mm'));
        option.xAxis[0].data = chartData && xData ? xData : [];

        option.yAxis[0].axisLabel.formatter = '{value}';
        option.yAxis[0].minInterval = 1;
        option.series = [{
            name: '统计值',
            type: 'line',
            smooth: true,
            symbolSize: 8,
            data: yData
        }];

        if (!isEmpty(chartData)) {
            // 非枚举值需要显示基线
            option.series[0].markLine = currentRecord.operator === 'in' ? undefined : {
                silent: true,
                itemStyle: {
                    normal: {
                        label: {
                            formatter: function () {
                                return '阈值'
                            }
                        }
                    }
                },
                data: [
                    {
                        yAxis: +currentRecord.threshold
                    }
                ]
            };
        }

        myChart.setOption(option);
        this.setState({ lineChart: myChart });
    }

    render () {
        const { visible, currentRecord, taskDetail, showSnapshot } = this.state;

        const filterTaskDetail = taskDetail ? taskDetail.filter(
            (item) => {
                if (showSnapshot) {
                    return true;
                }
                return item.isSnapshot == 0;
            }
        ) : []

        let cardTitle = (
            !isEmpty(currentRecord) ? `指标最近波动图（${currentRecord.columnName} -- ${currentRecord.functionName}）` : ''
        )

        if (currentRecord && currentRecord.isCustomizeSql) {
            cardTitle = '指标最近波动图'
        }

        return (
            <div style={{ padding: 10 }}>
                <Checkbox value={showSnapshot} style={{ marginBottom: '10px' }} onChange={this.isSnapshotChange.bind(this)}>查看历史规则</Checkbox>
                <Table
                    rowKey="id"
                    className="m-table"
                    columns={this.initRulesColumns()}
                    pagination={false}
                    dataSource={filterTaskDetail}
                    style={{ marginBottom: 15 }}
                    scroll={{ y: 250 }}
                />

                {
                    visible &&
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        className="shadow"
                        title={cardTitle}
                    >
                        <Resize onResize={this.resize}>
                            <article id="TaskTrend" style={{ width: '100%', height: '300px' }} />
                        </Resize>
                    </Card>
                }
            </div>
        );
    }
}
