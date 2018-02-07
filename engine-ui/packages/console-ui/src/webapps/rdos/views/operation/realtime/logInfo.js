import React from 'react'

import Editor from '../../../components/code-editor'

export default function LogInfo(props) {
    const log = props.logInfo
    const taskLogs = log['all-exceptions']
    const logList = taskLogs && taskLogs.map((item) => {
        return (
            <section key={item.jobid}>
                <h3 className="log-title bd-bottom">{item.location}</h3>
                <Editor value={item.exception} />
            </section>
        )
    })
    return (
        <div className="task-log-info">
            <article>
                <h2 className="log-title">提交日志</h2>
                <Editor className="bd" value={log.msg_info} />
            </article>
            <article style={{ display: log['root-exception'] ? 'block' : 'none' }}>
                <h2 className="log-title">运行日志</h2>
                <Editor className="bd" value={log['root-exception']} />
                {logList}
            </article>
        </div>
    )
}

