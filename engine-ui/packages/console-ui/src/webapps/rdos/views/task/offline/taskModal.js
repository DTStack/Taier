import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Icon, Input, Select, Radio, Tooltip } from 'antd';

import ajax from '../../../api';

import {
    modalAction,
    taskTreeAction,
    workbenchAction
} from '../../../store/modules/offlineTask/actionType';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import { formItemLayout, TASK_TYPE, MENU_TYPE, RESOURCE_TYPE, DATA_SYNC_TYPE, HELP_DOC_URL } from '../../../comm/const'

import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Optioin;
const RadioGroup = Radio.Group;

class TaskForm extends React.Component {
    constructor(props) {
        super(props);

        this.handleRadioChange = this.handleRadioChange.bind(this);
        this.isEditExist = false;
        this.state = {
            value: 0,
            taskTypes: []

        };

        this._resChange = false;
    }

    componentDidMount() {
        this.loadTaskTypes();
    }

    handleSelectTreeChange(value) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    handleResSelectTreeChange(value) {
        this._resChange = true;
        this.props.form.setFieldsValue({ 'resourceIdList': value });
        this.props.form.validateFields(['resourceIdList']);
    }

    loadTaskTypes = () => {
        ajax.getTaskTypes().then(res => {
            if (res.code === 1) {
                this.setState({
                    taskTypes: res.data || [],
                })
            }
        })
    }

