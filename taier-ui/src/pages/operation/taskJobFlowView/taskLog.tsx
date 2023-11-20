/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useMemo } from 'react';
import type { PaginationProps } from 'antd';
import { Col,Pagination, Row } from 'antd';

import Editor from '@/components/editor';
import { createLinkMark, createLog } from '@/services/taskResultService';
import { formatDateTime, prettierJSONstring } from '@/utils';

const defaultEditorStyle: React.CSSProperties = { height: '300px' };

function wrappTitle(title: string) {
    return `====================${title}====================`;
}

function showTaskInfo(obj: any) {
    return Object.keys(obj)
        .map((key: any) => {
            return `${key}: ${obj[key]}`;
        })
        .join('\n');
}

function resolveSteps(stepArr: any) {
    let stepText = '';
    stepArr.forEach((item: any) => {
        stepText += `${formatDateTime(Number(item.info.startTime))} ${item.step_status} ${
            item.sequence_id
        } Step Name: ${item.name} Data Size: ${item.info.hdfs_bytes_written ? item.info.hdfs_bytes_written : 0}
            Duration: ${(item.info.endTime - item.info.startTime) / 1000 / 60} mins Waiting: ${
            item.exec_wait_time
        } seconds\n`;
    });
    return stepText;
}

function resoveApplogs(stepArr: any) {
    let appLogs = '';
    stepArr.forEach((item: any) => {
        appLogs += `${item.info.yarn_application_id ? item.info.yarn_application_id : ''}\n`;
    });
    return appLogs;
}

interface ILogInfoProps {
    /**
     * 支持 `JSON.parse` 的字符串
     */
    log?: string;
    /**
     * 当前 sql
     */
    sqlText?: string;
    /**
     * 数据同步任务指标字段展示
     */
    syncLog?: string;
    /**
     * 下载地址
     */
    downLoadUrl?: string;
    /**
     * 同步任务的属性
     */
    syncJobInfo?: { execTime: string; readNum: number; writeNum: number; dirtyPercent: number };
    /**
     * 日志下载地址
     */
    downloadLog?: string;
    subNodeDownloadLog?: string;
    page?: { current: number; total: number };
    onChangePage?: PaginationProps['onChange'];
    height?: string;
}

export default function LogInfo(props: ILogInfoProps) {
    const { syncLog, sqlText, downLoadUrl } = props;
    const logText = useMemo(() => {
        /**
         * 这里要多加一些空格后缀，不然codemirror计算滚动的时候会有问题
         */
        const safeSpace = ' ';
        let text = '';
        try {
            // first to render syncSql
            if (syncLog) {
                text = `${text}${wrappTitle('数据同步指标日志')}\n${syncLog}`;
            }
            // then to render sqlInfo or engineLog
            const log: Record<string, any> = props.log
                ? JSON.parse(props.log.replace(/\n/g, '\\n').replace(/\r/g, '\\r').replace(/\t/g, '\\t'))
                : {};
            const errors = log['all-exceptions'] || '';
            const { engineLogErr } = log;
            const flinkLog = errors;

            const appLogs = engineLogErr && `${wrappTitle('appLogs')}\n${engineLogErr}\n`;
            if (props.downloadLog) {
                text = `完整日志下载地址：${createLinkMark({
                    href: props.downloadLog,
                    download: '',
                })}\n`;
            }
            if (props.subNodeDownloadLog) {
                Object.entries(props.subNodeDownloadLog).forEach(([key, value]) => {
                    text = `${text} ${key}：${createLinkMark({
                        href: value,
                        download: '',
                    })}\n`;
                });
            }
            if (log.msg_info) {
                text = `${text}${wrappTitle('基本日志')}\n${createLog(log.msg_info, 'info')} ${safeSpace} \n`;
                if (log.taskInfo && log.taskInfo.taskType === 'Kylin') {
                    if (log.steps) {
                        text = `${text}${wrappTitle('appLogs')}\n${resoveApplogs(log.steps)} ${safeSpace} \n`;
                        text = `${text}${wrappTitle('Kylin日志')}\n${resolveSteps(log.steps)} ${safeSpace} \n`;
                    }
                    text = `${text}${wrappTitle('Kylin日志')}\n${wrappTitle('任务信息')}\n${showTaskInfo(
                        log.taskInfo
                    )} ${safeSpace} \n`;
                }
            }

            if (log.perf) {
                text = `${text}\n${wrappTitle('性能指标')}\n${createLog(log.perf, 'warning')}${safeSpace} \n`;
            }
            /**
             * 数据增量同步配置信息
             */
            if (log.increInfo) {
                text = `${text}\n${wrappTitle('增量标志信息')}\n${createLog(log.increInfo, 'info')}${safeSpace} \n`;
            }

            if (flinkLog || log['root-exception']) {
                text = `${text}\n\n${wrappTitle('Flink日志')} \n${createLog(flinkLog, 'error')} \n ${
                    createLog(log['root-exception'], 'error') || ''
                }`;
            }

            if (appLogs) {
                text = `${text} \n${createLog(appLogs, 'error')} \n`;
            }

            if (log.msg_info) {
                let logSql = log.sql;
                if (logSql && typeof logSql === 'object') {
                    logSql = JSON.stringify(logSql, null, 2);
                }
                if (logSql) {
                    text = `${text}${wrappTitle('任务信息')}\n${createLog(logSql, 'info')} \n`;
                }
            }
            if (Array.isArray(log.ruleLogList) && log.ruleLogList.length > 0) {
                for (const logInfo of log.ruleLogList) {
                    text = `${text}\n${wrappTitle('')}\n${createLog(logInfo, 'info')} ${safeSpace} \n`;
                }
                text = `${text}${wrappTitle('')}\n${safeSpace} \n`;
            }

            if (downLoadUrl) {
                text = `${text}完整日志下载地址：${createLinkMark({
                    href: downLoadUrl,
                    download: '',
                })}\n`;
            }

            // last to render sqlText
            if (sqlText) {
                text = `${text}${wrappTitle('任务信息')}\n${prettierJSONstring(sqlText)}`;
            }
        } catch (e: any) {
            text = `${createLog(props.log || '', 'error')}`;
        }

        return text;
    }, [props.log]);

    const editorStyle = { ...defaultEditorStyle, height: props.height };

    return (
        <div>
            {props.syncJobInfo ? (
                <Row style={{ marginBottom: '14px' }}>
                    <p>运行时长：{props.syncJobInfo.execTime}秒</p>
                    <p>
                        <span>读取数据：{props.syncJobInfo.readNum}条</span>
                        &nbsp;&nbsp;
                        <span>写入数据：{props.syncJobInfo.writeNum}条</span>
                        &nbsp;&nbsp;
                        <span>脏数据：{props.syncJobInfo.dirtyPercent}%</span>
                        &nbsp;&nbsp;
                        {/* <span><Link to={`/data-manage/dirty-data/table/${syncJobInfo.tableId}`}>查看脏数据</Link></span> */}
                    </p>
                </Row>
            ) : (
                ''
            )}
            {Boolean(props.page?.total) && (
                <Row>
                    <div
                        style={{
                            float: 'right',
                            display: 'flex',
                            marginBottom: '8px',
                        }}
                    >
                        <span>历史运行次数：</span>
                        <Pagination
                            size="small"
                            total={props.page?.total}
                            current={props.page?.current}
                            pageSize={1}
                            onChange={props.onChangePage}
                        />
                    </div>
                </Row>
            )}
            <Row style={editorStyle}>
                <Col span={24}>
                    <Editor
                        style={{ height: editorStyle.height }}
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
                </Col>
            </Row>
        </div>
    );
}
