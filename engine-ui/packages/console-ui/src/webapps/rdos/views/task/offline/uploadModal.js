import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Icon, Input, Select, message } from 'antd';
import assign from 'object-assign';

import ajax from '../../../api';

import {
    modalAction,
    resTreeAction
} from '../../../store/modules/offlineTask/actionType';
import { formItemLayout, MENU_TYPE, RESOURCE_TYPE } from '../../../comm/const'

import FolderPicker from './folderTree';

const FormItem = Form.Item;
const Option = Select.Option;

class ResForm extends React.Component {
    constructor(props) {
        super(props);

        this.changeFileType = this.changeFileType.bind(this);
        this.fileChange = this.fileChange.bind(this);
        this.state = {
            file: '',
            accept: '.jar',
            fileType: RESOURCE_TYPE.JAR,
        };
    }

    handleSelectTreeChange(value) {
        this.props.form.setFieldsValue({'nodePid': value});
    }

    handleCoverTargetChange(value) {
        this.props.form.setFieldsValue({ 'id': value });
        this.props.form.validateFields(['id']);
    }

    validateFileType(rule, value, callback) {
        const reg = /\.(jar|sql|py)$/

        if (value && !reg.test(value)) {
            callback('资源文件只能是Jar、SQL或者Python文件!');
        }
        callback();
    }

    changeFileType(value) {
        let acceptType = ''
        switch(value) {
            case RESOURCE_TYPE.JAR:
                acceptType = '.jar'; break;
            case RESOURCE_TYPE.PY:
                acceptType = '.py,.zip,.egg'; break;
            default:
                acceptType = ''; break;
        }
        this.setState({
            accept: acceptType,
            fileType: value,
        });
    }

    fileChange(e) {
        const file = e.target;

        this.setState({ file });
        this.props.handleFileChange(file);
    }

