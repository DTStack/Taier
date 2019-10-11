import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input, notification } from 'antd';

import { getContainer } from 'funcs';

import FolderPicker from './folderTree';
import ajax from '../../../api';
import { MENU_TYPE } from '../../../comm/const';
import {
    modalAction,
    fnTreeAction
} from '../../../store/modules/offlineTask/actionType';

const FormItem = Form.Item;

class FnForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    handleResSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'resources': value });
        this.props.form.validateFields(['resources']);
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { defaultData, isCreateFromMenu, isCreateNormal } = this.props;

        const formItemLayout: any = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 6 }
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 14 }
            }
        };

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="函数名称"
                    hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true, message: '函数名称不可为空！'
                        }, {
                            pattern: /^[-a-z0-9_]+$/,
                            message: '函数名称只能由小写字母、数字、下划线、短横线组成!'
                        }, {
                            max: 20,
                            message: '函数名称不得超过20个字符！'
                        }]
                    })(
                        <Input placeholder="请输入函数名称" />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="类名"
                    hasFeedback
                >
                    {getFieldDecorator('className', {
                        rules: [{
                            required: true, message: '类名不能为空'
                        }, {
                            pattern: /^[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,
                            message: '请输入有效的类名!'
                        }]
                    })(
                        <Input placeholder="请输入类名"></Input>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="资源"
                    hasFeedback
                >
                    {getFieldDecorator('resources', {
                        rules: [{
                            required: true, message: '请选择关联资源'
                        }, {
                            validator: this.checkNotDir.bind(this)
                        }]
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={MENU_TYPE.RESOURCE}
                        ispicker
                        isFilepicker
                        treeData={ this.props.resTreeData }
                        onChange={ this.handleResSelectTreeChange.bind(this) }
                    />
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="用途"
                    hasFeedback
                >
                    {getFieldDecorator('purpose')(
                        <Input placeholder=""></Input>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="参数说明"
                    hasFeedback
                >
                    {getFieldDecorator('paramDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }]
                    })(
                        <Input.TextArea rows={4} placeholder="请输入函数的参数说明"/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="调用测试"
                    hasFeedback
                >
                    {getFieldDecorator('commandFormate', {
                        rules: [{
                            required: false, message: '调用测试不能为空'
                        }, {
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }]
                    })(
                        <Input.TextArea rows={4} placeholder="请输入调用函数的命令，例如：dateFormat('20190101', 'yyyyMMdd', 'yyyy-MM-dd')，将返回2019-01-01"/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择存储位置"
                    hasFeedback
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: isCreateNormal ? this.props.functionTreeData.id
                            : isCreateFromMenu ? defaultData.parentId : undefined
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={MENU_TYPE.FUNCTION}
                        ispicker
                        treeData={ this.props.functionTreeData }
                        onChange={ this.handleSelectTreeChange.bind(this) }
                        defaultNode={ isCreateNormal ? this.props.functionTreeData.name
                            : isCreateFromMenu ? this.getFolderName(defaultData.parentId) : undefined
                        }
                    />
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
        const { functionTreeData } = this.props;
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

        loop([functionTreeData]);

        return name;
    }
}

const FnFormWrapper = Form.create<any>()(FnForm);

class FnModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    form: any;
    dtcount: number;
    shouldComponentUpdate (nextProps: any, nextState: any) {
        return this.props !== nextProps;
    }

    handleSubmit () {
        const { addFn, engineType } = this.props;
        const form = this.form;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                addFn(Object.assign(values, { engineType: engineType }))
                    .then(
                        (res: any) => {
                            if (res && res.code == 1) {
                                const msg = res.data && res.data.list && res.data.list[1];
                                notification.success({
                                    message: '创建成功',
                                    description: `测试样例返回结果: ${msg || '无'}`
                                  });
                                this.closeModal();
                                form.resetFields();
                            }
                        }
                    );
            }
        });
    }

    handleCancel () {
        this.closeModal();
    }

    closeModal () {
        this.dtcount++;
        this.props.emptyModalDefault();
        this.props.toggleCreateFn();
    }

    render () {
        const { isModalShow, functionTreeData, resTreeData, defaultData } = this.props;

        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;

        return (
            <div id="JS_func_modal">
                <Modal
                    title="创建函数"
                    visible={ isModalShow }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    key={ this.dtcount }
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_func_modal')}
                >
                    <FnFormWrapper
                        ref={(el: any) => this.form = el}
                        functionTreeData={ functionTreeData }
                        resTreeData={ resTreeData }
                        isCreateFromMenu={ isCreateFromMenu }
                        isCreateNormal={ isCreateNormal }
                        isEditExist={ isEditExist }
                        defaultData={ defaultData }
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        isModalShow: state.offlineTask.modalShow.createFn,
        functionTreeData: state.offlineTask.functionTree,
        resTreeData: state.offlineTask.resourceTree,
        engineType: state.offlineTask.modalShow.engineType,
        defaultData: state.offlineTask.modalShow.defaultData // 表单默认数据
    }
},
(dispatch: any) => {
    return {
        toggleCreateFn: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FN
            });
        },

        addFn: function(params: any) {
            return ajax.addOfflineFunction(params)
                .then((res: any) => {
                    console.log(res);
                    let { data } = res;
                    if (res.code === 1) {
                        dispatch({
                            type: fnTreeAction.ADD_FOLDER_CHILD,
                            payload: data
                        });
                        return res;
                    }
                })
        },
        emptyModalDefault () {
            dispatch({
                type: modalAction.EMPTY_MODAL_DEFAULT
            });
        }
    }
})(FnModal);
