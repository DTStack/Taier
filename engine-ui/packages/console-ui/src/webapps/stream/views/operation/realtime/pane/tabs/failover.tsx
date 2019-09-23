import * as React from 'react'

import LogInfo from '../../logInfo'
import Api from '../../../../../api'

class Failover extends React.Component<any, any> {
    state: any = {
        logInfo: ''
    }
    isUnmount = false;
    componentDidMount () {
        this.getLog();
    }
    async getLog () {
        const data = this.props.data;
        if (!data || !data.id) {
            return;
        }
        let res: any;
        res = await Api.getTaskLogs({ taskId: data.id });
        if (res && res.code == 1) {
            this.setState({
                logInfo: res.data
            });
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

export default Failover;
