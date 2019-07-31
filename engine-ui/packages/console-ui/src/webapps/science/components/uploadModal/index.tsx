import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, Select } from 'antd';
import { get } from 'lodash';
import { getContainer } from 'funcs';
import * as fileTreeActions from '../../actions/base/fileTree';
import * as resourceActions from '../../actions/resourceActions';
import { formItemLayout, RESOURCE_TYPE, siderBarType } from '../../consts';

import FolderTree from '../../components/folderTree';

const FormItem = Form.Item;
const Option = Select.Option;

class ResForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.fileChange = this.fileChange.bind(this);
        this.state = {
            file: '',
            accept: '.jar',
            fileType: props.resourceData ? props.resourceData.resourceType : RESOURCE_TYPE.JAR
        };
    }

    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    handleCoverTargetChange (value: any) {
        this.props.form.setFieldsValue({ 'id': value });
        this.props.form.validateFields(['id']);
    }

    validateFileType = (rule: any, value: any, callback: any) => {
        if (!value) {
            // eslint-disable-next-line
            callback();
            return;
        }
        const { fileType } = this.state;
        const fileSuffix = RESOURCE_TYPE[fileType];
        const suffix = value.split('.').slice(1).pop();
        if (fileType == RESOURCE_TYPE.OTHER) {
            callback();
            return;
        }
        if (fileSuffix != suffix) {
            // eslint-disable-next-line
            callback(`资源文件只能是${fileSuffix}文件!`);
            return;
        }
        callback();
    }

    fileChange (e: any) {
        const file = e.target;
        this.setState({ file });
        this.props.handleFileChange(file);
    }
    resetFile () {
        this.setState({ file: '' });
        this.props.handleFileChange('');
        this.props.form.resetFields(['file']);
    }
    loadData (node: any) {
        return this.props.loadTreeData(this.props.type, node.props.data.id);
    }
    getRootNode () {
        const { treeData } = this.props;
        if (treeData && treeData.length) {
            const myFolder = treeData.find((node: any) => {
                return node.name == '资源管理'
            })
            return myFolder && myFolder.id
        }
        return null;
    }
    renderFormItem = () => {
        const { file, fileType } = this.state;
        const { getFieldDecorator } = this.props.form;
        const {
            resourceData, isCoverUpload, treeData
        } = this.props;
        let accept: any;
        if (fileType != RESOURCE_TYPE.OTHER) {
            accept = `.${RESOURCE_TYPE[fileType]}`;
        }
        if (!isCoverUpload) {
            return [
                <FormItem
                    {...formItemLayout}
                    label="资源名称"
                    hasFeedback
                    key="resourceName"
                >
                    {getFieldDecorator('resourceName', {
                        rules: [{
                            required: true, message: '资源名称不可为空!'
                        }, {
                            pattern: /^[A-Za-z0-9_-]+$/,
                            message: '资源名称只能由字母、数字、下划线组成!'
                        }, {
                            max: 20,
                            message: '资源名称不得超过20个字符!'
                        }]
                    })(
                        <Input placeholder="请输入资源名称" />
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="资源类型"
                    hasFeedback
                    key="resourceType"
                >
                    {getFieldDecorator('resourceType', {
                        rules: [{
                            required: true, message: '资源类型不可为空!'
                        }],
                        initialValue: fileType
                    })(
                        <Select onChange={(value: any) => {
                            this.setState({
                                fileType: value
                            })
                            this.resetFile();
                        }}>
                            <Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>{RESOURCE_TYPE[RESOURCE_TYPE.JAR]}</Option>
                            <Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>{RESOURCE_TYPE[RESOURCE_TYPE.PY]}</Option>
                            <Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>{RESOURCE_TYPE[RESOURCE_TYPE.EGG]}</Option>
                            <Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>{RESOURCE_TYPE[RESOURCE_TYPE.ZIP]}</Option>
                            <Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>其它</Option>
                        </Select>
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="上传"
                    key="file"
                    hasFeedback
                >
                    {getFieldDecorator('file', {
                        rules: [{
                            required: true, message: '请选择上传文件'
                        }, {
                            validator: this.validateFileType
                        }]
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn btn-upload"
                                htmlFor="myOfflinFile">选择文件</label>
                            <span> {file.files && file.files.length && file.files[0].name}</span>
                            <input
                                name="file"
                                type="file"
                                accept={accept}
                                id="myOfflinFile"
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="选择存储位置"
                    key="nodePid"
                    hasFeedback
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: get(resourceData, 'id') || this.getRootNode()
                    })(
                        <FolderTree loadData={this.loadData.bind(this)} treeData={treeData} isSelect={true} hideFiles={true} />
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="描述"
                    key="resourceDesc"
                    hasFeedback
                >
                    {getFieldDecorator('resourceDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }],
                        initialValue: ''
                    })(
                        <Input type="textarea" rows={4} />
                    )}
                </FormItem>
            ];
        } else {
            return [
                <FormItem
                    {...formItemLayout}
                    label="选择目标替换资源"
                    key="id"
                    hasFeedback
                >
                    {getFieldDecorator('id', {
                        rules: [{
                            required: true, message: '替换资源为必选！'
                        }, {
                            validator: this.checkNotDir.bind(this)
                        }],
                        initialValue: get(resourceData, 'id') || this.getRootNode()
                    })(
                        <FolderTree loadData={this.loadData.bind(this)} treeData={treeData} isSelect={true} />
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="资源类型"
                    hasFeedback
                    key="resourceType"
                >
                    {getFieldDecorator('resourceType', {
                        rules: [{
                            required: true, message: '资源类型不可为空!'
                        }],
                        initialValue: fileType
                    })(
                        <Select onChange={(value: any) => {
                            this.setState({
                                fileType: value
                            })
                            this.resetFile();
                        }}>
                            <Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>{RESOURCE_TYPE[RESOURCE_TYPE.JAR]}</Option>
                            <Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>{RESOURCE_TYPE[RESOURCE_TYPE.PY]}</Option>
                            <Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>{RESOURCE_TYPE[RESOURCE_TYPE.EGG]}</Option>
                            <Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>{RESOURCE_TYPE[RESOURCE_TYPE.ZIP]}</Option>
                            <Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>其它</Option>
                        </Select>
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="上传"
                    key="file"
                    hasFeedback
                >
                    {getFieldDecorator('file', {
                        rules: [{
                            required: true, message: '请选择上传文件'
                        }, {
                            validator: this.validateFileType
                        }]
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn"
                                htmlFor="myOfflinFile">选择文件</label>
                            <span> {file.files && file.files.length && file.files[0].name}</span>
                            <input
                                name="file"
                                type="file"
                                id="myOfflinFile"
                                accept={accept}
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="描述"
                    key="resourceDesc"
                    hasFeedback
                >
                    {getFieldDecorator('resourceDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }],
                        initialValue: resourceData.resourceDesc
                    })(
                        <Input type="textarea" rows={4} />
                    )}
                </FormItem>
            ]
        }
    }

    render () {
        return (
            <Form>
                {this.renderFormItem()}
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

    /**
     * @description 检查所选是否为文件夹
     * @param {any} rule
     * @param {any} value
     * @param {any} cb
     */
    checkNotDir (rule: any, value: any, callback: any) {
        const { treeData } = this.props;
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

        loop(treeData);

        if (nodeType === 'folder') {
            /* eslint-disable-next-line */
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }
}

const ResFormWrapper = Form.create<any>()(ResForm);
class ResModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.state = {
            file: ''
        };

        this.dtcount = 0;
    }

    handleSubmit () {
        const form = this.form;
        const { resourceData, isCoverUpload } = this.props;
        form.validateFields(async (err: any, values: any) => {
            if (!err) {
                values.file = this.state.file.files[0];
                this.setState({
                    loading: true
                })
                let res = await this.props.addResource(values, resourceData, isCoverUpload);
                if (res) {
                    this.setState({
                        loading: false
                    })
                    this.closeModal();
                    this.setState({ file: '' });
                    form.resetFields();
                } else {
                    this.setState({ loading: false })
                }
            }
        });
    }

    handleCancel = () => {
        this.closeModal();
    }

    closeModal () {
        this.dtcount++;
        this.props.onCancel()
    }

    handleFileChange (file: any) {
        this.setState({ file });
    }

    render () {
        const { visible,
            resourceData, isCoverUpload, type
        } = this.props;
        const treeData = this.props[type]; // 资源目录
        const { loading } = this.state;
        return (
            <div id="JS_upload_modal">
                <Modal
                    title={isCoverUpload ? '替换资源' : '上传资源'}
                    visible={visible}
                    footer={[
                        <Button key="back" size="large" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" loading={loading} type="primary" size="large" onClick={this.handleSubmit}> 确认 </Button>
                    ]}
                    key={this.dtcount}
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_upload_modal')}
                >
                    <ResFormWrapper
                        ref={(el: any) => this.form = el}
                        treeData={treeData}
                        handleFileChange={this.handleFileChange.bind(this)}
                        resourceData={resourceData || {}}
                        isCoverUpload={isCoverUpload}
                        loadTreeData={this.props.loadTreeData}
                        type={type}
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        [siderBarType.notebook]: state.notebook.files,
        [siderBarType.experiment]: state.experiment.files,
        [siderBarType.resource]: state.resource.files
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(resourceActions, dispatch)
    }
})(ResModal);
