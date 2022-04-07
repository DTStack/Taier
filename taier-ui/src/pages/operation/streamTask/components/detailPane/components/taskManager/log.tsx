import * as React from 'react'
import Editor from '@/components/codeEditor'
import { TASK_STATUS } from '@/constant'
import { Pagination, Tooltip, Breadcrumb } from 'antd'
import { SyncOutlined } from '@ant-design/icons';
import { isEmpty } from 'lodash'
import { ITaskList } from './list';
import { IStreamTaskProps } from '@/interface';

const Api = {} as any

declare var APP_CONF: any;

const API_PREFIX = APP_CONF.API_PREFIX || '';

const editorOptions = {
    mode: 'simpleLog',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

interface IState {
    logInfo: {
        place?: number;
        totalPage?: number;
        engineLog?: string;
        downLoadLog?: string;
    } | null,
    spin: boolean;
    current: number;
}

interface IProps {
    isFail: boolean, 
    taskDetail: ITaskList | null, 
    data: IStreamTaskProps | undefined,
    toTaskDetail: (record: ITaskList | null) => void;
}

class TaskManagerLog extends React.Component<IProps, IState> {
    state: IState = {
        logInfo: {
            place: -1,
            engineLog: ''
        },
        spin: false,
        current: 1
    }
    FIRST_CURRENT = 1
    MAX_ENGINE_LOG = 1024 * 1024
    _editorRef: any

    componentDidMount () {
        this.getLog()
    }

    prepareLogInfo (logInfo: IState['logInfo']): IState['logInfo'] {
        if (isEmpty(logInfo)) return null
        let { engineLog } = logInfo || {}
        if (engineLog!.length > this.MAX_ENGINE_LOG) {
            engineLog = engineLog?.substr(0, this.MAX_ENGINE_LOG)
        }
        return {
            ...logInfo,
            engineLog
        }
    }

    getLog = async () => {
        const { taskDetail, data } = this.props
        const { current, logInfo } = this.state
        if (!taskDetail || !taskDetail.id) {
            return;
        }
        const params: any = {
            taskId: data?.id,
            taskManagerId: taskDetail.id,
            currentPage: current,
            place: current == this.FIRST_CURRENT ? -1 : logInfo?.place
        }
        if (data?.status == TASK_STATUS.RUNNING) {
            const res = await Api.getTaskManageLog(params)
            if (res && res.code == 1) {
                this.setState({
                    logInfo: this.prepareLogInfo(res.data),
                    spin: false
                })
            }
        } else {
            const res = await Api.getTaskLogs({
                taskId: data?.id,
                taskManagerId: taskDetail.id
            })
            if (res && res.code == 1) {
                this.setState({
                    logInfo: res.data,
                    spin: false
                })
            }
        }
    }

    refreshData = () => {
        this.setState({
            spin: true
        }, this.getLog)
    }

    onPaginationChange = (current: number) => {
        this.setState({
            current
        }, this.getLog)
    }

    getBaseInfo () {
        const { logInfo, current, spin } = this.state
        const { isFail, taskDetail, data } = this.props
        const isRunning = data?.status == TASK_STATUS.RUNNING
        const pagination: any = {
            current,
            pageSize: 1,
            total: logInfo?.totalPage ?? 1
        };
        
        return (
            <div style={{ height: '100%' }}>
                <div className="c-taskManage__log__header">
                    <Breadcrumb>
                        <Breadcrumb.Item onClick={() => { this.props.toTaskDetail(null) }}>
                            <a>Task List</a>
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>{taskDetail?.id}</Breadcrumb.Item>
                    </Breadcrumb>
                    <Tooltip title="刷新数据">
                        <SyncOutlined
                            type="sync"
                            spin={spin}
                            onClick={this.refreshData}
                            style={{
                                cursor: 'pointer',
                                color: '#94A8C6'
                            }}
                        />
                    </Tooltip>
                </div>
                {isFail && <div className="c-taskManage__log__downLoadLog">
                    当前Task Manager完整日志下载地址：{logInfo?.downLoadLog ? <a href={`${API_PREFIX}${logInfo.downLoadLog}`}>logDownload</a> : <span>日志加载中...</span>}
                </div>}
                <div className="c-taskManage__log__editor">
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        value={logInfo?.engineLog ?? ''}
                        options={editorOptions}
                        editorRef={(ref: any) => {
                            this._editorRef = ref;
                        }} />
                </div>
                {isRunning && <Pagination
                    { ...pagination }
                    showQuickJumper
                    className="c-taskManage__log__pagination"
                    onChange={this.onPaginationChange} />}
            </div>
        )
    }

    render () {
        return this.getBaseInfo();
    }
}

export default TaskManagerLog;
