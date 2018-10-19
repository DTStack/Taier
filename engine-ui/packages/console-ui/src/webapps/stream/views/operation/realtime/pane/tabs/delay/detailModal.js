import React from "react"
import { cloneDeep } from "lodash"
import utils from "utils"

import { Modal, Button } from "antd";
import Resize from 'widgets/resize';

import Api from "../../../../../../api"
import { lineAreaChartOptions } from "../../../../../../comm/const"

const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/legend');
require('echarts/lib/component/legendScroll');
require('echarts/lib/component/tooltip');

class DelayDetailModal extends React.Component {

    state = {
        data: [],
        lineChart: null
    }

    componentDidMount() {
        this.getDetail();
    }
    componentWillReceiveProps(nextProps) {
        const { taskId, partitionId } = this.props;
        const { taskId: nextTaskId, partitionId: nextPartitionId } = nextProps;
        if (taskId != nextTaskId || partitionId != nextPartitionId) {
            this.getDetail(nextTaskId, nextPartitionId);
        }
    }
    getDetail(taskId, partitionId) {
        taskId = typeof taskId == "undefined" ? this.props.taskId : taskId;
        partitionId = typeof partitionId == "undefined" ? this.props.partitionId : partitionId;
        if (!taskId || !partitionId) {
            return;
        }
        this.setState({
            data: []
        })
        Api.getDelayDetail({
            taskId: taskId,
            partitionId: partitionId
        })
            .then((res) => {
                if (res.code == 1) {
                    this.setState({
                        data: res.data
                    }, this.initGraph.bind(this))
                }
            })
    }
    initGraph(lineData, time) {

        lineData = lineData || this.state.data;
        if (!lineData) {
            return;
        }
        let myChart = echarts.init(document.getElementById("delayDetail"));
        let options = cloneDeep(lineAreaChartOptions);
        /**
         * 隐藏标题
         */
        options.title.show = false;
        /**
         * 设置横坐标数值
         */
        options.xAxis[0].data = lineData.map((item) => {
            return item.time;
        })
        options.xAxis[0].axisLabel = {
            ...options.xAxis[0].axisLabel,
            formatter: (value) => {
                return utils.formatHours(parseInt(value))
            }
        }
        options.xAxis[0].axisPointer = {
            label: {
                formatter: (params) => {
                    return utils.formatDateTime(parseInt(params.value))
                }
            }
        }
        /**
         * 画图区域的定位
         */
        options.grid.bottom = "8px";
        options.grid.left = "20px";
        options.grid.right = "20px";
        options.grid.top = "20px";
        options.grid.containLabel = true
        /**
         * 设置具体的数据
         */
        options.series = {
            name: "延迟数",
            data: lineData.map(
                (item) => {
                    return item.data;
                }
            ),
            type: "line",
            smooth: true,
            areaStyle:{}    
        }
        /**
         * 执行绘图
         */
        myChart.setOption(options)
        this.setState({
            lineChart: myChart
        })
    }
    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    render() {
        return (
            <Modal
                title="数据延迟（最近24小时）"
                visible={this.props.visible}
                onCancel={this.props.closeDetail}
                width={700}
                footer={(
                    <Button onClick={this.props.closeDetail}>关闭</Button>
                )}
            >
                <Resize onResize={this.resize.bind(this)}>
                    <article id="delayDetail" style={{ width: '100%', height: '300px' }} />
                </Resize>
            </Modal>
        )
    }
}

export default DelayDetailModal;