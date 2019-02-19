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
    formItemLayout, MENU_TYPE
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
        taskTypes: state.offlineTask.comm.taskTypes
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
        confirmCloneToWorkflow: function (params, defaultData) {
            return ajax.cloneTaskToWorkflow(params)
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
            loading: false,
            workFlowLists: [], // 工作流列表
            workFlowNodes: []
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    componentDidMount () {
        // this.getWorkflowLists();
    }
    // 获取工作流
    getWorkflowLists = () => {
        ajax.getWorkflowLists().then(res => {
            if (res.code === 1) {
                this.setState({
                    workFlowLists: res.data
                })
            }
        })
    }
    workFlowListsOption () {
        const { workFlowLists } = this.state;
        return workFlowLists.map(item => {
            return <Option
                key={item.id}
                value={item.id}
            >
                {item.name}
            </Option>
        })
    }
    // 选择工作流
    selectWorkflow (value) {
        this.setState({
            workFlowNodes: []
        }, () => {
            this.getTaskWorkflowNodes({
                workFlowId: value
            })
        })
    }
    /**
     * 获取工作流下节点
     */
    getTaskWorkflowNodes = (params) => {
        ajax.getTaskWorkflowNodes(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    workFlowNodes: res.data
                })
            }
        })
    }
    workflowNodesOption () {
        const { workFlowNodes } = this.state;
        return workFlowNodes.map(item => {
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
            defaultData, confirmCloneToWorkflow
        } = this.props;
        const { validateFields, resetFields } = this.props.form;
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
                confirmCloneToWorkflow(values, defaultData).then(handRes);
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
        console.log(defaultData)
        // const cloneName = defaultData.name;

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
                            {getFieldDecorator('workFlowId', {
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
                                    onChange={this.selectWorkflow.bind(this)}
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
                            {getFieldDecorator('nodesName', {
                                rules: [{
                                    required: true,
                                    message: '节点名称不可为空！'
                                }]
                            })(
                                <Select
                                    placeholder="请选择节点名称"
                                    showSearch
                                >
                                    {this.workflowNodesOption()}
                                </Select>
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
