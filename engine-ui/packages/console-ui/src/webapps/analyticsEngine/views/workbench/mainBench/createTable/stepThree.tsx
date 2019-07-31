/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { Input, Table, Select, Icon, Button, Row } from 'antd'

const Option = Select.Option;

// const indexes: any = [];
// const area_list: any = [];

export default class StepThree extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            indexes: props.formData.indexes || []

        }
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { formData } = nextProps;
        this.setState({
            indexes: formData.indexes || []
        })
    }

    next = () => {
        this.props.handleNextStep();
    }
    last = () => {

    }

    addNewLine = () => {
        let { indexes } = this.state;
        let _fid = 0;
        indexes.map((o: any) => {
            if (o._fid > _fid) { _fid = o._fid }
        })
        indexes[indexes.length] = {
            _fid: _fid + 1,
            columnType: '',
            index_type: '',
            comment: ''
        }
        this.setState({
            indexes: indexes
        })
    }

    handleSelectChange = (e: any, record: any) => {
        record.columnType = e;
        this.saveDataToStorage();
    }
    handleIndexTypeChange = (e: any, record: any) => {
        record.index_type = e;
        this.saveDataToStorage();
    }
    handleCommentChange = (e: any, record: any) => {
        record.comment = e.target.value;
        this.saveDataToStorage();
    }

    remove = (record: any, flag?: any) => {
        let { indexes } = this.state;

        indexes.splice(indexes.indexOf(record), 1)

        this.setState({
            indexes: indexes
        })
        this.saveDataToStorage();
    }

    move = (record: any, type: any) => {
        // type 1上移 2下移
        // let mid: any = {};
        let { indexes } = this.state;
        let list = indexes;
        console.log(type)
        console.log(list.indexOf(record))
        console.log(list.length)

        if ((type === 1 && list.indexOf(record) === 0) || (type === 2 && list.indexOf(record) === list.length - 1)) { return }

        let x = list.indexOf(record); let y = type === 1 ? list.indexOf(record) - 1 : list.indexOf(record) + 1;

        let midId = list[y]._fid;
        let midItem = list[x]

        list[y]._fid = -1;

        list[x] = list[y]; // fid=-1
        list[y] = midItem

        list[x]._fid = midId

        console.log(list)

        this.setState({
            indexes: list
        })

        this.saveDataToStorage();
    }

    /**
   * 保存输入的值
   */
    saveDataToStorage = () => {
        const { indexes } = this.state;
        this.props.saveNewTableData([{
            key: 'indexes',
            value: indexes
        }])
    }

    getTableCol = () => {
        let tableCol: any = [
            {
                title: '字段',
                dataIndex: 'columnType',
                render: (text: any, record: any) => (
                    <Select style={{ width: 159 }} defaultValue={text || undefined} onChange={(e: any) => this.handleSelectChange(e, record)}>
                        {
                            this.props.formData.columns.map((o: any) => (
                                <Option key={o._fid} value={o.columnName}>{o.columnName}</Option>
                            ))
                        }
                    </Select>
                )
            }, {
                title: '索引类型',
                dataIndex: 'index_type',
                render: (text: any, record: any) => (
                    <Select style={{ width: 159 }} defaultValue={text || undefined} onChange={(e: any) => this.handleIndexTypeChange(e, record)}>
                        <Option value="STRING">STRING</Option>
                        <Option value="INT">INT</Option>
                        <Option value="LONG">LONG</Option>
                        <Option value="BLOG">BLOG</Option>
                    </Select>
                )
            }, {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    <Input style={{ width: 159 }} defaultValue={text} onChange={(e: any) => this.handleCommentChange(e, record)}/>
                )
            }, {
                title: '操作',
                dataIndex: 'action',
                render: (text: any, record: any) => (
                    <span className="action-span">
                        <a href="javascript:;" onClick={() => this.move(record, 1)}>上移</a>
                        <span className="line"/>
                        <a href="javascript:;" onClick={() => this.move(record, 2)}>下移</a>
                        <span className="line"/>
                        <a href="javascript:;" onClick={() => this.remove(record)}>删除</a>
                    </span>
                )
            }
        ]

        return tableCol;
    }

    render () {
        let { indexes } = this.state;

        return (
            <Row className="step-three-container step-container">
                <div className="table-panel">
                    <Table
                        columns={this.getTableCol()}
                        dataSource={indexes}
                        rowKey="_fid"
                        pagination={false}
                        size="small"
                    ></Table>
                    <a className="btn" href="javascript:;" onClick={() => this.addNewLine()}><Icon className="icon" type="plus-circle-o" />添加字段</a>
                </div>

                <div className="nav-btn-box">
                    <Button onClick={this.props.handleLastStep}>上一步</Button>
                    <Button type="primary" onClick={this.props.handleSave}>下一步</Button>
                </div>
            </Row>
        )
    }
}
