import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Input, Select, message, Icon } from 'antd';
import ajax from '../../../api/dataManage';
import { formItemLayout, TABLE_TYPE } from '../../../comm/const';
const FormItem = Form.Item;
const Option = Select.Option;

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
            selectRule: [], // 选中的脱敏规则
            selectTableName: undefined,
            upwardColumnsInfo: [] // 上游字段信息
        }
    }
    /**
     * 获取脱敏规则列表
     */
    getDesRulesList = (params) => {
        this.setState({
            rulesList: []
        })
        this.props.form.resetFields(['ruleId'])
        ajax.getDesRulesList(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    rulesList: res.data
                })
            }
        })
    }
    /**
     * 获取表列表
     */
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
            const tableTypeName = item.tableType == TABLE_TYPE.HIVE ? '(hive)' : '(libra)'
            return <Option
                key={item.id}
                value={`${item.id}`}
            >
                {`${item.tableName}${tableTypeName}`}
            </Option>
        })
    }
    /**
     * 字段列表
     */
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
    getUpColumnInfo = (value) => {
        const { getFieldValue } = this.props.form;
        const projectId = getFieldValue('projectId');
        ajax.checkUpwardColumns({
            tableName: this.state.selectTableName,
            belongProjectId: projectId,
            column: value
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    upwardColumnsInfo: res.data
                })
            }
        })
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
                {`${item.name}`}
            </Option>
        })
    }

    /**
     * 判断是否输入正确脱敏配置
     * @param {Array} sampleDataArr 样例数组
     * @param {Number} beginPos 开始值
     * @param {Number} endPos 结束值
     * @returns {Object} 是否正确输入情况
     */
    isPassConfig = (sampleDataArr, beginPos, endPos) => {
        const firstCase = (endPos >= sampleDataArr.length && sampleDataArr.length >= beginPos) ||
        (endPos > sampleDataArr.length && sampleDataArr.length >= beginPos) ||
        (endPos >= sampleDataArr.length && sampleDataArr.length > beginPos) ||
        (endPos > sampleDataArr.length && sampleDataArr.length > beginPos);

        const secondCase = (endPos <= sampleDataArr.length && endPos >= beginPos) ||
        (endPos < sampleDataArr.length && endPos >= beginPos) ||
        (endPos <= sampleDataArr.length && endPos > beginPos) ||
        (endPos < sampleDataArr.length && endPos > beginPos);

        const thirdCase = (sampleDataArr.length < beginPos && beginPos < endPos) ||
        (sampleDataArr.length < beginPos && beginPos <= endPos) ||
        (sampleDataArr.length <= beginPos && beginPos < endPos) ||
        (sampleDataArr.length <= beginPos && beginPos <= endPos);
        return {
            firstCase,
            secondCase,
            thirdCase
        }
    }

    /**
     * 设置newReplaceData
     * @param {Array} sampleDataArr 样例数据
     * @param {Number} beginPos 开始值
     * @param {Number} endPos 结束值
     * @param {String} newStr 替换新字符
     */
    setNewReplaceData = (sampleDataArr, beginPos, endPos, newStr) => {
        sampleDataArr.splice(beginPos - 1, (endPos - beginPos + 1), newStr).join('');
        this.setState({
            newReplaceData: sampleDataArr
        })
    }

    /**
     * 字符串替换
     */
    repeatStr = (str, n) => {
        return new Array(n + 1).join(str)
    }

    /**
     * 预览
     */
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('example');
        let ruleId = getFieldValue('ruleId');
        const { maskType, replaceStr } = this.state.selectRule;
        let { beginPos, endPos } = {
            beginPos: Number(this.state.selectRule.beginPos),
            endPos: Number(this.state.selectRule.endPos)
        }
        if (!ruleId) {
            message.warning('请先选择脱敏规则');
        } else {
            if (maskType === 1) {
                this.setState({
                    newReplaceData: ''
                })
                let sampleDataArr = (sampleData || '').split('');
                console.log(sampleDataArr.length, beginPos, endPos)
                let repeatCount = 0;
                const cases = this.isPassConfig(sampleDataArr, beginPos, endPos);
                if (cases.firstCase) {
                    repeatCount = sampleDataArr.length - beginPos + 1;
                    const newStr = this.repeatStr(replaceStr, repeatCount);
                    this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
                } else if (cases.secondCase) {
                    repeatCount = endPos - beginPos + 1;
                    const newStr = this.repeatStr(replaceStr, repeatCount);
                    this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
                } else if (cases.thirdCase) {
                    repeatCount = 0;
                    const newStr = this.repeatStr(replaceStr, repeatCount);
                    this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
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
    /**
     * 选择项目
     */
    changeProject (value) {
        this.props.form.resetFields(['tableId', 'columnName']);
        this.setState({
            tableList: [],
            columnsList: []
        }, () => {
            this.getTableList({ projectId: value })
            this.getDesRulesList({ projectId: value })
        })
    }
    /**
     * 选择表
     */
    changeTable (value) {
        this.props.form.resetFields(['columnName']);
        const { tableList } = this.state;
        const selectTableName = tableList.filter((item, index) => {
            return `${item.id}` === value
        })
        this.setState({
            selectTableName: selectTableName[0].tableName
        })
        this.getColumnsList(value)
    }
    changeColumnName (value) {
        this.getUpColumnInfo(value)
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
                onOk(desensitizationData)
            }
        });
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { projects } = this.props;
        const { newReplaceData, upwardColumnsInfo } = this.state;
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
                maskClosable={false}
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
                                showSearch
                                optionFilterProp="children"
                                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                onChange={this.changeProject.bind(this)}
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
                                showSearch
                                optionFilterProp="children"
                                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                onChange={this.changeTable.bind(this)}
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
                                onChange={this.changeColumnName.bind(this)}
                            >
                                {this.columnsOption()}
                            </Select>
                        )}
                    </FormItem>
                    <div style={{ margin: '-6px 0 10px 120px' }}>
                        <div>
                            <Icon type="info-circle-o" style={{ fontSize: 14, margin: '0 5px 0 0', color: '#999999' }}/>上游表、下游表的相关字段会自动脱敏
                        </div>
                        {
                            upwardColumnsInfo && upwardColumnsInfo.length > 0 ? (
                                <div style={{ width: '287px' }}>
                                    <Icon type="info-circle-o" style={{ fontSize: 14, margin: '10px 5px 0 0', color: '#999999' }}/>
                                    {`您选择的表至少存在1个上游表(${upwardColumnsInfo[0].tableName}.${upwardColumnsInfo[0].columnName})，建议将脱敏规则配置在根节点表`}
                                </div>
                            ) : ''
                        }
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
                            >
                                {this.rulesOption()}
                            </Select>
                        )}
                    </FormItem>
                    <div style={{ color: '#2491F7', marginLeft: '122px', marginTop: '-18px', float: 'left' }}>
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
