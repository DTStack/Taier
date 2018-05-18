import React, { Component } from 'react';
import moment from 'moment';
import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import {
    Col, Button, message, Modal, Form, Select, Icon
} from 'antd'

import utils from 'utils'

import API from '../../../../../api'
import { formItemLayout, DATA_SOURCE, HELP_DOC_URL, dataSourceTypes } from '../../../../../comm/const'

import {
    workbenchAction,
    dataSourceListAction
} from '../../../../../store/modules/offlineTask/actionType';

const FormItem = Form.Item;
const Option = Select.Option;


class ImportTemplateForm extends Component {

    state = {
        sourceType: undefined,
        targetType: undefined,
        sourceTable: [],
        targetTable: []
    }

    newSource() {
        hashHistory.push("/database");
    }

    getTableList = (sourceId, type) => {
        const ctx = this
        const newState = {};

        if (type == "source") {
            newState.sourceTable = [];
        } else {
            newState.targetTable = [];
        }

        this.setState(newState,
            () => {
                API.getOfflineTableList({
                    sourceId,
                    isSys: false
                }).then(res => {
                    if (res.code === 1) {
                        if (type == "source") {
                            ctx.setState({
                                sourceTable: res.data || []
                            });
                        } else {
                            ctx.setState({
                                targetTable: res.data || []
                            });
                        }
                    }
                });
            });
    }

    sourceTypeChange(value) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            sourceType: value
        },
            () => {
                setFieldsValue({
                    sourceId: undefined,
                    sourceTable: undefined
                })
            })
    }

    targetTypeChange(value) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            targetType: value
        },
            () => {
                setFieldsValue({
                    targetSourceId: undefined,
                    targetTable: undefined
                })
            })

    }

    sourceIdChange(sourceId) {
        this.getTableList(sourceId, "source");
    }

    targetIdChange(targetId) {
        this.getTableList(targetId, "target");
    }

    getSourceTable() {
        const { sourceTable } = this.state;

        return sourceTable.map(
            (item) => {
                return (
                    <Option key={item} value={item}>
                        {item}
                    </Option>
                )
            }
        )
    }

    getTargetTable() {
        const { targetTable } = this.state;

        return targetTable.map(
            (item) => {
                return (
                    <Option key={item} value={item}>
                        {item}
                    </Option>
                )
            }
        )

    }

    getSourceList() {
        const { sourceType } = this.state;
        const { dataSourceList } = this.props;

        return dataSourceList
            .filter(src => {
                return src.type == sourceType;
            })
            .map(src => {
                return <Option key={src.id} name={src.dataName} value={`${src.id}`}>
                    {src.dataName}( {dataSourceTypes[src.type]} )
                </Option>
            })
    }

    getTargetList() {
        const { targetType } = this.state;
        const { dataSourceList } = this.props;

        return dataSourceList
            .filter(src => {
                return src.type == targetType;
            })
            .map(src => {
                return <Option key={src.id} name={src.dataName} value={`${src.id}`}>
                    {src.dataName}( {dataSourceTypes[src.type]} )
                </Option>
            })

    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { dataSourceList } = this.props;

        const sourceTypeOptions = Object.keys(DATA_SOURCE).map(
            (key) => {
                return <Option key={DATA_SOURCE[key]} value={DATA_SOURCE[key].toString()}>{dataSourceTypes[DATA_SOURCE[key]]}</Option>
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
                            onChange={this.sourceTypeChange.bind(this)}
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
                            onChange={this.sourceIdChange.bind(this)}
                        >
                            {this.getSourceList()}
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
                            {this.getSourceTable()}
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
                            onChange={this.targetTypeChange.bind(this)}
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
                            onChange={this.targetIdChange.bind(this)}
                        >
                            {this.getTargetList()}
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
                            {this.getTargetTable()}
                        </Select>
                    )}
                </FormItem>
            </Form>
        )
    }
}
const WrapTemplateForm = Form.create()(ImportTemplateForm);


@connect(state => {
    const { dataSync } = state.offlineTask;

    return {
        dataSourceList: dataSync.dataSourceList,
    };
}, dispatch => {
    return {
        getDataSource: () => {
            API.getOfflineDataSource()
                .then(res => {
                    let data = []
                    if (res.code === 1) {
                        data = res.data
                    }
                    dispatch({
                        type: dataSourceListAction.LOAD_DATASOURCE,
                        payload: data
                    });
                });
        },
    }
})
class SyncToolbar extends Component {

    state = {
        execConfirmVisible: false
    }

    componentDidMount() {
        const { getDataSource } = this.props;
        getDataSource();
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

export default SyncToolbar;