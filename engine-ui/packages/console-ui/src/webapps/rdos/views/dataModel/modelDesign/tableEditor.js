import React, { Component } from 'react';
import { connect } from 'react-redux';
import { browserHistory, hashHistory } from 'react-router';
import {
    Input, Button, message,
    Modal, Form, Row
} from 'antd';

import { isEmpty } from 'lodash';
import moment from 'moment';

import utils from 'utils';
import GoBack from 'main/components/go-back';

import ajax from '../../../api/dataManage';
import dateModelAPI from '../../../api/dataModel';
import ColumnsPartition from './columnsPartition';
import actions from '../../../store/modules/dataManage/actionCreator';
import LifeCycle from '../../dataManage/lifeCycle';
import CatalogueTree from '../../dataManage/catalogTree';

const FormItem = Form.Item
const confirm = Modal.confirm;

class TableEditor extends Component {
    state = {
        dataCatalogue: [],
        columnFileds: [] // 指标字段
    }

    constructor (props) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
    }

    componentDidMount () {
        this.props.getTableDetail({
            tableId: this.tableId
        });
        this.loadTableInfo();
    }

    loadTableInfo = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data]
            })
        })

        // 获取指标字段
        dateModelAPI.getTablePartitions().then(res => {
            if (res.code === 1) {
                this.setState({
                    columnFileds: res.data || []
                })
            }
        });
    }

    render () {
        const { tableData, modifyDesc } = this.props;
        const { getFieldDecorator } = this.props.form;

        const {
            tableName, project, gmtCreate,
            desc, chargeUser, lifeDay, catalogueId
        } = tableData;

        const formItemLayout = {
            labelCol: { span: 2 },
            wrapperCol: { span: 12 }
        };
        console.log('tableEditor', tableData);

        return <div className="g-tableeditor box-1">
            <div className="box-card">
                <main>
                    <h1 className="card-title flex-middle"><GoBack type="textButton" /> {tableData && <span>编辑表：{tableName}</span>}</h1>
                    <Row className="box-card m-tablebasic">
                        <h3>基本信息</h3>
                        <table width="100%" cellPadding="0" cellSpacing="0">
                            <tbody>
                                <tr>
                                    <th>表名</th>
                                    <td>{tableName}</td>
                                </tr>
                                <tr>
                                    <th>所属项目</th>
                                    <td>{project}</td>
                                </tr>
                                <tr>
                                    <th>负责人</th>
                                    <td>{chargeUser}</td>
                                </tr>
                                <tr>
                                    <th>创建时间</th>
                                    <td>{ moment(gmtCreate).format('YYYY-MM-DD HH:mm:ss') }</td>
                                </tr>
                            </tbody>
                        </table>
                        <Form>
                            <FormItem
                                {...formItemLayout}
                                label="所属类目"
                            >
                                {getFieldDecorator('belongCatalogue', {
                                    rules: [{
                                        required: true,
                                        message: '请选择所属类目'
                                    }],
                                    initialValue: catalogueId
                                })(
                                    <CatalogueTree
                                        id="catalogue"
                                        value={catalogueId}
                                        isPicker
                                        isFolderPicker
                                        treeData={this.state.dataCatalogue}
                                        onChange={(val) => {
                                            modifyDesc({ name: 'catalogueId', value: val })
                                        }}
                                    />
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="生命周期"
                            >
                                <LifeCycle
                                    key={`lifeCycle-${tableData.id}`}
                                    width={80}
                                    value={lifeDay}
                                    onChange={(val) => {
                                        modifyDesc({ name: 'lifeDay', value: val < 0 ? 1 : val })
                                    }}
                                />
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="描述"
                            >
                                {getFieldDecorator('tableDesc', {
                                    rules: [{
                                        max: 200,
                                        message: '描述不得超过200个字符！'
                                    }],
                                    initialValue: desc
                                })(
                                    <Input
                                        name="tableDesc"
                                        onChange={this.changeTable.bind(this)}
                                        type="textarea" placeholder="描述信息"
                                    />
                                )}
                            </FormItem>
                        </Form>
                    </Row>
                    <Row className="box-card">
                        {!isEmpty(tableData) && <ColumnsPartition
                            {...tableData}
                            addRow={this.addRow.bind(this)}
                            delRow={this.delRow.bind(this)}
                            columnFileds={this.state.columnFileds}
                            replaceRow={this.replaceRow.bind(this)}
                            moveRow={this.moveRow.bind(this)}
                            isEdit
                        />}
                    </Row>
                    <Row className="box-card txt-right">
                        <Button
                            type="danger"
                            style={{ marginRight: '20px' }}
                            onClick={this.delTable.bind(this)}
                        >
                            删除表
                        </Button>
                        <Button
                            type="primary"
                            onClick={this.saveTable.bind(this)}
                        >
                            保存
                        </Button>
                    </Row>
                </main>
            </div>
        </div>
    }

    /**
     * @description 新曾一行
     * @param {any} data 新数据
     * @param {number} type 1: columns 2: partitions
     * @memberof TableCreator
     */
    addRow (data, type) {
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
    delRow (uuid, type) {
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
    replaceRow (newCol, type) {
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
    moveRow (uuid, type, isUp) {
        this.props.moveRow({
            uuid, type, isUp
        });
    }

    changeTable (evt) {
        const { name, value } = evt.target;
        this.props.modifyDesc({ name, value });
    }

    delTable () {
        const the = this;
        confirm({
            title: '删除表',
            content: '删除表后无法恢复，确认将其删除？',
            onOk () {
                dateModelAPI.deleteTable({
                    tableId: the.tableId
                }).then(res => {
                    if (res.code === 1) {
                        message.info('删除成功, 即将返回列表页');
                        setTimeout(() => {
                            the.props.router.replace('/data-model/table');
                        }, 1000);
                    }
                })
            },
            onCancel () { }
        });
    }

    saveTable () {
        const { tableData, form } = this.props;
        const ctx = this;
        // 组装参数
        const queryParams = {};
        queryParams.tableId = tableData.id;
        queryParams.tableName = tableData.tableName;
        queryParams.tableDesc = tableData.tableDesc;
        // queryParams.delim = tableData.id;
        queryParams.lifeDay = tableData.lifeDay;
        // queryParams.storedType = tableData.id;
        queryParams.catalogueId = tableData.catalogueId;
        queryParams.columns = tableData.columns;
        queryParams.partition_keys = tableData.partition_keys;
        if (this.checkColumnsIsNull(tableData.columns)) {
            message.error('新建字段名称不可为空！')
        } else {
            form.validateFields((err) => {
                if (!err) {
                    // ctx.props.saveTable(tableData);
                    dateModelAPI.alterTable(queryParams).then(res => {
                        if (res.code === 1) {
                            message.success('修改成功！');
                            this.goBack()
                        }
                    })
                }
            });
        }
    }

    goBack = () => {
        const { url, history } = this.props
        if (url) {
            if (history) { browserHistory.push(url) } else { hashHistory.push(url) }
        } else {
            browserHistory.go(-1)
        }
    }

    checkColumnsIsNull (cols) {
        if (cols && cols.length > 0) {
            for (let i = 0; i < cols.length; i++) {
                if (utils.trim(cols[i].name) === '') {
                    return true
                }
            }
        }
        return false
    }
}

const mapDispatch = dispatch => ({
    getTableDetail (params) {
        dispatch(actions.getTableDetail(params));
    },
    modifyDesc (params) {
        dispatch(actions.modifyDesc(params));
    },
    addRow (params) {
        dispatch(actions.addRow(params));
    },
    delRow (params) {
        dispatch(actions.delRow(params));
    },
    replaceRow (params) {
        dispatch(actions.replaceRow(params));
    },
    moveRow (params) {
        dispatch(actions.moveRow(params));
    },
    saveTable (params) {
        dispatch(actions.saveTable(params));
    }
});

const BaseFormWrapper = Form.create()(TableEditor);

export default connect((state) => {
    return {
        tableData: state.dataManage.tableManage.tableCurrent
    }
}, mapDispatch)(BaseFormWrapper);
