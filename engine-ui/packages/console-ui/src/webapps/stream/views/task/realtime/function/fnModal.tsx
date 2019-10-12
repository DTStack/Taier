import * as React from 'react';
import {
    Modal, Button, Form, Input, Radio
} from 'antd';
import { isEmpty } from 'lodash';

import { getContainer } from 'funcs';
import { formItemLayout, MENU_TYPE } from '../../../../comm/const';

import FolderPicker from '../folderTree';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class FnForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const {
            resTreeData, fnTreeData,
            activeNode, loadTreeData
        } = this.props;
        const rowFix = {
            rows: 4
        }
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
                            pattern: /^[A-Za-z0-9_-]+$/,
                            message: '函数名称只能由字母、数字、下划线组成!'
                        }, {
                            max: 20,
                            message: '函数名称不得超过20个字符！'
                        }],
                        initialValue: ''
                    })(
                        <Input placeholder="请输入函数名称" />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="函数类型"
                    hasFeedback
                >
                    {getFieldDecorator('udfType', {
                        rules: [{
                            required: true
                        }],
                        initialValue: 0
                    })(
                        <RadioGroup>
                            <Radio key={0} value={0}>UDF</Radio>
                            <Radio key={1} value={1}>UDTF</Radio>
                            <Radio key={2} value={2}>UDAF</Radio>
                        </RadioGroup>
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
                            message: '请输入有效的类名'
                        }]
                    })(
                        <Input placeholder="com.test.Example"></Input>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="资源"
                    hasFeedback
                >
                    {getFieldDecorator('resourceId', {
                        rules: [{
                            required: true, message: '请选择关联资源'
                        }, {
                            validator: this.checkNotDir.bind(this)
                        }]
                    })(
                        <FolderPicker
                            isPicker
                            placeholder="请选择关联资源"
                            treeData={ resTreeData }
                            loadData={ loadTreeData }
                        />
                    )}
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
                    label="命令格式"
                    hasFeedback
                >
                    {getFieldDecorator('commandFormat', {
                        rules: [{
                            required: true, message: '命令格式不能为空'
                        }, {
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }]
                    })(
                        <Input type="textarea" {...rowFix} placeholder="请输入函数的命令格式，例如：datetime dateadd(datetime date, bigint delta, string datepart)"/>
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
                        <Input type="textarea" {...rowFix} placeholder="请输入函数的参数说明"/>
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
                        initialValue: !isEmpty(activeNode) ? activeNode.id : ''
                    })(
                        <FolderPicker
                            isPicker
                            isFolderPicker
                            treeData={ fnTreeData }
                            loadData={ loadTreeData }
                        />
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
        let loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                if (node.id === value) {
                    nodeType = node.type;
                } else {
                    loop(node.children || []);
                }
            });
        };
        loop(resTreeData);
        if (nodeType === 'folder') {
            const error = '请选择具体文件, 而非文件夹'
            callback(error);
        }
        callback();
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName (id: any) {
        const { fnTreeData } = this.props;
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

        loop([fnTreeData]);

        return name;
    }
}

const FnFormWrapper = Form.create<any>()(FnForm);

class FnModal extends React.Component<any, any> {
    form: any;
    dtcount: any;
    constructor (props: any) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
    }

    shouldComponentUpdate (nextProps: any, nextState: any) {
        return this.props !== nextProps;
    }

    handleSubmit () {
        const { handOk } = this.props;
        const form = this.form;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                handOk(values);
                setTimeout(() => {
                    form.resetFields();
                }, 500);
            }
        });
    }

    handleCancel () {
        const { handCancel } = this.props;
        this.form.resetFields();
        if (handCancel) handCancel();
    }

    render () {
        const {
            visible,
            activeNode,
            fnTreeData,
            resTreeData,
            loadTreeData
        } = this.props;

        const customFuncs = fnTreeData.find((item: any) => {
            return item.catalogueType === MENU_TYPE.COSTOMFUC
        })
        return (
            <div id="JS_create_func">
                <Modal
                    title="创建函数"
                    visible={ visible }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_create_func')}
                >
                    <FnFormWrapper
                        ref={(el: any) => this.form = el}
                        fnTreeData={ customFuncs ? [customFuncs] : [] }
                        resTreeData={ resTreeData }
                        activeNode={ activeNode }
                        loadTreeData={ loadTreeData }
                    />
                </Modal>
            </div>
        )
    }
}

export default FnModal;
