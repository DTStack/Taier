import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Icon, Input, Select, Radio, Tooltip,message } from 'antd';

import ajax from '../../../api';

import {
    modalAction,
    taskTreeAction,
} from '../../../store/modules/offlineTask/actionType';
import HelpDoc from "../../helpDoc"

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import {
    formItemLayout, TASK_TYPE, MENU_TYPE, RESOURCE_TYPE, DATA_SYNC_TYPE,
    HELP_DOC_URL, LEARNING_TYPE, PYTON_VERSION, DEAL_MODEL_TYPE
} from '../../../comm/const'

import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;
const RadioGroup = Radio.Group;

class TaskForm extends React.Component {
    constructor(props) {
        super(props);

        // this.handleRadioChange = this.handleRadioChange.bind(this);
        this.handleTaskTypeChange = this.handleTaskTypeChange.bind(this);
        this.isEditExist = false;
        this.state = {
            value: 0,
            operateModel: '',
            taskTypes: [],
        };

        this._resChange = false;
    }

    componentWillMount() {
        const { defaultData } = this.props;
        this.loadTaskTypes();
        this.setState({
            operateModel: (defaultData&&defaultData.operateModel) ? defaultData.operateModel : DEAL_MODEL_TYPE.RESOURCE
        })
    }
    // componentWillReceiveProps(nextProps){
    //     if(this.props.defaultData!=nextProps.project.id){
    //         this.setState({
    //             projectId:"all",
    //             expandedKeys:[],
    //             tableId:'',
    //             searchName:""
    //         })
    //     }
    // }
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

    // handleRadioChange(e) {
    //     this.setState({
    //         value: e.target.value
    //     });
    // }
    handleTaskTypeChange(value) {
        this.setState({
            value: value
        })
    }

    handleOperateModel(event) {
        this.setState({
            operateModel: event.target.value
        })
    }

