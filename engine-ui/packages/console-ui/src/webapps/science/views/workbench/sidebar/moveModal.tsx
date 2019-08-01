import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';
import { Modal, Form, Input } from 'antd';

import { formItemLayout, siderBarType } from '../../../consts';
import api from '../../../api';
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
    return {
        ...bindActionCreators(fileTreeActions, dispatch)
    };
}) as any)
class NewFolder extends React.Component<any, any> {
    state: any = {
        modalKey: null
    }
    loadData(node: any) {
        return this.props.loadTreeData(this.props.type, node.props.data.id);
    }
    async moveFile(values: any) {
        const { type, data } = this.props;
        let res: any;
        res = await api.fileTree.updateFolder({
            nodeName: values.fileName,
            nodePid: values.nodePid,
            id: get(data, 'id'),
            isFile: 1
        });
        if (res && res.code == 1) {
            this.props.loadTreeData(type, get(data, 'nodePid'));
            this.props.loadTreeData(type, values.nodePid);
            this.props.onOk();
            this.resetForm();
        }
    }
    onOk = () => {
        const { form, type, data } = this.props;
        const isFile = get(data, 'isFile');
        const { validateFields } = form;
        validateFields(async (err: any, values: any) => {
            if (!err) {
                if (values.nodePid == get(data, 'nodePid')) {
                    return;
                }
                if (isFile) {
                    this.moveFile(values);
                } else {
                    let res: any;
                    res = await this.props.updateFolder(data.id, type, values.folderName, values.nodePid);
                    this.props.loadTreeData(type, get(data, 'nodePid'));
                    this.props.removeExpandedkey(type, data.key);
                    if (res) {
                        this.props.onOk();
                        this.resetForm();
                    }
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
        const isFile = get(data, 'isFile');
        const files = this.props[type];
        const { getFieldDecorator } = form;
        return (
            <Modal
                title={'编辑文件夹'}
                visible={this.props.visible}
                onOk={this.onOk}
                key={modalKey}
                onCancel={this.onCancel}
            >
                <Form>
                    <FormItem
                        label={isFile ? '名称' : '文件夹名称'}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(isFile ? 'fileName' : 'folderName', {
                            initialValue: get(data, 'name')
                        })(
                            <Input disabled />
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
                            <FolderTree
                                disabledNode={isFile ? [] : [`${get(data, 'id')}`]}
                                loadData={this.loadData.bind(this)}
                                treeData={files}
                                isSelect={true}
                                hideFiles={true}
                            />
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(NewFolder);
