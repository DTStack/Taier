import React from 'react'

import StreamDetailGraph from './graph'
import LogInfo from '../../logInfo'
import { TASK_STATUS } from '../../../../../comm/const';
import Api from '../../../../../api'

class BaseInfo extends React.Component {
    state = {
        logInfo: ''
    }
    componentDidMount () {
        console.log('BaseInfo')
        this.getLog();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id || data.status != nextData.status
        ) {
            this.getLog(nextData);
        }
    }
    getLog (data) {
        data = data || this.props.data;
        if (!data || (
            data.status != TASK_STATUS.RUN_FAILED &&
            data.status != TASK_STATUS.SUBMIT_FAILED
        )) {
            return;
        }
        this.setState({
            logInfo: ''
        })
        Api.getTaskLogs({ taskId: data.id }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    logInfo: res.data
                })
            }
        })
    }
    getBaseInfo () {
        const { data = {}, isShow } = this.props;
        const { status } = data;
        const { logInfo } = this.state;
        /**
         * 不显示的时候这里不能渲染，
         * 因为Editor和echarts绘图的时候会计算当前dom大小
         * 不显示的时候大小为0，会造成显示错误
         */
        if (!isShow) {
            return null;
        }
        switch (status) {
            case TASK_STATUS.RUN_FAILED:
            case TASK_STATUS.SUBMIT_FAILED: {
                return (
                    <div style={{ paddingLeft: '8px', background: '#f7f7f7' }}>
                        <LogInfo status={status} log={logInfo} />
                    </div>
                )
            }
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.FINISHED: {
                return (
                    <StreamDetailGraph data={data} />
                )
            }
            default: {
                return <div className="not-run-box">
                    <img src="/public/stream/img/not_run.svg" className="icon" />
                    <p className="text">该任务暂未运行</p>
                </div>
            }
        }
    }
    render () {
        return (
            <div>
                {this.getBaseInfo()}
            </div>
        )
    }
}

export default BaseInfo;
