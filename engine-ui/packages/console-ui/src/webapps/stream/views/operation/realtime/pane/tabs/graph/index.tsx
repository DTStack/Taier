import * as React from 'react'

import { Radio, Collapse, Icon, Tooltip, Alert } from 'antd'
import AlarmBaseGraph from './baseGraph';

import utils from 'utils';
import { TIME_TYPE, CHARTS_COLOR, TASK_TYPE, DATA_SOURCE_TEXT, SOURCE_INPUT_BPS_UNIT_TYPE } from '../../../../../../comm/const';
import Api from '../../../../../../api'

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;

const defaultTimeValue = '10m';
const metricsType = {
    FAILOVER_RATE: 'fail_over_rate',
    DELAY: 'data_delay',
    SOURCE_TPS: 'source_input_tps',
    SINK_OUTPUT_RPS: 'sink_output_rps',
    SOURCE_RPS: 'source_input_rps',
    SOURCE_INPUT_BPS: 'source_input_bps',
    SOURCE_DIRTY: 'source_dirty_data',
    SOURCE_DIRTY_OUT: 'source_dirty_out',
    DATA_COLLECTION_RPS: 'data_acquisition_rps',
    DATA_COLLECTION_BPS: 'data_acquisition_bps',
    DATA_DISABLE_TPS: 'data_discard_tps',
    DATA_DISABLE_COUNT: 'data_discard_count',
    DATA_COLLECTION_TOTAL_RPS: 'data_acquisition_record_sum',
    DATA_COLLECTION_TOTAL_BPS: 'data_acquisition_byte_sum'
}
const defaultLineData: any = {
    x: [],
    y: [[]],
    loading: true
}
const defaultData: any = {
    [metricsType.FAILOVER_RATE]: defaultLineData,
    [metricsType.DELAY]: defaultLineData,
    [metricsType.SOURCE_TPS]: defaultLineData,
    [metricsType.SINK_OUTPUT_RPS]: defaultLineData,
    [metricsType.SOURCE_RPS]: defaultLineData,
    [metricsType.SOURCE_INPUT_BPS]: defaultLineData,
    [metricsType.SOURCE_DIRTY]: defaultLineData,
    [metricsType.SOURCE_DIRTY_OUT]: defaultLineData,
    [metricsType.DATA_COLLECTION_RPS]: defaultLineData,
    [metricsType.DATA_COLLECTION_BPS]: defaultLineData,
    [metricsType.DATA_DISABLE_TPS]: defaultLineData,
    [metricsType.DATA_DISABLE_COUNT]: defaultLineData,
    [metricsType.DATA_COLLECTION_TOTAL_RPS]: defaultLineData,
    [metricsType.DATA_COLLECTION_TOTAL_BPS]: defaultLineData
}
function matchSourceInputUnit (metricsData: any = {}) {
    const { y = [[]] } = metricsData;
    let unit = '';
    const dataFlat = y.flat() || [];
    const maxVal = Math.max.apply(null, dataFlat);
    const isBps = maxVal < 1024;
    const isKbps = dataFlat.some((item: any) => item >= 1024 && item < Math.pow(1024, 2));
    const isMbps = dataFlat.some((item: any) => item >= Math.pow(1024, 2) && item < Math.pow(1024, 3));
    const isGbps = dataFlat.some((item: any) => item >= Math.pow(1024, 3) && item < Math.pow(1024, 4));
    const isTbps = dataFlat.some((item: any) => item >= Math.pow(1024, 4));
    if (isBps) {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.BPS;
    } else if (isKbps) {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.KBPS;
    } else if (isMbps) {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.MBPS;
    } else if (isGbps) {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.GBPS;
    } else if (isTbps) {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.TBPS;
    } else {
        unit = SOURCE_INPUT_BPS_UNIT_TYPE.BPS;
    }
    return unit
}

