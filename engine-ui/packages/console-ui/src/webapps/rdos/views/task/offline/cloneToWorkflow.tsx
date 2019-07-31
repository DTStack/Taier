import * as React from 'react';
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
import { getRandomInt } from '../../../../../funcs';
// import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Option;
@(connect((state: any) as any) => {
    return {
        isModalShow: state.offlineTask.modalShow.cloneToWorkflow,
        workflow: state.offlineTask.workflow,
        taskTreeData: state.offlineTask.taskTree,
        currentTab: state.offlineTask.workbench.currentTab,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        resourceTreeData: state.offlineTask.resourceTree,
        taskTypes: state.offlineTask.comm.taskTypes,
        tabs: state.offlineTask.workbench.tabs,
        workFlowLists: state.offlineTask.modalShow.workFlowLists // 工作流列表
    }
}, (dispatch: any) => {
    const benchActions = workbenchActions(dispatch)

    return {
        toggleCloneToWorkflow: function () {
            benchActions.toggleCloneToWorkflow();
        },

        updateWorkflow: function(workflow: any) {
            benchActions.updateWorkflow(workflow)
        },

        createWorkflowTask: function(data: any) {
            return benchActions.createWorkflowTask(data)
        },

        /**
         * @description 新建或编辑
         * @param {any} params 表单参数
         * @param {boolean} isEditExist 是否编辑
         * @param {any} 修改前的数据
        */
        confirmCloneToWorkflow: function (params: any, defaultData: any, coordsExtra: any) {
            return ajax.cloneTaskToWorkflow({
                ...params,
                coordsExtra
            })
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
                        // 重新刷新克隆之后的工作流
                        // ajax.getOfflineTaskDetail({
                        //     id: res.data.flowId
                        // }).then((res: any) => {
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
class CloneToWorkflowModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            loading: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    workFlowListsOption () {
        const { workFlowLists } = this.props;
        return workFlowLists && workFlowLists.map((item: any) => {
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
        const coordsExtra: any = {
            'vertex': true,
            'edge': false,
            'x': getRandomInt(10, 850),
            'y': getRandomInt(-360, 50),
            'value': null
        }
        // 获取克隆位置的工作流id
        const { getFieldValue } = this.props.form;
        const selectWorkFlowId = getFieldValue('flowId');
        // 选中工作流节点
        const selectFlow = tabs && tabs.filter((item: any) => {
            return item.id === selectWorkFlowId
        })
        // 选中工作流子节点
        const selectFlowNodes = tabs && tabs.filter((item: any) => {
            return item.flowId === selectWorkFlowId
        })
        validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })

                const handRes = (isSuccess: any) => {
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
                    const notSyncedFlow = selectFlow[0] && selectFlow[0].notSynced;
                    const notSyncedFlowNodes = selectFlowNodes && selectFlowNodes.some((item: any) => {
                        return item.notSynced == true
                    })
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

    handleSelectTreeChange(value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }
    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName(id: any) {
        const { taskTreeData } = this.props;
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
                        ref={(el: any) => this.form = el}
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
                                    filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
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

export default Form.create<any>()(CloneToWorkflowModal);
