import * as React from 'react';
import { Form, Input, Radio, message } from 'antd';
import { connect } from 'react-redux';

import { matchTaskParams, isProjectCouldEdit, checkNotDir } from '../../../comm'
import { formItemLayout, TASK_TYPE, MENU_TYPE } from '../../../comm/const';
import { workbenchAction } from '../../../store/modules/offlineTask/actionType';

import FolderPicker from './folderTree';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

/**
 * TODO 当前的表单逻辑需要重构，目前代码的维护性比较差
 */
class NormalTaskForm extends React.Component<any, any> {
    checkResource () {
        const { resTreeData, form, taskType } = this.props;
        const formData = form.getFieldsValue();
        const isPyTask = taskType === TASK_TYPE.PYTHON;
        const resourceLable = !isPyTask ? '资源' : '入口资源';
        let invalid = false;
        if (!formData.resourceIdList || formData.resourceIdList.length === 0) {
            invalid = true;
            message.error(`${resourceLable}不可为空！`)
        } else if (formData.resourceIdList && formData.resourceIdList.length > 0 && checkNotDir(formData.resourceIdList[0], resTreeData)) {
            if (formData.refResourceIdList && formData.refResourceIdList.length > 0 && !checkNotDir(formData.refResourceIdList[0], resTreeData)) {
                invalid = true;
            }
        }
        this.props.setFieldsValue({
            invalid
        })
    }

    handleResChange (value: any) {
        this.props.form.validateFields(['resourceIdList']);
        this.props.form.setFieldsValue({
            resourceIdList: value ? [value] : []
        });
        this.checkResource();
    }

    handleRefResChange = (value: any) => {
        this.props.form.setFieldsValue({
            refResourceIdList: value ? [value] : []
        });
        this.checkResource();
    }

