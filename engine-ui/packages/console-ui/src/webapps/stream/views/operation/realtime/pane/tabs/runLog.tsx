import * as React from 'react'

import LogInfo from '../../logInfo'
import Api from '../../../../../api'
import { TASK_STATUS } from '../../../../../comm/const';

class RunLog extends React.Component<any, any> {
    MAX_ENGINE_LOG = 10240
    state: any = {
        logInfo: '',
        offset: -1
    }
    _clock: any = null
    isUnmount = false;
    componentDidMount () {
        this.getLog();
    }
    componentWillUnmount () {
        this.isUnmount = true;
        window.clearTimeout(this._clock);
    }
    prepareLogInfo (logInfo: any = {}) {
        let { engineLog } = logInfo;
        const { engineLog: oldEngineLog } = this.state.logInfo;
        if (oldEngineLog) {
            engineLog = oldEngineLog + engineLog;
        }
        if (engineLog.length > this.MAX_ENGINE_LOG) {
            engineLog = engineLog.substr(-this.MAX_ENGINE_LOG);
        }
        return {
            ...logInfo,
            engineLog: engineLog.substr(-this.MAX_ENGINE_LOG)
        }
    }
    async getLog () {
        if (this.isUnmount) {
            return;
        }
        const data = this.props.data;
        if (!data || !data.id) {
            return;
        }
        const { offset } = this.state;
        let res: any;
        console.trace(Date.now());
        if (data.status == TASK_STATUS.RUNNING) {
            res = await Api.getTaskRunningLogs({ taskId: data.id, start: offset });
            if (res && res.code == 1) {
                this.setState({
                    logInfo: this.prepareLogInfo(res.data),
                    offset: res.data.totalFileLength
                });
                this._clock = setTimeout(() => {
                    this.getLog();
                }, 2000);
            }
        } else {
            res = await Api.getTaskLogs({ taskId: data.id });
            if (res && res.code == 1) {
                this.setState({
                    logInfo: res.data
                });
            }
        }
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
