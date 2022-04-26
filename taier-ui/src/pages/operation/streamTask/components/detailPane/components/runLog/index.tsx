import * as React from 'react'
import LogInfo from './logInfo'
import { TASK_STATUS } from '@/constant';
import { IStreamTaskProps } from '@/interface';

const Api = {} as any

interface IState {
    logInfo: {
        engineLog?: string;
    };
    offset: number;
}

interface IProps {
    data: IStreamTaskProps | undefined;
    isShow: boolean;
}

class RunLog extends React.Component<IProps, IState> {
    MAX_ENGINE_LOG = 10240
    timer: NodeJS.Timer | undefined
    state: IState = {
        logInfo: {},
        offset: -1
    }


    componentDidMount () {
        this.getLog()
        if (this.props?.data?.status == TASK_STATUS.RUNNING) {
            this.timer = setInterval(this.getLog, 5000)
        }
    }

    componentWillUnmount () {
        if(!this.timer) return
        clearInterval(this.timer)
    }

    prepareLogInfo (logInfo: IState['logInfo']) {
        if (!logInfo) return null
        let { engineLog } = logInfo;
        if(!engineLog) return null
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

    getLog = async () => {
        const { data, isShow } = this.props;
        if (!data?.id || !isShow) return
        const { offset } = this.state;
        let res: any;
        if (data.status == TASK_STATUS.RUNNING) {
            res = await Api.getTaskRunningLogs({ taskId: data.id, place: offset });
            if (res?.code == 1 && res?.data?.place) {
                this.setState((preState) => ({
                    logInfo: {
                        ...res.data,
                        engineLog: (preState.logInfo?.engineLog ?? '') + (res.data?.engineLog ?? '')
                    },
                    offset: res.data?.place
                }));
            }
        } else {
            res = await Api.getTaskLogs({ taskId: data.id });
            if (res?.code == 1) {
                this.setState({
                    logInfo: res.data
                });
            }
        }
    }

    render () {
        const { data, isShow } = this.props;
        const { status } = data || {};
        const { logInfo } = this.state;
        /**
         * 不显示的时候这里不能渲染，
         * 因为Editor和echarts绘图的时候会计算当前dom大小
         * 不显示的时候大小为0，会造成显示错误
         */
        return isShow ? 
            <div style={{ marginLeft: '20px', paddingLeft: '8px', height: 'calc(100% - 56px)', background: '#f7f7f7' }}>
                <LogInfo status={status} log={logInfo} />
            </div>: null
    }
}

export default RunLog;
