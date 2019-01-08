import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Input, Select, Alert, message } from 'antd';
import ajax from '../../../api/dataManage';
import { formItemLayout } from '../../../comm/const';
const FormItem = Form.Item;
const Option = Select.Option;
// mock
@connect(state => {
    return {
        projects: state.projects,
        user: state.user
    }
}, null)
class AddDesensitization extends Component {
    constructor (props) {
        super(props);
        this.state = {
            tableList: [],
            columnsList: [],
            rulesList: [], // 脱敏规则列表
            newReplaceData: '',
            selectRule: [] // 选中的脱敏规则
        }
    }
    componentDidMount () {
        this.getdesRulesList();
    }
    // 获取脱敏规则列表
    getdesRulesList = () => {
        ajax.getdesRulesList().then(res => {
            if (res.code === 1) {
                this.setState({
                    rulesList: res.data
                })
            }
        })
    }
    // 获取表列表
    getTableList = (params) => {
        this.setState({
            tableList: [],
            columnsList: []
        })
        ajax.getTableList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data
                })
            }
        })
    }
    tableListOption () {
        const { tableList } = this.state;
        return tableList.map((item, index) => {
            return <Option
                key={item.id}
                value={`${item.id}`}
            >
                {item.tableName}
            </Option>
        })
    }
    // 字段
    getColumnsList = (value) => {
        const { getFieldValue } = this.props.form;
        const projectId = getFieldValue('projectId');
        this.setState({
            columnsList: []
        })
        if (projectId && value) {
            ajax.getColumnsList({ tableId: value, projectId: projectId }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        columnsList: res.data
                    })
                }
            })
        }
    }
    columnsOption () {
        const { columnsList } = this.state;
        return columnsList.map((item, index) => {
            return <Option
                key={item.key}
                value={item.key}
            >
                {item.key}
            </Option>
        })
    }
    rulesOption () {
        const { rulesList } = this.state;
        return rulesList.map((item, index) => {
            return <Option
                key={item.id}
                value={`${item.id}`}
            >
                {item.name}
            </Option>
        })
    }
    // 字符串替换
    repeatStr = (str, n) => {
        return new Array(n + 1).join(str)
    }
    // 预览
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('example');
        let ruleId = getFieldValue('ruleId');
        const { maskType, beginPos, endPos, replaceStr } = this.state.selectRule;
        if (!ruleId) {
            message.warning('请先选择脱敏规则');
        } else {
            if (maskType === 1) {
                if (sampleData && (beginPos <= endPos)) {
                    let sampleDataArr = sampleData.split('');
                    let repeatCount = 0;
                    if ((endPos >= sampleDataArr.length) && (sampleDataArr.length >= beginPos)) {
                        repeatCount = sampleDataArr.length - beginPos + 1
                    } else if (endPos < sampleDataArr.length && (sampleDataArr.length >= beginPos)) {
                        repeatCount = endPos - beginPos + 1;
                    } else if (sampleDataArr.length < beginPos) {
                        repeatCount = 0;
                    }
                    const newStr = this.repeatStr(replaceStr, repeatCount);
                    sampleDataArr.splice(beginPos - 1, (endPos - beginPos + 1), newStr).join('');
                    this.setState({
                        newReplaceData: sampleDataArr
                    })
                } else {
                    message.warning('请正确输入脱敏替换配置');
                }
            } else {
                if (sampleData) {
                    let sampleDataAllArr = sampleData.split('');
                    const newStr = this.repeatStr('*', sampleDataAllArr.length);
                    sampleDataAllArr.splice(0, sampleDataAllArr.length, newStr).join('');
                    this.setState({
                        newReplaceData: sampleDataAllArr
                    })
                } else {
                    this.setState({
                        newReplaceData: ''
                    })
                }
            }
        }
    }
    changeRule (value) {
        const { rulesList } = this.state;
        const selectRule = rulesList.filter((item, index) => {
            return `${item.id}` === value
        })
        this.setState({
            selectRule: selectRule[0]
        })
    }
    // 选择项目
    changeProject (value) {
        this.props.form.resetFields(['tableId', 'columnName']);
        this.setState({
            tableList: [],
            columnsList: []
        })
        this.getTableList({ projectId: value })
    }
    // 选择表
    changeTable (value) {
        this.props.form.resetFields(['columnName']);
        this.getColumnsList(value)
    }
    cancel = () => {
        const { onCancel } = this.props;
        onCancel();
    }
    submit = () => {
        const { onOk } = this.props;
        const desensitizationData = this.props.form.getFieldsValue();
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields()
                onOk(desensitizationData)
            }
        });
    }
    /* eslint-disable */
    render () {
        const { getFieldDecorator } = this.props.form;
        const { projects } = this.props;
        const { newReplaceData } = this.state;
        const projectsOptions = projects.map(item => {
            return <Option
                title={item.projectAlias}
                key={item.id}
                name={item.projectAlias}
                value={`${item.id}`}
            >
                {item.projectAlias}
            </Option>
        })
        return (
            <Modal
                visible={this.props.visible}
                title='添加脱敏'
                onCancel={this.cancel}
                onOk={this.submit}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏名称"
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true,
                                message: '脱敏名称不可为空！'
                            }]
                        })(
                            <Input placeholder='请输入脱敏名称' />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目"
                    >
                        {getFieldDecorator('projectId', {
                            rules: [{
                                required: true,
                                message: '项目不可为空！'
                            }]
                        })(
                            <Select
                                placeholder='请选择项目'
                                onChange={this.changeProject.bind(this)}
                                // allowClear
                            >
                                {projectsOptions}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('tableId', {
                            rules: [{
                                required: true,
                                message: '表不可为空！'
                            }]
                        })(
                            <Select
                                placeholder='请选择表'
                                onChange={this.changeTable.bind(this)}
                                // allowClear
                            >
                                {this.tableListOption()}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                        {getFieldDecorator('columnName', {
                            rules: [{
                                required: true,
                                message: '字段不可为空！'
                            }]
                        })(
                            <Select
                                placeholder='请选择字段'
                                // allowClear
                            >
                                {this.columnsOption()}
                            </Select>
                        )}
                    </FormItem>
                    <div style={{ marginLeft: '106px', marginTop: '-14px' }} className='desenAlert'>
                        <Alert message='上游表、下游表的相关字段会自动脱敏' type="info" showIcon />
                    </div>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('example', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }]
                        })(
                            <Input type="textarea" rows={4} placeholder='请输入样例数据，不超过200字符'/>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏规则"
                    >
                        {getFieldDecorator('ruleId', {
                            rules: [{
                                required: true,
                                message: '脱敏规则不可为空！'
                            }]
                        })(
                            <Select
                                placeholder='请选择脱敏规则'
                                onChange={this.changeRule.bind(this)}
                                // allowClear
                            >
                                {this.rulesOption()}
                            </Select>
                        )}
                    </FormItem>
                    <div style={{ color: '#2491F7', marginLeft: '122px', marginTop: '-6px' }}>
                        <img src="/public/rdos/img/icon/icon-preview.svg" style={{ display: 'block', float: 'left' }} />
                        <a onClick={this.preview} style={{ marginLeft: '5px' }}>效果预览</a>
                        <div>{newReplaceData}</div>
                    </div>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddDesensitization);
