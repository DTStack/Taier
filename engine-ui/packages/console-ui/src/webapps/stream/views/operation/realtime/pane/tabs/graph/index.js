import React from "react"
import moment from "moment"
import utils from "utils";

import { Radio, Collapse, Row, Col } from "antd"

import AlarmBaseGraph from "./baseGraph";
import { TIME_TYPE, CHARTS_COLOR } from "../../../../../../comm/const";
import Api from "../../../../../../api"

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;

const defaultTimeValue = '10m';
const metricsType = {
    IN_OUT_RPS: "input_output_rps",
    IN_OUT_BPS: "input_output_bps",
    SOURCE_TPS: "source_tps",
    SOURCE_RPS: "source_rps",
    FAILOVER_RATE: "fail_over_rate",
    DELAY: "delay",
    SOURCE_DIRTY: "source_dirty_data"

}
const defaultLineData = {
    x: [],
    y: [[]],
    loading: true
}
class StreamDetailGraph extends React.Component {

    state = {
        time: defaultTimeValue,
        data: [],
        loading: false,
        lineDatas: {
            [metricsType.IN_OUT_BPS]: defaultLineData,
            [metricsType.IN_OUT_RPS]: defaultLineData,
            [metricsType.SOURCE_RPS]: defaultLineData,
            [metricsType.SOURCE_TPS]: defaultLineData,
            [metricsType.FAILOVER_RATE]: defaultLineData,
            [metricsType.DELAY]: defaultLineData,
            [metricsType.SOURCE_DIRTY]: defaultLineData,
        }
    }
    componentDidMount() {
        this.initData();
    }
    componentWillReceiveProps(nextProps) {
        const data = nextProps.data
        const oldData = this.props.data
        if (oldData && data && oldData.id !== data.id) {
            this.initData(data)
        }
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
                case metricsType.IN_OUT_BPS: {
                    y[0] = lineData.map((data) => { return data.input_bps });
                    y[1] = lineData.map((data) => { return data.output_bps });
                    break;
                }
                case metricsType.IN_OUT_RPS: {
                    y[0] = lineData.map((data) => { return data.input_rps });
                    y[1] = lineData.map((data) => { return data.output_rps });
                    break;
                }
                case metricsType.SOURCE_TPS:
                case metricsType.SOURCE_RPS:
                case metricsType.SOURCE_DIRTY: {
                    let tmp_map = {};
                    let legend=[];
                    for (let i = 0; i < lineData.length; i++) {
                        let chartData=lineData[i];
                        for (let key in chartData) {
                            if (key == "time") {
                                continue;
                            }
                            if (tmp_map[key]) {
                                tmp_map[key].push(chartData[key])
                            }else{
                                tmp_map[key]=[chartData[key]];
                            }
                        }
                    }
                    for(let key in tmp_map){
                        let datas=tmp_map[key];
                        y.push(datas);
                        legend.push(key);
                    }
                    ext.legend=legend;
                    break;
                }
                case metricsType.FAILOVER_RATE: {
                    y[0] = lineData.map((data) => { return data.fail_over_rate });
                    break;
                }
                case metricsType.DELAY: {
                    y[0] = lineData.map((data) => { return data.biz_time });
                    y[1] = lineData.map((data) => { return data.data_interval_time });
                    y[2] = lineData.map((data) => { return data.data_delay_time });
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
        const { time } = this.state;
        this.setState({
            loading: true
        })
        Api.getTaskMetrics({
            taskId: data.id,
            timeStr: time,
            metricNames: [
                metricsType.IN_OUT_BPS,
                metricsType.IN_OUT_RPS,
                metricsType.SOURCE_TPS,
                metricsType.SOURCE_RPS,
                metricsType.FAILOVER_RATE,
                metricsType.DELAY,
                metricsType.SOURCE_DIRTY]
        }).then(
            (res) => {
                if (res.code == 1) {
                    this.setLineData(res.data)
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    changeTime(e) {
        this.setState({
            time: e.target.value
        },this.initData.bind(this))
    }
    render() {
        const { time, lineDatas } = this.state;
        return (
            <div>
                <header style={{ padding: "10px 20px 10px 0px", overflow: "hidden" }}>
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
                <Collapse className="middle-collapse" defaultActiveKey={['OverView']}>
                    <Panel header="OverView" key="OverView">
                        <div className="alarm-graph-row">
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        color: CHARTS_COLOR,
                                        legend: ["输入RPS", "输出RPS"],
                                        ...lineDatas[metricsType.IN_OUT_RPS],
                                    }}
                                    title="输入/输出RPS" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        color: CHARTS_COLOR,
                                        legend: ["输入BPS", "输出BPS"],
                                        ...lineDatas[metricsType.IN_OUT_BPS],
                                    }}
                                    title="输入/输出BPS" />
                            </section>
                        </div>
                        <div className="alarm-graph-row">
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        color:CHARTS_COLOR,
                                        ...lineDatas[metricsType.SOURCE_TPS],
                                        unit: "bps"
                                    }}
                                    title="各Source的TPS数据输入" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...lineDatas[metricsType.SOURCE_RPS],
                                        color:CHARTS_COLOR,
                                        unit: "rps"
                                    }}
                                    title="各Source的RPS数据输入" />
                            </section>
                        </div>
                        <div className="alarm-graph-row">
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...lineDatas[metricsType.FAILOVER_RATE],
                                        color:CHARTS_COLOR,
                                        legend: ["Rate"]
                                    }}
                                    title="FailOver Rate" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...lineDatas[metricsType.DELAY],
                                        color:CHARTS_COLOR,
                                        legend: ["业务延时", "数据间隔时间", "数据滞留时间"],
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
                                        ...lineDatas[metricsType.SOURCE_DIRTY],
                                        color:CHARTS_COLOR,
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
                                        ...lineDatas[metricsType.DELAY],
                                        color:CHARTS_COLOR,
                                        legend: ["丢弃TPS"]
                                    }}
                                    title="数据迟到丢弃TPS" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...lineDatas[metricsType.DELAY],
                                        color:CHARTS_COLOR,
                                        legend: ["丢弃数"]
                                    }}
                                    title="数据迟到累计丢弃数" />
                            </section>
                        </div>
                    </Panel>
                </Collapse>
            </div>
        )
    }
}

export default StreamDetailGraph;