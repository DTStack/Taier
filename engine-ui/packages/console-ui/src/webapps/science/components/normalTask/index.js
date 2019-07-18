import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { get } from 'lodash';

import { Form, Input, Select } from 'antd';
import FolderTree from '../folderTree';
import { formItemLayout, siderBarType, TASK_TYPE } from '../../consts'
import * as fileTreeActions from '../../actions/base/fileTree'
import * as notebookActions from '../../actions/notebookActions'
import workbenchActions from '../../actions/workbenchActions'

const FormItem = Form.Item;
const Option = Select.Option;
@connect(state => {
    return {
        files: state.notebook.files,
        resourceFiles: state.resource.files,
        modal: state.modal,
        taskType: state.taskType.taskType
    }
}, dispatch => {
    return {
        ...bindActionCreators(fileTreeActions, dispatch),
        ...bindActionCreators(notebookActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    };
})
class NormalTaskForm extends React.Component {
    loadData = (siderType, node) => {
        return this.props.loadTreeData(siderType, node.props.data.id);
    }
    getRootNode () {
        const { files } = this.props;
        if (files && files.length) {
            const children = files[0].children;
            const myFolder = children && children.find((node) => {
                return node.name == '我的Notebook'
            })
            return myFolder && myFolder.id
        }
        return null;
    }
    /**
     * @description 检查所选是否为文件夹
     * @param {any} rule
     * @param {any} value
     * @param {any} cb
     */
    checkNotDir (rule, value, callback) {
        const { resourceFiles } = this.props;
        let nodeType;

        let loop = (arr) => {
            arr.forEach((node, i) => {
                if (node.id == value) {
                    nodeType = node.type;
                } else {
                    loop(node.children || []);
                }
            });
        };

        loop(resourceFiles);

        if (nodeType === 'folder') {
            /* eslint-disable-next-line */
            callback('请选择具体文件, 而非文件夹');
        }
        callback();
    }
    render () {
        const { files, form, resourceFiles, taskType = [], tabData = {} } = this.props;
        const { getFieldDecorator } = form;
        const isPyTask = tabData.taskType == TASK_TYPE.PYSPARK;
        const resourceLable = !isPyTask ? '资源' : '入口资源';
        const taskOptions = taskType.map(item =>
            <Option key={item.key} value={item.key}>{item.value}</Option>
        )
        return (
            <div style={{ padding: '60' }}>
                <Form>
                    <FormItem
                        label='Notebook名称'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true,
                                message: '请输入名字'
                            }, {
                                pattern: /^[\w\d_]{1,32}$/,
                                message: '名字不超过32个字符,只支持字母、数字、下划线'
                            }],
                            initialValue: tabData.name
                        })(
                            <Input disabled/>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label='任务类型'
                    >
                        {getFieldDecorator('taskType', {
                            rules: [{
                                required: true, message: `请选择任务类型`
                            }],
                            initialValue: tabData.taskType
                        })(
                            <Select
                                disabled={true}
                                onChange={this.handleTaskTypeChange}
                            >
                                {taskOptions}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="参数"
                    >
                        {getFieldDecorator('options', {
                            initialValue: tabData.options
                        })(
                            <Input type="textarea" autosize={{ minRows: 2, maxRows: 4 }} placeholder="输入命令行参数，多个参数用空格隔开" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label={resourceLable}
                        hasFeedback
                    >
                        {getFieldDecorator('resourceIdList', {
                            rules: [{
                                required: true,
                                message: `请选择${resourceLable}`
                            }, {
                                validator: this.checkNotDir.bind(this)
                            }],
                            initialValue: tabData.resourceList && tabData.resourceList[0] && tabData.resourceList[0].id
                        })(
                            <FolderTree loadData={this.loadData.bind(this, siderBarType.resource)} treeData={resourceFiles} isSelect={true} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="引用资源"
                        hasFeedback
                    >
                        {getFieldDecorator('refResourceIdList', {
                            rules: [{
                                validator: this.checkNotDir.bind(this)
                            }],
                            initialValue: tabData.refResourceList && tabData.refResourceList.length > 0 ? tabData.refResourceList.map(res => res.id) : []
                        })(
                            <FolderTree loadData={this.loadData.bind(this, siderBarType.resource)} treeData={resourceFiles} isSelect={true} />
                        )}
                    </FormItem>
                    <FormItem
                        label='存储位置'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('nodePid', {
                            rules: [{
                                required: true,
                                message: '请选择存储位置'
                            }],
                            initialValue: get(tabData, 'nodePid') || this.getRootNode()
                        })(
                            <FolderTree loadData={this.loadData.bind(this, siderBarType.notebook)} treeData={files} isSelect={true} hideFiles={true} />
                        )}
                    </FormItem>
                    <FormItem
                        label='Notebook描述'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('taskDesc', {
                            rules: [{
                                max: 64,
                                message: '最大字符不能超过64'
                            }],
                            initialValue: tabData.taskDesc
                        })(
                            <Input.TextArea />
                        )}
                    </FormItem>
                </Form>
            </div>
        )
    }
}
export default Form.create()(NormalTaskForm);
