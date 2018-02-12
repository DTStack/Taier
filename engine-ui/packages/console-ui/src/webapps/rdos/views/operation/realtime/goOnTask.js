import React, { Component } from 'react'
import moment from 'moment'

import {
    message, Modal, 
    DatePicker, TimePicker,
    Select, Alert,
 } from 'antd'

 import utils from 'utils'
 
import Api from '../../../api'

const { RangePicker } = DatePicker;
const Option = Select.Option

export default class GoOnTask extends Component {

    state = {
        checkPoints: [],
        dateRange: {},
        externalPath: '',
        rangeValue: [
            moment().hour(0).minute(0).second(0), 
            moment().hour(23).minute(59).second(59)
        ],
    }

    componentWillReceiveProps(nextProps) {
        const taskId = nextProps.taskId
        const old = this.props.taskId
        if (taskId && old !== taskId) {
            this.getCheckPointRange({
                taskId,
            })
        }
    }

    getCheckPointRange = (params) => {
        Api.getCheckPointRange(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    dateRange: res.data,
                })
            }
        })
    }

    doGoOn = () => {
        const externalPath = this.state.externalPath
        if (!externalPath) {
            message.error('请选择续跑点！')
            return;
        }
        Api.startTask({
            id: this.props.taskId,
            externalPath,
        }).then((res) => {
            if (res.code === 1) {
                message.success('续跑操作成功！')
                this.props.onOk(res);
            }
        })
    }

    cancel = () => {
        this.setState({
            externalPath: '',
            dateRange: {},
            checkPoints: []
        }, () => {
            this.props.onCancel();
        })
    }

    getCheckPoints = (params) => {
        Api.getCheckPoints(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    checkPoints: res.data,
                })
            }
        })
    }

    taskReadRangeChange = (value) => {
        const start = value[0].hour(0).minute(0).second(0)
        const end = value[1].hour(23).minute(59).second(59)

        this.setState({
            rangeValue: [start, end],
        }, () => {
            this.getCheckPoints({
                taskId: this.props.taskId,
                startTime: start.valueOf(),
                endTime: end.valueOf(),
            })
        })
    }

    taskReadTimeChange = (value) => {
        this.setState({
            externalPath: value,
        })
    }

    disabledDate = (current) => {
        const data = this.state.dateRange
        const min = data ? data.left : moment().valueOf()
        const max = data ? data.right : moment().valueOf()
        return current && (current.valueOf() < min || current.valueOf() > max)
    }

    render() {
        const { visible } = this.props
        const checkPoints = this.state.checkPoints

        const options = checkPoints && checkPoints.map(item => {
            const time = utils.formatDateTime(item.time)
            return <Option key={item.id} value={item.externalPath} name={time}>
                {time}
            </Option>
        })

        return (
            <Modal
                title="续跑任务"
                visible={visible}
                onOk={this.doGoOn}
                onCancel={this.cancel}
                okText="确认"
                cancelText="取消"
                >
                <Alert message="续跑，任务将恢复至停止前的状态继续运行!" type="warning" />
                <div style={{lineHeight: '30px'}}>指定读取数据时间:</div>
                <div> 
                    <span style={{marginRight: '5px'}}>
                        <RangePicker
                            format="YYYY-MM-DD HH:mm:ss"
                            disabledDate={this.disabledDate}
                            onChange={this.taskReadRangeChange}
                            value={this.state.rangeValue}
                        />
                    </span>
                    <span>
                        <Select
                            showSearch
                            size="default"
                            style={{width: '160px'}}
                            placeholder="时间点"
                            optionFilterProp="name"
                            onChange={this.taskReadTimeChange}
                        >
                            { options }
                        </Select>
                    </span>
                </div>
            </Modal>
        )
    }

}