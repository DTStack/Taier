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
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';

import type { ITaskResultStates } from '@/services/taskResultService';
import taskResultService from '@/services/taskResultService';
import Editor from '../editor';

interface ILogEditorProps {
    results: ITaskResultStates;
    editor: molecule.model.IEditor;
}

export default connect(
    { results: taskResultService, editor: molecule.editor },
    ({ editor, results: { logs } }: ILogEditorProps) => {
        const { current } = editor;

        const value = useMemo(() => {
            if (current?.tab?.id && logs[current.tab.id]) {
                return logs[current.tab.id];
            }
            return '暂无日志';
        }, [logs, current?.tab?.id]);

        if (!current || !current.activeTab) {
            return (
                <div
                    style={{
                        marginTop: 10,
                        textAlign: 'center',
                    }}
                >
                    无法获取任务日志
                </div>
            );
        }

        return (
            <Editor
                language="jsonlog"
                value={value}
                sync
                options={{
                    automaticLayout: true,
                    readOnly: true,
                    wordWrap: 'on',
                    contextmenu: false,
                    scrollBeyondLastLine: true,
                    lineNumbers: 'off',
                    minimap: {
                        enabled: false,
                    },
                }}
            />
        );
    }
);
