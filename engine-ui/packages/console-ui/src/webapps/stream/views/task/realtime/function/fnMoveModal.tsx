import * as React from 'react';
import { isEmpty } from 'lodash';
import { Modal, Button, Form, Input } from 'antd';

import { getContainer } from 'funcs';
import { MENU_TYPE, formItemLayout } from '../../../../comm/const';

import FolderPicker from '../folderTree';

const FormItem = Form.Item;

class FnMoveForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const {
            defaultData, fnTreeData, loadTreeData
        } = this.props;

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="函数名称"
                    hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true
                        }],
                        initialValue: !isEmpty(defaultData) ? defaultData.name : ''
                    })(
                        <Input type="text" disabled={true}/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择目标存储位置"
                    hasFeedback
                >
                    {getFieldDecorator('nodePid', {
                        rules: [{
                            required: true, message: '存储位置必选！'
                        }],
                        initialValue: !isEmpty(defaultData) ? defaultData.parentId : ''
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
}

const FnMoveFormWrapper = Form.create<any>()(FnMoveForm);

class FnMoveModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }

    shouldComponentUpdate (nextProps: any, nextState: any) {
        return this.props !== nextProps;
    }

    handleSubmit () {
        const { handOk, defaultData } = this.props;
        const form = this.form;

        form.validateFields((err: any, values: any) => {
            if (!err) {
                values.funcId = defaultData.id;
                handOk(values);
                setTimeout(() => {
                    form.resetFields()
                }, 100)
            }
        });
    }

    handleCancel () {
        this.form.resetFields();
        this.props.handCancel();
    }

    render () {
        const {
            visible, defaultData,
            fnTreeData, loadTreeData
        } = this.props;

        const customFuncs = fnTreeData && fnTreeData.find((item: any) => {
            return item.catalogueType === MENU_TYPE.COSTOMFUC
        })

        return (
            <div id="JS_func_modal">
                <Modal
                    title="移动函数"
                    visible={ visible }
                    footer={[
                        <Button key="back" size="large" onClick={ this.handleCancel }>取消</Button>,
                        <Button key="submit" type="primary" size="large" onClick={ this.handleSubmit }> 确认 </Button>
                    ]}
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_func_modal')}
                >
                    <FnMoveFormWrapper
                        ref={(el: any) => this.form = el}
                        loadTreeData={ loadTreeData }
                        fnTreeData={ customFuncs ? [customFuncs] : [] }
                        defaultData={defaultData}
                    />
                </Modal>
            </div>
        )
    }
}

export default FnMoveModal;
