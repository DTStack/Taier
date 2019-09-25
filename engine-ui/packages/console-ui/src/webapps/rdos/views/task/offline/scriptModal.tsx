import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, Radio } from 'antd';
import ajax from '../../../api';
import { getContainer } from 'funcs';

import {
    modalAction,
    scriptTreeAction,
    workbenchAction
} from '../../../store/modules/offlineTask/actionType';

import { formItemLayout, MENU_TYPE } from '../../../comm/const'
import FolderPicker from './folderTree';
import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class ScriptForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.handleRadioChange = this.handleRadioChange.bind(this);
        this.isEditExist = false;
        this.state = {
            value: 0
        };
    }
    isEditExist: boolean;
    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    handleRadioChange (e: any) {
        this.setState({
            value: e.target.value
        });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { defaultData, scriptTypes = [] } = this.props;

        /**
         * 1. 从按钮新建(createNormal)没有默认数据
         * 2. 有默认数据的情况分以下两种：
         *  a. 编辑任务时,默认数据是task对象
         *  b. 从文件夹新建时默认数据只有一个{ parentId }
         */
        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';

        this.isEditExist = !isCreateNormal && !isCreateFromMenu;

        const defaultScriptType = scriptTypes && scriptTypes.length > 0 ? scriptTypes[0].value : undefined;
        const value = isCreateNormal
            ? defaultScriptType
            : (!isCreateFromMenu ? defaultData.type : defaultScriptType);

        const scriptRadios = scriptTypes.map((item: any) => <Radio key={item.value} value={item.value}>{item.name}</Radio>)
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="脚本名称"
                    hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true, message: '脚本名称不可为空！'
                        }, {
                            max: 64,
                            message: '脚本名称不得超过20个字符！'
                        }, {
                            pattern: /^[A-Za-z0-9_-]+$/,
                            message: '脚本名称只能由字母、数字、下划线组成!'
                        }],
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.name
                    })(
                        <Input placeholder="请输入脚本名称" />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="脚本类型"
                >
                    {getFieldDecorator('type', {
                        rules: [{
                            required: true, message: '请选择脚本类型'
                        }],
                        initialValue: value
                    })(
                        <RadioGroup
                            disabled={ isCreateNormal ? false : !isCreateFromMenu }
                            onChange={ this.handleRadioChange }
                        >
                            {scriptRadios}
                        </RadioGroup>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="存储位置"
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: isCreateNormal ? this.props.treeData.id : isCreateFromMenu ? defaultData.parentId : defaultData.nodePid
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={MENU_TYPE.SCRIPT}
                        ispicker
                        treeData={ this.props.treeData }
                        onChange={ this.handleSelectTreeChange.bind(this) }
                        defaultNode={ isCreateNormal
                            ? this.props.treeData.name
                            : isCreateFromMenu
                                ? this.getFolderName(defaultData.parentId)
                                : this.getFolderName(defaultData.nodePid)
                        }
                    />
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="描述"
                    hasFeedback
                >
                    {getFieldDecorator('scriptDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }],
                        initialValue: isCreateNormal ? undefined : isCreateFromMenu ? undefined : defaultData.scriptDesc
                    })(
                        <Input.TextArea rows={4} placeholder="请输入脚本描述"/>
                    )}
                </FormItem>
            </Form>
        )
    }

    /* eslint-disable */
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

        let loop = (arr: any) => {
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
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }
    /* eslint-disable */

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName(id: any) {
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
}

const ScriptFormWrapper = Form.create<any>()(ScriptForm);

class ScriptModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.closeModal = this.closeModal.bind(this);

        this.dtcount = 0;
    }
    dtcount: number;
    form: any;
    isCreate: boolean;
    handleSubmit () {
        const { defaultData } = this.props;
        const form = this.form;

        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                if (values.resourceIdList) {
                    values.resourceIdList = [values.resourceIdList];
                }
                if (typeof defaultData !== 'undefined' && typeof defaultData.id !== 'undefined') {
                    values.id = defaultData.id
                    values.version = defaultData.version;
                    values.readWriteLockVO = Object.assign({}, defaultData.readWriteLockVO);
                }

                this.props.createScript(values, isEditExist, defaultData)
                    .then((success: any) => {
                        if (success) {
                            this.closeModal();
                            form.resetFields();
                        }
                    });
            }
        })
    }

    closeModal () {
        this.dtcount++;

        this.props.emptyModalDefault();
        this.props.toggleCreateScript();
    }

    render () {
        const { isModalShow, scriptTreeData, defaultData, editModalKey, scriptTypes } = this.props;

        if (!defaultData) this.isCreate = true;
        else {
            if (!defaultData.name) this.isCreate = true;
            else this.isCreate = false;
        }

        return (
            <div id="JS_script_modal">
                <Modal
                    key={editModalKey}
                    title={!this.isCreate ? '编辑脚本' : '新建脚本' }
                    visible={ isModalShow }
                    footer={[
                        <Button key="back"
                            size="large"
                            onClick={this.closeModal }
                        >取消</Button>,
                        <Button key="submit"
                            type="primary"
                            size="large"
                            onClick={ this.handleSubmit }
                        > 确认 </Button>
                    ]}
                    onCancel={this.closeModal}
                    getContainer={() => getContainer('JS_script_modal')}
                >
                    <ScriptFormWrapper
                        ref={(el: any) => this.form = el}
                        treeData={ scriptTreeData }
                        defaultData={ defaultData }
                        scriptTypes={scriptTypes}
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        isModalShow: state.offlineTask.modalShow.createScript,
        scriptTreeData: state.offlineTask.scriptTree,
        defaultData: state.offlineTask.modalShow.defaultData, // 表单默认数据
        editModalKey: state.offlineTask.modalShow.editModalKey,
        scriptTypes: state.offlineTask.comm.scriptTypes
    }
},
(dispatch: any) => {
    const benchActions = workbenchActions(dispatch);

    return {
        toggleCreateScript: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT
            });
        },

        /**
         * @description 新建或编辑
         * @param {any} params 表单参数
         * @param {boolean} isEditExist 是否编辑
         * @param {any} 修改前的数据
         */
        createScript: function (params: any, isEditExist: any, defaultData: any) {
            return ajax.saveScript(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        ajax.getScriptById({
                            id: res.data.id
                        }).then((res2: any) => {
                            if (!isEditExist) { // 编辑脚本
                                dispatch({
                                    type: workbenchAction.LOAD_TASK_DETAIL,
                                    payload: res2.data
                                });

                                dispatch({
                                    type: workbenchAction.OPEN_TASK_TAB,
                                    payload: res2.data.id
                                });
                            } else { // 新建脚本
                                let newData = Object.assign(defaultData, res.data);
                                newData.originPid = defaultData.nodePid
                                dispatch({
                                    type: scriptTreeAction.EDIT_FOLDER_CHILD,
                                    payload: newData
                                });

                                benchActions.updateTabData(res2.data);
                            }
                            benchActions.loadTreeNode(res.data.parentId, MENU_TYPE.SCRIPT)
                        });

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
})(ScriptModal);
