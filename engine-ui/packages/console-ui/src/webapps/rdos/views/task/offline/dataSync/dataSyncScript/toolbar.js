import React, { Component } from 'react';
import moment from 'moment';
import { hashHistory } from 'react-router';

import {
    Col, Button, message, Modal, Form, Select, Icon
} from 'antd'

import utils from 'utils'

import API from '../../../../../api'
import { formItemLayout, DATA_SOURCE, HELP_DOC_URL } from '../../../../../comm/const'

import {
    workbenchAction
} from '../../../../../store/modules/offlineTask/actionType';

const FormItem = Form.Item;
const Option = Select.Option;

class ImportTemplateForm extends Component {

    newSource() {
        hashHistory.push("/database");
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        const sourceTypeOptions = Object.keys(DATA_SOURCE).map(
            (key) => {
                return <Option key={DATA_SOURCE[key]} value={DATA_SOURCE[key].toString()}>{key}</Option>
            }
        )

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="来源类型"
                    hasFeedback
                >
                    {getFieldDecorator('sourceType', {
                        rules: [{
                            required: true, message: '来源类型不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择来源类型"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    style={{ marginBottom: "8px" }}
                    {...formItemLayout}
                    label="数据源"
                    hasFeedback
                >
                    {getFieldDecorator('sourceId', {
                        rules: [{
                            required: true, message: '数据源不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择数据源"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                    <a onClick={this.newSource.bind(this)}>新增数据源</a>
                </FormItem>
                <FormItem
                    style={{ marginBottom: "40px" }}
                    {...formItemLayout}
                    label="来源表"
                    hasFeedback
                >
                    {getFieldDecorator('sourceTable', {
                        rules: [{
                            required: true, message: '来源表不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择来源表"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="目标类型"
                    hasFeedback
                >
                    {getFieldDecorator('targetType', {
                        rules: [{
                            required: true, message: '目标类型不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择目标类型"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    style={{ marginBottom: "8px" }}
                    {...formItemLayout}
                    label="数据源"
                    hasFeedback
                >
                    {getFieldDecorator('targetSourceId', {
                        rules: [{
                            required: true, message: '数据源不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择目标数据源"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                    <a onClick={this.newSource.bind(this)}>新增数据源</a>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="目标表"
                    hasFeedback
                >
                    {getFieldDecorator('targetTable', {
                        rules: [{
                            required: true, message: '目标表不可为空！',
                        }]
                    })(
                        <Select
                            placeholder="请选择目标表"
                        >
                            {sourceTypeOptions}
                        </Select>
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapTemplateForm = Form.create()(ImportTemplateForm);

export default class SyncToolbar extends Component {

    state = {
        execConfirmVisible: false
    }

    importTemplate() {
        this.setState({
            execConfirmVisible: true
        })
    }

    sqlFormat = () => {
        const { sqlText, dispatch } = this.props;
        const data = {
            merged: true,
        }

        data.sqlText = utils.jsonFormat(sqlText);

        if (!data.sqlText) {
            message.error("您的JSON格式有误")
            return;
        }
        dispatch({
            type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
            payload: data,
        })

    }

    render() {
        const { execConfirmVisible } = this.state
        const { currentTabData } = this.props;

        return (
            <div className="ide-toolbar toolbar">
                <Button
                    onClick={this.importTemplate.bind(this)}
                    title="导入模版"
                    icon="plus-circle-o"
                    style={{ marginLeft: '0px' }}>导入模版
                </Button>
                <Button
                    icon="appstore-o"
                    title="格式化"
                    onClick={this.sqlFormat}
                >
                    格式化
                </Button>
                <span style={{ float: "right", marginRight: "18px", lineHeight: "28px" }}>
                    <Icon 
                        style={{ color: "#2491F7", marginRight: "2px" }} 
                        type="question-circle-o" />
                    <a 
                        href={HELP_DOC_URL.DATA_SYNC} 
                        target="blank">
                        帮助文档
                    </a>
                </span>

                <Modal
                    maskClosable
                    visible={execConfirmVisible}
                    title="导入模版"
                    onCancel={() => { this.setState({ execConfirmVisible: false }) }}
                >
                    <WrapTemplateForm {...this.props} />
                </Modal>
            </div>
        )
    }
}
