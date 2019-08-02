import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Form, Input } from 'antd';

import { getContainer } from 'funcs';
import ajax from '../../../api';

import FolderPicker from './folderTree';
import { MENU_TYPE, formItemLayout } from '../../../comm/const';

import {
    modalAction,
    fnTreeAction
} from '../../../store/modules/offlineTask/actionType';

const FormItem = Form.Item;

class FnMoveForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    handleSelectTreeChange (value: any) {
        this.props.form.setFieldsValue({ 'nodePid': value });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { originFn, isVisible } = this.props;
        const { parentId, name } = originFn;

        return !isVisible ? null : (
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
                            max: 20,
                            message: '函数名称不得超过20个字符！'
                        }],
                        initialValue: name
                    })(
                        <Input disabled placeholder="请输入函数名称" />
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
                        initialValue: parentId
                    })(
                        <Input type="hidden"></Input>
                    )}
                    <FolderPicker
                        type={MENU_TYPE.FUNCTION}
                        ispicker
                        treeData={ this.props.functionTreeData }
                        onChange={ this.handleSelectTreeChange.bind(this) }
                        defaultNode={ this.getFolderName(parentId) }
                    />
                </FormItem>
            </Form>
        )
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName (id: any) {
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

const FnMoveFormWrapper = Form.create<any>()(FnMoveForm);

class FnMoveModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }
    dtcount: number;
    form: any;
    shouldComponentUpdate (nextProps: any, nextState: any) {
        return this.props !== nextProps;
    }

    handleSubmit () {
        const { doMoveFn, moveFnData } = this.props;
        const functionId = moveFnData.originFn.id;
        const form = this.form;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                values.functionId = functionId;
                delete values.name;

                doMoveFn(values, moveFnData.originFn)
                    .then(
                        (success: any) => {
                            if (success) {
                                this.closeModal();
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
        this.props.toggleMoveFn();
    }

    render () {
        const { moveFnData, functionTreeData } = this.props;
        const isVisible = typeof moveFnData !== 'undefined';

        return (
            <div id="JS_fnMove_modal">
                <Modal
                    title="移动函数"
                    visible={ isVisible }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    key={ this.dtcount }
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_fnMove_modal')}
                >
                    <FnMoveFormWrapper
                        ref={(el: any) => this.form = el}
                        functionTreeData={ functionTreeData }
                        { ...moveFnData }
                    />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        moveFnData: state.offlineTask.modalShow.moveFnData,
        functionTreeData: state.offlineTask.functionTree
    }
},
(dispatch: any) => {
    return {
        toggleMoveFn: function () {
            dispatch({
                type: modalAction.TOGGLE_MOVE_FN
            });
        },

        doMoveFn: function (params: any, originFn: any) {
            return ajax.moveOfflineFn(params)
                .then((res: any) => {
                    if (res.code === 1) {
                        let newData = originFn;

                        newData.originPid = originFn.parentId;
                        newData.parentId = params.nodePid;

                        dispatch({
                            type: fnTreeAction.EDIT_FOLDER_CHILD,
                            payload: newData
                        });
                        return true;
                    }
                })
        }
    }
})(FnMoveModal);
