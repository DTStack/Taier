import React from 'react';
import assign from 'object-assign';
import { Modal, Button, Form, Input } from 'antd';

import FolderPicker from '../../components/folderPicker';
import { formItemLayout } from '../../comm/const';
import { getContainer } from '../resourceManager/resModal';

const FormItem = Form.Item;

class FolderForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
    }

    handleSelectTreeChange(value: any) {
        this.props.form.setFieldsValue({ nodePid: value });
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { defaultData, dataType } = this.props;
        // 没有默认数据
        const isCreateNormal = typeof defaultData === 'undefined';
        return (
            <Form>
                <FormItem
                    key="dt_nodeName"
                    label="目录名称"
                    {...formItemLayout}
                >
                    {/* 这里不能直接叫nodeName
                https://github.com/facebook/react/issues/6284 */}
                    {getFieldDecorator('dt_nodeName', {
                        rules: [
                            {
                                max: 20,
                                message: '目录名称不得超过20个字符！',
                            },
                            {
                                required: true,
                                message: '文件夹名称不能为空',
                            },
                        ],
                        initialValue: isCreateNormal
                            ? undefined
                            : defaultData.name,
                    })(<Input type="text" placeholder="文件夹名称" />)}
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
                                message: '请选择目录位置',
                            },
                        ],
                        initialValue: isCreateNormal
                            ? this.props.treeData.id
                            : defaultData.parentId,
                    })(<Input type="hidden"></Input>)}
                    <FolderPicker
                        showFile={false}
                        dataType={dataType}
                        defaultValue={
                            isCreateNormal
                                ? this.props.treeData.id
                                : defaultData.parentId
                        }
                        onChange={this.handleSelectTreeChange.bind(this)}
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
    getFolderName(data: any) {
        const { treeData } = this.props;
        let name: any;
        let loop = (arr: any) => {
            arr.forEach((node: any, i: any) => {
                if (node.id === data.parentId && node.type === data.type) {
                    name = node.id;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop([treeData]);
        return name;
    }
}

const FolderFormWrapper = Form.create<any>()(FolderForm);

class FolderModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.dtcount = 0;
        this.state = {
            loading: false,
        };

        this.wrapper = React.createRef<HTMLDivElement>();
    }
    wrapper: React.RefObject<HTMLDivElement>;
    dtcount: number;
    form: any;
    isCreate: any;
    handleSubmit() {
        const { cateType, defaultData } = this.props;
        const form = this.form;
        this.setState(
            {
                loading: true,
            },
            () => {
                form.validateFields((err: any, values: any) => {
                    if (!err) {
                        values['nodeName'] = values['dt_nodeName'];
                        values['dt_nodeName'] = undefined;
                        if (this.isCreate) {
                            this.props
                                .addOfflineCatalogue(values, cateType)
                                .then((success: any) => {
                                    if (success) {
                                        this.closeModal();
                                        form.resetFields();
                                    }
                                })
                                .finally(() => {
                                    this.setState({
                                        loading: false,
                                    });
                                });
                        } else {
                            this.props
                                .editOfflineCatalogue(
                                    assign(values, {
                                        id: defaultData.id,
                                        type: 'folder', // 文件夹编辑，新增参数固定为folder
                                    }),
                                    defaultData,
                                    cateType
                                )
                                .then((success: any) => {
                                    if (success) {
                                        this.closeModal();
                                        form.resetFields();
                                    }
                                })
                                .finally(() => {
                                    this.setState({
                                        loading: false,
                                    });
                                });
                        }
                    }
                });
            }
        );
    }

    handleCancel() {
        this.closeModal();
    }

    closeModal() {
        const { toggleCreateFolder } = this.props;
        toggleCreateFolder();
        this.dtcount++;
    }

    render() {
        const { isModalShow, defaultData, treeData, dataType } = this.props;
        const { loading } = this.state;

        if (!defaultData) this.isCreate = true;
        else {
            if (!defaultData.name) this.isCreate = true;
            else this.isCreate = false;
        }

        return (
            <div ref={this.wrapper}>
                <Modal
                    title={!this.isCreate ? '编辑文件夹' : '新建文件夹'}
                    visible={isModalShow}
                    key={this.dtcount}
                    footer={[
                        <Button
                            key="back"
                            size="large"
                            onClick={this.handleCancel}
                        >
                            取消
                        </Button>,
                        <Button
                            key="submit"
                            type="primary"
                            size="large"
                            onClick={this.handleSubmit}
                            loading={loading}
                        >
                            {' '}
                            确认{' '}
                        </Button>,
                    ]}
                    onCancel={this.handleCancel}
                    getContainer={() => this.wrapper.current!}
                >
                    <FolderFormWrapper
                        ref={(el: any) => (this.form = el)}
                        treeData={treeData}
                        dataType={dataType}
                        defaultData={defaultData}
                    />
                </Modal>
            </div>
        );
    }
}

export default FolderModal;
