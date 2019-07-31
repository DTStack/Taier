/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { Row, Table, Button, Input, Form, Select, Icon, Checkbox, message, notification, Modal } from 'antd';
import API from '../../../../api';
import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';
import { CATALOGUE_TYPE } from '../../../../consts';

const confirm = Modal.confirm;

const FormItem = Form.Item;
const Option = Select.Option;

const options: any = [{
    name: '3天',
    value: 3
}, {
    name: '7天',
    value: 7
}, {
    name: '30天',
    value: 30
}, {
    name: '90天',
    value: 90
}, {
    name: '365天',
    value: 365
}, {
    name: '自定义',
    value: -1
}]

const fieldTypes: any = [
    {
        name: 'SMALLINT',
        value: 'SMALLINT'
    }, {
        name: 'INT/INTEGER',
        value: 'INT'
    }, {
        name: 'BIGINT',
        value: 'BIGINT'
    }, {
        name: 'DOUBLE',
        value: 'DOUBLE'
    }, {
        name: 'TIMESTAMP',
        value: 'TIMESTAMP'
    }, {
        name: 'DATE',
        value: 'DATE'
    }, {
        name: 'STRING',
        value: 'STRING'
    }, {
        name: 'BOOLEAN',
        value: 'BOOLEAN'
    }, {
        name: 'DECIMAL',
        value: 'DECIMAL'
    }
]

const decimalPrecision: any = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38]
const decimalScale: any = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

