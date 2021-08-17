import React from 'react';
import { MonacoEditor } from 'molecule/esm/components';
import { editor as monacoEditor, Uri } from 'molecule/esm/monaco';
import { useEffect, useRef } from 'react';
import { IEditor, IEditorTab } from 'molecule/esm/model';
import { ENV_PARAMS } from '../common/utils/const';

/**
 * [TODO]: [#231](https://github.com/DTStack/molecule/issues/231) will resolve this problems
 */
type IStandaloneCodeEditor = any;

const defualt_sql_value = `## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## SparkContext 启动时是否记录有效 SparkConf信息,默认false
# loigConf=false

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
# executor.memory=512m

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead`;

const getUniqPath = (path: string) => {
    return Uri.parse(`file://tab/${path}`);
};

interface IEnvParams extends IEditor {
    onChange?: (tab: IEditorTab, value: string) => void;
}

export default ({ current, onChange }: IEnvParams) => {
    const editorIns = useRef<IStandaloneCodeEditor>(null);

    useEffect(() => {
        if (current && current.tab?.id !== 'createTask') {
            const model =
                monacoEditor.getModel(getUniqPath(current.tab?.data.path)) ||
                monacoEditor.createModel(
                    current.tab?.data.taskParams || defualt_sql_value,
                    'ini',
                    getUniqPath(current.tab?.data.path)
                );

            editorIns.current?.setModel(model);
        }
    }, [current?.id && current.tab?.id]);

    if (!current || !current.activeTab)
        return (
            <div style={{ textAlign: 'center', marginTop: 20 }}>
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
                    const currentValue = editorIns.current
                        .getModel(getUniqPath(current.tab?.data.path))
                        ?.getValue();

                    onChange?.(current.tab!, currentValue);
                });

                const model =
                    monacoEditor.getModel(
                        getUniqPath(current.tab?.data.path)
                    ) ||
                    monacoEditor.createModel(
                        current.tab?.data.taskParams || defualt_sql_value,
                        'ini',
                        getUniqPath(current.tab?.data.path)
                    );

                editorInstance.setModel(model);
            }}
        />
    );
};
