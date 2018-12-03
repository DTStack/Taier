import React from 'react';
import { Form, Input, Select, Radio } from 'antd';
import { connect } from 'react-redux';

import { matchTaskParams, isProjectCouldEdit } from '../../../comm'
import { formItemLayout, TASK_TYPE, MENU_TYPE, RESOURCE_TYPE } from '../../../comm/const';
import { workbenchAction } from '../../../store/modules/offlineTask/actionType';

import FolderPicker from './folderTree';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class NormalTaskForm extends React.Component {
    render () {
        const { getFieldDecorator } = this.props.form;
        const taskData = this.props;
        const taskType = taskData.taskType;
        const { taskTypes, isWorkflowNode, user, project } = this.props;

        const isMrTask = taskType === TASK_TYPE.MR;
        const isPyTask = taskType === TASK_TYPE.PYTHON;
        const isVirtual = taskType == TASK_TYPE.VIRTUAL_NODE;
        const isDeepLearning = taskType == TASK_TYPE.DEEP_LEARNING;
        const isPython23 = taskType == TASK_TYPE.PYTHON_23;
        const isMl = taskType == TASK_TYPE.ML;
        const isHadoopMR = taskType == TASK_TYPE.HAHDOOPMR;

        const acceptType = (isMl || isHadoopMR || isMrTask) ? RESOURCE_TYPE.JAR : (isPyTask || isPython23 || isDeepLearning) ? RESOURCE_TYPE.PY : '';

        const mainClassShow = !isPyTask && !isPython23 && !isVirtual && !isDeepLearning;
        const exeArgsShow = !isVirtual && !isPython23 && !isDeepLearning;
        const optionsShow = isDeepLearning || isPython23;
        const couldEdit = isProjectCouldEdit(project, user);
        return <Form>
            <FormItem
                {...formItemLayout}
                label="任务名称"
            >
                {getFieldDecorator('name', {
                    rules: [{
                        max: 64,
                        message: '任务名称不得超过20个字符！'
                    }],
                    initialValue: taskData.name
                })(
                    <Input disabled />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="任务类型"
            >
                {getFieldDecorator('taskType', {
                    rules: [{}],
                    initialValue: taskType
                })(
                    <RadioGroup disabled onChange={this.handleRadioChange}>
                        {taskTypes.map(item =>
                            <Radio key={item.key} value={item.key}>{item.value}</Radio>
                        )}
                    </RadioGroup>
                )}
            </FormItem>
            {
                !isVirtual &&
                <FormItem
                    {...formItemLayout}
                    label="资源"
                >
                    {getFieldDecorator('resourceIdList', {
                        rules: [{
                            required: true, message: '请选择关联资源'
                        }],
                        initialValue: taskData.resourceList.length
                            ? taskData.resourceList[0].id : ''
                    })(
                        <Input disabled={!couldEdit} type="hidden" ></Input>
                    )}
                    <FolderPicker
                        couldEdit={couldEdit}
                        ispicker
                        isFilepicker
                        acceptRes={acceptType}
                        type={MENU_TYPE.RESOURCE}
                        treeData={this.props.resTreeData}
                        onChange={this.handleResChange.bind(this)}
                        defaultNode={taskData.resourceList.length ? taskData.resourceList[0].resourceName : ''}
                    />
                </FormItem>
            }
            {
                mainClassShow &&
                <FormItem
                    {...formItemLayout}
                    label="mainClass"
                    hasFeedback
                >
                    {getFieldDecorator('mainClass', {
                        rules: [{
                            required: true, message: 'mainClass 不可为空！'
                        }],
                        initialValue: taskData.mainClass
                    })(
                        <Input disabled={!couldEdit} placeholder="请输入 mainClass" />
                    )}
                </FormItem>
            }
            {
                exeArgsShow && <FormItem
                    {...formItemLayout}
                    label="任务参数"
                >
                    {getFieldDecorator('exeArgs', {
                        initialValue: taskData.exeArgs
                    })(
                        <Input disabled={!couldEdit} placeholder="请输入任务参数" />
                    )}
                </FormItem>
            }
            {
                isDeepLearning && <span>
                    <FormItem
                        {...formItemLayout}
                        label="数据输入路径"
                    >
                        {getFieldDecorator('input', {
                            initialValue: taskData.input
                        })(
                            <Input disabled={!couldEdit} placeholder="请输入数据输入路径" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="模型输出路径"
                    >
                        {getFieldDecorator('output', {
                            initialValue: taskData.output
                        })(
                            <Input disabled={!couldEdit} placeholder="请输入模型输出路径" />
                        )}
                    </FormItem>
                </span>
            }
            {
                optionsShow && <FormItem
                    {...formItemLayout}
                    label="参数"
                >
                    {getFieldDecorator('options', {
                        initialValue: taskData.options
                    })(
                        <Input disabled={!couldEdit} placeholder="请输入命令行参数" />
                    )}
                </FormItem>
            }
            {
                !isWorkflowNode &&
                <FormItem
                    {...formItemLayout}
                    label="存储位置"
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: taskData.nodePid
                    })(
                        <Input disabled={!couldEdit} type="hidden"></Input>
                    )}
                    <FolderPicker
                        couldEdit={couldEdit}
                        type={MENU_TYPE.TASK}
                        ispicker
                        isFilepicker={false}
                        treeData={this.props.pathTreeData}
                        onChange={this.handlePathChange.bind(this)}
                        defaultNode={taskData.nodePName}
                    ></FolderPicker>
                </FormItem>
            }
            <FormItem
                {...formItemLayout}
                label="描述"
                hasFeedback
            >
                {getFieldDecorator('taskDesc', {
                    rules: [{
                        max: 200,
                        message: '描述请控制在200个字符以内！'
                    }],
                    initialValue: taskData.taskDesc
                })(
                    <Input disabled={!couldEdit} type="textarea" rows={4} placeholder="请输入任务描述" />
                )}
            </FormItem>
            <FormItem style={{ display: 'none' }}>
                {getFieldDecorator('computeType', {
                    initialValue: 1
                })(
                    <Input disabled={!couldEdit} type="hidden"></Input>
                )}
            </FormItem>
        </Form>
    }

    handleResChange (value) {
        this.props.form.setFieldsValue({
            resourceIdList: [value]
        });
    }

    handlePathChange (value) {
        this.props.form.setFieldsValue({
            nodePid: value
        });
    }
}

const NormalTaskFormWrapper = Form.create({
    onValuesChange (props, values) {
        const { setFieldsValue, taskCustomParams } = props;

        // invalid为一个验证标记，
        // 次标记为上方任务保存按钮是否有效提供依据
        let invalid = false
        if (values.mainClass === '') { // mainClass不可为空
            invalid = true
        }
        values.invalid = invalid

        // 获取任务自定义参数
        if (values.exeArgs !== '') {
            values.taskVariables = matchTaskParams(taskCustomParams, values.exeArgs)
        }

        setFieldsValue(values);
    }
})(NormalTaskForm);

class NormalTaskEditor extends React.Component {
    constructor (props) {
        super(props);
    }

    render () {
        return <div className="m-taskedit" style={{ padding: 60 }}>
            <NormalTaskFormWrapper {...this.props} />
        </div>
    }
}

const mapState = (state, ownProps) => {
    const { offlineTask, user, project } = state
    return {
        resTreeData: offlineTask.resourceTree,
        pathTreeData: offlineTask.taskTree,
        taskCustomParams: offlineTask.workbench.taskCustomParams,
        taskTypes: offlineTask.comm.taskTypes,
        user,
        project
    }
};

const mapDispatch = dispatch => {
    return {
        setFieldsValue (params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }
};

export default connect(mapState, mapDispatch)(NormalTaskEditor);
