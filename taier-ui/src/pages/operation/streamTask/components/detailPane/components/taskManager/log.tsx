import { useEffect,useState } from 'react';
import { SyncOutlined } from '@ant-design/icons';
import { Breadcrumb,Pagination, Tooltip } from 'antd';
import { isEmpty } from 'lodash';

import stream from '@/api';
import Editor from '@/components/editor';
import { TASK_STATUS } from '@/constant';
import { IStreamJobProps } from '@/interface';
import { ITaskList } from './list';
import './log.scss';

interface IProps {
    taskDetail: ITaskList | null;
    data: IStreamJobProps | undefined;
    toTaskDetail: (record: ITaskList | null) => void;
}

interface ILogInfoProps {
    place?: number;
    totalPage?: number;
    engineLog?: string;
    downLoadLog?: string;
}

const FIRST_CURRENT = 1;
const MAX_ENGINE_LOG = 1024 * 1024;

export default function TaskManagerLog({ data, taskDetail, toTaskDetail }: IProps) {
    const [spin, setSpin] = useState(false);
    const [logInfo, setLogInfo] = useState<ILogInfoProps>({
        place: -1,
        engineLog: '',
    });
    const [current, setCurrent] = useState(1);

    const prepareLogInfo = (logInfo: ILogInfoProps): ILogInfoProps | null => {
        if (isEmpty(logInfo)) return null;
        let { engineLog } = logInfo || {};
        if (engineLog!.length > MAX_ENGINE_LOG) {
            engineLog = engineLog?.substr(0, MAX_ENGINE_LOG);
        }
        return {
            ...logInfo,
            engineLog,
        };
    };

    const getLog = async () => {
        if (!taskDetail || !taskDetail.id) {
            return;
        }
        const params = {
            taskId: data?.id,
            taskManagerId: taskDetail.id,
            currentPage: current,
            place: current == FIRST_CURRENT ? -1 : logInfo?.place,
        };
        setSpin(true);
        if (data?.status == TASK_STATUS.RUNNING) {
            const res = await stream.getTaskManagerLog(params);
            if (res && res.code == 1) {
                const nextLogInfo = prepareLogInfo(res.data);
                if (nextLogInfo) {
                    setLogInfo(nextLogInfo);
                }
            }
            setSpin(false);
        } else {
            const res = await stream.getTaskLogs({
                taskId: data?.id,
                taskManagerId: taskDetail.id,
            });
            if (res && res.code == 1) {
                setLogInfo(res.data);
            }
            setSpin(false);
        }
    };

    const refreshData = () => {
        getLog();
    };

    const onPaginationChange = (current: number) => {
        setCurrent(current);
    };

    useEffect(() => {
        getLog();
    }, [current]);

    const isRunning = data?.status == TASK_STATUS.RUNNING;
    const isFail = data?.status == TASK_STATUS.RUN_FAILED || data?.status == TASK_STATUS.STOPED;

    return (
        <div style={{ height: '100%' }}>
            <div className="c-taskManage__log__header">
                <Breadcrumb>
                    <Breadcrumb.Item
                        onClick={() => {
                            toTaskDetail(null);
                        }}
                    >
                        <a>Task List</a>
                    </Breadcrumb.Item>
                    <Breadcrumb.Item>{taskDetail?.id}</Breadcrumb.Item>
                </Breadcrumb>
                <Tooltip title="刷新数据">
                    <SyncOutlined
                        type="sync"
                        spin={spin}
                        onClick={refreshData}
                        style={{
                            cursor: 'pointer',
                            color: '#94A8C6',
                        }}
                    />
                </Tooltip>
            </div>
            {isFail && (
                <div className="c-taskManage__log__downLoadLog">
                    当前Task Manager完整日志下载地址：
                    {logInfo?.downLoadLog ? (
                        <a href={`taier/${logInfo.downLoadLog}`}>logDownload</a>
                    ) : (
                        <span>日志加载中...</span>
                    )}
                </div>
            )}
            <div className="c-taskManage__log__editor">
                <Editor
                    sync
                    style={{ height: '100%' }}
                    value={logInfo?.engineLog ?? ''}
                    language="jsonlog"
                    options={{
                        readOnly: true,
                        minimap: {
                            enabled: false,
                        },
                    }}
                />
            </div>
            {isRunning && (
                <Pagination
                    current={current}
                    pageSize={1}
                    total={logInfo?.totalPage ?? 1}
                    showQuickJumper
                    className="c-taskManage__log__pagination"
                    onChange={onPaginationChange}
                />
            )}
        </div>
    );
}
