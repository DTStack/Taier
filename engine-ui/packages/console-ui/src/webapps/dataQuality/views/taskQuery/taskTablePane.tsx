import * as React from 'react';
import { isEmpty, cloneDeep } from 'lodash';
import { Table, Row, Col, Card } from 'antd';
import moment from 'moment';

import Resize from 'widgets/resize';

import { lineAreaChartOptions, DATA_SOURCE } from '../../consts';
import TQApi from '../../api/taskQuery';
import echarts from 'echarts/lib/echarts';
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

export default class TaskTablePane extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            lineChart: '',
            tableReport: {}
        };
    }

    componentDidMount () {
        const { data } = this.props;

        this.loadReports({
            recordId: data.id,
            tableId: data.tableId
        })
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        let oldData = this.props.data;

        let newData = nextProps.data;

        if (!isEmpty(newData) && oldData !== newData) {
            this.loadReports({
                recordId: newData.id,
                tableId: newData.tableId
            })
        }

        if (this.props.currentTab !== nextProps.currentTab) {
            this.resize();
        }
    }

    loadReports = (params: any) => {
        TQApi.getTaskTableReport(params).then((res: any) => {
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

    initLineChart (chartData: any) {
        let myChart = echarts.init(document.getElementById('TableReportTrend') as HTMLDivElement);

        let option = cloneDeep(lineAreaChartOptions);

        let xData = chartData.map((item: any) => moment(item.executeTime).format('YYYY-MM-DD HH:mm'));

        let legends: any = [{
            key: 'dayCountRecord',
            name: '记录数'
        }, {
            key: 'dayCountTrigger',
            name: '总告警数'
        }];

        option.grid = {
            left: 20,
            right: 20,
            containLabel: true
        };
        option.title.text = '';
        option.tooltip.axisPointer.label.formatter = '{value}';

        option.xAxis[0].axisTick = {
            show: false,
            alignWithLabel: true
        };
        option.xAxis[0].boundaryGap = ['5%', '5%'];
        option.xAxis[0].axisLabel.formatter = (value: any, index: any) => (moment(value).format('MM-DD HH:mm'));
        option.xAxis[0].data = chartData && xData ? xData : [];
        option.yAxis[0].axisLabel.formatter = '{value}';
        option.yAxis[0].minInterval = 1;

        option.legend.data = legends.map((item: any) => item.name);
        option.series = this.getSeries(chartData, legends);
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart });
    }

    getSeries = (data: any, legends: any) => {
        let arr: any = [];

        if (data.length) {
            legends.forEach((legend: any) => {
                arr.push({
                    name: legend.name,
                    type: 'line',
                    smooth: true,
                    symbolSize: 8,
                    data: data.map((item: any) => item[legend.key])
                });
            });
        }

        return arr;
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
            render: (text: any) => text && text.toFixed ? text.toFixed(2) : text
        }]
    }

    init30TimesTableReport = () => {
        const { data } = this.props;
        let colums: any = [{
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (value: any) => (moment(value).format('YYYY-MM-DD HH:mm:ss')),
            width: '40%'
        }, {
            title: '记录数',
            dataIndex: 'dayCountRecord',
            key: 'dayCountRecord',
            width: '20%'
        }, {
            title: '总告警数',
            dataIndex: 'dayCountTrigger',
            key: 'dayCountTrigger',
            width: '20%'
        }];

        // Hive表，增加分区显示
        if (
            data.dataSourceType === DATA_SOURCE.HIVE ||
            data.dataSourceType === DATA_SOURCE.MAXCOMPUTE
        ) {
            colums.splice(1, 0, {
                title: '分区',
                dataIndex: 'partition',
                key: 'partition',
                width: '20%'
            })
        }
        return colums
    }

    render () {
        const { tableReport } = this.state;

        let reportData = !isEmpty(tableReport) ? [tableReport] : [];

        let usage = tableReport.usage ? [...tableReport.usage].reverse() : [];

        const tableReportTitle = (
            <div>
                表级统计
                <span
                    style={{ fontSize: 12, color: '#999' }}>
                    （执行时间：{moment(tableReport.executeTime).format('YYYY-MM-DD HH:mm:ss')}）
                </span>
            </div>
        )

        return (
            <div style={{ padding: 20 }}>
                <Table
                    rowKey="tableName"
                    className="m-table txt-center-table"
                    columns={this.initTableInfoColumns()}
                    pagination={false}
                    dataSource={reportData}
                />

                <Row style={{ padding: '20px 0' }} gutter={16}>
                    <Col span={12}>
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

                    <Col span={12}>
                        <Card
                            noHovering
                            bordered={false}
                            loading={false}
                            className="shadow"
                            title="最近30次综合报告"
                        >
                            <Table
                                rowKey="id"
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
                            title="最近30次表级统计"
                        >
                            <Table
                                rowKey="id"
                                className="m-table txt-center-table"
                                columns={this.init30TimesTableReport()}
                                pagination={false}
                                dataSource={usage}
                                scroll={{ y: 245 }}
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
                                <article id="TableReportTrend" style={{ width: '100%', height: '280px' }}/>
                            </Resize>
                        </Card>
                    </Col>
                </Row>
            </div>
        );
    }
}
