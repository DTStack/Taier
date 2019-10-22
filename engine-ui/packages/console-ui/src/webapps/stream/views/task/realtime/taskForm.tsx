import * as React from 'react'
import { isArray, isNumber } from 'lodash';
import {
    Form, Input,
    Radio, Modal
} from 'antd'

import { getContainer } from 'funcs';
import FolderPicker from './folderTree'
import HelpDoc from '../../helpDoc';
import { formItemLayout, TASK_TYPE, DATA_SYNC_TYPE } from '../../../comm/const'

const FormItem = Form.Item
const RadioGroup = Radio.Group

class TaskFormModal extends React.Component<any, any> {
    state: any = {
        taskType: TASK_TYPE.SQL,
        selectedRes: [],
        createModel: DATA_SYNC_TYPE.GUIDE
    }

    _update = false; // update flag;

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const newTask = nextProps.taskInfo
        const oldTask = this.props.taskInfo
        if (newTask.id !== oldTask.id) {
            this.setState({
                taskType: newTask.taskType,
                createModel: newTask.createModel || DATA_SYNC_TYPE.GUIDE
            })
        }
    }

    taskTypeChange = (e: any) => {
        this.props.form.resetFields(['resourceIdList'])
        this.setState({ taskType: e.target.value })
    }

    onChangeResList = (value: any) => {
        if (isArray(value)) {
            const newVals: any = [...value]

            const { taskInfo } = this.props
            const resourceList = isArray(taskInfo.resourceList)
                ? taskInfo.resourceList.map((item: any) => item.id) : []

            newVals.forEach((v: any, i: any) => {
                if (isNumber(v) && resourceList.indexOf(v) < 0) {
                    resourceList.push(v)
                }
            })

            this.setState({
                selectedRes: resourceList
            })
        } else {
            this.setState({
                selectedRes: value
            })
        }
        this.props.form.setFieldsValue({
            resourceIdList: value
        })
        this._update = true;
    }

    submit = (e: any) => {
        e.preventDefault()
        const { handOk, form, taskInfo } = this.props
        const { selectedRes } = this.state
        const task = this.props.form.getFieldsValue()
        const fileds: any = ['name', 'nodePid']
        task.computeType = 0 // 实时任务

        if (task.taskType === TASK_TYPE.MR) { // 如果是MR任务，资源为必填项
            fileds.push('resourceIdList')
        }

        if (selectedRes && !Array.isArray(selectedRes)) {
            task.resourceIdList = [selectedRes]
        } else {
            task.resourceIdList = selectedRes
        }

        // 如果为进行过更新操作，忽略资源列表
        if (!this._update) {
            task.resourceIdList = taskInfo && taskInfo.resourceList
                ? taskInfo.resourceList.map((res: any) => res.id) : []
        }

        this.props.form.validateFields(fileds, (err: any) => {
            if (!err) {
                handOk(task).then((err: boolean) => {
                    if (!err) {
                        this.setState({ taskType: TASK_TYPE.SQL })
                        form.resetFields()
                    }
                })
            }
        });
    }

    cancle = () => {
        const { handCancel, form } = this.props
        this.setState({ taskType: TASK_TYPE.SQL }, () => {
            handCancel()
            form.resetFields()
        })
    }

    checkNotDir (rule: any, values: any, callback: any) {
        const { resRoot } = this.props;

        let loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                let flag = false;
                if (isArray(values)) {
                    flag = values.indexOf(node.id) > -1
                } else {
                    flag = values === node.id
                }

                if (flag && node.type === 'folder') {
                    const error = '请选择具体文件, 而非文件夹';
                    callback(error);
                } else {
                    loop(node.children || []);
                }
            });
        };
        loop(resRoot);
        callback();
    }

    render () {
        const {
            form, ayncTree, visible, taskTypes,
            taskInfo, taskRoot, resRoot, operation
        } = this.props

        const { getFieldDecorator } = form
        const { taskType, createModel } = this.state

        const isEdit = operation && operation.indexOf('EDIT') > -1
        const title = isEdit ? '编辑任务' : '创建任务'

        const taskRadios = taskTypes && taskTypes.map((item: any) =>
            <Radio key={item.key} value={item.key}>{item.value}</Radio>
        )

        const resourceIds = taskInfo && taskInfo.resourceList
            ? taskInfo.resourceList.map((res: any) => res.id) : []

        const resouceNames = taskInfo && taskInfo.resourceList
            ? taskInfo.resourceList.map((res: any) => res.resourceName) : []

        const defaultRes = resRoot[0] && resRoot[0].children &&
            resRoot[0].children.lenght > 0 ? resourceIds : resouceNames;

        const isDataCollection = taskType == TASK_TYPE.DATA_COLLECTION;
        const isShowResource = TASK_TYPE.MR === taskType;
        const rowFix = {
            rows: 4
        }
        return (
            <div id="JS_task_modal_realtime">
                <Modal
                    title={title}
                    visible={visible}
                    onOk={this.submit}
                    onCancel={this.cancle}
                    getContainer={() => getContainer('JS_task_modal_realtime')}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="任务名称"
                            hasFeedback
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true, message: '任务名称不可为空！'
                                }, {
                                    pattern: /^[A-Za-z0-9_]+$/,
                                    message: '任务名称只能由字母、数字、下划线组成!'
                                }, {
                                    max: 64,
                                    message: '任务名称不得超过64个字符！'
                                }],
                                initialValue: taskInfo ? taskInfo.name : ''
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="任务类型"
                        >
                            {getFieldDecorator('taskType', {
                                rules: [],
                                initialValue: taskInfo.taskType || TASK_TYPE.SQL
                            })(
                                <RadioGroup onChange={this.taskTypeChange} disabled={isEdit}>
                                    {taskRadios}
                                </RadioGroup>
                            )}
                            <HelpDoc doc="newStreamTask" />
                        </FormItem>
                        {isDataCollection && (
                            <FormItem
                                {...formItemLayout}
                                label="配置模式"
                            >
                                {getFieldDecorator('createModel', {
                                    rules: [],
                                    initialValue: createModel
                                })(
                                    <RadioGroup onChange={
                                        (e: any) => {
                                            this.setState({ createModel: e.target.value })
                                        }
                                    } disabled={isEdit}>
                                        <Radio key={DATA_SYNC_TYPE.GUIDE} value={DATA_SYNC_TYPE.GUIDE}>向导模式</Radio>
                                        <Radio key={DATA_SYNC_TYPE.SCRIPT} value={DATA_SYNC_TYPE.SCRIPT}>脚本模式</Radio>
                                    </RadioGroup>
                                )}
                            </FormItem>
                        )}
                        {isShowResource && (
                            <FormItem
                                {...formItemLayout}
                                label="资源"
                            >
                                {getFieldDecorator('resourceIdList', {
                                    rules: [{
                                        required: taskType === TASK_TYPE.MR,
                                        message: '请选择资源！'
                                    },
                                    {
                                        validator: this.checkNotDir.bind(this)
                                    }],
                                    initialValue: defaultRes
                                })(
                                    <FolderPicker
                                        isPicker
                                        id="resourceIdList"
                                        placeholder="请选择资源"
                                        onChange={this.onChangeResList}
                                        multiple={taskType !== TASK_TYPE.MR}
                                        treeData={resRoot}
                                        loadData={ayncTree}
                                    />
                                )}
                            </FormItem>
                        )}
                        <FormItem
                            {...formItemLayout}
                            label="mainClass"
                            style={{ display: taskType === TASK_TYPE.MR ? 'block' : 'none' }}
                        >
                            {getFieldDecorator('mainClass', {
                                rules: [{
                                    required: taskType === TASK_TYPE.MR,
                                    message: '请输入mainClass'
                                }],
                                initialValue: taskInfo && taskInfo.mainClass
                            })(
                                <Input placeholder="请输入mainClass" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="参数"
                            style={{ display: taskType === TASK_TYPE.MR ? 'block' : 'none' }}
                        >
                            {getFieldDecorator('exeArgs', {
                                rules: [{}],
                                initialValue: taskInfo && taskInfo.exeArgs
                            })(
                                <Input placeholder="请输入任务参数" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="选择存储位置"
                            hasFeedback
                        >
                            {getFieldDecorator('nodePid', {
                                rules: [{
                                    required: true, message: '必须选择存储位置！'
                                }],
                                initialValue: taskInfo && taskInfo.nodePid ? taskInfo.nodePid : taskRoot && taskRoot[0] ? taskRoot[0].id : ''
                            })(
                                <FolderPicker
                                    id="nodePid"
                                    placeholder="请选择存储位置"
                                    isPicker
                                    isFolderPicker
                                    treeData={taskRoot}
                                    loadData={ayncTree}
                                />
                            )}
                        </FormItem>
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
                                initialValue: taskInfo ? taskInfo.taskDesc : ''
                            })(
                                <Input
                                    type="textarea"
                                    // rows={4}
                                    {...rowFix}
                                />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        )
    }
}
const wrappedForm = Form.create<any>()(TaskFormModal);
export default wrappedForm
