import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import moment from 'moment';
import SplitPane from 'react-split-pane';
import { Row, Col, Table, Button, Tabs, Radio, Icon, Modal, message } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';

import utils from 'utils';

import Editor from '../../../../components/code-editor';
import ajax from '../../../../api';
import TableOverview from './overview';
import TableAnalytics from './analytics';

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group

export default class TableDetail extends React.Component {

    constructor(props) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
        this.state = {
            tableData: {},
            showType: 0, // 0/1 (非)字段
        };
    }

    componentDidMount() {
        this.getTable();
    }

    getTable() {
        ajax.getDirtyDataTableInfo({ tableId: this.tableId }).then(res => {
            if(res.code === 1) {
                this.setState({
                    tableData: res.data
                });
            }
        });
    }

    switchType(evt) {
        const showType = evt.target.value;
        this.setState({
            showType
        });
    }

    getPreview(key) {
        const { previewData } = this.state;
        if(previewData) return;
        if(+key === 2 || +key === 3) {
            ajax.previewTable({tableId: this.tableId}).then(res => {
                if(res.code === 1 && res.data) {
                    this.setState({
                        previewData: this.formatPreviewData(res.data)
                    });
                }
            });
        }
    }

    formatPreviewData(arr) {
        const cols = arr.shift();

        this.previewCols = cols;
        return arr.map(keyArr => {
            let o = {};
            for(let i = 0; i < keyArr.length; i++) {
                o[cols[i]] = keyArr[i]
            }
            return o
        });
    }

    handleCancel() {
        this.setState({
            visible: false
        });
    }

    handleOk() {
        message.info('复制成功，代码窗口即将关闭');
        setTimeout(() => {
            this.setState({
                visible: false
            });
        }, 1000)
    }

    render() {
        const { showType, tableData, previewData } = this.state;
        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index'
        },{
            title: '字段名称',
            dataIndex: 'name',
            key: 'name'
        },{
            title: '类型',
            dataIndex: 'type',
            key: 'type'
        },{
            title: '注释',
            dataIndex: 'comment',
            key: 'comment',
            render(text) {
                return text
            }
        }];

        const fieldsData = showType === 0 ? tableData.column : tableData.partition;
        const tableInfo = tableData.table || {};
        const relTasks = tableInfo.tasks && tableInfo.tasks.map(i => i.name)
        
        return <div className="g-tableviewer">
            <div className="m-tableviewerhead">
                <Button type="default" className="f-fr" style={{ marginTop: 7 }}>
                    <Link to="/data-manage/dirty-data">返回</Link>
                </Button>
                <h3>{ tableInfo && tableInfo.tableName }</h3>
            </div>
            <SplitPane split="vertical" minSize={200} defaultSize={300}>
                <div className="m-tablebasic">
                    <h3 className="clearfix">基本信息</h3>
                    { tableInfo && <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>所属项目</th>
                                <td>{ tableInfo.project }</td>
                            </tr>
                            <tr>
                                <th>创建者：</th>
                                <td>{ tableInfo.userName }</td>
                            </tr>
                            <tr>
                                <th>创建时间</th>
                                <td>{ moment(tableInfo.createTime).format('YYYY-MM-DD HH:mm:ss') }</td>
                            </tr>
                            <tr>
                                <th>相关任务</th>
                                <td>{ relTasks || '无' }</td>
                            </tr>
                            <tr>
                                <th>描述</th>
                                <td>{ tableInfo.tableDesc }</td>
                            </tr>
                        </tbody>
                    </table> }
                    <h3>存储信息</h3>
                    { tableInfo && <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>物理存储量</th>
                                <td>{ tableInfo.storeSize }</td>
                            </tr>
                            <tr>
                                <th>生命周期</th>
                                <td>{tableInfo.lifeDay}天</td>
                            </tr>
                            <tr>
                                <th>是否分区</th>
                                <td>{ tableInfo.partitions ? '是' : '否' }</td>
                            </tr>
                            <tr>
                                <th>DDL最后变更时间</th>
                                <td>{ utils.formatDateTime(tableInfo.lastDDLTime) }</td>
                            </tr>
                            <tr>
                                <th>数据最后变更时间</th>
                                <td>{ utils.formatDateTime(tableInfo.lastDataChangeTime) }</td>
                            </tr>
                        </tbody>
                    </table> }
                </div>
                <div className="m-tabledetail">
                    <Tabs type="card"
                        onChange={ this.getPreview.bind(this) }
                    >
                        <TabPane tab="概览" key="1">
                            <TableOverview routeParams={ this.props.routeParams }/>
                        </TabPane>
                        <TabPane tab="原因分析" key="2">
                            <TableAnalytics routeParams={ this.props.routeParams }/>
                        </TabPane>
                        <TabPane tab="字段信息" key="3">
                            <div className="box">
                                <RadioGroup
                                    defaultValue={showType}
                                    onChange={ this.switchType.bind(this) }
                                    style={{ marginBottom: 10 }}
                                >
                                    <RadioButton value={0}>非分区字段</RadioButton>
                                    <RadioButton value={1}>分区字段</RadioButton>
                                </RadioGroup>
                                { tableData && <Table
                                    rowKey="index"
                                    columns={ columns }
                                    dataSource={ fieldsData }
                                />}
                                <p style={{ marginTop: 5, color: '#ccc' }}>
                                    共 { fieldsData && fieldsData.length || 0} 个字段
                                </p>
                            </div>
                        </TabPane>
                    </Tabs>
                </div>
            </SplitPane>
        </div>
    }
}