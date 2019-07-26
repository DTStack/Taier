import React from 'react';
import { Modal, Form, Input, Table, Button, message } from 'antd';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux'

import utils from 'utils';
import { formItemLayout } from '../../../../../console/consts';
import Api from '../../../../api'
import { publishType, TASK_TYPE } from '../../../../comm/const';
import { getTaskTypes } from '../../../../store/modules/offlineTask/comm';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

@connect(state => {
    return {
        project: state.project,
        taskTypes: {
            offline: state.offlineTask.comm.taskTypes
        },
        taskTypeFilter: state.offlineTask.comm.taskTypeFilter
    }
}, dispatch => {
    return bindActionCreators({
        getTaskTypes
    }, dispatch);
})
class PublishModal extends React.Component {
    state = {
        pagination: {
            current: 1,
            pageSize: 5,
            total: 0
        }
    }
    componentDidMount () {
        this.props.getTaskTypes();
    }
    getPackageName () {
        const { mode, form, isPublish } = this.props;
        const { setFieldsValue } = form;
        if (isPublish) {
            return;
        }
        Api.getPackageName({}, mode)
            .then(
                (res) => {
                    if (res.code == 1) {
                        setFieldsValue({ packageName: res.data })
                    }
                }
            )
    }
    /* eslint-disable */
    componentWillReceiveProps (nextProps) {
        const { visible } = nextProps;
        const { visible: old_visible } = this.props;
        if (visible && visible != old_visible) {
            this.reset();
            this.getPackageName();
        }
    }
    /* eslint-ensable */
    reset () {
        this.props.form.resetFields();
        this.setState({
            pagination: {
                current: 1,
                pageSize: 5,
                total: 0
            }
        })
    }
    onTableChange (pagination) {
        this.setState({
            pagination
        })
    }
    initColumns () {
        const { isPublish, taskTypeFilter } = this.props;
        const offlineTaskTypesMap = new Map(taskTypeFilter.map((item) => { return [item.value, item.text] }));
        let columns = [{
            title: '对象名称',
            dataIndex: 'itemName',
            width: '150px',
            render (text, record) {
                let extText = '';
                if (record.itemType == publishType.TASK) {
                    extText = ` (${offlineTaskTypesMap.get(record.itemInnerType)})`;
                }
                return <span >
                    {`${text}${extText}`}
                </span>
            }
        }, {
            title: '类型',
            dataIndex: 'itemType',
            width: '100px',
            render (text, record) {
                switch (text) {
                    case publishType.TASK: {
                        return `${offlineTaskTypesMap.get(record.itemInnerType)}任务`
                    }
                    case publishType.RESOURCE: {
                        return '资源'
                    }
                    case publishType.FUNCTION: {
                        return '函数'
                    }
                    case publishType.TABLE: {
                        return '表'
                    }
                }
            }
        }, {
            title: '环境参数',
            dataIndex: 'publishParamJson',
            width: '80px',
            render (publishParamJson, record) {
                const showEnv = record.itemType == publishType.TASK && record.data.taskType != TASK_TYPE.SYNC
                return showEnv ? (publishParamJson && publishParamJson.updateEnvParam ? '更新' : '不更新') : '-';
            }
        }, {
            title: '创建人',
            dataIndex: 'createUser'
        }, {
            title: '修改人',
            dataIndex: 'modifyUser'
        }, {
            title: '修改时间',
            dataIndex: 'modifyTime',
            width: '150px',
            render (modifyTime) {
                return utils.formatDateTime(modifyTime);
            }
        }]
        if (isPublish) {
            columns.splice(2, 1);
        }
        return columns
    }
    onOk () {
        const { mode, form, data } = this.props;
        form.validateFields(null, {}, (err, values) => {
            if (!err) {
                Api.createPackage({
                    ...values,
                    items: data.items
                }, mode)
                    .then(
                        (res) => {
                            if (res.code == 1) {
                                message.success('打包成功')
                                this.props.onOk();
                            }
                        }
                    )
            }
        })
    }
    render () {
        const { pagination } = this.state;
        const { visible, form, data, isPublish, project } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                width={800}
                visible={visible}
                title={isPublish ? '查看发布包' : '创建发布包'}
                footer={isPublish ? null : (
                    <span>
                        <Button onClick={this.props.onCancel}>取消</Button>
                        <Button type="primary" onClick={this.onOk.bind(this)}>确定</Button>
                    </span>
                )}
                onCancel={this.props.onCancel}
            >
                <FormItem
                    label="发布包名称"
                    {...formItemLayout}
                >
                    {getFieldDecorator('packageName', {
                        rules: [{
                            required: true,
                            message: '请输入发布包名称'
                        }, {
                            pattern: /^\w*$/,
                            message: '发布包名称只能由字母、数字、下划线组成!'
                        }],
                        initialValue: isPublish ? data.name : ''
                    })(
                        <Input disabled={isPublish} />
                    )}
                </FormItem>
                <FormItem
                    label="发布描述"
                    {...formItemLayout}
                >
                    {getFieldDecorator('packageDesc', {
                        rules: [{
                            required: true,
                            message: '请输入发布描述'
                        }],
                        initialValue: isPublish ? data.comment : ''
                    })(
                        <TextArea disabled={isPublish} autosize={{ minRows: 3, maxRows: 5 }} />
                    )}
                </FormItem>
                <FormItem
                    label="发布到目标项目"
                    {...formItemLayout}
                >
                    {project.produceProject}
                </FormItem>
                <Table
                    rowKey={(record) => {
                        if (isPublish) {
                            return record.id;
                        }
                        return `${record.itemType}%${record.itemId}%${record.itemName}`
                    }}
                    onChange={this.onTableChange.bind(this)}
                    pagination={pagination}
                    className="dt-ant-table dt-ant-table--border"
                    columns={this.initColumns()}
                    dataSource={data.items}
                />
            </Modal>
        )
    }
}
const WrapPublishModal = Form.create()(PublishModal);
export default WrapPublishModal;