    handleRadioChange(e) {
        this.setState({
            value: e.target.value
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { defaultData } = this.props;
        const { taskTypes } = this.state;

        /**
         * 1. 从按钮新建(createNormal)没有默认数据
         * 2. 有默认数据的情况分以下两种：
         *  a. 编辑任务时,默认数据是task对象
         *  b. 从文件夹新建时默认数据只有一个{ parentId }
         */
        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';

        this.isEditExist = !isCreateNormal && !isCreateFromMenu;

        const value = isCreateNormal ? this.state.value :
            (!isCreateFromMenu ? defaultData.taskType : this.state.value);

        const taskRadios = taskTypes.map(item =>
            <Radio key={item.key} value={item.key}>{item.value}</Radio>
        )

        const syncTaskHelp = (
            <div>
                功能释义：
                <br/>
                向导模式：便捷、简单，可视化字段映射，快速完成同步任务配置
                <br/>
                脚本模式：全能 高效，可深度调优，支持全部数据源
                <br/>
                <a href={HELP_DOC_URL.DATA_SOURCE} target="blank">查看支持的数据源</a>
            </div> 
        )

        const isMrTask = value === TASK_TYPE.MR
        const isPyTask = value === TASK_TYPE.PYTHON
        const isSyncTast = value == TASK_TYPE.SYNC
        const acceptType = isMrTask ? RESOURCE_TYPE.JAR : isPyTask ? RESOURCE_TYPE.PY : '';

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="任务名称"
                    hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true, message: '任务名称不可为空！',
                        }, {
                            max: 64,
                            message: '任务名称不得超过64个字符！',
                        }, {
                            pattern: /^[A-Za-z0-9_]+$/,
                            message: '任务名称只能由字母、数字、下划线组成!',
                        }],
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.name
                    })(
                        <Input placeholder="请输入任务名称" />,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="任务类型"
                >
                    {getFieldDecorator('taskType', {
                        rules: [{
                            required: true, message: '请选择任务类型',
                        }],
                        initialValue: this.isEditExist ? defaultData.taskType : (taskTypes.length > 0 && taskTypes[0].key)
                    })(
                        <RadioGroup
                            disabled={isCreateNormal ? false : !isCreateFromMenu}
                            onChange={this.handleRadioChange}
                        >
                            {taskRadios}
                        </RadioGroup>
                    )}
                </FormItem>
                {
                    (isMrTask || isPyTask) && <span>
                        <FormItem
                            {...formItemLayout}
                            label="资源"
                            hasFeedback
                        >
                            {getFieldDecorator('resourceIdList', {
                                rules: [{
                                    required: true,
                                    message: '请选择关联资源'
                                }, {
                                    validator: this.checkNotDir.bind(this)
                                }],
                                initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0].id
                            })(
                                <Input type="hidden" ></Input>
                            )}
                            <FolderPicker
                                type={MENU_TYPE.RESOURCE}
                                ispicker
                                placeholder="请选择关联资源"
                                isFilepicker
                                acceptRes={acceptType}
                                treeData={this.props.resTreeData}
                                onChange={this.handleResSelectTreeChange.bind(this)}
                                defaultNode={isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0]["resourceName"]}
                            />
                        </FormItem>
                        {
                            isMrTask && <FormItem
                                {...formItemLayout}
                                label="mainClass"
                                hasFeedback
                            >
                                {getFieldDecorator('mainClass', {
                                    rules: [{
                                        required: true, message: 'mainClass 不可为空！',
                                    }, {
                                        pattern: /^[A-Za-z0-9_.-]+$/,
                                        message: 'mainClass 只能由字母、数字、下划线、分隔点组成!',
                                    }],
                                    initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.mainClass
                                })(
                                    <Input placeholder="请输入 mainClass" />,
                                )}
                            </FormItem>
                        }
                        <FormItem
                            {...formItemLayout}
                            label="参数"
                            hasFeedback
                        >
                            {getFieldDecorator('exeArgs', {
                                rules: [{
                                    pattern: /^[A-Za-z0-9_\/-]+$/,
                                    message: '任务参数只能由字母、数字、下划线、斜杠组成!',
                                }],
                                initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.exeArgs
                            })(
                                <Input placeholder="请输入任务参数" />,
                            )}
                        </FormItem>
                    </span>
                }
                {
                    isSyncTast &&
                        <FormItem
                            {...formItemLayout}
                            label="配置模式"
                        >
                            {getFieldDecorator('createModel', {
                                rules: [{
                                    required: true, message: '请选择配置模式',
                                }],
                                initialValue: this.isEditExist ? defaultData.createModel : DATA_SYNC_TYPE.GUIDE
                            })(
                                <RadioGroup
                                    disabled={isCreateNormal ? false : !isCreateFromMenu}
                                >
                                    <Radio key={DATA_SYNC_TYPE.GUIDE} value={DATA_SYNC_TYPE.GUIDE}>向导模式</Radio>
                                    <Radio key={DATA_SYNC_TYPE.SCRIPT} value={DATA_SYNC_TYPE.SCRIPT}>脚本模式</Radio>

                                </RadioGroup>
                                
                            )}
                            <Tooltip  placement="right" title={syncTaskHelp}>
                                <Icon type="question-circle-o" />
                            </Tooltip>
                        </FormItem>
                }
                <FormItem
                    {...formItemLayout}
                    label="存储位置"
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！',
                        }],
                        initialValue: isCreateNormal ? this.props.treeData.id : isCreateFromMenu ? defaultData.parentId : defaultData.nodePid
                    })(
                        <FolderPicker
                            type={MENU_TYPE.TASK}
                            ispicker
                            treeData={this.props.treeData}
                            onChange={this.handleSelectTreeChange.bind(this)}
                            defaultNode={isCreateNormal ?
                                this.props.treeData.name :
                                isCreateFromMenu ?
                                    this.getFolderName(defaultData.parentId) :
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
                            message: '描述请控制在200个字符以内！',
                        }],
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.taskDesc
                    })(
                        <Input type="textarea" rows={4} placeholder="请输入任务描述" />,
                    )}
                </FormItem>
                <FormItem style={{ display: 'none' }}>
                    {getFieldDecorator('computeType', {
                        initialValue: 1
                    })(
                        <Input type="hidden"></Input>
                    )}
                </FormItem>
            </Form>
        )
    }

    /**
     * @description 检查所选是否为文件夹
     * @param {any} rule
     * @param {any} value
     * @param {any} cb
     * @memberof TaskForm
     */
    checkNotDir(rule, value, callback) {
        const { resTreeData } = this.props;
        let nodeType;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if (node.id === value) {
                    nodeType = node.type;
                }
                else {
                    loop(node.children || []);
                }
            });
        };

        loop([resTreeData]);

        if (nodeType === 'folder') {
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName(id) {
        const { treeData } = this.props;
        let name;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if (node.id === id) {
                    name = node.name;
                }
                else {
                    loop(node.children || []);
                }
            });
        };

        loop([treeData]);

        return name;
    }
}

