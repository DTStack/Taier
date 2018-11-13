import React from "react"
import moment from "moment"
import utils from "utils";

import { Radio, Collapse, Row, Col, Button } from "antd"

import AlarmBaseGraph from "./baseGraph";
import { TIME_TYPE, CHARTS_COLOR } from "../../../../../../comm/const";
import Api from "../../../../../../api"
import { TASK_TYPE } from "../../../../../../../stream/comm/const";

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;

const defaultTimeValue = '10m';
const metricsType = {
    FAILOVER_RATE: "fail_over_rate",
    DELAY: "data_delay",
    SOURCE_TPS: "source_input_tps",
    SINK_OUTPUT_RPS: "sink_output_rps",
    SOURCE_RPS: "source_input_rps",
    SOURCE_INPUT_BPS: "source_input_bps",
    SOURCE_DIRTY: "source_dirty_data",
    DATA_COLLECTION_RPS: "jlogstash_rps",
    DATA_COLLECTION_BPS: "jlogstash_bps",
    DATA_DISABLE_TPS: "data_discard_tps",
    DATA_DISABLE_COUNT: "data_discard_count",
    DATA_COLLECTION_TOTAL_RPS: "jlogstash_record_sum",
    DATA_COLLECTION_TOTAL_BPS: "jlogstash_byte_sum",
}
const defaultLineData = {
    x: [],
    y: [[]],
    loading: true
}
class StreamDetailGraph extends React.Component {

