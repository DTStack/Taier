import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty, cloneDeep } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm, Row, Col, Card } from 'antd';
import { taskQueryActions } from '../../actions/taskQuery';
import moment from 'moment';
import Resize from 'widgets/resize';
import { lineAreaChartOptions } from '../../consts';
import TQApi from '../../api/taskQuery';

const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const mapStateToProps = state => {
    const { taskQuery, common } = state;
    return { taskQuery, common }
}

const mapDispatchToProps = dispatch => ({
    getTaskTableReport(params) {
        dispatch(taskQueryActions.getTaskTableReport(params));
    },
   
})

@connect(mapStateToProps, mapDispatchToProps)
export default class TaskTablePane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            lineChart: '',
            tableReport: {}
        };
    }

    componentDidMount() {
        const { data } = this.props;
        this.loadReports({
            recordId: data.id,
            tableId: data.tableId
        })
    }

    componentWillReceiveProps(nextProps) {
        const { data } = nextProps
        if (!isEmpty(data) && this.props.data !== data) {
            this.loadReports({
                recordId: data.id,
                tableId: data.tableId
            })
        }
    }

    loadReports = (params) => {
        TQApi.getTaskTableReport(params).then((res) => {
            if (res.code === 1) {
                this.setState({ tableReport: res.data });
                this.initLineChart(res.data.usage);
            }
        });
    }

    resize = () => {
        if (this.state.lineChart) {
            this.state.lineChart.resize()
        }
    }

    initLineChart(chartData) {
        let myChart = echarts.init(document.getElementById('TableReportTrend')),
            option  = cloneDeep(lineAreaChartOptions),
            xData = chartData.map(item => moment(item.executeTime).format('YYYY-MM-DD HH:mm')),
            legends = [{ 
                key: 'dayCountRecord',
                name: '记录数'
            }, { 
                key: 'dayCountTrigger',
                name: '总告警数'
            }];

        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true,
        }
        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = (value, index) => (moment(value).format('YYYY-MM-DD HH:mm'));
        option.xAxis[0].data = chartData && xData ? xData : [];
        option.yAxis[0].axisLabel.formatter = '{value} 次';
        option.yAxis[0].minInterval = 1;

        option.legend.data = legends.map(item => item.name);
        option.series = this.getSeries(chartData, legends);
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart });
    }

    getSeries = (data, legends) => {
        let arr = [];

        if (data.length) {
            legends.forEach((legend) => {
                arr.push({
                    name: legend.name,
                    symbol: 'none',
                    type:'line',
                    data: data.map(item => item[legend.key]),
                })
            })
        }

        return arr
    }

    initTableInfoColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '40%'
        }, {
            title: '总分区数量',
            dataIndex: 'partitionNum',
            key: 'partitionNum',
            width: '20%'
        }, {
            title: '类型',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            width: '40%'
        }]  
    }

    initTableReportColumns = () => {
        return [{
            title: '记录数',
            dataIndex: 'countData',
            key: 'countData',
            width: '50%'
        }, {
            title: '报警数',
            dataIndex: 'countTrigger',
            key: 'countTrigger',
            width: '50%'
        }]  
    }

    init30TimesInfo = () => {
        return [{
            title: '记录数平均波动率',
            dataIndex: 'standardDeviation',
            key: 'standardDeviation'
        }, {
            title: '平均记录数',
            dataIndex: 'avgRecord',
            key: 'avgRecord'
        }, {
            title: '日平均告警数',
            dataIndex: 'avgTrigger',
            key: 'avgTrigger'
        }, {
            title: '平均告警率',
            dataIndex: 'alarmRate',
            key: 'alarmRate',
            render: (text => text && text.toFixed ? text.toFixed(2) : text)
        }]  
    }

    init30TimesTableReport = () => {
        return [{
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (value) => (moment(value).format("YYYY-MM-DD HH:mm:ss")),
            width: '40%'
        }, {
            title: '记录数',
            dataIndex: 'dayCountRecord',
            key: 'dayCountRecord',
            width: '30%'
        }, {
            title: '总告警数',
            dataIndex: 'dayCountTrigger',
            key: 'dayCountTrigger',
            width: '30%'
        }]  
    }

    render() {
        const { data, taskQuery, common } = this.props;
        const { monitorId, visible, selectedIds, remark, tableReport } = this.state;
        const { loading } = taskQuery;

        let reportData = !isEmpty(tableReport) ? [tableReport] : [];

        const tableReportTitle = (
            <div>
                表级报告
                <span style={{ fontSize: 12, color: '#999' }}>（执行时间：{moment(tableReport.executeTime).format("YYYY-MM-DD")}）</span>
            </div>
        )

        return (
            <div style={{ 
                padding: 20, 
                overflow: 'hidden', 
                height: '100%', 
                overflowY: 'auto' 
            }}>
                <Table 
                    rowKey="tableName"
                    className="m-table txt-center-table"
                    columns={this.initTableInfoColumns()}
                    pagination={false}
                    dataSource={reportData}
                />

                <Row style={{ margin: '20px 0' }} gutter={16}>
                    <Col span={12} style={{paddingLeft: 0}}>
                        <Card   
                            noHovering
                            bordered={false}
                            loading={false} 
                            className="shadow"
                            title={tableReportTitle} 
                        >
                            <Table 
                                rowKey="tableName"
                                className="m-table txt-center-table"
                                columns={this.initTableReportColumns()}
                                pagination={false}
                                dataSource={reportData}
                            />
                        </Card>
                    </Col>
                    <Col span={12} style={{paddingRight: 0}}>
                        <Card   
                            noHovering
                            bordered={false}
                            loading={false} 
                            className="shadow"
                            title="最近30次综合报告" 
                        >
                            <Table 
                                rowKey="tableName"
                                className="m-table txt-center-table"
                                columns={this.init30TimesInfo()}
                                pagination={false}
                                dataSource={reportData}
                            />
                        </Card>
                    </Col>
                </Row>

                <Row gutter={16}>
                    <Col span={12}>
                        <Card   
                            noHovering
                            bordered={false}
                            loading={false} 
                            className="shadow"
                            title="最近30次表级报告" 
                        >
                            <Table 
                                rowKey="executeTime"
                                className="m-table txt-center-table"
                                columns={this.init30TimesTableReport()}
                                pagination={false}
                                dataSource={tableReport.usage ? tableReport.usage : []}
                                scroll={{ y: 250 }}
                            />
                        </Card>
                    </Col>
                    <Col span={12}>
                        <Card   
                            noHovering
                            bordered={false}
                            loading={false} 
                            className="shadow"
                            title="最近30次表数据波动图"
                            style={{ width: '100%' }}
                        >
                            <Resize onResize={this.resize}>
                                <article id="TableReportTrend" style={{ width: '100%', height: '250px' }}/>
                            </Resize>
                        </Card>
                    </Col>
                </Row>
            </div>
        );
    }
}