    renderFormItem = () => {
        const { file } = this.state;
        const { getFieldDecorator } = this.props.form;
        const {
            defaultData, isEditExist, isCreateFromMenu,
            isCreateNormal, isCoverUpload
        } = this.props;
        
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
                            required: true, message: '资源名称不可为空!',
                        }, {
                            pattern: /^[A-Za-z0-9_-]+$/,
                            message: '资源名称只能由字母、数字、下划线组成!',
                        }, {
                            max: 20,
                            message: '资源名称不得超过20个字符!',
                        }],
                    })(
                        <Input placeholder="请输入资源名称" />,
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="资源类型"
                    key="resourceType"
                    hasFeedback
                >
                    {getFieldDecorator('resourceType', {
                        rules: [{
                            required: true, message: '资源类型必选！',
                        }],
                        initialValue: RESOURCE_TYPE.JAR,
                    })(
                        <Select onChange={this.changeFileType}>
                            <Option key={RESOURCE_TYPE.JAR} value={RESOURCE_TYPE.JAR}>jar</Option>
                            <Option key={RESOURCE_TYPE.PY} value={RESOURCE_TYPE.PY}>python</Option>
                        </Select>,
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
                            required: true, message: '请选择上传文件',
                        }, {
                            validator: this.validateFileType,
                        }],
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn"
                                htmlFor="myOfflinFile">选择文件</label>
                            <span> {file.files && file.files[0].name}</span>
                            <input
                                name="file"
                                type="file"
                                id="myOfflinFile"
                                accept={this.state.accept}
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>,
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
                            required: true, message: '存储位置必选！',
                        }],
                        //@TODO: 编辑时最后的undefined为obj
                        initialValue: isCreateNormal ? this.props.treeData.id : isCreateFromMenu 
                            ? defaultData.parentId : undefined
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={MENU_TYPE.RESOURCE}
                        ispicker
                        treeData={this.props.treeData}
                        onChange={this.handleSelectTreeChange.bind(this)}
                        defaultNode={isCreateNormal ? this.props.treeData.name :
                            isCreateFromMenu ? this.getFolderName(defaultData.parentId) :
                                undefined
                        }
                    />
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
                            message: '描述请控制在200个字符以内！',
                        }],
                        initialValue: '',
                    })(
                        <Input type="textarea" rows={4} />,
                    )}
                </FormItem>,
                <FormItem key="computeType" style={{ display: 'none' }}>
                    {getFieldDecorator('computeType', {
                        initialValue: 1
                    })(
                        <Input type="hidden"></Input>
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
                            required: true, message: '替换资源为必选！',
                        }, {
                            validator: this.checkNotDir.bind(this)
                        }],
                        initialValue: isCreateNormal ? this.props.treeData.id : isCreateFromMenu 
                            ? defaultData.parentId : isEditExist ? defaultData.id : undefined
                    })(
                        <Input type="hidden"></Input>
                        )}
                    <FolderPicker
                        type={MENU_TYPE.RESOURCE}
                        ispicker
                        isFilepicker
                        treeData={this.props.treeData}
                        onChange={this.handleCoverTargetChange.bind(this)}
                        defaultNode={isCreateNormal ? this.props.treeData.name :
                            isCreateFromMenu ? this.getFolderName(defaultData.parentId) :
                                isEditExist ? defaultData.name : undefined
                        }
                    />
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="上传"
                    key="file"
                    hasFeedback
                >
                    {getFieldDecorator('file', {
                        rules: [{
                            required: true, message: '请选择上传文件',
                        }, {
                            validator: this.validateFileType,
                        }],
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn"
                                htmlFor="myOfflinFile">选择文件</label>
                            <span> {file.files && file.files[0].name}</span>
                            <input
                                name="file"
                                type="file"
                                id="myOfflinFile"
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>,
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
                            message: '描述请控制在200个字符以内！',
                        }],
                        initialValue: '',
                    })(
                        <Input type="textarea" rows={4} />,
                    )}
                </FormItem>
            ]
        }
    }

    render() {
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
    getFolderName(id) {
        const { treeData } = this.props;
        let name;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if(node.id === id) {
                    name = node.name;
                }
                else{
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
    checkNotDir(rule, value, callback) {
        const { treeData } = this.props;
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

        loop([treeData]);

        if (nodeType === 'folder') {
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }
}

const ResFormWrapper = Form.create()(ResForm);

class ResModal extends React.Component {
    constructor(props) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.state = {
            file: ''
        };

        this.dtcount = 0;
    }

    shouldComponentUpdate(nextProps, nextState) {
        return this.props !== nextProps;
    }

    handleSubmit() {
        const { isModalShow, toggleUploadModal, addResource } = this.props;
        const form = this.form;

        form.validateFields((err, values) => {
            if(!err) {
                this.closeModal();
                values.file = this.state.file.files[0];
                addResource(values);
                setTimeout(()=> {
                    this.setState({ file: '' })
                    form.resetFields();
                }, 500);
            }
        });
    }

    handleCancel() {
        const { isModalShow, toggleUploadModal } = this.props;

        this.closeModal();
    }

    closeModal() {
        this.dtcount++;
        this.props.toggleUploadModal();
        this.props.emptyModalDefault();
    }

    handleFileChange(file) {
        this.setState({ file });
    }

    render() {
        const { 
            isModalShow, toggleUploadModal, 
            resourceTreeData, defaultData, isCoverUpload, 
        } = this.props;

        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;

        return (
            <div>
                <Modal
                    title={ isCoverUpload ? '替换离线资源' : isEditExist ? '编辑资源' : '上传离线计算资源' }
                    visible={ isModalShow }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    key={ this.dtcount }
                    onCancel={this.handleCancel}
                >
                    <ResFormWrapper
                        ref={el => this.form = el}
                        treeData={ resourceTreeData }
                        handleFileChange={this.handleFileChange.bind(this)}
                        defaultData={ defaultData }
                        isCreateNormal={ isCreateNormal }
                        isCreateFromMenu={ isCreateFromMenu }
                        isCoverUpload={ isCoverUpload }
                        isEditExist={ isEditExist }
                    />
                </Modal>
            </div>
        )
    }
}

export default connect(state => {
    return {
        isModalShow: state.offlineTask.modalShow.upload,
        isCoverUpload: state.offlineTask.modalShow.isCoverUpload,
        resourceTreeData: state.offlineTask.resourceTree,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
    }
},
dispatch => {
    return {
        toggleUploadModal: function() {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD
            });
        },

        addResource: function(params) {
            ajax.addOfflineResource(params)
                .then(res => {
                    let {data} = res;

                    if(res.code === 1) {
                        message.success('资源上传成功！')
                        dispatch({
                            type: resTreeAction.ADD_FOLDER_CHILD,
                            payload: data
                        });
                    }
                })
        },
        emptyModalDefault() {
            dispatch({
                type: modalAction.EMPTY_MODAL_DEFAULT
            });
        }
    }
})(ResModal);