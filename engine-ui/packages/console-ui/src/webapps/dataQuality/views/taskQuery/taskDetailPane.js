import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty, cloneDeep } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm, Card } from 'antd';
import { taskQueryActions } from '../../actions/taskQuery';
import moment from 'moment';
import Resize from 'widgets/resize';

import { lineAreaChartOptions } from '../../consts';
import TQApi from '../../api/taskQuery';
import { FildCheckStatus } from '../../components/display'

const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
require('echarts/lib/component/markLine');

const mapStateToProps = state => {
    const { taskQuery, common } = state;
    return { taskQuery, common }
}

const mapDispatchToProps = dispatch => ({
    getTaskDetail(params) {
        dispatch(taskQueryActions.getTaskDetail(params));
    },
    getTaskAlarmNum(params) {
        dispatch(taskQueryActions.getTaskAlarmNum(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class TaskDetailPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            lineChart: '',
            visible: false,
            currentRecord: {}
        };
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;
        if (!isEmpty(newData) && oldData !== newData) {
            this.props.getTaskDetail({
                recordId: newData.id,
                monitorId: newData.monitorId
            });
            this.setState({ visible: false });
        }
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
                let value = record.isCustomizeSql ? record.customizeSql : text;
                let obj = {
                    children: value,
                    props: {
                        colSpan: record.isCustomizeSql ? 3 : 1
                    },
                };

                return obj;
            },
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => {
                let obj = {
                    children: record.functionName,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    },
                };

                return obj;
            },
        }, {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => {
                let obj = {
                    children: text,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    },
                };

                return obj;
            },
        }, {
            title: '校验方法',
            dataIndex: 'verifyTypeValue',
            key: 'verifyTypeValue'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text => <FildCheckStatus value={text} />)
        }, {
            title: '统计值',
            dataIndex: 'statistic',
            key: 'statistic',
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
        }, {
            title: '最近修改人',
            key: 'modifyUser',
            dataIndex: 'modifyUser',
            width: '13%',
            
        }, {
            title: '最近修改时间',
            key: 'gmtModified',
            dataIndex: 'gmtModified',
            width: '13%',
            render: (text) => (moment(text).format("YYYY-MM-DD HH:mm"))
        }, {
            title: '操作',
            render: (text, record) => (<a onClick={this.onCheckReport.bind(this, record)}>查看报告</a>)
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

    initLineChart(chartData) {
        const { currentRecord } = this.state;

        let myChart = echarts.init(document.getElementById('TaskTrend')),
            option  = cloneDeep(lineAreaChartOptions),
            xData   = Object.keys(chartData).map(item => moment(item).format('YYYY-MM-DD HH:mm')),
            yData   = Object.values(chartData);

        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';
        option.legend.data = ['统计值'];

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true,
        }
        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = (value, index) => (moment(value).format('YYYY-MM-DD HH:mm'));
        option.xAxis[0].data = chartData && xData ? xData : [];

        option.yAxis[0].axisLabel.formatter = '{value} 次';
        option.yAxis[0].minInterval = 1;
        option.series = [{
            name: '统计值',
            symbol: 'none',
            type:'line',
            data: yData,
            markLine : {
                silent: true,
                itemStyle: {
                    normal: {
                        label: {
                            formatter: function() {
                                return '阈值'
                            }
                        }
                    }
                },
                data : [
                    {
                        yAxis: +currentRecord.threshold,
                    }
                ]
            }
        }];

        myChart.setOption(option);
        this.setState({ lineChart: myChart });
    }

    render() {
        const { taskDetail } = this.props.taskQuery;
        const { visible, currentRecord } = this.state;

        let cardTitle = (
            isEmpty(currentRecord) ? '' : `指标最近波动图（${currentRecord.columnName} -- ${currentRecord.functionName}）`
        )

        return (
            <div style={{ margin: 20 }}>
                <Table 
                    rowKey="id"
                    className="m-table common-table"
                    columns={this.initRulesColumns()}
                    pagination={false}
                    dataSource={taskDetail}
                />

                {
                    visible
                    &&
                    <Card   
                        noHovering
                        bordered={false}
                        loading={false} 
                        className="shadow"
                        title={cardTitle}
                    >
                        <Resize onResize={this.resize}>
                            <article id="TaskTrend" style={{ width: '100%', height: '300px' }}/>
                        </Resize>
                    </Card>
                }
            </div>
        );
    }
}