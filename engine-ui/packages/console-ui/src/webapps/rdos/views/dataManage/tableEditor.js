import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import SplitPane from 'react-split-pane';
import { 
    Input, Button, message, 
    Modal, Form, Row, Col,
 } from 'antd';

import { isEmpty } from 'lodash';
import moment from 'moment';

import utils from 'utils';
import ajax from '../../api';
import { ColumnsPartition } from './tableCreator';
import actions from '../../store/modules/dataManage/actionCreator';
import { formItemLayout } from '../../comm/const';
import CatalogueTree from './catalogTree';
import LifeCycle from './lifeCycle';

const FormItem = Form.Item
const confirm = Modal.confirm;

class TableEditor extends Component {

    state = {
        dataCatalogue: [],
    }

    constructor(props) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
    }

    componentDidMount() {
        this.props.getTableDetail({
            tableId: this.tableId
        });
        this.loadCatalogue();
    }

    componentWillUnmount() {}

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
            })
        })
    }

    render() {
        const { tableData, modifyDesc } = this.props;
        const { 
            tableName, project, createTime,
            desc, userName, lifeDay, catalogueId,
        } = tableData;

        formItemLayout.wrapperCol.sm = 18; // 更改FormItem布局

        return <div className="g-tableeditor">
            <div className="m-tableviewerhead">
                <Button type="default" className="f-fr" style={{ marginTop: 7 }}>
                    <Link to="/data-manage/table">返回</Link>
                </Button>
                <h3>{ tableData && <span>编辑表：{ tableName }</span> }</h3>
            </div>
            <SplitPane split="vertical" minSize={200} defaultSize={300}>
                <div className="m-tablebasic">
                    <h3 className="clearfix">
                        基本信息
                    </h3>
                    <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>表名</th>
                                <td>{ tableName }</td>
                            </tr>
                            <tr>
                                <th>所属项目</th>
                                <td>{ project }</td>
                            </tr>
                            <tr>
                                <th>创建者</th>
                                <td>{userName}</td>
                            </tr>
                            <tr>
                                <th>创建时间</th>
                                <td>{ moment(createTime).format('YYYY-MM-DD HH:mm:ss') }</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <Row>
                        <Form>
                            <FormItem
                                {...formItemLayout}
                                label="所属类目"
                            >
                                <CatalogueTree
                                    id="catalogue"
                                    value={catalogueId}
                                    isPicker
                                    isFolderPicker
                                    treeData={this.state.dataCatalogue}
                                    onChange={(val) => {
                                        modifyDesc({name: 'catalogueId', value: val})
                                    }}
                                />
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="生命周期"
                            >
                                <LifeCycle
                                    width={80}
                                    value={lifeDay}
                                    onChange={(val) => {
                                        modifyDesc({name: 'lifeDay', value: val})
                                    }}
                                />
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="描述"
                            >
                                <Input type="textarea"
                                    name="desc"
                                    value={desc}
                                    onChange={this.changeTable.bind(this)}
                                ></Input>
                            </FormItem>
                        </Form>
                    </Row>

                    <Button type="danger"
                        onClick={ this.delTable.bind(this) }
                        style={{ float: 'right'}}
                    >删除表</Button>
                </div>
                <div className="m-tabledetail">
                    {!isEmpty(tableData) && <ColumnsPartition
                        {...tableData }
                        addRow={ this.addRow.bind(this) }
                        delRow={ this.delRow.bind(this) }
                        replaceRow={ this.replaceRow.bind(this) }
                        moveRow={ this.moveRow.bind(this) }
                        isEdit
                    />}
                    <Button type="primary"
                        onClick={ this.saveTable.bind(this) }
                        style={{ float: 'right', margin: '30 25' }}
                    >提交</Button>
                </div>
            </SplitPane>
        </div>
    }

    /**
     * @description 新曾一行
     * @param {any} data 新数据
     * @param {number} type 1: columns 2: partitions
     * @memberof TableCreator
     */
    addRow(data, type) {
        this.props.addRow({
            data, type
        });
    }

    /**
     * @description 删除一行
     * @param {any} uuid
     * @param {number} type type 1: columns 2: partitions
     * @memberof TableCreator
     */
    delRow(uuid, type) {
        this.props.delRow({
            uuid, type
        });
    }

    /**
     * @description 修改（置换）一行
     * @param {any} newCol
     * @param {number} type  1: columns 2: partitions
     * @memberof TableCreator
     */
    replaceRow(newCol, type) {
        this.props.replaceRow({
            newCol, type
        });
    }

    /**
     * @description 向上、下移动
     * @param {any} uuid
     * @param {number} type 1: columns 2: partitions
     * @param {boolean} isUp
     * @memberof TableCreator
     */
    moveRow(uuid, type, isUp) {
        this.props.moveRow({
            uuid, type, isUp
        });
    }

    changeTable(evt) {
        const { name, value } = evt.target;
        this.props.modifyDesc({name, value});
    }
    
    delTable() {
        const the = this;

        confirm({
            title: '删除表',
            content: '删除表后无法恢复，确认将其删除？',
            onOk() {
                ajax.dropTable({
                    tableId: the.tableId
                }).then(res => {
                    if(res.code === 1) {
                        message.info('删除成功, 即将返回列表页');
                        setTimeout(() => {
                            the.props.router.replace('/data-manage/table');
                        }, 1000);
                    }
                })
            },
            onCancel() {},
        });
    }

    saveTable() {
        const { tableData } = this.props;
        if (this.checkColumnsIsNull(tableData.columns)) {
            message.error('新建字段名称不可为空！')
        } else {
            this.props.saveTable(tableData);
        }
    }

    checkColumnsIsNull(cols) {
        if (cols && cols.length > 0) {
            for (let i = 0; i < cols.length; i++) {
                if (utils.trim(cols[i].name) === "") {
                    return true
                }
            }
        }
        return false
    }
}

const mapDispatch = dispatch => ({
    getTableDetail(params) {
        dispatch(actions.getTableDetail(params));
    },
    modifyDesc(params) {
        dispatch(actions.modifyDesc(params));
    },
    addRow(params) {
        dispatch(actions.addRow(params));
    },
    delRow(params) {
        dispatch(actions.delRow(params));
    },
    replaceRow(params) {
        dispatch(actions.replaceRow(params));
    },
    moveRow(params) {
        dispatch(actions.moveRow(params));
    },
    saveTable(params) {
        dispatch(actions.saveTable(params));
    }
});

export default connect((state) => {
    return {
        tableData: state.dataManage.tableManage.tableCurrent
    }
}, mapDispatch)(TableEditor);