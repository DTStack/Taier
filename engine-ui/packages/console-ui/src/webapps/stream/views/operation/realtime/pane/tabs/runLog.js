import React from 'react'

import LogInfo from '../../logInfo'
import Api from '../../../../../api'

class RunLog extends React.Component {
    state = {
        logInfo: ''
    }
    componentDidMount () {
        this.getLog();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { data = {} } = this.props;
        const { data: nextData = {} } = nextProps;
        if (data.id != nextData.id) {
            this.getLog(nextData);
        }
    }
    getLog (data) {
        data = data || this.props.data;
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
        return <div style={{ paddingLeft: '8px', height: '100%', background: '#f7f7f7' }}>
            <LogInfo status={status} log={logInfo} />
        </div>
    }
    render () {
        return this.getBaseInfo();
    }
}

export default RunLog;