const TaskFormWrapper = Form.create()(TaskForm);

class TaskModal extends React.Component {
    constructor(props) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }

    handleSubmit() {
        const { isModalShow, toggleCreateTask, addOfflineTask, defaultData } = this.props;
        const form = this.form;

        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;

        form.validateFields((err, values) => {
            if (!err) {
                values.lockVersion = 0;
                values.version = 0;

                if (values.resourceIdList) {
                    values.resourceIdList = [values.resourceIdList];
                }

                if (defaultData && defaultData.id) { // 更新操作
                    values.id = defaultData.id
                    values.version = defaultData.version;
                    values.readWriteLockVO = Object.assign({}, defaultData.readWriteLockVO);
                }

                this.closeModal();
                addOfflineTask(values, isEditExist, defaultData);
                setTimeout(() => {
                    form.resetFields();
                }, 500);
            }
        })
    }

    handleCancel() {
        const { toggleCreateTask } = this.props;

        this.closeModal();
    }

    closeModal() {
        this.dtcount++;

        this.props.emptyModalDefault();
        this.props.toggleCreateTask();
    }

    render() {
        const { isModalShow, toggleCreateTask, taskTreeData, resourceTreeData, defaultData } = this.props;

        if (!defaultData) this.isCreate = true;
        else {
            if (!defaultData.name) this.isCreate = true;
            else this.isCreate = false;
        }

        return (
            <div>
                <Modal
                    title={!this.isCreate ? '编辑离线任务' : '新建离线任务'}
                    visible={isModalShow}
                    footer={[
                        <Button key="back"
                            size="large"
                            onClick={this.handleCancel}
                        >取消</Button>,
                        <Button key="submit"
                            type="primary"
                            size="large"
                            onClick={this.handleSubmit}
                        > 确认 </Button>
                    ]}
                    key={this.dtcount}
                    onCancel={this.handleCancel}
                >
                    <TaskFormWrapper
                        ref={el => this.form = el}
                        treeData={taskTreeData}
                        resTreeData={resourceTreeData}
                        defaultData={defaultData}
                    />
                </Modal>
            </div>
        )
    }
}

export default connect(state => {
    return {
        isModalShow: state.offlineTask.modalShow.createTask,
        taskTreeData: state.offlineTask.taskTree,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        resourceTreeData: state.offlineTask.resourceTree
    }
},
    dispatch => {
        const benchActions = workbenchActions(dispatch)

        return {
            toggleCreateTask: function () {
                benchActions.toggleCreateTask();
            },

            /**
             * @description 新建或编辑
             * @param {any} params 表单参数
             * @param {boolean} isEditExist 是否编辑
             * @param {any} 修改前的数据
             */
            addOfflineTask: function (params, isEditExist, defaultData) {
                ajax.addOfflineTask(params)
                    .then(res => {
                        if (res.code === 1) {
                            if (!isEditExist) {
                                dispatch({
                                    type: taskTreeAction.ADD_FOLDER_CHILD,
                                    payload: res.data
                                });
                                benchActions.openTaskInDev(res.data.id)
                            }
                            else {
                                let newData = Object.assign(defaultData, res.data);
                                newData.originPid = defaultData.nodePid
                                dispatch({
                                    type: taskTreeAction.EDIT_FOLDER_CHILD,
                                    payload: newData
                                });

                                // 更新tabs数据
                                ajax.getOfflineTaskDetail({
                                    id: newData.id,
                                }).then(res => {
                                    if (res.code === 1) {
                                        dispatch({
                                            type: workbenchAction.SET_TASK_FIELDS_VALUE,
                                            payload: res.data,
                                        })
                                    }
                                });
                            }
                        }
                    });
            },

            emptyModalDefault() {
                dispatch({
                    type: modalAction.EMPTY_MODAL_DEFAULT
                });
            }
        }
    })(TaskModal);