import React, { Component } from 'react';
import { isEmpty, cloneDeep } from 'lodash';
import { Card, Icon, Tooltip } from 'antd';
import moment from 'moment';

import Resize from 'widgets/resize';
import RuleView from '../../components/ruleView';
import RuleDetailTableModal from './ruleDetailTable';

import { lineAreaChartOptions } from '../../consts';
import TQApi from '../../api/taskQuery';
import { getRuleType } from '../../consts/helper';
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
            // showSnapshot: false, // 原来的查看历史规则checkbox的值
            ruleRecord: null,
            ruleDetailTableModalVisible: false
        };
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
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

    // isSnapshotChange (e) {
    //     this.setState({
    //         showSnapshot: e.target.checked
    //     });
    // }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
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
        const { visible, currentRecord, taskDetail, ruleDetailTableModalVisible, ruleRecord } = this.state;
        const { data } = this.props;
        // const filterTaskDetail = taskDetail ? taskDetail.filter( // 原本由前端控制查看历史规则的显示和隐藏
        //     (item) => {
        //         if (showSnapshot) {
        //             return true;
        //         }
        //         return item.isSnapshot == 0;
        //     }
        // ) : []
        // console.log(filterTaskDetail)

        let cardTitle = (
            !isEmpty(currentRecord) ? `指标最近波动图（${currentRecord.columnName} -- ${currentRecord.functionName}）` : ''
        )

        if (currentRecord && currentRecord.isCustomizeSql) {
            cardTitle = '指标最近波动图'
        }

        return (
            <div style={{ padding: '15px 20px' }}>
                {/* <Checkbox value={showSnapshot} style={{ marginBottom: '10px', display: 'none' }} onChange={this.isSnapshotChange.bind(this)}>查看历史规则</Checkbox> */}
                {taskDetail.map((rule) => {
                    return <RuleView
                        key={rule.id}
                        tableName={data.tableName}
                        data={rule}
                        rightView={(
                            <React.Fragment>
                                {getRuleType(rule) == 'typeCheck' && (
                                    <Tooltip title='查看明细'>
                                        <a onClick={() => { this.setState({ ruleDetailTableModalVisible: true, ruleRecord: rule }) }} style={{ marginRight: '5px' }}>
                                            <Icon type='file-text' />
                                        </a>
                                    </Tooltip>
                                )}
                                {currentRecord.id == rule.id ? (
                                    <Icon type='line-chart' />
                                )
                                    : (
                                        <Tooltip title='查看趋势'>
                                            <a onClick={this.onCheckReport.bind(this, rule)}>
                                                <Icon type='line-chart' />
                                            </a>
                                        </Tooltip>
                                    )}
                            </React.Fragment>
                        )}
                    />
                })}

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
                <RuleDetailTableModal
                    visible={ruleDetailTableModalVisible}
                    ruleData={ruleRecord}
                    recordId={data.id}
                    onCancel={() => {
                        this.setState({
                            ruleDetailTableModalVisible: false,
                            ruleRecord: null
                        })
                    }}
                />
            </div>
        );
    }
}
