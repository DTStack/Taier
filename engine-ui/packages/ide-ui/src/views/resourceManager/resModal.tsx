import React from 'react';
import { Modal, Button, Form, Input, Select, Icon, Spin } from 'antd';

import ajax from '../../api';
import { RESOURCE_TYPE, formItemLayout } from '../../comm/const';

export function getContainer(id: string) {
    const container = document.createElement('div');
    document.getElementById(id)?.appendChild(container);
    return container;
}

const FormItem = Form.Item;
const Option = Select.Option;
const resourceType: any = RESOURCE_TYPE;

class ResForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.fileChange = this.fileChange.bind(this);
        this.state = {
            file: '',
            accept: '.jar',
            fileType: props.defaultData
                ? props.defaultData.resourceType
                : RESOURCE_TYPE.JAR,
            loading: false,
        };
    }

    handleSelectTreeChange(value: any) {
        this.props.form.setFieldsValue({ nodePid: value });
    }

    handleCoverTargetChange(value: any) {
        this.setState(
            {
                loading: true,
            },
            () => {
                this.getResDetail(value);
            }
        );
    }

    getResDetail(resId: any) {
        if (!resId) return;

        this.props.form.setFieldsValue({ id: resId });

        ajax.getOfflineRes({
            resourceId: resId,
        })
            .then((res: any) => {
                if (res.code === 1) {
                    this.props.form.setFieldsValue({
                        originFileName: res.data?.originFileName || '',
                    });
                    this.props.form.validateFields(['originFileName']);
                }
            })
            .finally(() => {
                this.setState({
                    loading: false,
                });
            });
    }

    validateFileType = (rule: any, value: any, callback: any) => {
        if (!value) {
            // eslint-disable-next-line
            callback();
            return;
        }
        const { fileType } = this.state;
        const fileSuffix = resourceType[fileType];
        const suffix = value.split('.').slice(1).pop();
        if (fileType == RESOURCE_TYPE.OTHER) {
            callback();
            return;
        }
        if (fileSuffix != suffix) {
            // eslint-disable-next-line
            callback(`资源文件只能是${fileSuffix}文件!`);
            return;
        }
        callback();
    };

    fileChange(e: any) {
        const file = e.target;
        this.setState({ file });
        this.props.handleFileChange(file);
    }

    resetFile() {
        this.setState({ file: '' });
        this.props.handleFileChange('');
        this.props.form.resetFields(['file']);
    }

    renderFormItem = () => {
        const { file, fileType } = this.state;
        const { getFieldDecorator } = this.props.form;
        const {
            defaultData,
            isEditExist,
            isCreateFromMenu,
            isCreateNormal,
            isCoverUpload,
        } = this.props;
        let accept: any;
        if (fileType != RESOURCE_TYPE.OTHER) {
            accept = `.${resourceType[fileType]}`;
        }

        if (!isCoverUpload) {
            return [
                <FormItem
                    {...formItemLayout}
                    label="资源名称"
                    key="resourceName"
                >
                    {getFieldDecorator('resourceName', {
                        rules: [
                            {
                                required: true,
                                message: '资源名称不可为空!',
                            },
                            {
                                pattern: /^[A-Za-z0-9_-]+$/,
                                message:
                                    '资源名称只能由字母、数字、下划线组成!',
                            },
                            {
                                max: 20,
                                message: '资源名称不得超过20个字符!',
                            },
                        ],
                    })(
                        <Input
                            autoComplete="off"
                            placeholder="请输入资源名称"
                        />
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="资源类型"
                    key="resourceType"
                >
                    {getFieldDecorator('resourceType', {
                        rules: [
                            {
                                required: true,
                                message: '资源类型不可为空!',
                            },
                        ],
                        initialValue: fileType,
                    })(
                        <Select
                            onChange={(value: any) => {
                                this.setState({
                                    fileType: value,
                                });
                                this.resetFile();
                            }}
                        >
                            <Option
                                value={RESOURCE_TYPE.JAR}
                                key={RESOURCE_TYPE.JAR}
                            >
                                {resourceType[RESOURCE_TYPE.JAR]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.PY}
                                key={RESOURCE_TYPE.PY}
                            >
                                {resourceType[RESOURCE_TYPE.PY]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.EGG}
                                key={RESOURCE_TYPE.EGG}
                            >
                                {resourceType[RESOURCE_TYPE.EGG]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.ZIP}
                                key={RESOURCE_TYPE.ZIP}
                            >
                                {resourceType[RESOURCE_TYPE.ZIP]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.OTHER}
                                key={RESOURCE_TYPE.OTHER}
                            >
                                其它
                            </Option>
                        </Select>
                    )}
                </FormItem>,
                <FormItem {...formItemLayout} label="上传" key="file">
                    {getFieldDecorator('file', {
                        rules: [
                            {
                                required: true,
                                message: '请选择上传文件',
                            },
                            {
                                validator: this.validateFileType,
                            },
                        ],
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn btn-upload"
                                htmlFor="myOfflinFile"
                            >
                                选择文件
                            </label>
                            <span>
                                {' '}
                                {file.files &&
                                    file.files.length &&
                                    file.files[0].name}
                            </span>
                            <input
                                name="file"
                                type="file"
                                accept={accept}
                                id="myOfflinFile"
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>
                    )}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="选择存储位置"
                    key="nodePid"
                >
                    {getFieldDecorator('nodePid', {
                        rules: [
                            {
                                required: true,
                                message: '存储位置必选！',
                            },
                        ],
                        // @TODO: 编辑时最后的undefined为obj
                        initialValue: isCreateNormal
                            ? this.props.treeData.id
                            : isCreateFromMenu
                            ? defaultData.parentId
                            : undefined,
                    })(<Input type="hidden"></Input>)}
                    <Select
                        defaultValue={
                            isCreateNormal
                                ? this.props.treeData.id
                                : isCreateFromMenu
                                ? defaultData.parentId
                                : undefined
                        }
                        onChange={this.handleSelectTreeChange.bind(this)}
                    >
                        <Option value={251}>251</Option>
                    </Select>
                    {/* <FolderPicker
                        type={MENU_TYPE.RESOURCE}
                        ispicker
                        treeData={this.props.treeData}
                        onChange={this.handleSelectTreeChange.bind(this)}
                        defaultNode={
                            isCreateNormal
                                ? this.props.treeData.id
                                : isCreateFromMenu
                                ? defaultData.parentId
                                : undefined
                        }
                    /> */}
                </FormItem>,
                <FormItem {...formItemLayout} label="描述" key="resourceDesc">
                    {getFieldDecorator('resourceDesc', {
                        rules: [
                            {
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            },
                        ],
                        initialValue: '',
                    })(<Input.TextArea rows={4} />)}
                </FormItem>,
                <FormItem key="computeType" style={{ display: 'none' }}>
                    {getFieldDecorator('computeType', {
                        initialValue: 1,
                    })(<Input type="hidden"></Input>)}
                </FormItem>,
            ];
        } else {
            return [
                <FormItem {...formItemLayout} label="选择目标替换资源" key="id">
                    {getFieldDecorator('id', {
                        rules: [
                            {
                                required: true,
                                message: '替换资源为必选！',
                            },
                            {
                                validator: this.checkNotDir.bind(this),
                            },
                        ],
                        initialValue: isCreateNormal
                            ? this.props.treeData.id
                            : isCreateFromMenu
                            ? defaultData.parentId
                            : isEditExist
                            ? defaultData.id
                            : undefined,
                    })(<Input type="hidden"></Input>)}
                    <Select
                        defaultValue={
                            isCreateNormal
                                ? this.props.treeData.id
                                : isCreateFromMenu
                                ? defaultData.parentId
                                : isEditExist
                                ? defaultData.id
                                : undefined
                        }
                        onChange={this.handleCoverTargetChange.bind(this)}
                    >
                        <Option value={5}>5</Option>
                    </Select>
                    {/* <FolderPicker
                        type={MENU_TYPE.RESOURCE}
                        ispicker
                        isFilepicker
                        treeData={this.props.treeData}
                        onChange={this.handleCoverTargetChange.bind(this)}
                        defaultNode={
                            isCreateNormal
                                ? this.props.treeData.id
                                : isCreateFromMenu
                                ? defaultData.parentId
                                : isEditExist
                                ? defaultData.id
                                : undefined
                        }
                    /> */}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="文件名"
                    key="originFileName"
                >
                    {getFieldDecorator('originFileName', {
                        initialValue: defaultData.originFileName,
                    })(<Input disabled readOnly />)}
                </FormItem>,
                <FormItem
                    {...formItemLayout}
                    label="资源类型"
                    key="resourceType"
                >
                    {getFieldDecorator('resourceType', {
                        rules: [
                            {
                                required: true,
                                message: '资源类型不可为空!',
                            },
                        ],
                        initialValue: fileType,
                    })(
                        <Select
                            onChange={(value: any) => {
                                this.setState({
                                    fileType: value,
                                });
                                this.resetFile();
                            }}
                        >
                            <Option
                                value={RESOURCE_TYPE.JAR}
                                key={RESOURCE_TYPE.JAR}
                            >
                                {resourceType[RESOURCE_TYPE.JAR]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.PY}
                                key={RESOURCE_TYPE.PY}
                            >
                                {resourceType[RESOURCE_TYPE.PY]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.EGG}
                                key={RESOURCE_TYPE.EGG}
                            >
                                {resourceType[RESOURCE_TYPE.EGG]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.ZIP}
                                key={RESOURCE_TYPE.ZIP}
                            >
                                {resourceType[RESOURCE_TYPE.ZIP]}
                            </Option>
                            <Option
                                value={RESOURCE_TYPE.OTHER}
                                key={RESOURCE_TYPE.OTHER}
                            >
                                其它
                            </Option>
                        </Select>
                    )}
                </FormItem>,
                <FormItem {...formItemLayout} label="上传" key="file">
                    {getFieldDecorator('file', {
                        rules: [
                            {
                                required: true,
                                message: '请选择上传文件',
                            },
                            {
                                validator: this.validateFileType,
                            },
                        ],
                    })(
                        <div>
                            <label
                                style={{ lineHeight: '28px' }}
                                className="ant-btn"
                                htmlFor="myOfflinFile"
                            >
                                选择文件
                            </label>
                            <span>
                                {' '}
                                {file.files &&
                                    file.files.length &&
                                    file.files[0].name}
                            </span>
                            <input
                                name="file"
                                type="file"
                                id="myOfflinFile"
                                accept={accept}
                                onChange={this.fileChange}
                                style={{ display: 'none' }}
                            />
                        </div>
                    )}
                </FormItem>,
                <FormItem {...formItemLayout} label="描述" key="resourceDesc">
                    {getFieldDecorator('resourceDesc', {
                        rules: [
                            {
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            },
                        ],
                        initialValue: defaultData.resourceDesc,
                    })(<Input.TextArea rows={4} />)}
                </FormItem>,
            ];
        }
    };

    render() {
        return (
            <Spin spinning={this.state.loading}>
                <Form>{this.renderFormItem()}</Form>
            </Spin>
        );
    }

    /**
     * @description 获取节点名称
     * @param {any} id
     * @memberof FolderForm
     */
    getFolderName(id: any) {
        const { treeData } = this.props;
        let name: any;

        const loop = (arr: any) => {
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

    /**
     * @description 检查所选是否为文件夹
     * @param {any} rule
     * @param {any} value
     * @param {any} cb
     */
    checkNotDir(rule: any, value: any, callback: any) {
        // TODO
        callback();
        // const { treeData } = this.props;
        // let nodeType: any;

        // let loop = (arr: any) => {
        //     arr.forEach((node: any, i: any) => {
        //         if (node.id === value) {
        //             nodeType = node.type;
        //         } else {
        //             loop(node.children || []);
        //         }
        //     });
        // };

        // loop([treeData]);

        // if (nodeType === 'folder') {
        //     /* eslint-disable-next-line */
        //     callback('请选择具体文件, 而非文件夹');
        // }
        // callback();
    }
}

const ResFormWrapper = Form.create<any>()(ResForm);
class ResModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.state = {
            file: '',
        };

        this.dtcount = 0;
    }

    dtcount: any;
    form: any;
    handleSubmit() {
        const form = this.form;
        form.validateFields((err: any, values: any) => {
            if (!err) {
                values.file = this.state.file.files[0];
                values.resourceDesc = values.resourceDesc || '';
                this.setState({
                    loading: true,
                });
                if (this.props.isCoverUpload) {
                    this.props.replaceResource(values).then((success: any) => {
                        this.setState({
                            loading: false,
                        });
                        if (success) {
                            this.closeModal();
                            this.setState({ file: '' });
                            form.resetFields();
                        }
                    });
                } else {
                    this.props.addResource(values).then((success: any) => {
                        this.setState({
                            loading: false,
                        });
                        if (success) {
                            this.closeModal();
                            this.setState({ file: '' });
                            form.resetFields();
                        }
                    });
                }
            }
        });
    }

    handleCancel() {
        this.closeModal();
    }

    closeModal() {
        this.dtcount++;
        this.props.toggleUploadModal();
    }

    handleFileChange(file: any) {
        this.setState({ file });
    }

    render() {
        const { isModalShow, resourceTreeData, defaultData, isCoverUpload } =
            this.props;
        const { loading } = this.state;
        const isCreateNormal = typeof defaultData === 'undefined';
        const isCreateFromMenu =
            !isCreateNormal && typeof defaultData.id === 'undefined';
        const isEditExist = !isCreateNormal && !isCreateFromMenu;

        return (
            <div id="JS_upload_modal">
                <Modal
                    title={
                        isCoverUpload
                            ? '替换资源'
                            : isEditExist
                            ? '编辑资源'
                            : '上传资源'
                    }
                    visible={isModalShow}
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
                            loading={loading}
                            type="primary"
                            size="large"
                            onClick={this.handleSubmit}
                        >
                            {' '}
                            确认{' '}
                        </Button>,
                    ]}
                    key={this.dtcount}
                    onCancel={this.handleCancel}
                    getContainer={() => getContainer('JS_upload_modal')}
                >
                    {isCoverUpload && (
                        <div className="task_offline_message">
                            <Icon
                                type="exclamation-circle-o"
                                style={{ marginRight: 7 }}
                            />
                            替换资源时，如果资源的新文件与现有文件名称保持一致，那么替换后关联函数对应任务可立即生效，否则关联函数对应任务需重新提交才可生效。
                        </div>
                    )}
                    <ResFormWrapper
                        ref={(el: any) => (this.form = el)}
                        treeData={resourceTreeData}
                        handleFileChange={this.handleFileChange.bind(this)}
                        defaultData={defaultData || {}}
                        isCreateNormal={isCreateNormal}
                        isCreateFromMenu={isCreateFromMenu}
                        isCoverUpload={isCoverUpload}
                        isEditExist={isEditExist}
                    />
                </Modal>
            </div>
        );
    }
}

export default ResModal;
