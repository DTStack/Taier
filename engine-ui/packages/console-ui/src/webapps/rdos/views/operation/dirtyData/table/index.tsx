import * as React from 'react';
import moment from 'moment';
import {
    Row,
    Col,
    Table,
    Tabs,
    Radio,
    message,
    Card
} from 'antd';
import '../../../../styles/pages/dataManage.scss';

import utils from 'utils';
import GoBack from 'main/components/go-back';

import ajax from '../../../../api/dataManage';
import TableOverview from './overview';
import TableAnalytics from './analytics';

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class TableDetail extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
        this.state = {
            tableData: {},
            showType: 0 // 0/1 (非)字段
        };
    }
    tableId: any;
    componentDidMount () {
        this.getTable();
    }

    getTable () {
        ajax.getDirtyDataTableInfo({ tableId: this.tableId }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data
                });
            }
        });
    }

    switchType (evt: any) {
        const showType = evt.target.value;
        this.setState({
            showType
        });
    }

    handleCancel () {
        this.setState({
            visible: false
        });
    }

    handleOk () {
        message.info('复制成功，代码窗口即将关闭');
        setTimeout(() => {
            this.setState({
                visible: false
            });
        }, 1000);
    }

    render () {
        const { showType, tableData } = this.state;
        const columns: any = [
            {
                title: '序号',
                dataIndex: 'id',
                key: 'id',
                render (id: any) {
                    return id;
                }
            },
            {
                title: '字段名称',
                dataIndex: 'columnName',
                key: 'columnName'
            },
            {
                title: '类型',
                dataIndex: 'columnType',
                key: 'columnType'
            },
            {
                title: '注释',
                dataIndex: 'comment',
                key: 'comment',
                render (text: any) {
                    return text;
                }
            }
        ];

        const fieldsData =
            showType === 0 ? tableData.column : tableData.partition;
        console.log('--------fieldsData', fieldsData);

        const tableInfo = tableData.table || {};
        const relTasks = tableInfo.tasks && tableInfo.tasks.map((i: any) => i.name);
        const widthFix = { width: '100%' };
        return (
            <div className="g-datamanage">
                <div className="box-1">
                    <div className="box-card full-screen-table-40">
                        <main>
                            <h1 className="card-title">
                                <GoBack type="textButton" /> 查看表：{tableInfo &&
                                    tableInfo.tableName}
                            </h1>
                            <Row className="box-card m-tablebasic">
                                <Col span={12} className="col-sep">
                                    <h3>基本信息</h3>
                                    {tableInfo && (
                                        <table
                                            {...widthFix}
                                            cellPadding="0"
                                            cellSpacing="0"
                                        >
                                            <tbody>
                                                <tr>
                                                    <th>所属项目</th>
                                                    <td>{tableInfo.project}</td>
                                                </tr>
                                                <tr>
                                                    <th>创建者</th>
                                                    <td>
                                                        {tableInfo.chargeUser}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>创建时间</th>
                                                    <td>
                                                        {moment(
                                                            tableInfo.createTime
                                                        ).format(
                                                            'YYYY-MM-DD HH:mm:ss'
                                                        )}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>相关任务</th>
                                                    <td>{relTasks || '无'}</td>
                                                </tr>
                                                <tr>
                                                    <th>描述</th>
                                                    <td>
                                                        {tableInfo.tableDesc}
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    )}
                                </Col>
                                <Col span={12} className="col-sep">
                                    <h3>存储信息</h3>
                                    {tableInfo && (
                                        <table
                                            {...widthFix}
                                            cellPadding="0"
                                            cellSpacing="0"
                                        >
                                            <tbody>
                                                <tr>
                                                    <th>物理存储量</th>
                                                    <td>
                                                        {tableInfo.tableSize}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>生命周期</th>
                                                    <td>
                                                        {tableInfo.lifeDay}天
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>是否分区</th>
                                                    <td>
                                                        {tableInfo.partitions
                                                            ? '是'
                                                            : '否'}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>DDL最后变更时间</th>
                                                    <td>
                                                        {utils.formatDateTime(
                                                            tableInfo.lastDdlTime
                                                        )}
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>数据最后变更时间</th>
                                                    <td>
                                                        {utils.formatDateTime(
                                                            tableInfo.lastDmlTime
                                                        )}
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    )}
                                </Col>
                            </Row>
                            <Row style={{ padding: '0 30px', height: '500px' }}>
                                <div className="m-tabs m-card bd">
                                    <Tabs
                                        animated={false}
                                    >
                                        <TabPane tab="概览" key="1">
                                            <TableOverview
                                                routeParams={
                                                    this.props.routeParams
                                                }
                                            />
                                        </TabPane>
                                        <TabPane tab="原因分析" key="2">
                                            <TableAnalytics
                                                routeParams={
                                                    this.props.routeParams
                                                }
                                            />
                                        </TabPane>
                                        <TabPane tab="字段信息" key="3">
                                            <Card
                                                bordered={false}
                                                noHovering
                                                title={
                                                    <RadioGroup
                                                        defaultValue={showType}
                                                        onChange={this.switchType.bind(
                                                            this
                                                        )}
                                                    >
                                                        <RadioButton value={0}>
                                                            非分区字段
                                                        </RadioButton>
                                                        <RadioButton value={1}>
                                                            分区字段
                                                        </RadioButton>
                                                    </RadioGroup>
                                                }
                                                extra={
                                                    <p
                                                        style={{
                                                            color: '#ccc'
                                                        }}
                                                    >
                                                        共{' '}
                                                        {(fieldsData &&
                                                            fieldsData.length) ||
                                                            0}{' '}
                                                        个字段
                                                    </p>
                                                }
                                            >
                                                {tableData && (
                                                    <Table
                                                        rowKey="index"
                                                        className="dt-ant-table dt-ant-table--border dt-ant-table--padding"
                                                        columns={columns}
                                                        dataSource={fieldsData}
                                                    />
                                                )}
                                            </Card>
                                        </TabPane>
                                    </Tabs>
                                </div>
                            </Row>
                        </main>
                    </div>
                </div>
            </div>
        );
    }
}