class StreamDetailGraph extends React.Component<any, any> {
    state: any = {
        time: defaultTimeValue,
        lineDatas: defaultData,
        sourceStatusList: []
    }
    componentDidMount () {
        this.initData();
        this.checkSourceStatus();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.clear();
            this.initData(data)
            this.checkSourceStatus(data);
        }
    }
    clear () {
        this.setState({
            lineDatas: defaultData,
            sourceStatusList: []
        })
    }
    checkSourceStatus (data?: any) {
        data = data || this.props.data;
        if (!data.id) {
            return;
        }
        Api.checkSourceStatus({
            taskId: data.id
        }).then((res: any) => {
            if (res.code == 1) {
                let data = res.data || {};
                this.setState({
                    sourceStatusList: Object.entries(data)
                })
            }
        })
    }
    setLineData (data: any = []) {
        const { lineDatas } = this.state;
        let stateLineData: any = { ...lineDatas };
        for (let i = 0; i < data.length; i++) {
            let item = data[i];
            let lineData = item.data;
            let type = item.chartName;
            let x: any = []; let y: any = []; let ext: any = {};
            x = lineData.map((data: any) => { return data.time });
            switch (type) {
                case metricsType.SOURCE_INPUT_BPS:
                case metricsType.SINK_OUTPUT_RPS:
                case metricsType.SOURCE_TPS:
                case metricsType.SOURCE_RPS:
                case metricsType.SOURCE_DIRTY:
                case metricsType.SOURCE_DIRTY_OUT:
                case metricsType.DELAY: {
                    let tmpMap: any = {};
                    let legend: any = [];
                    for (let i = 0; i < lineData.length; i++) {
                        let chartData = lineData[i];
                        for (let key in chartData) {
                            if (key == 'time') {
                                continue;
                            }
                            if (tmpMap[key]) {
                                tmpMap[key].push([i, chartData[key]])
                            } else {
                                tmpMap[key] = [[i, chartData[key]]];
                            }
                        }
                    }
                    for (let key in tmpMap) {
                        let datas = tmpMap[key];
                        y.push(datas);
                        legend.push(key);
                    }
                    ext.legend = legend;
                    break;
                }
                case metricsType.FAILOVER_RATE: {
                    y[0] = lineData.map((data: any) => { return data.fail_over_rate });
                    break;
                }
                case metricsType.DATA_COLLECTION_BPS: {
                    y[0] = lineData.map((data: any) => { return data.data_acquisition_input_bps });
                    y[1] = lineData.map((data: any) => { return data.data_acquisition_output_bps });
                    break;
                }
                case metricsType.DATA_COLLECTION_RPS: {
                    y[0] = lineData.map((data: any) => { return data.data_acquisition_input_rps });
                    y[1] = lineData.map((data: any) => { return data.data_acquisition_output_rps });
                    break;
                }
                case metricsType.DATA_DISABLE_TPS: {
                    y[0] = lineData.map((data: any) => { return data.data_discard_tps });
                    break;
                }
                case metricsType.DATA_DISABLE_COUNT: {
                    y[0] = lineData.map((data: any) => { return data.data_discard_count });
                    break;
                }
                case metricsType.DATA_COLLECTION_TOTAL_BPS: {
                    y[0] = lineData.map((data: any) => { return data.data_acquisition_input_byte_sum });
                    y[1] = lineData.map((data: any) => { return data.data_acquisition_output_byte_sum });
                    break;
                }
                case metricsType.DATA_COLLECTION_TOTAL_RPS: {
                    y[0] = lineData.map((data: any) => { return data.data_acquisition_input_record_sum });
                    y[1] = lineData.map((data: any) => { return data.data_acquisition_output_record_sum });
                    break;
                }
            }
            stateLineData[type] = {
                x,
                y,
                loading: false,
                ...ext
            }
        }
        this.setState({
            lineDatas: stateLineData
        })
    }
    initData (data?: any) {
        data = data || this.props.data;
        if (!data.id) {
            return;
        }
        const { taskType } = data;
        const isDataCollection = taskType == TASK_TYPE.DATA_COLLECTION;
        const { time } = this.state;
        let metricsList: any = [];

        if (isDataCollection) {
            metricsList.push(metricsType.DATA_COLLECTION_BPS)
            metricsList.push(metricsType.DATA_COLLECTION_RPS)
            metricsList.push(metricsType.DATA_COLLECTION_TOTAL_BPS)
            metricsList.push(metricsType.DATA_COLLECTION_TOTAL_RPS)
        } else {
            metricsList.push(metricsType.FAILOVER_RATE)
            metricsList.push(metricsType.DELAY)
            metricsList.push(metricsType.SOURCE_TPS)
            metricsList.push(metricsType.SINK_OUTPUT_RPS)
            metricsList.push(metricsType.SOURCE_RPS)
            metricsList.push(metricsType.SOURCE_INPUT_BPS)
            metricsList.push(metricsType.SOURCE_DIRTY)
            metricsList.push(metricsType.SOURCE_DIRTY_OUT)
            metricsList.push(metricsType.DATA_DISABLE_COUNT)
            metricsList.push(metricsType.DATA_DISABLE_TPS)
        }

        const successFunc = (res: any) => {
            if (res.code == 1) {
                this.setLineData(res.data)
            }
        }

        for (let i = 0; i < metricsList.length; i++) {
            let serverChart = metricsList[i];
            // 间隔时间，防止卡顿
            setTimeout(() => {
                Api.getTaskMetrics({
                    taskId: data.id,
                    timeStr: time,
                    chartNames: [serverChart]
                }).then(successFunc)
            }, 100 + 25 * i)
        }
    }

    changeTime (e: any) {
        this.setState({
            time: e.target.value
        }, this.initData.bind(this))
    }
    renderAlertMsg () {
        const { sourceStatusList = [] } = this.state;
        const msg = utils.textOverflowExchange(sourceStatusList.map(([ sourceName, type ]: any) => {
            return `${sourceName}(${DATA_SOURCE_TEXT[type]})`
        }).join('，'), 60);
        return msg ? <Alert
            message={`数据源${msg}连接异常`}
            type="warning"
            showIcon
        /> : null
    }
    render () {
        const { time, lineDatas } = this.state;
        const { data = {} } = this.props;
        const { taskType } = data;
        const isDataCollection = taskType == TASK_TYPE.DATA_COLLECTION;
        const sourceIptUnit = matchSourceInputUnit({ ...lineDatas[metricsType.SOURCE_INPUT_BPS] })
        return (
            <div className="pane-graph-box">
                <header className="graph-header">
                    <div style={{ flexGrow: 1, marginRight: '10px' }}>
                        {this.renderAlertMsg()}
                    </div>
                    <span className="m-radio-group" style={{ float: 'right' }}>
                        <Tooltip
                            title="刷新"
                        >
                            <Icon
                                onClick={this.initData.bind(this, null)}
                                type="reload"
                                style={{ color: '#333', marginRight: '20px', cursor: 'pointer' }}
                            />
                        </Tooltip>
                        <RadioGroup
                            className="no-bd nobackground"
                            onChange={this.changeTime.bind(this)}
                            value={time}
                        >
                            <RadioButton value={TIME_TYPE.M10}>最近10分钟</RadioButton>
                            <RadioButton value={TIME_TYPE.H1}>最近1小时</RadioButton>
                            <RadioButton value={TIME_TYPE.H6}>最近6小时</RadioButton>
                            <RadioButton value={TIME_TYPE.D1}>最近一天</RadioButton>
                            <RadioButton value={TIME_TYPE.W1}>最近一周</RadioButton>
                        </RadioGroup>
                    </span>
                </header>
                <div className="graph-content-box">
                    {isDataCollection ? (
                        <div style={{ padding: '10px 16px' }}>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_RPS],
                                            color: CHARTS_COLOR,
                                            unit: 'rps',
                                            legend: ['输入RPS', '输出RPS']
                                        }}
                                        desc="输入/输出数据量，单位是RecordPerSecond。"
                                        title="输入/输出RPS" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_BPS],
                                            color: CHARTS_COLOR,
                                            unit: 'bps',
                                            legend: ['输入BPS', '输出BPS']
                                        }}
                                        desc="输入/输出数据量，单位是BytePerSecond。"
                                        title="输入/输出BPS" />
                                </section>
                            </div>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_TOTAL_RPS],
                                            color: CHARTS_COLOR,
                                            unit: '条',
                                            legend: ['累计输入记录数', '累计输出记录数']
                                        }}
                                        desc="累计输入/输出记录数，单位是条"
                                        title="累计输入/输出记录数" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_TOTAL_BPS],
                                            color: CHARTS_COLOR,
                                            unit: 'Bytes',
                                            legend: ['累计输入数据量', '累计输出数据量']
                                        }}
                                        desc="累计输入/输出数据量，单位是Bytes或其他存储单位"
                                        title="累计输入/输出数据量" />
                                </section>
                            </div>
                        </div>
                    ) : (<Collapse className="middle-collapse middle-collapse--noBorderTop" defaultActiveKey={['OverView']}>
                        <Panel header="OverView" key="OverView">
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.FAILOVER_RATE],
                                            color: CHARTS_COLOR,
                                            legend: ['Rate']
                                        }}
                                        desc="当前任务出现Failover（错误或者异常）的频率。计算方法：当前Failover时间点的前一分钟内出现Failover的累计次数/60次。"
                                        title="FailOver Rate" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DELAY],
                                            color: CHARTS_COLOR,
                                            unit: 's'
                                        }}
                                        desc="数据中的时间戳与数据进入计算引擎之间的时间差，例如原始数据中的时间戳是2018-01-01 12:12:12，而流计算集群的当前时间为2018-01-01 12:13:12，则业务延迟为1分钟，每个KafkaTopic对应的业务延迟独立显示为不同的曲线。"
                                        title="业务延时" />
                                </section>
                            </div>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            color: CHARTS_COLOR,
                                            ...lineDatas[metricsType.SOURCE_TPS],
                                            unit: 'tps'
                                        }}
                                        desc="对流式数据输入（Kafka）进行统计，单位是TPS(Transaction Per Second)。"
                                        title="各Source的TPS数据输入" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.SINK_OUTPUT_RPS],
                                            color: CHARTS_COLOR,
                                            unit: 'rps'
                                        }}
                                        desc="对流式数据输出至MySQL、HBase、ElasticSearch等第三方存储系统的数据输出量，单位是RPS（Record Per Second）。"
                                        title="各Sink的RPS数据输出" />
                                </section>
                            </div>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.SOURCE_RPS],
                                            color: CHARTS_COLOR,
                                            unit: 'rps'
                                        }}
                                        desc="对流式数据输入（Kafka）进行统计，单位是RPS(Record Per Second)。"
                                        title="各Source的RPS数据输入" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.SOURCE_INPUT_BPS],
                                            color: CHARTS_COLOR,
                                            unit: sourceIptUnit,
                                            metricsType: metricsType.SOURCE_INPUT_BPS
                                        }}
                                        desc="对流式数据输入（Kafka）进行统计，单位是BPS(Byte Per Second)，系统会根据实际数据量自动转化单位，如Mbps、Gbps。"
                                        title="各Source的BPS数据输入" />
                                </section>
                            </div>

                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.SOURCE_DIRTY],
                                            color: CHARTS_COLOR
                                        }}
                                        desc="各Source的脏数据，反映实时计算 Flink的Source段是否有脏数据的情况。"
                                        title="各Source的脏数据" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.SOURCE_DIRTY_OUT],
                                            color: CHARTS_COLOR
                                        }}
                                        desc="各Sink的脏数据，反映实时计算 Flink的Sink段脏数据产生情况"
                                        title="各Sink的脏数据输出" />
                                </section>
                            </div>
                        </Panel>
                        <Panel header="WaterMark" key="WaterMark" style={{ marginBottom: '50px' }}>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_DISABLE_TPS],
                                            color: CHARTS_COLOR,
                                            legend: ['数据迟到丢弃TPS']
                                        }}
                                        desc="基于事件时间（EventTime）的流任务中，如果element的事件时间迟于允许的最大延迟时间，在窗口相关的操作中，该element将会被丢弃。"
                                        title="数据迟到丢弃TPS" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_DISABLE_COUNT],
                                            color: CHARTS_COLOR,
                                            legend: ['数据迟到累计丢弃数']
                                        }}
                                        desc="数据因迟到被丢弃的累计数量。"
                                        title="数据迟到累计丢弃数" />
                                </section>
                            </div>
                        </Panel>
                    </Collapse>)}
                </div>
            </div>
        )
    }
}

export default StreamDetailGraph;
