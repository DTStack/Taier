import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import moment from 'moment';
import SplitPane from 'react-split-pane';
import { 
    Row, Col, Table, Button, 
    Tabs, Radio, Icon, 
    Modal, message, Card
} from 'antd';

import GoBack from 'main/components/go-back';

import Editor from '../../components/code-editor';
import ajax from '../../api';
import CopyToClipboard from 'react-copy-to-clipboard';
import TablePartition from './tablePartition';
import TableRelation from './tableRelation';

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group

export default class TableViewer extends React.Component{
    constructor(props) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
        this.state = {
            showType: 0, // 0/1 (非)字段
            visible: false,
            code: ''
        };
    }

    componentDidMount() {
        this.getTable();
    }

    getTable() {
        ajax.getTable({tableId: this.tableId}).then(res => {
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

    getCreateCode() {
        !this.state.code ? ajax.getCreateTableCode({
            tableId: this.tableId
        }).then(res => {
            if(res.code === 1 && res.data) {
                this.setState({
                    visible: true,
                    code: res.data
                });
            }
            else{
                message.error('从服务器获取数据失败！');
            }
        }) :
        this.setState({
            visible: true,
            code: this.state.code
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
            key: 'index',
            render(index) {
                return ++index
            }
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

        return <div className="g-tableviewer box-1">
            <div className="box-card">
                <main>
                    <h1 className="card-title"><GoBack /> 查看表：{ tableData && tableData.table.tableName }</h1>
                    <Row className="box-card m-tablebasic">
                        <Col span={12} className="col-sep">
                            <h3>
                                基本信息
                                <Button 
                                    type="primary" 
                                    className="right"
                                    onClick={ this.getCreateCode.bind(this) }
                                >
                                    生成建表语句
                                </Button>
                            </h3>
                            { tableData && <table width="100%" cellPadding="0" cellSpacing="0">
                                <tbody>
                                    <tr>
                                        <th>所属项目</th>
                                        <td>{ tableData.table.project }</td>
                                    </tr>
                                    <tr>
                                        <th>创建者：</th>
                                        <td>{ tableData.table.userName }</td>
                                    </tr>
                                    <tr>
                                        <th>创建时间</th>
                                        <td>{ moment(tableData.table.createTime).format('YYYY-MM-DD HH:mm:ss') }</td>
                                    </tr>
                                    <tr>
                                        <th>所属类目</th>
                                        <td>{ tableData.table.catalogue }</td>
                                    </tr>
                                    <tr>
                                        <th>描述</th>
                                        <td style={{height: '50px', width: '100%', border: 0 }} className="cell-overflow no-scroll-bar">
                                            {tableData.table.tableDesc}
                                        </td>
                                    </tr>
                                </tbody>
                            </table> }
                        </Col>
                        <Col span={12} className="col-sep">
                            <h3>存储信息</h3>
                            { tableData && <table width="100%" cellPadding="0" cellSpacing="0">
                                <tbody>
                                    <tr>
                                        <th>物理存储量</th>
                                        <td>{ tableData.table.storeSize }</td>
                                    </tr>
                                    <tr>
                                        <th>生命周期</th>
                                        <td>{tableData.table.lifeDay}天</td>
                                    </tr>
                                    <tr>
                                        <th>是否分区</th>
                                        <td>{ tableData.table.partitions ? '是' : '否' }</td>
                                    </tr>
                                    <tr>
                                        <th>DDL最后变更时间</th>
                                        <td>{ moment(tableData.table.lastDDLTime).format('YYYY-MM-DD HH:mm:ss') }</td>
                                    </tr>
                                    <tr>
                                        <th>数据最后变更时间</th>
                                        <td>{ moment(tableData.table.lastDataChangeTime).format('YYYY-MM-DD HH:mm:ss') }</td>
                                    </tr>
                                </tbody>
                            </table> }
                        </Col>
                    </Row>
                    <Row style={{ padding: '0 30px' }}>
                        <div className="m-tabs m-card bd" style={{height: '700px'}}>
                            <Tabs 
                                animated={false}
                                onChange={ this.getPreview.bind(this) }
                            >
                                <TabPane tab="字段信息" key="1">
                                    <Card
                                        bordered={false}
                                        noHovering
                                        title={
                                            <RadioGroup
                                                defaultValue={showType}
                                                onChange={ this.switchType.bind(this) }
                                                style={{ marginTop: 10 }}
                                            >
                                                <RadioButton value={0}>非分区字段</RadioButton>
                                                <RadioButton value={1}>分区字段</RadioButton>
                                            </RadioGroup>
                                        }
                                        extra={
                                            <p style={{ color: '#ccc' }}>
                                                共 { tableData ? (tableData[showType === 0 ? 'column' : 'partition'].length) : 0} 个字段
                                            </p>
                                        }
                                    >
                                        { tableData && <Table
                                            className="m-table"
                                            columns={ columns }
                                            dataSource={ showType === 0 ? tableData.column : tableData.partition }
                                        />}
                                    </Card>
                                </TabPane>
                                <TabPane tab="分区信息" key="2">
                                    <TablePartition table={tableData && tableData.table} />
                                </TabPane>
                                <TabPane tab="数据预览" key="3">
                                    <div className="box">
                                        { previewData ? <Table
                                            columns={ this.previewCols.map((str,i) => ({
                                                title: str,
                                                dataIndex: str,
                                                key: str + i
                                            })) }
                                            className="m-table"
                                            dataSource={ previewData }
                                            scroll={{ x: 200 * this.previewCols.length }}
                                        ></Table> :
                                            <p style={{
                                                marginTop: 20,
                                                textAlign: 'center',
                                                fontSize: 12,
                                                color: '#ddd'
                                            }}><Icon type="exclamation-circle-o" /> 此表中没有数据 </p>
                                        }
                                    </div>
                                </TabPane>
                                <TabPane tab="血缘信息" key="4">
                                    <TableRelation tableData={tableData && tableData.table}/>
                                </TabPane>
                            </Tabs>
                        </div>
                    </Row>
                </main>
            </div>

            <Modal className="m-codemodal"
                title="建表语句"
                width="750"
                visible={this.state.visible}
                closable
                onCancel={this.handleCancel.bind(this)}
                footer={[
                    <Button key="cancel" onClick={ this.handleCancel.bind(this) }>取消</Button>,
                     <CopyToClipboard key="copy" text={this.state.code}
                        onCopy={this.handleOk.bind(this)}>
                        <Button type="primary">复制</Button>
                    </CopyToClipboard>
                ]}
            >
                <Editor value={ this.state.code } readOnly style={{height: '400px'}}/>
            </Modal>
        </div>
    }
}