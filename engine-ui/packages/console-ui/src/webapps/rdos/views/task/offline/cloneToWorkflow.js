import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, message, Select } from 'antd';

import ajax from '../../../api';
import { getContainer } from 'funcs';

import {
    modalAction, workbenchAction
} from '../../../store/modules/offlineTask/actionType';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import {
    formItemLayout, MENU_TYPE, TASK_TYPE
} from '../../../comm/const'

// import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Option;
@connect(state => {
    return {
        isModalShow: state.offlineTask.modalShow.cloneToWorkflow,
        workflow: state.offlineTask.workflow,
        taskTreeData: state.offlineTask.taskTree,
        currentTab: state.offlineTask.workbench.currentTab,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        resourceTreeData: state.offlineTask.resourceTree,
        taskTypes: state.offlineTask.comm.taskTypes,
        tabs: state.offlineTask.workbench.tabs
    }
}, dispatch => {
    const benchActions = workbenchActions(dispatch)

    return {
        toggleCloneToWorkflow: function () {
            benchActions.toggleCloneToWorkflow();
        },

        updateWorkflow: function (workflow) {
            benchActions.updateWorkflow(workflow)
        },

        createWorkflowTask: function (data) {
            return benchActions.createWorkflowTask(data)
        },

        /**
         * @description 新建或编辑
         * @param {any} params 表单参数
         * @param {boolean} isEditExist 是否编辑
         * @param {any} 修改前的数据
        */
        confirmCloneToWorkflow: function (params, defaultData, coordsExtra) {
            return ajax.cloneTaskToWorkflow({
                ...params,
                coordsExtra
            })
                .then(res => {
                    if (res.code === 1) {
                        ajax.getOfflineTaskDetail({
                            id: res.data.id // 需后端返回克隆之后的任务id
                        }).then(res => {
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
                        // 重新刷新克隆之后的工作流
                        // ajax.getOfflineTaskDetail({
                        //     id: res.data.flowId
                        // }).then(res => {
                        //     if (res.code === 1) {
                        //         dispatch({
                        //             type: workbenchAction.LOAD_TASK_DETAIL,
                        //             payload: res.data
                        //         });
                        //     }
                        // })
                        benchActions.reloadTaskTab(res.data.flowId)
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
})
class CloneToWorkflowModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            loading: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    workFlowListsOption () {
        const { taskTreeData } = this.props;
        const workFlowTreeData = taskTreeData && taskTreeData.children && taskTreeData.children.filter(item => item.taskType === TASK_TYPE.WORKFLOW);
        return workFlowTreeData && workFlowTreeData.map(item => {
            return <Option
                key={item.id}
                value={item.id}
            >
                {item.name}
            </Option>
        })
    }
    handleSubmit () {
        const {
            defaultData, confirmCloneToWorkflow, tabs
        } = this.props;
        const { validateFields, resetFields } = this.props.form;
        const coordsExtra = {
            'vertex': true,
            'edge': false,
            'x': 10,
            'y': 10,
            'value': null
        }
        // 获取克隆位置的工作流id
        const { getFieldValue } = this.props.form;
        const selectWorkFlowId = getFieldValue('flowId');
        // 选中工作流节点
        const selectFlow = tabs && tabs.filter(item => {
            return item.id === selectWorkFlowId
        })
        // 选中工作流子节点
        const selectFlowNodes = tabs && tabs.filter(item => {
            return item.flowId === selectWorkFlowId
        })
        const notSyncedFlow = selectFlow[0] && selectFlow[0].notSynced;
        const notSyncedFlowNodes = selectFlowNodes && selectFlowNodes.some(item => {
            return item.notSynced == true
        })
        validateFields((err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })

                const handRes = (isSuccess) => {
                    this.setState({
                        loading: false
                    })
                    if (isSuccess) {
                        message.success('任务克隆至工作流成功！')
                        resetFields();
                        this.closeModal();
                    }
                }
                if (selectFlow.length === 0) { // 选中工作流与tabs数据无关
                    confirmCloneToWorkflow(values, defaultData, coordsExtra).then(handRes);
                } else {
                    if (notSyncedFlow || notSyncedFlowNodes) {
                        message.warning('工作流任务未保存，请先保存工作流任务!')
                        this.setState({
                            loading: false
                        })
                    } else {
                        confirmCloneToWorkflow(values, defaultData, coordsExtra).then(handRes);
                    }
                }
            }
        })
    }

    handleCancel () {
        this.closeModal();
    }

    closeModal () {
        this.props.toggleCloneToWorkflow();
        this.props.emptyModalDefault();
        this.dtcount++;
        this.setState({
            loading: false
        })
    }

    handleSelectTreeChange (value) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }
    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName (id) {
        const { taskTreeData } = this.props;
        let name;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if (node.id === id) {
                    name = node.name;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop([taskTreeData]);

        return name;
    }
    render () {
        const { isModalShow, defaultData } = this.props;
        const { loading } = this.state;
        const { getFieldDecorator } = this.props.form;
        const cloneName = defaultData && defaultData.name;
        return (
            <div id="JS_task_modal">
                <Modal
                    title="克隆至工作流"
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
                    {/* <CloneToWorkflowFormWrapper
                        ref={el => this.form = el}
                        treeData={taskTreeData}
                        resTreeData={resourceTreeData}
                        defaultData={defaultData}
                        createOrigin={workflow}
                    /> */}
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="克隆至"
                            hasFeedback
                        >
                            {getFieldDecorator('flowId', {
                                rules: [{
                                    required: true,
                                    message: '工作流名称不可为空！'
                                }]
                            })(
                                <Select
                                    placeholder="按工作流名称搜索"
                                    showSearch
                                    optionFilterProp="children"
                                    filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                >
                                    {this.workFlowListsOption()}
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="节点名称"
                            hasFeedback
                        >
                            {getFieldDecorator('taskName', {
                                rules: [{
                                    required: true, message: '节点名称不可为空！'
                                }, {
                                    max: 64,
                                    message: '节点名称不得超过64个字符！'
                                }, {
                                    pattern: /^[A-Za-z0-9_]+$/,
                                    message: '节点名称只能由字母、数字、下划线组成!'
                                }],
                                initialValue: `${cloneName}_copy`
                            })(
                                <Input placeholder="请输入节点名称" autoComplete="off"/>
                            )}
                        </FormItem>
                        {/* <FormItem
                            {...formItemLayout}
                            label="存储位置"
                        >
                            {getFieldDecorator('nodePid', {
                                rules: [{
                                    required: true, message: '存储位置必选！'
                                }],
                                initialValue: defaultData && defaultData.nodePid
                            })(
                                <FolderPicker
                                    ispicker
                                    id="Task_dev_catalogue"
                                    type={MENU_TYPE.TASK_DEV}
                                    treeData={taskTreeData}
                                    onChange={this.handleSelectTreeChange.bind(this)}
                                    defaultNode={
                                        this.getFolderName(defaultData && defaultData.nodePid)
                                    }
                                />
                            )}
                        </FormItem> */}

                        <FormItem style={{ display: 'none' }}>
                            {getFieldDecorator('taskId', {
                                initialValue: defaultData && defaultData.id
                            })(
                                <Input type="hidden"></Input>
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        )
    }
}

export default Form.create()(CloneToWorkflowModal);
