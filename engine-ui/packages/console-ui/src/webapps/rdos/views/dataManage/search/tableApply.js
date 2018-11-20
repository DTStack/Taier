import React, { Component } from 'react'
import { Form, Input, Modal, InputNumber, Checkbox, Pagination } from 'antd'

import { formItemLayout } from '../../../comm/const'
import ajax from '../../../api/dataManage';

import '../../../styles/pages/dataManage.scss';

const FormItem = Form.Item
const CheckboxGroup = Checkbox.Group;
const pageSize = 1;

class TableApply extends Component {

    state = {
        checkDdlAll: false,
        checkedList: [],
        indeterminate: true,

        checkDmlAll: false,
        checkedDmlList: [],
        indeterminateDml: true,
        tableData: [],
        IdsTest: [
            { label: 'Apple', value: 1 },
            { label: 'Pear', value: 2 },
            { label: 'Orange', value: 3 },
            { label: 'Apple1', value: 4 },
            { label: 'Pear1', value: 5 },
            { label: 'Orange1', value: 6 },
            { label: 'Apple2', value: 7 },
            { label: 'Pear2', value: 8 },
            { label: 'Orange2', value: 9 },
            { label: 'Apple3', value: 10 },
            { label: 'Pear3', value: 11 },
            { label: 'Orange3', value: 12 },
            { label: 'Apple4', value: 13 },
            { label: 'Pear4', value: 14 },
            { label: 'Orange4', value: 15 },
            { label: 'Apple5', value: 16 },
            { label: 'Pear5', value: 17 },
            { label: 'Orange5', value: 18 },
        ],
        arr: [],
        checkIdsAll: false,
        checkedIdsList: [],
        indeterminateIds: true,

        currentPage: 1,
    }

    componentWillReceiveProps(nextProps) {
        console.log("++++++++++++++++++++++++++++")
        // console.log(nextProps.props)
        // this.setState({
        //     tableData: nextProps.props.table > this.props.table
        // })
    }

    componentDidMount() {
        const { IdsTest } = this.state;
        this.setState({
            arr: IdsTest.slice(0,pageSize)
        })
        this.getSimpleColumns();
    }

    
    // 获取字段
    getSimpleColumns = () => {
        // const {columns} = this.state;
        // console.log("++++++++++++")
        // console.log(columns)
        // ajax.getSimpleColumns({
        //     tableId: columns.id,
        //     tableName: columns.tableName
        // }).then(res => {
        //     if(res.code ===1 ) {
        //         console.log("++++++++++++++++++++++++++")
        //         console.log(res.data);
        //     }
        // })j
        // console.log(this.state.tableData)
    }

    // 复选框
    changeDdlGroup = (checkedList) => {
        const {ddlList} = this.props;
        this.setState({
            checkedList,
            indeterminate: !!checkedList.length && (checkedList.length < ddlList.length),
            checkAll: checkedList.length === ddlList.length
        })
    }

    changeDmlGroup = (checkedDmlList) => {
        const {dmlList} = this.props;
        this.setState({
            checkedDmlList,
            indeterminateDml: !!checkedDmlList.length && (checkedDmlList.length < dmlList.length),
            checkAll: checkedDmlList.length === dmlList.length
        })
    }

    changeIdsGroup = (checkedIdsList) => {
        const {IdsTest} = this.state;
        this.setState({
            checkedIdsList,
            indeterminateIds: !!checkedIdsList.length && (checkedIdsList.length < IdsTest.length),
            checkIdsAll: checkedIdsList.length === IdsTest.length
        })
    }


    // 全选
    onCheckDdlAll = (e) => {
        const {ddlList, dmlList} = this.props;
        const ddlListData = ddlList.map(item => {
            return item.value
        })
        this.setState({
            checkedList: e.target.checked ? ddlListData : [],
            indeterminate: false,
            checkDdlAll: e.target.checked
        })
    }
    
    onCheckDmlAll = (e) => {
        const {ddlList, dmlList} = this.props;
        const dmlListData = dmlList.map(item => {
            return item.value
        })
        this.setState({
            checkedDmlList: e.target.checked ? dmlListData : [],
            indeterminateDml: false,
            checkDmlAll: e.target.checked
        })
    }

