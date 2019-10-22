import * as React from 'react'
import moment from 'moment'

import {
    message, Modal,
    DatePicker,
    Select, Alert, Button
} from 'antd'

import utils from 'utils'
import pureRender from 'utils/pureRender'

import Api from '../../../api'

const { RangePicker } = DatePicker;
const Option = Select.Option

@pureRender
class GoOnTask extends React.Component<any, any> {
    state: any = {
        checkPoints: [],
        dateRange: null,
        externalPath: '',
        rangeValue: []
    }
    componentDidMount () {
        const taskId = this.props.taskId;
        if (taskId) {
            this.getCheckPointRange({
                taskId
            })
        }
    }

    getCheckPointRange = (params: any) => {
        Api.getCheckPointRange(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    dateRange: res.data
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
            isRestoration: 0
        }).then((res: any) => {
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
            checkPoints: [],
            rangeValue: []
        }, () => {
            this.props.onCancel();
        })
    }

    getCheckPoints = (params: any) => {
        const { dateRange } = this.state
        if (!dateRange) return;
        Api.getCheckPoints(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    checkPoints: res.data
                })
            }
        })
    }

    taskReadRangeChange = (value: any) => {
        this.setState({
            rangeValue: value,
            externalPath: '',
            checkPoints: []
        })
        if (!value || value.length === 0) return;

        const start = value[0].hour(0).minute(0).second(0)
        const end = value[1].hour(23).minute(59).second(59)

        this.getCheckPoints({
            taskId: this.props.taskId,
            startTime: start.valueOf(),
            endTime: end.valueOf()
        })
    }

    taskReadTimeChange = (value: any) => {
        this.setState({
            externalPath: value
        })
    }

    disabledDate = (current: any) => {
        const { dateRange } = this.state
        if (dateRange) {
            const min = moment(dateRange.left)
            const max = moment(dateRange.right)

            min.set({ hour: 0, minute: 0, second: 0, millisecond: 0 })
            max.set({ hour: 23, minute: 59, second: 59, millisecond: 0 })

            return current.valueOf() < min.valueOf() || current.valueOf() > max.valueOf()
        }
        return false;
    }

    render () {
        const { visible } = this.props
        const { dateRange, checkPoints, rangeValue, externalPath } = this.state;

        const options = checkPoints && checkPoints.map((item: any) => {
            const time = utils.formatDateTime(item.time)
            const nameFix = {
                name: item
            }
            return <Option title={time} key={item.id} value={item.externalPath} {...nameFix}>
                {time}
            </Option>
        })

        return (
            <Modal
                title="续跑任务"
                visible={visible}
                okText="确认"
                onCancel={this.cancel}
                cancelText="取消"
                footer={
                    <span>
                        <Button onClick={this.cancel}>取消</Button>
                        <Button disabled={!dateRange} type="primary" onClick={this.doGoOn}>确认</Button>
                    </span>
                }
            >
                {!dateRange && <Alert style={{ marginBottom: '8px' }} message="当前没有可用的checkpoint" type="error" />}
                <Alert message="续跑，任务将恢复至停止前的状态继续运行!" type="warning" />
                <div style={{ lineHeight: '30px' }}>指定读取数据时间:</div>
                <div>
                    <span style={{ marginRight: '5px' }}>
                        <RangePicker
                            style={{ width: '300px' }}
                            format="YYYY-MM-DD"
                            disabledDate={this.disabledDate}
                            onChange={this.taskReadRangeChange}
                            value={rangeValue}
                            disabled={!dateRange}
                        />
                    </span>
                    <span>
                        <Select
                            showSearch
                            size="default"
                            style={{ width: '160px' }}
                            placeholder="时间点"
                            optionFilterProp="name"
                            onChange={this.taskReadTimeChange}
                            disabled={!dateRange}
                            value={externalPath}
                        >
                            {options}
                        </Select>
                    </span>
                </div>
            </Modal>
        )
    }
}

export default GoOnTask
