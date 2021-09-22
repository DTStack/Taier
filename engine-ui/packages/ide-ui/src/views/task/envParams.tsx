import React, { useEffect, useRef }  from 'react';
import { MonacoEditor } from 'molecule/esm/components';
import { editor as monacoEditor, Uri } from 'molecule/esm/monaco';
import { IEditor, IEditorTab } from 'molecule/esm/model';
import type { editor } from 'molecule/esm/monaco';
import { ENV_PARAMS } from '../common/utils/const';

const getUniqPath = (path: string) => {
    return Uri.parse(`file://tab/${path}`);
};

interface IEnvParams extends IEditor {
    onChange?: (tab: IEditorTab, value: string) => void;
}

export default ({ current, onChange }: IEnvParams) => {
    const editorIns = useRef<editor.IStandaloneCodeEditor>();

    useEffect(() => {
        if (current && typeof(current.tab?.id) === 'number') {
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

    if (!current || !current.activeTab)
        return (
            <div 
                style={{
                    marginTop: 10,
                    textAlign: 'center',
                    color: '#fff',
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