class EditTable extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            customLifeCycle: '',
            short: false,
            tableDetail: { columns: [] }
        }
    }
    componentDidMount () {
        const { tableDetail } = this.props.data;
        tableDetail.columns = tableDetail.columns || [];
        tableDetail.partitions = tableDetail.partitions || [];
        console.log(tableDetail.lifeDay)
        console.log([3, 7, 30, 90, 365].indexOf(tableDetail.lifeDay))
        if ([3, 7, 30, 90, 365].indexOf(tableDetail.lifeDay) === -1) {
            tableDetail.shortLisyCycle = tableDetail.lifeDay
            this.setState({
                customLifeCycle: tableDetail.shortLisyCycle
            })
            tableDetail.lifeDay = -1;
            this.setState({
                short: true
            })
        }

        this.setState({
            tableDetail: tableDetail
        }, () => {
            console.log(tableDetail)
        })
    }
    saveInfo = () => {
        const { form } = this.props;
        form.validateFields((err: any, value: any) => {
            console.log(value)
            if (!err) {
                // if(value.lifeDay === -1){
                //   value.lifeDay = this.state.customLifeCycle;
                // }
                console.log(value)
            }
        })
    }
    handleSelectChange = (e: any) => {
        if (e === -1) {
            this.setState({
                short: true
            })
        }
    }
    handleFieldNameChange = (e: any, record: any) => {
        record.name = e.target.value;
        this.saveDataToStorage();
    }
    handleFieldTypeChange = (e: any, record: any) => {
        record.type = e;
        this.saveDataToStorage();
    }
    handleFieldCommentChange = (e: any, record: any) => {
        record.comment = e.target.value;
        this.saveDataToStorage();
    }

    handleInvert = (e: any, record: any) => {
        record.invert = e.target.checked ? 1 : 0
        this.saveDataToStorage();
    }

    handleDictionary = (e: any, record: any) => {
        record.dictionary = e.target.checked ? 1 : 0
        this.saveDataToStorage();
    }

    handleSortColumn = (e: any, record: any) => {
        record.sortColumn = e.target.checked ? 1 : 0
        this.saveDataToStorage();
    }

    addNewLine = (flag: any) => {
        console.log(this.state.tableDetail)
        let { tableDetail } = this.state;
        let _fid = 0;

        if (flag === 1) {
            // 字段
            tableDetail.columns.map((o: any) => {
                if (o._fid > _fid) { _fid = o._fid }
            })
            console.log(_fid)
            tableDetail.columns.push({
                _fid: _fid + 1,
                name: '',
                type: '',
                comment: '',
                invert: 1,
                dictionary: 1,
                sortColumn: 1,
                isNew: true
            })
            this.setState({
                tableDetail: tableDetail
            })
        } else if (flag === 2) {
            // 索引
            tableDetail.partitions.map((o: any) => {
                if (o._fid > _fid) { _fid = o._fid }
            })
            tableDetail.partitions.push({
                _fid: _fid + 1,
                name: '',
                field_type: '',
                index_type: '',
                comment: '',
                isNew: true
            })
            this.setState({
                tableDetail: tableDetail
            })
        }
    }
    handleDelTable = () => {
        let self = this;
        const { databaseId, id } = this.props.data.tableDetail;
        confirm({
            title: '删除表后无法恢复，确认将其删除？',
            onOk () {
                API.dropTable({ databaseId, id }).then((res: any) => {
                    if (res.code === 1) {
                        message.success('删除成功');
                        self.props.closeTab();
                        self.props.loadCatalogue({ id: databaseId }, CATALOGUE_TYPE.DATA_BASE);
                    } else {
                        notification.error({
                            message: '提示',
                            description: res.message
                        })
                    }
                })
            }
        })
    }

    move = (record: any, flag: any, type: any) => {
        // type 1上移 2下移
        // let mid: any = {};
        let { tableDetail } = this.state;
        let list = flag === 1 ? tableDetail.columns : tableDetail.partitions;
        console.log(type)
        console.log(list.indexOf(record))
        console.log(list.length)

        // 是否到顶
        if ((type === 1 && list.indexOf(record) === 0) || (type === 2 && list.indexOf(record) === list.length - 1)) { return }

        // console.log(!list[list.indexOf(record)+1].isNew)
        // 只可以在新增行中上下移动
        if ((type === 2 && (!list[list.indexOf(record) + 1].isNew)) || (type === 1 && (!list[list.indexOf(record) - 1].isNew))) {
            return;
        }

        let x = list.indexOf(record); let y = type === 1 ? list.indexOf(record) - 1 : list.indexOf(record) + 1;

        let midId = list[y]._fid;
        let midItem = list[x]

        list[y]._fid = -1;

        list[x] = list[y]; // fid=-1
        list[y] = midItem

        list[x]._fid = midId

        console.log(list)

        this.setState({
            tableDetail: tableDetail
        })

        this.saveDataToStorage();
    }

    remove = (record: any, flag: any) => {
        let { tableDetail } = this.state;

        flag === 1 ? tableDetail.columns.splice(tableDetail.columns.indexOf(record), 1) : tableDetail.partitions.splice(tableDetail.partitions.indexOf(record), 1);

        console.log(tableDetail)
        this.setState({
            tableDetail: tableDetail
        })
        this.saveDataToStorage();
    }

    handleIndexTypeChange = (e: any, record: any) => {
        record.index_type = e;
        this.saveDataToStorage();
    }

    handleLifeDayCusChange = (e: any) => {
        let { tableDetail } = this.state;
        tableDetail.shortLisyCycle = e.target.value;
        this.saveDataToStorage();
    }

    /**
     * 保存输入的值
     */
    saveDataToStorage = () => {
        const { tableDetail } = this.state;
        this.props.saveEditTableInfo([{
            key: 'columns',
            value: tableDetail.columns
        }, {
            key: 'partitions',
            value: tableDetail.partitions
        }])
    }

    handleDECIMALSelectChange = (e: any, record: any, flag: any) => {
        if (flag === 1) {
            record.precision = e
        } else {
            record.scale = e;
        }
        this.saveDataToStorage();
    }

    render () {
        const { tableDetail } = this.state;
        const { getFieldDecorator, getFieldsValue } = this.props.form;
        const tableColField: any = [
            {
                title: '字段名称',
                dataIndex: 'name',
                render: (text: any, record: any) => {
                    if (record.isNew) {
                        return <Input style={{ width: 159, height: 26 }} defaultValue={text} onChange={(e: any) => this.handleFieldNameChange(e, record)} />
                    } else { return text }
                }
            }, {
                title: '字段类型',
                dataIndex: 'type',
                render: (text: any, record: any) => {
                    if (record.isNew) {
                        if (record.type === 'DECIMAL') {
                            return <span>
                                <Select style={{ width: 90, marginRight: 5 }} defaultValue={text} onChange={(e: any) => this.handleFieldTypeChange(e, record)}>
                                    {fieldTypes.map((o: any) => {
                                        return <Option key={o.value} value={o.value}>{o.name}</Option>
                                    })}
                                </Select>
                                <span>
                                    <Select style={{ width: 50, marginRight: 5 }} defaultValue={record.precision ? record.precision : undefined} onChange={(e: any) => this.handleDECIMALSelectChange(e, record, 1)}>
                                        {
                                            decimalPrecision.map((o: any) => {
                                                return <Option key={o} value={o}>{o}</Option>
                                            })
                                        }
                                    </Select>
                                    <Select style={{ width: 50, marginRight: 5 }} defaultValue={record.scale ? record.scale : undefined} onChange={(e: any) => this.handleDECIMALSelectChange(e, record, 2)}>
                                        {
                                            decimalScale.map((o: any) => {
                                                return <Option key={o} value={o}>{o}</Option>
                                            })
                                        }
                                    </Select>
                                    <HelpDoc style={relativeStyle} doc="decimalType" />
                                </span>
                            </span>
                        } else {
                            return <Select style={{ width: 159 }} defaultValue={text} onChange={(e: any) => this.handleFieldTypeChange(e, record)}>
                                {fieldTypes.map((o: any) => {
                                    return <Option key={o.value} value={o.value}>{o.name}</Option>
                                })}
                            </Select>
                        }
                    } else { return text }
                }
            }, {
                title: '倒排索引',
                dataIndex: 'invert',
                render: (text: any, record: any) => (
                    text !== 1 ? '-'
                        : <Checkbox disabled={!record.isNew} defaultChecked={text === 1} onChange={(e: any) => this.handleInvert(e, record)}></Checkbox>
                )
            }, {
                title: '字典编码',
                dataIndex: 'dictionary',
                render: (text: any, record: any) => (
                    text !== 1 ? '-'
                        : <Checkbox disabled={!record.isNew} defaultChecked={text === 1} onChange={(e: any) => this.handleDictionary(e, record)}></Checkbox>
                )
            }, {
                title: '多维索引',
                dataIndex: 'sortColumn',
                render: (text: any, record: any) => (
                    text !== 1 ? '-'
                        : <Checkbox disabled={record.type === 'DOUBLE' || record.type === 'DECIMAL' || !record.isNew} defaultChecked={(record.type === 'DECIMAL' || record.type === 'DOUBLE') ? false : text === 1} onChange={(e: any) => this.handleSortColumn(e, record)}></Checkbox>
                )
            }, {
                title: '注释内容',
                dataIndex: 'comment',
                render: (text: any, record: any) => {
                    if (record.isNew) {
                        return <Input style={{ width: 159, height: 26 }} defaultValue={text} onChange={(e: any) => this.handleFieldCommentChange(e, record)} />
                    } else {
                        if (!text) return '-'
                        else return text
                    }
                }
            }, {
                title: '操作',
                dataIndex: 'action',
                render: (text: any, record: any) => {
                    if (record.isNew) {
                        return <span className="action-span">
                            <a href="javascript:;" onClick={() => this.move(record, 1, 1)}>上移</a>
                            <span className="line" />
                            <a href="javascript:;" onClick={() => this.move(record, 1, 2)}>下移</a>
                            <span className="line" />
                            <a href="javascript:;" onClick={() => this.remove(record, 1)}>删除</a>
                        </span>
                    }
                }
            }
        ]

        const tableCOlPartition: any = [
            {
                title: '字段名',
                dataIndex: 'name'
                // render: (text: any, record: any) =>(
                //   <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
                // )
            }, {
                title: '字段类型',
                dataIndex: 'type'
                // render: (text: any, record: any) =>(
                //   <Select getPopupContainer={(triggerNode: any) => triggerNode.parentNode} style={{width: 159}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
                //     {
                //       field_type.map(o=>{
                //         return <Option key={o.value} value={o.value}>{o.name}</Option>
                //       })
                //     }
                //   </Select>
                // )
            }, {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    text || '-'
                )
            }
        ]
        return (
            <div className="edit-table-container">
                <Row className="panel">
                    <div className="title">基本信息</div>
                    <Form>
                        <FormItem

                            label="表名">
                            {
                                getFieldDecorator('tableName', {
                                    rules: [
                                        { required: true, message: '表名不可为空' }
                                    ],
                                    initialValue: tableDetail.tableName || undefined
                                })(
                                    <Input style={{ width: 430, height: 36 }} />
                                )
                            }
                        </FormItem>
                        <FormItem

                            label="生命周期">
                            <span >
                                {
                                    getFieldDecorator('lifeDay', {
                                        rules: [
                                            { required: true, message: '生命周期不能为空' }
                                        ],
                                        initialValue: tableDetail.lifeDay || undefined
                                    })(
                                        <Select onChange={this.handleSelectChange} style={{ width: getFieldsValue().lifeDay === -1 ? 78 : 430, height: 36 }}>
                                            {options.map((o: any) => (
                                                <Option key={o.value} value={o.value}>{o.name}</Option>
                                            ))}
                                        </Select>
                                    )
                                }
                                {
                                    getFieldsValue().lifeDay === -1 &&
                                    <Input size="large" style={{ width: 340, height: 36, marginLeft: 10 }} defaultValue={this.state.customLifeCycle} onChange={this.handleLifeDayCusChange} />
                                }
                            </span>
                        </FormItem>
                        <FormItem

                            label="描述">
                            {
                                getFieldDecorator('tableDesc', {
                                    rules: [
                                        { required: true, message: '描述不可为空' }
                                    ],
                                    initialValue: tableDetail.tableDesc || undefined
                                })(
                                    <Input style={{ width: 430, height: 36 }} />
                                )
                            }
                        </FormItem>
                    </Form>
                </Row>

                <Row className="panel table-box" id="table-panel">
                    <div className="title">字段信息</div>
                    <Table
                        size="small"
                        className="table-small"
                        columns={tableColField}
                        rowKey="_fid"
                        dataSource={tableDetail.columns}
                        pagination={false}>
                    </Table>
                    <a className="btn" style={{ marginTop: 16, display: 'inline-block' }} href="javascript:;" onClick={() => this.addNewLine(1)}><Icon style={{ marginRight: 5 }} className="icon" type="plus-circle-o" />添加字段</a>
                </Row>

                <Row className="panel table-box">
                    <div className="title">分区信息</div>
                    <Table
                        size="small"
                        className="table-small"
                        columns={tableCOlPartition}
                        rowKey="_fid"
                        dataSource={tableDetail.partitions}
                        pagination={false}>
                    </Table>
                </Row>
                <Button type="danger" style={{ marginLeft: 20, width: 90, height: 30 }} onClick={this.handleDelTable}>删除</Button>
                <Button type="primary" style={{ marginLeft: 20, width: 90, height: 30 }} onClick={this.props.saveTableInfo}>保存</Button>
            </div>
        )
    }
}
export default Form.create({
    onValuesChange (props: any, changedValues: any) {
        console.log(props)
        let p: any = [];
        for (let key in changedValues) {
            p.push({
                key: key,
                value: changedValues[key]
            })
        }
        props.saveEditTableInfo(p)
    }
})(EditTable)
