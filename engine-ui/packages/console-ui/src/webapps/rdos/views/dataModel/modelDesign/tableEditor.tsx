import * as React from 'react';
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
import { TABLE_TYPE } from '../../../comm/const'
const FormItem = Form.Item
const confirm = Modal.confirm;

class TableEditor extends React.Component<any, any> {
    state: any = {
        dataCatalogue: [],
        columnFileds: [] // 指标字段
    }

    constructor (props: any) {
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
        ajax.getDataCatalogues().then((res: any) => {
            this.setState({
                dataCatalogue: res.data && [res.data]
            })
        })

        // 获取指标字段
        dateModelAPI.getTablePartitions().then((res: any) => {
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
            desc, chargeUser, lifeDay, catalogueId, tableType
        } = tableData;

        const formItemLayout: any = {
            labelCol: { span: 2 },
            wrapperCol: { span: 12 }
        };
        console.log('tableEditor', tableData);
        const isHiveTable = tableType == TABLE_TYPE.HIVE;
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
                                    initialValue: catalogueId + ''
                                })(
                                    <CatalogueTree
                                        id="catalogue"
                                        value={catalogueId + ''}
                                        isPicker
                                        isFolderPicker
                                        treeData={this.state.dataCatalogue}
                                        onChange={(val: any) => {
                                            modifyDesc({ name: 'catalogueId', value: val })
                                        }}
                                    />
                                )}
                            </FormItem>
                            {
                                isHiveTable && (
                                    <FormItem
                                        {...formItemLayout}
                                        label="生命周期"
                                    >
                                        <LifeCycle
                                            key={`lifeCycle-${tableData.id}`}
                                            width={80}
                                            value={lifeDay}
                                            onChange={(val: any) => {
                                                modifyDesc({ name: 'lifeDay', value: val < 0 ? 1 : val })
                                            }}
                                        />
                                    </FormItem>
                                )
                            }
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
    addRow (data: any, type: any) {
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
    delRow (uuid: any, type: any) {
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
    replaceRow (newCol: any, type: any) {
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
    moveRow (uuid: any, type: any, isUp: any) {
        this.props.moveRow({
            uuid, type, isUp
        });
    }

    changeTable (evt: any) {
        const { name, value } = evt.target;
        this.props.modifyDesc({ name, value });
    }

    delTable () {
        const the: any = this;
        confirm({
            title: '删除表',
            content: '删除表后无法恢复，确认将其删除？',
            onOk () {
                dateModelAPI.deleteTable({
                    tableId: the.tableId
                }).then((res: any) => {
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
        // 组装参数
        const queryParams: any = {};
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
            form.validateFields((err: any) => {
                if (!err) {
                    // ctx.props.saveTable(tableData);
                    dateModelAPI.alterTable(queryParams).then((res: any) => {
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

    checkColumnsIsNull (cols: any) {
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

const mapDispatch = (dispatch: any) => ({
    getTableDetail (params: any) {
        dispatch(actions.getTableDetail(params));
    },
    modifyDesc (params: any) {
        dispatch(actions.modifyDesc(params));
    },
    addRow (params: any) {
        dispatch(actions.addRow(params));
    },
    delRow (params: any) {
        dispatch(actions.delRow(params));
    },
    replaceRow (params: any) {
        dispatch(actions.replaceRow(params));
    },
    moveRow (params: any) {
        dispatch(actions.moveRow(params));
    },
    saveTable (params: any) {
        dispatch(actions.saveTable(params));
    }
});

const BaseFormWrapper = Form.create<any>()(TableEditor);

export default connect((state: any) => {
    return {
        tableData: state.dataManage.tableManage.tableCurrent
    }
}, mapDispatch)(BaseFormWrapper);
