import React from "react"
import moment from "moment"
import utils from "utils";

import { Radio, Collapse, Row, Col } from "antd"

import AlarmBaseGraph from "./baseGraph";
import { TIME_TYPE } from "../../../../../../comm/const";
import Api from "../../../../../../api"

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;

const defaultTimeValue = '10m';
const metricsType={
    IN_OUT_RPS:"input_output_rps",
    IN_OUT_BPS:"input_output_bps",
    SOURCE_TPS:"source_tps",
    SOURCE_RPS:"source_rps",
    FAILOVER_RATE:"fail_over_rate",
    DELAY:"delay",
    SOURCE_DIRTY:"source_dirty_data"

}
class StreamDetailGraph extends React.Component {

    state = {
        time: defaultTimeValue,
        data: [],
        loading: false
    }
    componentWillMount() {
        let data = {
            x: [],
            y: [[], []]
        }
        const date = new moment();
        for (let i = 0; i < 20; i++) {
            data.x.push(date.add(10, 's').valueOf());
            data.y[0].push((~~(Math.random() * 90)))
            data.y[1].push((~~(Math.random() * 80)))
        }
        this._data = data;
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
    initData(data) {
        data=data||this.props.data;
        const { time } = this.state;
        this.setState({
            loading: true
        })
        Api.getTaskMetrics({
            taskId: data.id,
            timeStr: time,
            metricNames: []
        }).then(
            (res) => {
                if (res.code == 1) {
                    console.log(res);
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
        })
    }
    render() {
        const { time } = this.state;
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
                                        ...this._data,
                                        color: ["#00a6e7", "#1abb9c"],
                                        legend: ["输入RPS", "输出RPS"]
                                    }}
                                    title="输入/输出RPS" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...this._data,
                                        color: ["#00a6e7", "#1abb9c"],
                                        legend: ["输入BPS", "输出BPS"]
                                    }}
                                    title="输入/输出BPS" />
                            </section>
                        </div>
                        <div className="alarm-graph-row">
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
                                        legend: ["SourceT"],
                                        unit: "bps"
                                    }}
                                    title="各Source的TPS数据输入" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
                                        legend: ["SourceR"],
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
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
                                        legend: ["Rate"]
                                    }}
                                    title="FailOver Rate" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...this._data,
                                        color: ["#00a6e7", "#87e0ff"],
                                        legend: ["业务延时", "数据间隔时间"],
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
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
                                        legend: ["脏数据"]
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
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
                                        legend: ["丢弃TPS"]
                                    }}
                                    title="数据迟到丢弃TPS" />
                            </section>
                            <section>
                                <AlarmBaseGraph
                                    time={time}
                                    lineData={{
                                        ...this._data,
                                        y: [this._data.y[0]],
                                        color: ["#00a6e7"],
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