    render() {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { defaultData } = this.props;
        const { taskTypes, operateModel } = this.state;

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

        const taskOptions = taskTypes.map(item =>
            <Option key={item.key} value={item.key}>{item.value}</Option>
        )

        const syncTaskHelp = (
            <div>
                功能释义：
                <br />
                向导模式：便捷、简单，可视化字段映射，快速完成同步任务配置
                <br />
                脚本模式：全能 高效，可深度调优，支持全部数据源
                <br />
                <a href={HELP_DOC_URL.DATA_SOURCE} target="blank">查看支持的数据源</a>
            </div>
        )


        const isMrTask = value === TASK_TYPE.MR
        const isPyTask = value === TASK_TYPE.PYTHON
        const isSyncTast = value == TASK_TYPE.SYNC
        const isDeepLearning = value == TASK_TYPE.DEEP_LEARNING
        const isPython23 = value == TASK_TYPE.PYTHON_23
        const acceptType = isMrTask ? RESOURCE_TYPE.JAR : isPyTask ? RESOURCE_TYPE.PY : '';
        const savePath = isCreateNormal ? this.props.treeData.id : isCreateFromMenu ? defaultData.parentId : defaultData.nodePid;
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
                        <Select
                            disabled={isCreateNormal ? false : !isCreateFromMenu}
                            onChange={this.handleTaskTypeChange}
                        >
                            {taskOptions}
                        </Select>
                    )}
                    {isMrTask && <Tooltip title={(
                        <div>
                            <p>支持基于Spark MLLib的机器学习任务</p>
                            <p>支持基于Spark API的Java处理程序</p>
                        </div>
                    )}>
                        <Icon className="formItem_inline_icon" type="question-circle-o" />
                    </Tooltip>}
                </FormItem>
                {
                    isDeepLearning && (
                        <FormItem
                            {...formItemLayout}
                            label="框架类型"
                        >
                            {getFieldDecorator('learningType', {
                                rules: [{
                                    required: true, message: '请选择框架类型',
                                }],
                                initialValue: this.isEditExist ? defaultData.learningType : LEARNING_TYPE.TENSORFLOW
                            })(
                                <RadioGroup
                                    disabled={isCreateNormal ? false : !isCreateFromMenu}
                                >
                                    <Radio key={LEARNING_TYPE.TENSORFLOW} value={LEARNING_TYPE.TENSORFLOW}>TensorFlow</Radio>
                                    <Radio key={LEARNING_TYPE.MXNET} value={LEARNING_TYPE.MXNET}>MXNet</Radio>
                                </RadioGroup>
                            )}
                        </FormItem>
                    )
                }
                {
                    (isDeepLearning || isPython23) && (
                        <div>
                            <FormItem
                                {...formItemLayout}
                                label="python版本"
                            >
                                {getFieldDecorator('pythonVersion', {
                                    rules: [{
                                        required: true, message: '请选择python版本',
                                    }],
                                    initialValue: this.isEditExist ? defaultData.pythonVersion : PYTON_VERSION.PYTHON2
                                })(
                                    <RadioGroup
                                        disabled={isCreateNormal ? false : !isCreateFromMenu}
                                    >
                                        <Radio key={PYTON_VERSION.PYTHON2} value={PYTON_VERSION.PYTHON2}>python2.x</Radio>
                                        <Radio key={PYTON_VERSION.PYTHON3} value={PYTON_VERSION.PYTHON3}>python3.x</Radio>
                                    </RadioGroup>
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="操作模式"
                            >
                                {getFieldDecorator('operateModel', {
                                    rules: [{
                                        required: true, message: '请选择操作模式',
                                    }],
                                    initialValue: operateModel
                                })(
                                    <RadioGroup
                                        disabled={isCreateNormal ? false : !isCreateFromMenu}
                                        onChange={this.handleOperateModel.bind(this)}
                                    >
                                        <Radio key={DEAL_MODEL_TYPE.RESOURCE} value={DEAL_MODEL_TYPE.RESOURCE}>资源上传</Radio>
                                        <Radio key={DEAL_MODEL_TYPE.EDIT} value={DEAL_MODEL_TYPE.EDIT}>WEB编辑</Radio>
                                    </RadioGroup>
                                )}
                            </FormItem>
                        </div>
                    )
                }
                {
                    isDeepLearning && (
                        <div>
                            <FormItem
                                {...formItemLayout}
                                label="数据输入路径"
                            >
                                {getFieldDecorator('input', {
                                    initialValue: this.isEditExist ? defaultData.input : ''
                                })(
                                    <Input placeholder="请输入数据输入路径" />
                                )}
                                <HelpDoc doc="inputTaskHelp" />
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="模型输出路径"
                            >
                                {getFieldDecorator('output', {
                                    initialValue: this.isEditExist ? defaultData.output : ''
                                })(
                                    <Input placeholder="请输入模型输出路径" />
                                )}
                                <HelpDoc doc="outputTaskHelp" />
                            </FormItem>
                        </div>
                    )
                }
                {
                    (isDeepLearning || isPython23) && (
                        <FormItem
                            {...formItemLayout}
                            label="参数"
                        >
                            {getFieldDecorator('options', {
                                initialValue: this.isEditExist ? defaultData.options : ''
                            })(
                                <TextArea autosize={{ minRows: 2, maxRows: 4 }} placeholder="请输入命令行参数" />
                            )}
                            <HelpDoc doc="optionsTaskHelp" />
                        </FormItem>
                    )
                }
                {
                    (isMrTask || isPyTask || ((isDeepLearning || isPython23) && operateModel == DEAL_MODEL_TYPE.RESOURCE)) && <span>
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
                                initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0] && defaultData.resourceList[0].id
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
                                defaultNode={isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0] && defaultData.resourceList[0]["resourceName"]}
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
                        {(isMrTask || isPyTask) && <FormItem
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
                        </FormItem>}
                    </span>
                }
                {
                    isSyncTast &&
                    <FormItem
                        {...formItemLayout}
                        label={"配置模式"}
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
                        <Tooltip placement="right" title={syncTaskHelp}>
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
                        initialValue: savePath,
                    })(
                        <FolderPicker
                            ispicker
                            id="Task_dev_catalogue"
                            type={MENU_TYPE.TASK_DEV}
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
        const { addOfflineTask, defaultData } = this.props;
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

                addOfflineTask(values, isEditExist, defaultData)
                    .then(isSuccess => {
                        if (isSuccess) {
                            message.success("操作成功")
                            form.resetFields();
                            this.closeModal();
                        }
                    }
                    );
            }
        })
    }

    handleCancel() {
        this.closeModal();
    }

    closeModal() {
        this.props.toggleCreateTask();
        this.props.emptyModalDefault();
        this.dtcount++;
    }

    render() {
        const { isModalShow, taskTreeData, resourceTreeData, defaultData } = this.props;

        let isCreate = true;
        if (defaultData && defaultData.name) {
            isCreate = false;
        }

        return (
            <div>
                <Modal
                    title={isCreate ? '新建离线任务' : '编辑离线任务'}
                    key={this.dtcount}
                    visible={isModalShow}
                    maskClosable={false}
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
        currentTab: state.offlineTask.workbench.currentTab,
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
                return ajax.addOfflineTask(params)
                    .then(res => {
                        if (res.code === 1) {
                            // Reload current TreeNodes
                            if (!isEditExist) {
                                benchActions.openTaskInDev(res.data.id);
                            }
                            else {
                                // 如果文件位置有移动，则进行文件移动处理
                                let newData = Object.assign(defaultData, res.data);
                                newData.originPid = defaultData.nodePid;
                                dispatch({
                                    type: taskTreeAction.EDIT_FOLDER_CHILD,
                                    payload: newData
                                });

                                // 更新tabs数据
                                ajax.getOfflineTaskDetail({
                                    id: defaultData.id,
                                }).then(res => {
                                    if (res.code === 1) {
                                        benchActions.updateTabData(res.data);
                                    }
                                });
                            }
                            benchActions.loadTreeNode(res.data.parentId,MENU_TYPE.TASK_DEV)
                            return true;
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