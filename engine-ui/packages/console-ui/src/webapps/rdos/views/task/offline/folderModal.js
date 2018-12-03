import React from 'react';
import { connect } from 'react-redux';
import assign from 'object-assign';
import { Modal, Button, Form, Input } from 'antd';

import FolderPicker from './folderTree';

import ajax from '../../../api';
import { getContainer } from 'funcs';

import {
    modalAction
} from '../../../store/modules/offlineTask/actionType';
import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';
import { formItemLayout, MENU_TYPE } from '../../../comm/const'

const FormItem = Form.Item;

class FolderForm extends React.Component {
    constructor (props) {
        super(props);
    }

    handleSelectTreeChange (value) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { defaultData } = this.props;
        // 没有默认数据
        const isCreateNormal = typeof defaultData === 'undefined';

        return (
            <Form>
                <FormItem
                    key="nodeName"
                    label="目录名称"
                    {...formItemLayout}
                    hasFeedback
                >
                    {getFieldDecorator('nodeName', {
                        rules: [
                            {
                                max: 20,
                                message: '项目名称不得超过20个字符！'
                            },
                            {
                                required: true,
                                message: '文件夹名称不能为空'
                            }
                        ],
                        initialValue: isCreateNormal ? undefined : defaultData.name
                    })(
                        <Input type="text" placeholder="文件夹名称" />
                    )}
                </FormItem>
                <FormItem
                    key="nodePid"
                    label="选择目录位置"
                    {...formItemLayout}
                >
                    {getFieldDecorator('nodePid', {
                        rules: [
                            {
                                required: true,
                                message: '请选择目录位置'
                            }
                        ],
                        initialValue: isCreateNormal ? this.props.treeData.id : defaultData.parentId
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={ this.props.cateType }
                        ispicker
                        id="Task_CREATE_FOLDER"
                        treeData={ this.props.treeData }
                        onChange={ this.handleSelectTreeChange.bind(this) }
                        defaultNode={ isCreateNormal ?
                            this.props.treeData.name :
                            this.getFolderName(defaultData)
                        }
                    />
                </FormItem>
            </Form>
        );
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName(data) {
        const { treeData } = this.props;
        let name;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if(node.id === data.parentId && node.type === data.type) {
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

const FolderFormWrapper = Form.create()(FolderForm);

class FolderModal extends React.Component {
    constructor (props) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }

    handleSubmit () {
        const { cateType, defaultData } = this.props;
        const form = this.form;

        form.validateFields((err, values) => {
            if (!err) {
                if (this.isCreate) {
                    this.props.addOfflineCatalogue(values, cateType)
                        .then(success => {
                            if (success) {
                                this.closeModal();
                                form.resetFields();
                            }
                        });
                } else {
                    this.props.editOfflineCatalogue(assign(values, {
                        id: defaultData.id
                    }), defaultData, cateType)
                        .then(success => {
                            if (success) {
                                this.closeModal();
                                form.resetFields();
                            }
                        });
                }
            }
        })
    }

    handleCancel () {
        this.closeModal();
    }

    closeModal () {
        const { toggleCreateFolder, emptyModalDefault } = this.props;

        this.dtcount++;

        emptyModalDefault();
        toggleCreateFolder();
    }

    getTreeData (cateType) {
        switch (cateType) {
        case MENU_TYPE.TASK:
        case MENU_TYPE.TASK_DEV:
            return this.props.taskTreeData;
        case MENU_TYPE.RESOURCE:
            return this.props.resourceTreeData;
        case MENU_TYPE.COSTOMFUC:
        case MENU_TYPE.FUNCTION:
        case MENU_TYPE.SYSFUC:
            return this.props.functionTreeData;
        case MENU_TYPE.SCRIPT:
            return this.props.scriptTreeData;
        default:
            return this.props.taskTreeData;
        }
    }

    render () {
        const { isModalShow, cateType, defaultData } = this.props;

        if (!defaultData) this.isCreate = true;
        else {
            if (!defaultData.name) this.isCreate = true;
            else this.isCreate = false;
        }

        return (
            <div id="JS_folder_modal">
                <Modal
                    title={ !this.isCreate ? '编辑文件夹' : '新建文件夹' }
                    visible={ isModalShow }
                    key={ this.dtcount }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_folder_modal')}
                >
                    <FolderFormWrapper
                        ref={el => this.form = el}
                        treeData={ this.getTreeData(cateType) }
                        defaultData={ defaultData }
                    />
                </Modal>
            </div>
        )
    }
}

export default connect(state => {
    const { offlineTask } = state;

    return {
        isModalShow: state.offlineTask.modalShow.createFolder,
        cateType: state.offlineTask.modalShow.cateType,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        taskTreeData: offlineTask.taskTree,
        resourceTreeData: offlineTask.resourceTree,
        functionTreeData: offlineTask.functionTree,
        scriptTreeData: offlineTask.scriptTree
    }
},
dispatch => {
    const benchActions = workbenchActions(dispatch)
    return {
        toggleCreateFolder: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FOLDER
            });
        },
        addOfflineCatalogue: function (params, cateType) {
            return ajax.addOfflineCatalogue(params)
                .then(res => {
                    if(res.code === 1) {
                        benchActions.loadTreeNode(params.nodePid, cateType)
                        return true;
                    }
                });
        },

        editOfflineCatalogue: function (params, defaultData, cateType) {
            return ajax.editOfflineCatalogue(params)
                .then(res => {
                    if (res.code === 1) {
                        let newData = defaultData;
                        
                        newData.name = params.nodeName;
                        newData.originPid = defaultData.parentId;
                        newData.parentId = params.nodePid;

                        benchActions.loadTreeNode(params.nodePid, cateType)
                        return true;
                    }
                })
        },
        emptyModalDefault () {
            dispatch({
                type: modalAction.EMPTY_MODAL_DEFAULT
            });
        }
    }
})(FolderModal);
