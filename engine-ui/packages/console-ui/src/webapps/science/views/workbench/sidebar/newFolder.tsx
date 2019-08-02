import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';
import { Modal, Form, Input } from 'antd';

import { formItemLayout, siderBarType } from '../../../consts';
import FolderTree from '../../../components/folderTree';
import * as fileTreeActions from '../../../actions/base/fileTree';

const FormItem = Form.Item;

@(connect((state: any) => {
    return {
        [siderBarType.notebook]: state.notebook.files,
        [siderBarType.experiment]: state.experiment.files,
        [siderBarType.resource]: state.resource.files
    }
}, (dispatch: any) => {
    const actions = bindActionCreators(fileTreeActions, dispatch);
    return actions;
}) as any)
class NewFolder extends React.Component<any, any> {
    state: any = {
        modalKey: null
    }
    loadData (node: any) {
        return this.props.loadTreeData(this.props.type, node.props.data.id);
    }
    onOk = () => {
        const { form, type, data } = this.props;
        const { validateFields } = form;
        validateFields(async (err: any, values: any) => {
            if (!err) {
                let res: any;
                const isEdit = !!get(data, 'id');
                if (isEdit) {
                    res = await this.props.updateFolder(data.id, type, values.folderName, values.nodePid);
                } else {
                    res = await this.props.addFolder(type, values.folderName, values.nodePid);
                }
                if (res) {
                    this.props.onOk();
                    this.resetForm();
                }
            }
        })
    }
    resetForm = () => {
        this.setState({
            modalKey: Math.random()
        })
    }
    onCancel = () => {
        this.resetForm();
        this.props.onCancel();
    }
    render () {
        const { modalKey } = this.state;
        const { form, data, type } = this.props;
        const files = this.props[type];
        const { getFieldDecorator } = form;
        const isEdit = !!get(data, 'id');
        return (
            <Modal
                title={isEdit ? '编辑文件夹' : '新建文件夹'}
                visible={this.props.visible}
                onOk={this.onOk}
                key={modalKey}
                onCancel={this.onCancel}
            >
                <Form>
                    <FormItem
                        label='文件夹名称'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('folderName', {
                            rules: [{
                                required: true,
                                message: '请输入文件夹名称'
                            }, {
                                max: 32, message: '不超过32个字符'
                            }],
                            initialValue: get(data, 'name')
                        })(
                            <Input placeholder='请输入文件夹名' />
                        )}
                    </FormItem>
                    <FormItem
                        label='父节点'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('nodePid', {
                            rules: [{
                                required: true,
                                message: '请选择父节点'
                            }],
                            initialValue: get(data, 'nodePid')
                        })(
                            <FolderTree disabled={isEdit} loadData={this.loadData.bind(this)} treeData={files} isSelect={true} hideFiles={true} />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(NewFolder);
