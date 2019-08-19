import * as React from 'react'
import { cloneDeep } from 'lodash'
import utils from 'utils'

import Resize from 'widgets/resize';

import Api from '../../../../../../api'
import { lineAreaChartOptions } from '../../../../../../comm/const'

const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
require('echarts/lib/component/legend');
require('echarts/lib/component/legendScroll');
require('echarts/lib/component/tooltip');

class TopicDetailTable extends React.Component<any, any> {
    state: any = {
        data: [],
        lineChart: null
    }

    componentDidMount () {
        this.getDetail();
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { taskId, partitionId, topicName } = this.props;
        const { taskId: nextTaskId, partitionId: nextPartitionId, topicName: nextTopicName } = nextProps;
        if (taskId != nextTaskId || partitionId != nextPartitionId || topicName != nextTopicName) {
            this.getDetail(nextTaskId, nextPartitionId, nextTopicName);
        }
    }
    getDetail (taskId?: any, partitionId?: any, topicName?: any) {
        taskId = typeof taskId == 'undefined' ? this.props.taskId : taskId;
        partitionId = typeof partitionId == 'undefined' ? this.props.partitionId : partitionId;
        topicName = typeof topicName == 'undefined' ? this.props.topicName : topicName;
        if (!taskId || !partitionId || !topicName) {
            return;
        }
        /**
         * 每次请求清空数据
         */
        this.setState({
            data: []
        })
        Api.getDelayDetail({
            taskId: taskId,
            partitionId: partitionId,
            topicName: topicName
        })
            .then((res: any) => {
                if (res.code == 1) {
                    this.setState({
                        data: res.data
                    }, this.initGraph.bind(this))
                }
            })
    }
    initGraph (lineData: any, time: any) {
        lineData = lineData || this.state.data;
        if (!lineData) {
            return;
        }
        let myChart = echarts.init(document.getElementById('delayDetail'));
        let options = cloneDeep(lineAreaChartOptions);
        /**
         * 隐藏标题
         */
        options.title.show = false;
        /**
         * 设置横坐标数值
         */
        options.xAxis[0].data = lineData.map((item: any) => {
            return item.time;
        })
        options.xAxis[0].axisLabel = {
            ...options.xAxis[0].axisLabel,
            formatter: (value: any) => {
                return utils.formatHours(parseInt(value))
            }
        }
        options.xAxis[0].axisPointer = {
            label: {
                formatter: (params: any) => {
                    return utils.formatDateTime(parseInt(params.value))
                }
            }
        }
        /**
         * 画图区域的定位
         */
        options.grid.bottom = '8px';
        options.grid.left = '0px';
        options.grid.right = '20px';
        options.grid.top = '20px';
        options.grid.containLabel = true
        /**
         * 设置具体的数据
         */
        options.series = {
            name: '延迟数',
            data: lineData.map(
                (item: any) => {
                    return item.data;
                }
            ),
            type: 'line',
            smooth: true,
            areaStyle: {}
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
    render () {
        return (
            <Resize onResize={this.resize.bind(this)}>
                <article id="delayDetail" style={{ width: '100%', height: '200px' }} />
            </Resize>
        )
    }
}

export default TopicDetailTable;
