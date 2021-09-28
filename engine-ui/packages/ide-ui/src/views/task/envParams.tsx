import React, { useEffect, useRef } from 'react';
import { MonacoEditor } from '@dtinsight/molecule/esm/components';
import { editor as monacoEditor, Uri } from '@dtinsight/molecule/esm/monaco';
import { IEditor, IEditorTab } from '@dtinsight/molecule/esm/model';
import type { editor } from '@dtinsight/molecule/esm/monaco';
import {
    CREATE_TASK_PREFIX,
    EDIT_FOLDER_PREFIX,
    EDIT_TASK_PREFIX,
    ENV_PARAMS,
} from '../common/utils/const';

const getUniqPath = (path: string) => {
    return Uri.parse(`file://tab/${path}`);
};

interface IEnvParams extends IEditor {
    onChange?: (tab: IEditorTab, value: string) => void;
}

export default ({ current, onChange }: IEnvParams) => {
    const editorIns = useRef<editor.IStandaloneCodeEditor>();

    useEffect(() => {
        if (current && typeof current.tab?.id === 'number') {
            const model =
                monacoEditor.getModel(getUniqPath(current.tab?.data.path)) ||
                monacoEditor.createModel(
                    current.tab?.data.taskParams || '',
                    'ini',
                    getUniqPath(current.tab?.data.path)
                );

            editorIns.current?.setModel(model);
        }
    }, [current?.id && current.tab?.id]);

    if (
        !current ||
        !current.activeTab ||
        current.activeTab.includes(EDIT_TASK_PREFIX) ||
        current.activeTab.includes(EDIT_FOLDER_PREFIX) ||
        current.activeTab.includes(CREATE_TASK_PREFIX)
    )
        return (
            <div
                style={{
                    marginTop: 10,
                    textAlign: 'center',
                }}
            >
                无法获取环境参数
            </div>
        );
    return (
        <MonacoEditor
            options={{
                value: '',
                language: 'ini',
                automaticLayout: true,
                minimap: {
                    enabled: false,
                },
            }}
            path={ENV_PARAMS}
            editorInstanceRef={(editorInstance) => {
                // This assignment will trigger moleculeCtx update, and subNodes update
                editorIns.current = editorInstance;

                editorInstance.onDidChangeModelContent(() => {
                    const currentValue =
                        editorIns.current?.getModel()?.getValue() || '';

                    onChange?.(current.tab!, currentValue);
                });

                const model =
                    monacoEditor.getModel(
                        getUniqPath(current.tab?.data.path)
                    ) ||
                    monacoEditor.createModel(
                        current.tab?.data.taskParams || '',
                        'ini',
                        getUniqPath(current.tab?.data.path)
                    );

                editorInstance.setModel(model);
            }}
        />
    );
};
