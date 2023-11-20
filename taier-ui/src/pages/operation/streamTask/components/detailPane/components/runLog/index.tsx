import React, { useMemo,useRef, useState } from 'react';
import { Spin } from 'antd';

import stream from '@/api';
import Editor from '@/components/editor';
import { TASK_STATUS } from '@/constant';
import { IStreamJobProps } from '@/interface';
import { createLinkMark, createLog } from '@/services/taskResultService';
import './index.scss';

interface IProps {
    data: IStreamJobProps | undefined;
}

interface ILogsProps {
    jobId: string;
    logInfo: string;
    engineLog: string;
    downLoadLog: string;
    submitLog: string;
    'all-exceptions'?: {
        exception?: string;
    }[];
}

const wrappTitle = (title: string) => {
    return `====================${title}====================`;
};

function getLogType(status: TASK_STATUS | undefined) {
    switch (status) {
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.SUBMIT_FAILED:
        case TASK_STATUS.PARENT_FAILD: {
            return 'error';
        }
        case TASK_STATUS.FINISHED: {
            return 'success';
        }
        default: {
            return 'info';
        }
    }
}

export default function RunLog({ data }: IProps) {
    const [logInfo, setLogInfo] = useState<ILogsProps>({
        jobId: '',
        logInfo: '',
        engineLog: '',
        downLoadLog: '',
        submitLog: '',
    });
    const [loading, setLoading] = useState(false);
    const offset = useRef();
    const timer = useRef<number>();

    const getLog = async () => {
        if (!data?.id) return;
        if (data.status == TASK_STATUS.RUNNING) {
            setLoading(true);
            const res = await stream.getJobManagerLog({ taskId: data.id, place: offset.current });
            if (res?.code == 1 && res?.data?.place) {
                setLogInfo((info) => ({
                    ...res.data,
                    engineLog: (info.engineLog ?? '') + (res.data?.engineLog ?? ''),
                }));
                offset.current = res.data.place;
            }
            setLoading(false);
        } else {
            const res = await stream.getTaskLogs({ taskId: data.id });
            if (res?.code == 1) {
                setLogInfo(res.data || {});
            }
        }
    };

    React.useEffect(() => {
        getLog();

        if (data?.status === TASK_STATUS.RUNNING) {
            timer.current = window.setInterval(getLog, 5000);
        }

        return () => {
            if (timer.current) {
                window.clearInterval(timer.current);
            }
        };
    }, []);

    const logText = useMemo(() => {
        const log = logInfo;
        let engineLog: Partial<ILogsProps> = {};

        try {
            engineLog = log.engineLog ? JSON.parse(log.engineLog) : {};
        } catch (e) {
            engineLog = {
                'all-exceptions': [{ exception: log.engineLog }],
            };
        }

        const errors = engineLog?.['all-exceptions'] || '';

        const engineLogs =
            Array.isArray(errors) && errors.length > 0
                ? errors.map((item: any) => {
                      return `${item.exception} \n`;
                  })
                : errors;

        let logText = '';
        if (log.downLoadLog) {
            logText = `完整日志下载地址：${createLinkMark({
                href: `${log.downLoadLog}`,
                download: '',
            })}\n`;
        }
        if (log.logInfo) {
            logText = `${logText}${wrappTitle('基本日志')}\n${createLog(log.logInfo, getLogType(data?.status))}`;
        }
        if (log.submitLog) {
            logText = `${logText} \n${wrappTitle('操作日志')}\n${createLog(log.submitLog)}`;
        }
        if (engineLogs) {
            logText = `${logText} \n${wrappTitle('引擎日志')} \n ${engineLogs}`;
        }

        return logText;
    }, [logInfo, data?.status]);

    return (
        <Spin wrapperClassName="dt-loading" spinning={loading}>
            <Editor
                style={{ height: '100%' }}
                sync
                value={logText}
                language="jsonlog"
                options={{
                    readOnly: true,
                    minimap: {
                        enabled: false,
                    },
                }}
            />
        </Spin>
    );
}
