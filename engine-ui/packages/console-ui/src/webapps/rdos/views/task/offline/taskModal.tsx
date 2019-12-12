import * as React from 'react';
import { connect } from 'react-redux';
import { isArray, get } from 'lodash';

import { Modal, Button, Form, Input, Select, Radio, message } from 'antd';

import ajax from '../../../api';
import { getContainer } from 'funcs';

import {
    modalAction,
    taskTreeAction
} from '../../../store/modules/offlineTask/actionType';
import HelpDoc, { relativeStyle } from '../../helpDoc'

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import {
    formItemLayout, TASK_TYPE, MENU_TYPE, DATA_SYNC_TYPE,
    LEARNING_TYPE, PYTON_VERSION, DEAL_MODEL_TYPE, DATA_SYNC_MODE, HADOOPMR_INITIAL_VALUE
} from '../../../comm/const'
import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const hadoopMRJsonValue = JSON.stringify(HADOOPMR_INITIAL_VALUE, null, 4)
class TaskForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        const defaultData = props.defaultData || {};
        this.handleTaskTypeChange = this.handleTaskTypeChange.bind(this);
        this.isEditExist = false;
        this.state = {
            value: undefined,
            operateModel: get(defaultData, 'operateModel', DEAL_MODEL_TYPE.RESOURCE),
            analyDataSourceLists: []
        };

        this._resChange = false;
    }
    isEditExist: boolean;
    _resChange: boolean;
    componentDidMount () {
        this.getAnalyDataSourceLists();
    }
    /**
     * 获取分析引擎数据源
     */
    getAnalyDataSourceLists = () => {
        ajax.getAnalyDataSourceLists().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    analyDataSourceLists: res.data
                })
            }
        })
    }
    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    handleResSelectTreeChange (value: any) {
        this._resChange = true;
        this.props.form.setFieldsValue({ resourceIdList: value });
        this.props.form.validateFields(['resourceIdList']);
    }

    handleRefResSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ refResourceIdList: value });
        this.props.form.validateFields(['refResourceIdList']);
    }

    checkSyncMode = async (rule: any, value: any, callback: any) => {
        const { defaultData } = this.props;
        // 当编辑同步任务，且改变同步模式为增量模式时，需要检测任务是否满足增量同步的条件
        if (this.isEditExist && value === '1') {
            const res = await ajax.checkSyncMode(defaultData);
            if (res.code === 1) {
                callback();
            } else {
                /* eslint-disable-next-line */
                callback('当前同步任务不支持增量模式！');
            }
        }
        callback();
    }

    handleTaskTypeChange (value: any) {
        this.setState({
            value
        })
    }

    handleOperateModel (event: any) {
        this.setState({
            operateModel: event.target.value
        })
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const {
            defaultData, taskTypes, createOrigin,
            labelPrefix, createFromGraph
        } = this.props;
        const { operateModel, analyDataSourceLists, value: taskTypeValue } = this.state;
        /**
         * 1. 从按钮新建(createNormal)没有默认数据
         * 2. 有默认数据的情况分以下三种：
         *  a. 编辑任务时,默认数据是task对象
         *  b. 从文件夹新建时默认数据只有一个{ parentId }
         *  c. 从欢迎页面创建，带有任务类型
         */
        let isCreateNormal = false;
        let isCreateFromMenu = false;
        let isCreateFromIndex = false;
        if (typeof defaultData === 'undefined') {
            isCreateNormal = true;
        } else if (typeof defaultData.id === 'undefined') {
            if (typeof defaultData.taskType !== 'undefined') {
                isCreateFromIndex = true;
            } else {
                isCreateFromMenu = true;
            }
        }
        this.isEditExist = !isCreateNormal && !isCreateFromMenu && !isCreateFromIndex;

        let value: any;
        let isValueEmpty = typeof taskTypeValue == 'undefined';
        if (createFromGraph) {
            value = createOrigin.taskType;
        } else if (isCreateNormal || isCreateFromMenu) {
            value = isValueEmpty ? (taskTypes.length > 0 && taskTypes[0].key) : taskTypeValue
        } else {
            value = isValueEmpty ? defaultData.taskType : taskTypeValue;
            // 当通过下拉框更改任务类型后，需要重置create源为normal
            // isCreateNormal = true;
        }

        const taskOptions = taskTypes.map((item: any) =>
            <Option key={item.key} value={item.key}>{item.value}</Option>
        )
        const dataSourceOptions = analyDataSourceLists && analyDataSourceLists.map((item: any) => {
            return <Option key={item.id} value={item.id}>{item.dataName}</Option>
        })
        const isCarbonSql = value === TASK_TYPE.CARBONSQL
        const isMrTask = value === TASK_TYPE.MR
        const isPyTask = value === TASK_TYPE.PYTHON
        const isSyncTask = value == TASK_TYPE.SYNC
        const isDeepLearning = value == TASK_TYPE.DEEP_LEARNING
        const isPython23 = value == TASK_TYPE.PYTHON_23
        const isMl = value == TASK_TYPE.ML;
        const isHadoopMR = value == TASK_TYPE.HAHDOOPMR;
        const savePath = (isCreateNormal || isCreateFromIndex) ? this.props.treeData.id : isCreateFromMenu ? defaultData.parentId : defaultData.nodePid;

        const resourceLable = !isPyTask ? '资源' : '入口资源';
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label={`${labelPrefix}名称`}
                    hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true, message: `${labelPrefix}名称不可为空！`
                        }, {
                            max: 64,
                            message: `${labelPrefix}名称不得超过64个字符！`
                        }, {
                            pattern: /^[A-Za-z0-9_]+$/,
                            message: `${labelPrefix}名称只能由字母、数字、下划线组成!`
                        }],
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.name
                    })(
                        <Input placeholder={`请输入${labelPrefix}名称`} autoComplete="off"/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label={`${labelPrefix}类型`}
                >
                    {getFieldDecorator('taskType', {
                        rules: [{
                            required: true, message: `请选择${labelPrefix}类型`
                        }],
                        initialValue: value
                    })(
                        <Select
                            disabled={this.isEditExist || createFromGraph}
                            onChange={this.handleTaskTypeChange}
                        >
                            {taskOptions}
                        </Select>
                    )}
                    <HelpDoc doc={isMrTask ? 'mrTaskHelp' : isMl ? 'mlTaskHelp' : ''} />
                </FormItem>
                {
                    isDeepLearning && (
                        <FormItem
                            {...formItemLayout}
                            label="框架类型"
                        >
                            {getFieldDecorator('learningType', {
                                rules: [{
                                    required: true, message: '请选择框架类型'
                                }],
                                initialValue: this.isEditExist ? defaultData.learningType : LEARNING_TYPE.TENSORFLOW
                            })(
                                <RadioGroup
                                    disabled={this.isEditExist}
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
                                        required: true, message: '请选择python版本'
                                    }],
                                    initialValue: this.isEditExist ? defaultData.pythonVersion : PYTON_VERSION.PYTHON2
                                })(
                                    <RadioGroup
                                        disabled={this.isEditExist}
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
                                {getFieldDecorator('operateModel@py23', {
                                    rules: [{
                                        required: true, message: '请选择操作模式'
                                    }],
                                    initialValue: operateModel
                                })(
                                    <RadioGroup
                                        disabled={this.isEditExist}
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
                    isPyTask && <div>
                        <FormItem
                            {...formItemLayout}
                            label="操作模式"
                        >
                            {getFieldDecorator('operateModel@py', {
                                rules: [{
                                    required: true, message: '请选择操作模式'
                                }],
                                initialValue: operateModel
                            })(
                                <RadioGroup
                                    disabled={this.isEditExist}
                                    onChange={this.handleOperateModel.bind(this)}
                                >
                                    <Radio key={DEAL_MODEL_TYPE.RESOURCE} value={DEAL_MODEL_TYPE.RESOURCE}>资源上传</Radio>
                                    <Radio key={DEAL_MODEL_TYPE.EDIT} value={DEAL_MODEL_TYPE.EDIT}>WEB编辑</Radio>
                                </RadioGroup>
                            )}
                        </FormItem>
                    </div>
                }
                {
                    (isHadoopMR || isMl || isMrTask || ((isDeepLearning || isPython23 || isPyTask) && operateModel == DEAL_MODEL_TYPE.RESOURCE)) && <span>
                        <FormItem
                            {...formItemLayout}
                            label={resourceLable}
                            hasFeedback
                        >
                            {getFieldDecorator('resourceIdList', {
                                rules: [{
                                    required: true,
                                    message: `请选择${resourceLable}`
                                }, {
                                    validator: this.checkNotDir.bind(this)
                                }],
                                initialValue: (isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0] && defaultData.resourceList[0].id
                            })(
                                <Input type="hidden" />
                            )}
                            <FolderPicker
                                type={MENU_TYPE.RESOURCE}
                                ispicker
                                placeholder={`请选择${resourceLable}`}
                                isFilepicker
                                treeData={this.props.resTreeData}
                                onChange={this.handleResSelectTreeChange.bind(this)}
                                defaultNode={(isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0] && defaultData.resourceList[0].resourceName}
                            />
                        </FormItem>
                        {
                            isPyTask && <FormItem
                                {...formItemLayout}
                                label="引用资源"
                                hasFeedback
                            >
                                {getFieldDecorator('refResourceIdList', {
                                    rules: [{
                                        validator: this.checkNotDir.bind(this)
                                    }],
                                    initialValue: (isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.refResourceList && defaultData.refResourceList.length > 0 ? defaultData.refResourceList.map((res: any) => res.id) : []
                                })(
                                    <Input type="hidden" />
                                )}
                                <FolderPicker
                                    key="createRefResourceIdList"
                                    ispicker
                                    placeholder="请选择关联资源"
                                    isFilepicker
                                    allowClear={true}
                                    treeData={this.props.resTreeData}
                                    onChange={this.handleRefResSelectTreeChange.bind(this)}
                                    defaultNode={(isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.refResourceList && defaultData.refResourceList.length > 0 && defaultData.refResourceList.map((res: any) => res.resourceName)}
                                />
                            </FormItem>
                        }
                        {
                            (isMl || isMrTask) && <FormItem
                                {...formItemLayout}
                                label="mainClass"
                                hasFeedback
                            >
                                {getFieldDecorator('mainClass', {
                                    rules: [{
                                        required: true, message: 'mainClass 不可为空！'
                                    }, {
                                        pattern: /^[A-Za-z0-9_.-]+$/,
                                        message: 'mainClass 只能由字母、数字、下划线、分隔点组成!'
                                    }],
                                    initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.mainClass
                                })(
                                    <Input placeholder="请输入 mainClass" />
                                )}
                            </FormItem>
                        }
                        {
                            (isMl || isMrTask || isDeepLearning || isPython23 || isPyTask || isHadoopMR) && (
                                <FormItem
                                    {...formItemLayout}
                                    label="参数"
                                >
                                    {getFieldDecorator('options', {
                                        initialValue: this.isEditExist ? defaultData.options : isHadoopMR ? hadoopMRJsonValue : undefined
                                    })(
                                        <Input type="textarea" autosize={{ minRows: 2, maxRows: 4 }} placeholder="请输入任务参数" />
                                    )}
                                </FormItem>
                            )
                        }
                    </span>
                }
                {
                    isSyncTask && <div>
                        <FormItem
                            {...formItemLayout}
                            label={'配置模式'}
                        >
                            {getFieldDecorator('createModel', {
                                rules: [{
                                    required: true, message: '请选择配置模式'
                                }],
                                initialValue: this.isEditExist ? defaultData.createModel : DATA_SYNC_TYPE.GUIDE
                            })(
                                <RadioGroup
                                    disabled={this.isEditExist}
                                >
                                    <Radio key={DATA_SYNC_TYPE.GUIDE} value={DATA_SYNC_TYPE.GUIDE}>向导模式</Radio>
                                    <Radio key={DATA_SYNC_TYPE.SCRIPT} value={DATA_SYNC_TYPE.SCRIPT}>脚本模式</Radio>
                                </RadioGroup>

                            )}
                            <HelpDoc doc="syncTaskHelp" style={relativeStyle} />
                        </FormItem>
                        {
                            !createFromGraph &&
                            <FormItem
                                {...formItemLayout}
                                label={'同步模式'}
                            >
                                {getFieldDecorator('syncModel', {
                                    rules: [{
                                        required: true, message: '请选择配置模式'
                                    }, {
                                        validator: this.checkSyncMode.bind(this)
                                    }],
                                    initialValue: this.isEditExist ? (`${defaultData.syncModel}` || `${DATA_SYNC_MODE.NORMAL}`) : `${DATA_SYNC_MODE.NORMAL}`
                                })(
                                    <RadioGroup>
                                        <Radio key="no" value={`${DATA_SYNC_MODE.NORMAL}`}>无增量标识</Radio>
                                        <Radio key="yes" value={`${DATA_SYNC_MODE.INCREMENT}`}>有增量标识</Radio>
                                    </RadioGroup>
                                )}
                                <HelpDoc doc="syncModeHelp" style={relativeStyle} />
                            </FormItem>
                        }
                    </div>
                }
                {
                    (!createFromGraph) &&
                    <FormItem
                        {...formItemLayout}
                        label="存储位置"
                    >
                        {getFieldDecorator('nodePid', {
                            rules: [{
                                required: true, message: '存储位置必选！'
                            }],
                            initialValue: savePath
                        })(
                            <FolderPicker
                                ispicker
                                id="Task_dev_catalogue"
                                type={MENU_TYPE.TASK_DEV}
                                treeData={this.props.treeData}
                                onChange={this.handleSelectTreeChange.bind(this)}
                                defaultNode={(isCreateNormal || isCreateFromIndex)
                                    ? this.props.treeData.name
                                    : isCreateFromMenu
                                        ? this.getFolderName(defaultData.parentId, defaultData.type)
                                        : this.getFolderName(defaultData.nodePid, defaultData.type)
                                }
                            />
                        )}
                    </FormItem>
                }
                {
                    isCarbonSql && (
                        <FormItem
                            {...formItemLayout}
                            label="数据源"
                        >
                            {getFieldDecorator('dataSourceId', {
                                rules: [{
                                    required: true, message: '请选择数据源'
                                }],
                                initialValue: this.isEditExist ? defaultData.dataSourceId : undefined
                            })(
                                <Select
                                    disabled={this.isEditExist}
                                >
                                    {dataSourceOptions}
                                </Select>
                            )}
                        </FormItem>
                    )
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
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.taskDesc
                    })(
                        <Input.TextArea rows={4} placeholder="请输入任务描述" />
                    )}
                </FormItem>
                <FormItem style={{ display: 'none' }}>
                    {getFieldDecorator('computeType', {
                        initialValue: 1
                    })(
                        <Input type="hidden" />
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
    checkNotDir (rule: any, value: any, callback: any) {
        const { resTreeData } = this.props;
        let nodeType: any;

        const loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                if (node.id === value) {
                    nodeType = node.type;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop([resTreeData]);

        if (nodeType === 'folder') {
            /* eslint-disable-next-line */
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName (id: any, type: any) {
        const { treeData } = this.props;
        let name: any;

        const loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                if (node.id === id && node.type === type) {
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

const TaskFormWrapper = Form.create<any>()(TaskForm);

class TaskModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    dtcount: number;
    form: any;
    handleSubmit () {
        const {
            addOfflineTask, defaultData, workflow,
            createWorkflowTask
        } = this.props;
        const form = this.form;

        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;
        const createFromGraph = workflow && workflow.status === 'create';

        form.validateFields((err: any, values: any) => {
            if (!err) {
                values.lockVersion = 0;
                values.version = 0;
                for (let key in values) {
                    const keys = key.split('@');
                    if (keys.length > 1) {
                        values[keys[0]] = values[key];
                        values[key] = undefined;
                    }
                }
                // 编辑基本信息标识
                if (isEditExist) values.isEditBaseInfo = true;

                if (values.resourceIdList) {
                    values.resourceIdList = [values.resourceIdList];
                }

                if (values.refResourceIdList && !isArray(values.refResourceIdList)) {
                    values.refResourceIdList = [values.refResourceIdList];
                }

                if (defaultData && defaultData.id) { // 更新操作
                    values.id = defaultData.id
                    values.version = defaultData.version;
                    values.readWriteLockVO = Object.assign({}, defaultData.readWriteLockVO);
                }
                this.setState({
                    loading: true
                })

                const handRes = (isSuccess: any) => {
                    this.setState({
                        loading: false
                    })
                    if (isSuccess) {
                        message.success('操作成功')
                        form.resetFields();
                        this.closeModal();
                    }
                }
                if (!createFromGraph) {
                    addOfflineTask(values, isEditExist, defaultData)
                        .then(handRes);
                } else {
                    // 如果是任务流创建节点，则执行保存任务工作流节点
                    values.flowId = workflow.workflowId;
                    values.nodePid = workflow.data.nodePid;
                    createWorkflowTask(values).then(handRes);
                }
            }
        })
    }

    handleCancel () {
        const { workflow, updateWorkflow } = this.props;
        const createFromGraph = workflow && workflow.status === 'create';
        if (createFromGraph) {
            updateWorkflow({
                status: 'cancel'
            })
        }
        this.closeModal();
    }

    closeModal () {
        this.props.toggleCreateTask();
        this.props.emptyModalDefault();
        this.dtcount++;
        this.setState({
            loading: false
        })
    }

    render () {
        const {
            isModalShow, taskTreeData, resourceTreeData,
            defaultData, taskTypes, workflow } = this.props;
        const { loading } = this.state;

        let isCreate = true;
        /**
         * 是否为工作流节点
         */
        const createFromGraph = workflow && workflow.status === 'create';
        const labelPrefix = createFromGraph ? '节点' : '任务';

        if (defaultData && defaultData.name) {
            isCreate = false;
        }

        return (
            <div id="JS_task_modal">
                <Modal
                    title={isCreate ? `新建${labelPrefix}` : `编辑${labelPrefix}`}
                    key={this.dtcount}
                    visible={isModalShow}
                    maskClosable={false}
                    getContainer={() => getContainer('JS_task_modal')}
                    footer={[
                        <Button key="back"
                            size="large"
                            onClick={this.handleCancel}
                        >取消</Button>,
                        <Button
                            key="submit"
                            type="primary"
                            size="large"
                            loading={loading}
                            onClick={this.handleSubmit.bind(this)}
                        > 确认 </Button>
                    ]}
                    onCancel={this.handleCancel}
                >
                    <TaskFormWrapper
                        ref={(el: any) => this.form = el}
                        treeData={taskTreeData}
                        resTreeData={resourceTreeData}
                        defaultData={defaultData}
                        createOrigin={workflow}
                        taskTypes={taskTypes}
                        labelPrefix={labelPrefix}
                        createFromGraph={createFromGraph}
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        isModalShow: state.offlineTask.modalShow.createTask,
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
        toggleCreateTask: function () {
            benchActions.toggleCreateTask();
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
        addOfflineTask: function (params: any, isEditExist: any, defaultData: any) {
            return ajax.addOfflineTask(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        // Reload current TreeNodes
                        if (!isEditExist) {
                            benchActions.openTaskInDev(res.data.id);
                        } else {
                            // 如果文件位置有移动，则进行文件移动处理
                            let newData = Object.assign(defaultData, res.data);
                            newData.originPid = defaultData.nodePid;
                            dispatch({
                                type: taskTreeAction.EDIT_FOLDER_CHILD,
                                payload: newData
                            });

                            // 更新tabs数据
                            ajax.getOfflineTaskDetail({
                                id: defaultData.id
                            }).then((res: any) => {
                                if (res.code === 1) {
                                    benchActions.updateTabData(res.data);
                                }
                            });
                        }
                        benchActions.loadTreeNode(res.data.parentId, MENU_TYPE.TASK_DEV)
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
})(TaskModal);
