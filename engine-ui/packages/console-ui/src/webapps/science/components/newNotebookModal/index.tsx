import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get, isArray } from 'lodash';

import { Modal, Form, Input, Select, Radio } from 'antd';
import FolderTree from '../folderTree';
import { formItemLayout, siderBarType, modalType, DEAL_MODEL_TYPE, TASK_TYPE } from '../../consts'
import * as fileTreeActions from '../../actions/base/fileTree'
import * as notebookActions from '../../actions/notebookActions'
import workbenchActions from '../../actions/workbenchActions'

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
@(connect((state: any) => {
    return {
        files: state.notebook.files,
        resourceFiles: state.resource.files,
        modal: state.modal,
        taskType: state.taskType.taskType
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(notebookActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    };
}) as any)
class NewNotebookModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            operateModel: DEAL_MODEL_TYPE.EDIT
        }
    }
    form = React.createRef();
    _key: number = null;
    onSubmit = () => {
        (this.form as any).props.form.validateFields(async (err: any, values: any) => {
            if (!err) {
                if (values.resourceIdList) {
                    values.resourceIdList = [values.resourceIdList];
                }

                if (values.refResourceIdList && !isArray(values.refResourceIdList)) {
                    values.refResourceIdList = [values.refResourceIdList];
                }
                let res = await this.props.addNotebook(values);
                if (res) {
                    this.props.onOk && this.props.onOk(res);
                    this.props.openNotebook(res.data.id);
                    this._key = Math.random();
                    this.setState({ operateModel: DEAL_MODEL_TYPE.EDIT })
                    this.props.resetModal();
                }
            }
        })
    }
    handleOperateModel (event: any) {
        this.setState({
            operateModel: event.target.value
        })
    }
    render () {
        const { modal, loadTreeData, files, resourceFiles, taskType } = this.props;
        const visible = modal.visibleModal == modalType.newNotebook;
        return <Modal
            title='新建Notebook'
            visible={visible}
            key={this._key}
            onCancel={() => {
                this.setState({ operateModel: DEAL_MODEL_TYPE.EDIT })
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
                taskType={taskType}
                operateModel={this.state.operateModel}
                handleOperateModel={(e: any) => { this.handleOperateModel(e) }}
                wrappedComponentRef={(_form: any) => { this.form = _form }}
            />
        </Modal>
    }
}
class NewNotebookModalForm extends React.Component<any, any> {
    loadData = (siderType: any, node: any) => {
        return this.props.loadTreeData(siderType, node.props.data.id);
    }
    getRootNode () {
        const { files } = this.props;
        if (files && files.length) {
            const children = files[0].children;
            const myFolder = children.find((node: any) => {
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
    checkNotDir (rule: any, value: any, callback: any) {
        const { resourceFiles } = this.props;
        let nodeType: any;

        let loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
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
        console.log('------', isResOperateModal, operateModel)
        const resourceLable = !isPyTask ? '资源' : '入口资源';
        const taskOptions = taskType.map((item: any) =>
            <Option key={item.key} value={item.key}>{item.value}</Option>
        )
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
                            onChange={(e: any) => { handleOperateModel(e) }}
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
const WrapNewNotebookModalForm = Form.create<any>()(NewNotebookModalForm);
export default NewNotebookModal;