    onCheckIdsAll = (e) => {
        const {IdsTest} = this.state;
        const idsTestData = IdsTest.map(item => {
            return item.value
        })
        this.setState({
            checkedIdsList: e.target.checked ? idsTestData : [],
            indeterminateIds: false,
            checkIdsAll: e.target.checked
        })
    }

    onChangePage = (currentPage, pageSize) => {
        const {IdsTest} = this.state;
        const arr = IdsTest.slice((currentPage-1)*pageSize,(currentPage-1)*pageSize+pageSize)
        this.setState({
            currentPage,
            arr
        })
        console.log(currentPage)
    }

    submit = (e) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const formData = form.getFieldsValue()
        const {checkedList, checkedDmlList, checkedIdsList} = this.state;
        console.log(formData)
        const ddl = {
            ddl: checkedList
        }
        const dml = {
            dml: checkedDmlList
        }
        const ids = {
            columnName: checkedIdsList
        }
        console.log({...ddl, ...dml, ...ids, ...formData})
        // form.validateFields((err) => {
        //     if (!err) {
        //         setTimeout(() => { form.resetFields() }, 200)
        //         onOk(formData)
        //     }
        // });
    }

    cancle = () => {
        const { onCancel, form } = this.props;
        const {checkedList,checkedDmlList} = this.state;
        onCancel()
        form.resetFields()
        this.setState({
            checkedList: [],
            checkedDmlList: []
        })
        
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, table, ddlList, dmlList } = this.props;
        const {checkDdlAll, checkedList, indeterminate, checkDmlAll, checkedDmlList, indeterminateDml,
            checkIdsAll, indeterminateIds, IdsTest, checkedIdsList
        } = this.state;
        const total = IdsTest.length;
        const {currentPage, arr} = this.state;
        return (
            <Modal
                title="申请授权"
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="正在申请的表为"
                        hasFeedback
                    >
                        <span>{table.tableName}</span>
                    </FormItem>

                    {/* 表段权限 */}
                    <div className="tablepermission">表段权限:</div>
                    <div className="content">
                        <p className="title">DDL</p>
                        <Checkbox checked={checkDdlAll} indeterminate={indeterminate} onChange={this.onCheckDdlAll}>全选</Checkbox>
                        <div>
                            <CheckboxGroup options={ddlList} value={checkedList} onChange={this.changeDdlGroup}></CheckboxGroup>
                        </div>
                    </div>

                    <div className="content">
                        <p className="title">DML</p>
                        <Checkbox checked={checkDmlAll} indeterminate={indeterminateDml} onChange={this.onCheckDmlAll}>全选</Checkbox>
                        <div>
                            <CheckboxGroup options={dmlList} value={checkedDmlList} onChange={this.changeDmlGroup}></CheckboxGroup>
                        </div>
                    </div>

                    {/* 字段权限 */}
                    <div className="tablepermission">字段权限:</div>
                    <div className="content">
                        <Checkbox checked={checkIdsAll} indeterminate={indeterminateIds} onChange={this.onCheckIdsAll}>全选(包括新增字段)</Checkbox>
                        <div>
                            <CheckboxGroup options={arr} value={checkedIdsList} onChange={this.changeIdsGroup}></CheckboxGroup>
                        </div>
                    </div>

                    <div style={{marginBottom:"20px"}}>
                        <Pagination
                            size="small"
                            total={total}
                            pageSize={pageSize}
                            current={currentPage}
                            onChange={this.onChangePage}
                        />
                    </div>


                    <FormItem
                        {...formItemLayout}
                        label="权限有效期"
                        hasFeedback
                    >
                        {getFieldDecorator('day', {
                            rules: [
                                {
                                    required: true, message: '请选择有效期时间',
                                }
                            ],
                        })(
                            <InputNumber min={1} placeholder="请输入申请时长（天）" style={{width: "100%"}}/>,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="申请理由"
                        hasFeedback
                    >
                        {getFieldDecorator('applyReason', {
                            rules: [{
                                max: 200,
                                message: '申请理由请控制在200个字符以内！',
                            },{
                                required: true, message: '必须填写申请理由',
                              }],
                        })(
                            <Input type="textarea" rows={4} placeholder="请输入申请理由" />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create()(TableApply);
export default FormWrapper