    state = {
        time: defaultTimeValue,
        lineDatas: {
            [metricsType.FAILOVER_RATE]: defaultLineData,
            [metricsType.DELAY]: defaultLineData,
            [metricsType.SOURCE_TPS]: defaultLineData,
            [metricsType.SINK_OUTPUT_RPS]: defaultLineData,
            [metricsType.SOURCE_RPS]: defaultLineData,
            [metricsType.SOURCE_INPUT_BPS]: defaultLineData,
            [metricsType.SOURCE_DIRTY]: defaultLineData,
            [metricsType.DATA_COLLECTION_RPS]: defaultLineData,
            [metricsType.DATA_COLLECTION_BPS]: defaultLineData,
            [metricsType.DATA_DISABLE_TPS]: defaultLineData,
            [metricsType.DATA_DISABLE_COUNT]: defaultLineData,
            [metricsType.DATA_COLLECTION_TOTAL_RPS]: defaultLineData,
            [metricsType.DATA_COLLECTION_TOTAL_BPS]: defaultLineData,
        }
    }
    componentDidMount() {
        this.initData();
    }
    componentWillReceiveProps(nextProps) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.clear();
            this.initData(data)
        }
    }
    clear() {
        this.setState({
            lineDatas: {
                [metricsType.FAILOVER_RATE]: defaultLineData,
                [metricsType.DELAY]: defaultLineData,
                [metricsType.SOURCE_TPS]: defaultLineData,
                [metricsType.SINK_OUTPUT_RPS]: defaultLineData,
                [metricsType.SOURCE_RPS]: defaultLineData,
                [metricsType.SOURCE_INPUT_BPS]: defaultLineData,
                [metricsType.SOURCE_DIRTY]: defaultLineData,
                [metricsType.DATA_COLLECTION_RPS]: defaultLineData,
                [metricsType.DATA_COLLECTION_BPS]: defaultLineData,
                [metricsType.DATA_DISABLE_TPS]: defaultLineData,
                [metricsType.DATA_DISABLE_COUNT]: defaultLineData,
                [metricsType.DATA_COLLECTION_TOTAL_RPS]: defaultLineData,
                [metricsType.DATA_COLLECTION_TOTAL_BPS]: defaultLineData,
            }
        })
    }
    setLineData(data = []) {
        const { lineDatas } = this.state;
        let stateLineData = { ...lineDatas };
        for (let i = 0; i < data.length; i++) {
            let item = data[i];
            let lineData = item.data;
            let type = item.chartName;
            let x = [], y = [], ext = {};
            x = lineData.map((data) => { return data.time });
            switch (type) {
                case metricsType.SOURCE_INPUT_BPS:
                case metricsType.SINK_OUTPUT_RPS:
                case metricsType.SOURCE_TPS:
                case metricsType.SOURCE_RPS:
                case metricsType.SOURCE_DIRTY: {
                    let tmp_map = {};
                    let legend = [];
                    for (let i = 0; i < lineData.length; i++) {
                        let chartData = lineData[i];
                        for (let key in chartData) {
                            if (key == "time") {
                                continue;
                            }
                            if (tmp_map[key]) {
                                tmp_map[key].push(chartData[key])
                            } else {
                                tmp_map[key] = [chartData[key]];
                            }
                        }
                    }
                    for (let key in tmp_map) {
                        let datas = tmp_map[key];
                        y.push(datas);
                        legend.push(key);
                    }
                    ext.legend = legend;
                    break;
                }
                case metricsType.FAILOVER_RATE: {
                    y[0] = lineData.map((data) => { return data.fail_over_rate });
                    break;
                }
                case metricsType.DELAY: {
                    y[0] = lineData.map((data) => { return data.biz_time });
                    break;
                }
                case metricsType.DATA_COLLECTION_BPS: {
                    y[0] = lineData.map((data) => { return data.jlogstash_input_bps });
                    y[1] = lineData.map((data) => { return data.jlogstash_output_bps });
                    break;
                }
                case metricsType.DATA_COLLECTION_RPS: {
                    y[0] = lineData.map((data) => { return data.jlogstash_input_rps });
                    y[1] = lineData.map((data) => { return data.jlogstash_output_rps });
                    break;
                }
                case metricsType.DATA_DISABLE_TPS: {
                    y[0] = lineData.map((data) => { return data.data_discard_tps });
                    break;
                }
                case metricsType.DATA_DISABLE_COUNT: {
                    y[0] = lineData.map((data) => { return data.data_discard_count });
                    break;
                }
                case metricsType.DATA_COLLECTION_TOTAL_BPS:{
                    y[0] = lineData.map((data) => { return data.jlogstash_input_byte_sum });
                    y[1] = lineData.map((data) => { return data.jlogstash_output_byte_sum });
                    break;
                }
                case metricsType.DATA_COLLECTION_TOTAL_RPS:{
                    y[0] = lineData.map((data) => { return data.jlogstash_input_record_sum });
                    y[1] = lineData.map((data) => { return data.jlogstash_output_record_sum });
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
    initData(data) {
        data = data || this.props.data;

        const { taskType } = data;
        const isDataCollection = taskType == TASK_TYPE.DATA_COLLECTION;
        const { time } = this.state;
        let metricsList = [];

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
            metricsList.push(metricsType.DATA_DISABLE_COUNT)
            metricsList.push(metricsType.DATA_DISABLE_TPS)
        }

        const successFunc = (res) => {
            if (res.code == 1) {
                this.setLineData(res.data)
            }
        }

        for (let i = 0; i < metricsList.length; i++) {
            let serverChart = metricsList[i];
            //间隔时间，防止卡顿
            setTimeout(() => {
                Api.getTaskMetrics({
                    taskId: data.id,
                    timeStr: time,
                    chartNames: [serverChart]
                }).then(successFunc)
            }, 100 + 25 * i)
        }

    }

    changeTime(e) {
        this.setState({
            time: e.target.value
        }, this.initData.bind(this))
    }
    render() {
        const { time, lineDatas } = this.state;
        const { data = {} } = this.props;
        const { taskType } = data;
        const isDataCollection = taskType == TASK_TYPE.DATA_COLLECTION;

        return (
            <div className="pane-graph-box">
                <header className="graph-header" style={{ padding: "10px 20px 10px 20px", overflow: "hidden" }}>
                    <Button onClick={this.initData.bind(this, null)} type="primary">刷新</Button>
                    <span className="m-radio-group" style={{ float: "right" }}>
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
                        <div style={{ padding: "10px 16px" }}>
                            <div className="alarm-graph-row">
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_RPS],
                                            color: CHARTS_COLOR,
                                            legend: ["输入RPS", "输出RPS"]
                                        }}
                                        title="输入/输出RPS" />
                                </section>
                                <section>
                                    <AlarmBaseGraph
                                        time={time}
                                        lineData={{
                                            ...lineDatas[metricsType.DATA_COLLECTION_BPS],
                                            color: CHARTS_COLOR,
                                            legend: ["输入BPS", "输出BPS"],
                                        }}
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
                                                    legend: ["累计输入RPS","累计输出RPS"]
                                                }}
                                                title="累计输入/输出RPS" />
                                        </section>
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.DATA_COLLECTION_TOTAL_BPS],
                                                    color: CHARTS_COLOR,
                                                    unit:'KB',
                                                    legend: ["累计输入BPS","累计输出BPS"]
                                                }}
                                                title="累计输入/输出BPS" />
                                        </section>
                                    </div>
                        </div>
                    ) : (
                            <Collapse className="middle-collapse" defaultActiveKey={['OverView']}>
                                <Panel header="OverView" key="OverView">
                                    <div className="alarm-graph-row">
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.FAILOVER_RATE],
                                                    color: CHARTS_COLOR,
                                                    legend: ["Rate"]
                                                }}
                                                title="FailOver Rate" />
                                        </section>
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.DELAY],
                                                    color: CHARTS_COLOR,
                                                    legend: ["业务延时"],
                                                    unit: "s"
                                                }}
                                                title="Delay" />
                                        </section>
                                    </div>
                                    <div className="alarm-graph-row">
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    color: CHARTS_COLOR,
                                                    ...lineDatas[metricsType.SOURCE_TPS],
                                                    unit: "bps 条/秒"
                                                }}
                                                title="各Source的TPS数据输入" />
                                        </section>
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.SINK_OUTPUT_RPS],
                                                    color: CHARTS_COLOR,
                                                    unit: "rps 条/秒"
                                                }}
                                                title="各Sink的数据输出" />
                                        </section>
                                    </div>
                                    <div className="alarm-graph-row">
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.SOURCE_RPS],
                                                    color: CHARTS_COLOR,
                                                    unit: "rps 条/秒"
                                                }}
                                                title="各Source的RPS数据输入" />
                                        </section>
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.SOURCE_INPUT_BPS],
                                                    color: CHARTS_COLOR,
                                                    unit: "KB"
                                                }}
                                                title="各Source的BPS数据输入" />
                                        </section>
                                    </div>

                                    <div className="alarm-graph-row">
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.SOURCE_DIRTY],
                                                    color: CHARTS_COLOR,
                                                }}
                                                title="各Source的脏数据" />
                                        </section>
                                    </div>
                                </Panel>
                                <Panel header="WaterMark" key="WaterMark" style={{ marginBottom: "50px" }}>
                                    <div className="alarm-graph-row">
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.DATA_DISABLE_TPS],
                                                    color: CHARTS_COLOR,
                                                    legend: ["数据迟到丢弃TPS"]
                                                }}
                                                title="数据迟到丢弃TPS" />
                                        </section>
                                        <section>
                                            <AlarmBaseGraph
                                                time={time}
                                                lineData={{
                                                    ...lineDatas[metricsType.DATA_DISABLE_COUNT],
                                                    color: CHARTS_COLOR,
                                                    legend: ["数据迟到累计丢弃数"]
                                                }}
                                                title="数据迟到累计丢弃数" />
                                        </section>
                                    </div>
                                </Panel>
                            </Collapse>
                        )}
                </div>
            </div>
        )
    }
}

export default StreamDetailGraph;