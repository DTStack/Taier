import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, message } from 'antd';

import ajax from '../../../api';
import { getContainer } from 'funcs';

import {
    modalAction, workbenchAction
} from '../../../store/modules/offlineTask/actionType';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import {
    formItemLayout, MENU_TYPE
} from '../../../comm/const'

import FolderPicker from './folderTree';

const FormItem = Form.Item;

class CloneTaskForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const {
            defaultData
        } = this.props;
        console.log(defaultData)
        const cloneName = defaultData.name;
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="任务名称"
                    hasFeedback
                >
                    {getFieldDecorator('taskName', {
                        rules: [{
                            required: true, message: '任务名称不可为空！'
                        }, {
                            max: 64,
                            message: '任务名称不得超过64个字符！'
                        }, {
                            pattern: /^[A-Za-z0-9_]+$/,
                            message: '任务名称只能由字母、数字、下划线组成!'
                        }],
                        initialValue: `${cloneName}_copy`
                    })(
                        <Input placeholder="请输入任务名称" autoComplete="off"/>
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="存储位置"
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: defaultData.nodePid
                    })(
                        <FolderPicker
                            ispicker
                            id="Task_dev_catalogue"
                            type={MENU_TYPE.TASK_DEV}
                            treeData={this.props.treeData}
                            onChange={this.handleSelectTreeChange.bind(this)}
                            defaultNode={
                                this.getFolderName(defaultData.nodePid)
                            }
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
                        initialValue: defaultData.taskDesc
                    })(
                        <Input type="textarea" rows={4} placeholder="请输入任务描述" />
                    )}
                </FormItem>

                <FormItem style={{ display: 'none' }}>
                    {getFieldDecorator('taskId', {
                        initialValue: defaultData.id
                    })(
                        <Input type="hidden"></Input>
                    )}
                </FormItem>

            </Form>
        )
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName (id: any) {
        const { treeData } = this.props;
        let name: any;

        let loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                if (node.id === id) {
                    name = node.name;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop([treeData]);

        return name;
    }
}

const CloneTaskFormWrapper = Form.create<any>()(CloneTaskForm);

class CloneTaskModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }

    handleSubmit () {
        const {
            defaultData, confirmClone
        } = this.props;
        const form = this.form;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })

                const handRes = (isSuccess: any) => {
                    this.setState({
                        loading: false
                    })
                    if (isSuccess) {
                        message.success('任务克隆成功！')
                        form.resetFields();
                        this.closeModal();
                    }
                }
                confirmClone(values, defaultData).then(handRes);
            }
        })
    }

    handleCancel () {
        this.closeModal();
    }

    closeModal () {
        this.props.toggleCloneTask();
        this.props.emptyModalDefault();
        this.dtcount++;
        this.setState({
            loading: false
        })
    }

    render () {
        const {
            isModalShow, taskTreeData, resourceTreeData,
            defaultData, workflow } = this.props;
        const { loading } = this.state;

        return (
            <div id="JS_task_modal">
                <Modal
                    title="克隆任务"
                    key={this.dtcount}
                    visible={isModalShow}
                    maskClosable={false}
                    getContainer={() => getContainer('JS_task_modal')}
                    footer={[
                        <Button key="back"
                            size="large"
                            onClick={this.handleCancel}
                        >取消</Button>,
                        <Button key="submit"
                            type="primary"
                            size="large"
                            loading={loading}
                            onClick={this.handleSubmit.bind(this)}
                        > 确认 </Button>
                    ]}
                    onCancel={this.handleCancel}
                >
                    <CloneTaskFormWrapper
                        ref={(el: any) => this.form = el}
                        treeData={taskTreeData}
                        resTreeData={resourceTreeData}
                        defaultData={defaultData}
                        createOrigin={workflow}
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        isModalShow: state.offlineTask.modalShow.cloneTask,
        workflow: state.offlineTask.workflow,
        taskTreeData: state.offlineTask.taskTree,
        currentTab: state.offlineTask.workbench.currentTab,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        resourceTreeData: state.offlineTask.resourceTree,
        taskTypes: state.offlineTask.comm.taskTypes
    }
},
(dispatch: any) => {
    const benchActions = workbenchActions(dispatch)

    return {
        toggleCloneTask: function () {
            benchActions.toggleCloneTask();
        },

        updateWorkflow: function (workflow: any) {
            benchActions.updateWorkflow(workflow)
        },

        createWorkflowTask: function (data: any) {
            return benchActions.createWorkflowTask(data)
        },
        /**
         * @description 新建或编辑
         * @param {any} params 表单参数
         * @param {boolean} isEditExist 是否编辑
         * @param {any} 修改前的数据
        */
        confirmClone: function (params: any, defaultData: any) {
            return ajax.cloneTask(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        ajax.getOfflineTaskDetail({
                            id: res.data.id // 需后端返回克隆之后的任务id
                        }).then((res: any) => {
                            if (res.code === 1) {
                                dispatch({
                                    type: workbenchAction.LOAD_TASK_DETAIL,
                                    payload: res.data
                                });
                                dispatch({
                                    type: workbenchAction.OPEN_TASK_TAB,
                                    payload: res.data.id
                                });
                            }
                        });
                        benchActions.loadTreeNode(defaultData.nodePid, MENU_TYPE.TASK_DEV)
                        return true;
                    }
                });
        },

        emptyModalDefault () {
            dispatch({
                type: modalAction.EMPTY_MODAL_DEFAULT
            });
        }
    }
})(CloneTaskModal);
