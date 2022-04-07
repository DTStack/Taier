import * as React from 'react'
import { Form, Input, Radio, Select } from 'antd';
import type { ITaskParams } from '@/interface';
import Editor from '@/components/codeEditor';
import Address from './address';
import ResultTable from './resultTable';
import { formItemLayout, DATA_SOURCE_ENUM, TASK_TYPE_ENUM, DATA_SYNC_TYPE } from '@/constant';

const Api = {} as any

const { TextArea } = Input;
const FormItem = Form.Item;
const RadioGroup = Radio.Group

interface IProps {
    data: Partial<ITaskParams>;
    isShow: boolean;
}

interface IState {
    tabKey: string;
    taskTypes: ITaskType[];
}

interface ITaskType {
    key: number;
    value: string;
}

class RunCode extends React.Component<IProps, IState> {
    state: IState = { 
        tabKey: 'env',
        taskTypes: []
    }

    componentDidMount () {
        this.getTaskTypes();
    }

    componentDidUpdate (prevProps: IProps) {
        if (prevProps.isShow && !this.props.isShow) {
            this.setState({ tabKey: 'env' })
        }
    }

    getTaskTypes = () => {
        Api.getRealtimeTaskTypes().then((res: any) => {
            if (res.code === 1) {
                this.setState({ taskTypes: res?.data || [] })
            }
        })
    }

    getRunCode () {
        const { data } = this.props;
        const { taskTypes } = this.state
        const {
            taskType,
            sqlText,
            taskDesc,
            name,
            mainClass,
            exeArgs,
            resourceList = [],
            additionalResourceList = []
        } = data || {};

        switch (taskType) {
            case TASK_TYPE_ENUM.SQL:
            case TASK_TYPE_ENUM.DATA_COLLECTION: {
                return (
                    <Editor
                        sync={true}
                        style={{ height: '100%' }}
                        language={this.getEditorLanguage(taskType)}
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={sqlText}
                    />
                )
            }
            case TASK_TYPE_ENUM.MR:
            default: {
                return (
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="任务名称"
                        >
                            <Input disabled value={name} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="任务类型"
                        >
                            <RadioGroup value={taskType} disabled>
                                {taskTypes.map(({key, value}) =>
                                    <Radio key={key} value={key}>{value}</Radio>
                                )}
                            </RadioGroup>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="资源"
                        >
                            <Input disabled value={resourceList && resourceList.length ? resourceList[0].resourceName : ''} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="mainClass"
                        >
                            <Input disabled value={mainClass} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="命令行参数"
                        >
                            <Input disabled value={exeArgs} />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="附加资源"
                        >
                            <Select
                                disabled
                                mode="multiple"
                                value={Array.isArray(additionalResourceList) && additionalResourceList.map((item: any) => item.resourceName)}
                            />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="描述"
                        >
                            <TextArea disabled value={taskDesc} />
                        </FormItem>
                    </Form>
                )
            }
        }
    }

    getEditorLanguage (taskType: number) {
        switch (taskType) {
            case TASK_TYPE_ENUM.SQL: {
                return 'sql'
            }
            case TASK_TYPE_ENUM.DATA_COLLECTION: {
                return 'json'
            }
            default: {
                return 'ini'
            }
        }
    }

    getEditor (value: string) {
        return <Editor
            sync={true}
            style={{ height: '100%' }}
            language="sql"
            options={{ readOnly: true, minimap: { enabled: false } }}
            value={value}
        />
    }

    render () {
        const { tabKey } = this.state;
        const { data } = this.props;
        const { 
            id, 
            taskType, 
            originSourceType, 
            targetSourceType, 
            createModel, 
            sourceParams, 
            sinkParams, 
            sideParams, 
            taskParams 
        } = data || {};
        const isShowAddress = taskType == TASK_TYPE_ENUM.DATA_COLLECTION && originSourceType == DATA_SOURCE_ENUM.BEATS;
        const isflinkSql = taskType == TASK_TYPE_ENUM.SQL;
        const isGuideMode = createModel != DATA_SYNC_TYPE.SCRIPT;
        const isShowResultTable = taskType == TASK_TYPE_ENUM.DATA_COLLECTION && targetSourceType == DATA_SOURCE_ENUM.HIVE;

        const editorBoxStyle: React.CSSProperties = { height: 'calc(100% - 44px)' }

        return (
            <div className="m-tabs" style={{ height: '100%' }}>
                <Radio.Group style={{ padding: '0 20px 12px' }} value={tabKey} onChange={e => { this.setState({ tabKey: e.target.value }) }}>
                    <Radio.Button value="code">运行代码</Radio.Button>
                    {(isflinkSql && isGuideMode) && <>
                        <Radio.Button value="source">源表</Radio.Button>
                        <Radio.Button value="sink">结果表</Radio.Button>
                        <Radio.Button value="side">维表</Radio.Button>
                    </>}
                    {isShowResultTable && <Radio.Button value="resultTable">结果表</Radio.Button>}
                    <Radio.Button value="env">环境参数</Radio.Button>
                    {isShowAddress && <Radio.Button value="address">运行地址</Radio.Button>}
                </Radio.Group>
                {tabKey === 'code' && <div style={editorBoxStyle}>{this.getRunCode()}</div>}
                {tabKey === 'source' && <div style={editorBoxStyle}>{this.getEditor(sourceParams!)}</div>}
                {tabKey === 'sink' && <div style={editorBoxStyle}>{this.getEditor(sinkParams!)}</div>}
                {tabKey === 'side' && <div style={editorBoxStyle}>{this.getEditor(sideParams!)}</div>}
                {tabKey === 'resultTable' && <ResultTable key={id} taskId={id!} />}
                {tabKey === 'env' && (
                    <div style={editorBoxStyle}>
                        <Editor
                            sync={true}
                            style={{ height: '100%' }}
                            language="ini"
                            options={{ readOnly: true, minimap: { enabled: false } }}
                            value={taskParams}
                        />
                    </div>
                )}
                {tabKey === 'address' && <Address style={editorBoxStyle} taskId={id!} />}
            </div>
        )
    }
}

export default RunCode;
