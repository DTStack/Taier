import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';

import { Modal, Form, Input, Select, Radio } from 'antd';
import FolderTree from '../folderTree';
import { formItemLayout, siderBarType, modalType, DEAL_MODEL_TYPE, TASK_TYPE } from '../../consts'
import * as fileTreeActions from '../../actions/base/fileTree'
import * as notebookActions from '../../actions/notebookActions'
import workbenchActions from '../../actions/workbenchActions'

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
@connect(state => {
    return {
        files: state.notebook.files,
        resourceFiles: state.resource.files,
        modal: state.modal,
        taskType: state.taskType.taskType
    }
}, dispatch => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(notebookActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    };
})
class NewNotebookModal extends React.Component {
    constructor (props) {
        super(props)
        this.state = {
            operateModel: DEAL_MODEL_TYPE.EDIT
        }
    }
    form = React.createRef();
    _key = null;
    onSubmit = () => {
        this.form.props.form.validateFields(async (err, values) => {
            if (!err) {
                let res = await this.props.addNotebook(values);
                if (res) {
                    this.props.onOk && this.props.onOk(res);
                    this.props.openNotebook(res.data.id);
                    this._key = Math.random();
                    this.props.resetModal();
                }
            }
        })
    }
    handleOperateModel (event) {
        this.setState({
            operateModel: event.target.value
        })
    }
    render () {
        const { modal, loadTreeData, files, resourceFiles } = this.props;
        const visible = modal.visibleModal == modalType.newNotebook;
        return <Modal
            title='新建Notebook'
            visible={visible}
            key={this._key}
            onCancel={() => {
                this._key = Math.random();
                this.props.resetModal();
            }}
            onOk={this.onSubmit}
        >
            <WrapNewNotebookModalForm
                loadTreeData={loadTreeData}
                modal={modal}
                files={files}
                resourceFiles={resourceFiles}
                operateModel={this.state.operateModel}
                handleOperateModel={(e) => { this.handleOperateModel(e) }}
                wrappedComponentRef={(_form) => { this.form = _form }}
            />
        </Modal>
    }
}
class NewNotebookModalForm extends React.Component {
    loadData = (siderType, node) => {
        return this.props.loadTreeData(siderType, node.props.data.id);
    }
    getRootNode () {
        const { files } = this.props;
        if (files && files.length) {
            const children = files[0].children;
            const myFolder = children.find((node) => {
                return node.name == '我的Notebook'
            })
            return myFolder && myFolder.id
        }
        return null;
    }
    /**
     * @description 检查所选是否为文件夹
     * @param {any} rule
     * @param {any} value
     * @param {any} cb
     */
    checkNotDir (rule, value, callback) {
        const { resourceFiles } = this.props;
        let nodeType;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if (node.id == value) {
                    nodeType = node.type;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop(resourceFiles);

        if (nodeType === 'folder') {
            /* eslint-disable-next-line */
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }
    render () {
        const { files, form, modal, operateModel, handleOperateModel, resourceFiles, taskType = [] } = this.props;
        const { getFieldDecorator, getFieldValue } = form;
        const { modalData = {} } = modal;
        const isPyTask = getFieldValue('taskType') == TASK_TYPE.PYSPARK;
        const isResOperateModal = operateModel == DEAL_MODEL_TYPE.RESOURCE;
        const resourceLable = !isPyTask ? '资源' : '入口资源';
        const taskOptions = taskType.map(item =>
            <Option key={item.key} value={item.key}>{item.value}</Option>
        )
        console.log('-----', operateModel)
        return (
            <Form>
                <FormItem
                    label='Notebook名称'
                    {...formItemLayout}
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true,
                            message: '请输入名字'
                        }, {
                            pattern: /^[\w\d_]{1,32}$/,
                            message: '名字不超过32个字符,只支持字母、数字、下划线'
                        }]
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label='任务类型'
                >
                    {getFieldDecorator('taskType', {
                        rules: [{
                            required: true, message: `请选择任务类型`
                        }],
                        initialValue: taskType[0] && taskType[0].key
                    })(
                        <Select
                            // disabled={this.isEditExist || createFromGraph}
                            onChange={this.handleTaskTypeChange}
                        >
                            {taskOptions}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="操作模式"
                >
                    {getFieldDecorator('operateModel', {
                        rules: [{
                            required: true, message: '请选择操作模式'
                        }],
                        initialValue: DEAL_MODEL_TYPE.EDIT
                    })(
                        <RadioGroup
                            // disabled={isCreateNormal ? false : !isCreateFromMenu}
                            onChange={(e) => { handleOperateModel(e) }}
                        >
                            <Radio key={DEAL_MODEL_TYPE.EDIT} value={DEAL_MODEL_TYPE.EDIT}>WEB编辑</Radio>
                            <Radio key={DEAL_MODEL_TYPE.RESOURCE} value={DEAL_MODEL_TYPE.RESOURCE}>资源上传</Radio>
                        </RadioGroup>
                    )}
                </FormItem>
                {
                    isResOperateModal && (
                        <>
                            <FormItem
                                {...formItemLayout}
                                label="参数"
                            >
                                {getFieldDecorator('options', {
                                    // initialValue: this.isEditExist ? defaultData.options : ''
                                })(
                                    <Input type="textarea" autosize={{ minRows: 2, maxRows: 4 }} placeholder="输入命令行参数，多个参数用空格隔开" />
                                )}
                            </FormItem>
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
                                    }]
                                    // initialValue: (isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.resourceList[0] && defaultData.resourceList[0].id
                                })(
                                    <FolderTree loadData={this.loadData.bind(this, siderBarType.resource)} treeData={resourceFiles} isSelect={true} />
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="引用资源"
                                hasFeedback
                            >
                                {getFieldDecorator('refResourceIdList', {
                                    rules: [{
                                        validator: this.checkNotDir.bind(this)
                                    }]
                                    // initialValue: (isCreateFromIndex || isCreateNormal) ? undefined : isCreateFromMenu ? undefined : defaultData.refResourceList && defaultData.refResourceList.length > 0 ? defaultData.refResourceList.map(res => res.id) : []
                                })(
                                    <FolderTree loadData={this.loadData.bind(this, siderBarType.resource)} treeData={resourceFiles} isSelect={true} />
                                )}
                            </FormItem>
                        </>
                    )
                }
                <FormItem
                    label='存储位置'
                    {...formItemLayout}
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true,
                            message: '请选择存储位置'
                        }],
                        initialValue: get(modalData, 'id') || this.getRootNode()
                    })(
                        <FolderTree loadData={this.loadData.bind(this, siderBarType.notebook)} treeData={files} isSelect={true} hideFiles={true} />
                    )}
                </FormItem>
                <FormItem
                    label='Notebook描述'
                    {...formItemLayout}
                >
                    {getFieldDecorator('taskDesc', {
                        rules: [{
                            max: 64,
                            message: '最大字符不能超过64'
                        }]
                    })(
                        <Input.TextArea />
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapNewNotebookModalForm = Form.create()(NewNotebookModalForm);
export default NewNotebookModal;