    handlePathChange (value: any) {
        this.props.form.setFieldsValue({
            nodePid: value
        });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const taskData = this.props;
        const taskType = taskData.taskType;
        const { taskTypeFilter, isWorkflowNode, user, project } = this.props;

        const isPyTask = taskType === TASK_TYPE.PYTHON;
        const isMR = taskType === TASK_TYPE.MR;
        const isVirtual = taskType == TASK_TYPE.VIRTUAL_NODE;
        const isDeepLearning = taskType == TASK_TYPE.DEEP_LEARNING;
        const isPython23 = taskType == TASK_TYPE.PYTHON_23;
        const isHadoopMR = taskType == TASK_TYPE.HAHDOOPMR;
        const isScienceTask = TASK_TYPE.NOTEBOOK == taskType || TASK_TYPE.EXPERIMENT == taskType
        const isExperimentTask = TASK_TYPE.EXPERIMENT == taskType;
        const isNoteBookTask = TASK_TYPE.NOTEBOOK == taskType;
        const mainClassShow = !isPyTask && !isPython23 && !isVirtual && !isDeepLearning && !isHadoopMR && !isScienceTask;

        const optionsShow = isHadoopMR || isMR || isDeepLearning || isPython23 || isPyTask || isNoteBookTask || isDeepLearning || isScienceTask;
        const couldEdit = isProjectCouldEdit(project, user) && !isScienceTask;

        const resourceLable = !isPyTask ? '资源' : '入口资源';

        const initialRefResourceName = taskData.refResourceList && taskData.refResourceList.length > 0
            ? taskData.refResourceList.map((res: any) => res.resourceName) : [];
        return (<Form>
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
                    <RadioGroup disabled>
                        {
                            taskTypeFilter.filter((item: any) => item.value == taskType)
                                .map((task: any) => {
                                    return <Radio key={task.value} value={task.value}>{task.text}</Radio>
                                })
                        }
                    </RadioGroup>
                )}
            </FormItem>
            {
                (!isVirtual && !isExperimentTask) &&
                <FormItem
                    {...formItemLayout}
                    label={resourceLable}
                >
                    {getFieldDecorator('resourceIdList', {
                        rules: [{
                            required: true, message: '请选择关联资源'
                        }],
                        initialValue: taskData.resourceList.length
                            ? [taskData.resourceList[0].id] : undefined
                    })(
                        <Input disabled={!couldEdit} type="hidden" />
                    )}
                    <FolderPicker
                        couldEdit={couldEdit}
                        ispicker
                        isFilepicker
                        type={MENU_TYPE.RESOURCE}
                        treeData={this.props.resTreeData}
                        onChange={this.handleResChange.bind(this)}
                        defaultNode={taskData.resourceList.length ? taskData.resourceList[0].resourceName : ''}
                    />
                </FormItem>
            }
            {
                (isPyTask || isNoteBookTask) && <FormItem
                    {...formItemLayout}
                    label="引用资源"
                >
                    {getFieldDecorator('refResourceIdList', {
                        rules: [],
                        initialValue: initialRefResourceName
                    })(
                        <Input disabled={!couldEdit} type="hidden" />
                    )}
                    <FolderPicker
                        couldEdit={couldEdit}
                        ispicker
                        isFilepicker
                        key={`refResourceIdList${initialRefResourceName}`}
                        allowClear={true}
                        treeData={this.props.resTreeData}
                        onChange={this.handleRefResChange.bind(this)}
                        defaultNode={initialRefResourceName}
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
                    label="任务参数"
                >
                    {getFieldDecorator('options', {
                        initialValue: taskData.options,
                        rules: []
                    })(
                        <Input disabled={!couldEdit} placeholder="请输入任务参数" />
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
                        <Input disabled={!couldEdit} type="hidden" />
                    )}
                    <FolderPicker
                        couldEdit={couldEdit}
                        type={MENU_TYPE.TASK}
                        ispicker
                        isFilepicker={false}
                        treeData={this.props.pathTreeData}
                        onChange={this.handlePathChange.bind(this)}
                        defaultNode={taskData.nodePName}
                    />
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
                    <Input.TextArea disabled={!couldEdit} rows={4} />
                )}
            </FormItem>
            <FormItem style={{ display: 'none' }}>
                {getFieldDecorator('computeType', {
                    initialValue: 1
                })(
                    <Input disabled={!couldEdit} type="hidden" />
                )}
            </FormItem>
        </Form>)
    }
}

function validValues (values: any, props: any) {
    // invalid为一个验证标记，
    // 次标记为上方任务保存按钮是否有效提供依据
    if (values.hasOwnProperty('mainClass') && values.mainClass === '') { // mainClass不可为空
        return true;
    }
    return false;
}

const NormalTaskFormWrapper = Form.create({
    onValuesChange (props: any, values: any) {
        const { setFieldsValue, taskCustomParams } = props;

        // 获取任务自定义参数
        if (values.hasOwnProperty('exeArgs')) {
            values.taskVariables = matchTaskParams(taskCustomParams, values.exeArgs)
        }
        if (values.hasOwnProperty('options')) {
            values.taskVariables = matchTaskParams(taskCustomParams, values.options)
        }
        values.invalid = validValues(values, props);
        setFieldsValue(values);
    }
})(NormalTaskForm);

class NormalTaskEditor extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        return (<div className="m-taskedit" style={{ padding: 60 }}>
            <NormalTaskFormWrapper {...this.props} />
        </div>)
    }
}

const mapState = (state: any, ownProps: any) => {
    const { offlineTask, user, project } = state
    return {
        resTreeData: offlineTask.resourceTree,
        pathTreeData: offlineTask.taskTree,
        taskCustomParams: offlineTask.workbench.taskCustomParams,
        taskTypes: offlineTask.comm.taskTypes,
        taskTypeFilter: offlineTask.comm.taskTypeFilter,
        user,
        project
    }
};

const mapDispatch = (dispatch: any) => {
    return {
        setFieldsValue (params: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }
};

export default connect(mapState, mapDispatch)(NormalTaskEditor);
