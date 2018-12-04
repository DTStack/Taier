import React from 'react';
import moment from 'moment';
import {
    Row, Col, Table, Button,
    Tabs, Radio, Icon,
    Modal, message, Card,
    notification
} from 'antd';

import GoBack from 'main/components/go-back';

import { APPLY_RESOURCE_TYPE } from '../../comm/const';
import Editor from 'widgets/editor';
import ajax from '../../api/dataManage';
import CopyToClipboard from 'react-copy-to-clipboard';
import TablePartition from './tablePartition';
import TableRelation from './tableRelation';
import TableApplyModal from './search/tableApply'

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class TableViewer extends React.Component {
    constructor (props) {
        super(props);
        this.tableId = this.props.routeParams.tableId;
        this.queryParams = { tableId: this.tableId };
        this.state = {
            showType: 0, // 0/1 (非)字段
            visible: false,
            code: '',
            isMark: false,
            tableData: '',
            applyButton: false,
            showTableRelation: true,
            applyModal: {
                visible: false,
                data: {}
            }
        };
    }

    componentDidMount () {
        this.getTable();
    }

    changeMark () {
        const { isMark } = this.state;
        if (isMark) {
            ajax.cancelMark(this.queryParams).then(res => {
                if (res.code === 1) {
                    message.info('取消收藏成功')
                    this.setState({
                        isMark: !isMark
                    })
                } else {
                    message.info('取消收藏失败,请再次点击')
                }
            })
        } else {
            ajax.addMark(this.queryParams).then(res => {
                if (res.code === 1) {
                    message.info('收藏成功')
                    this.setState({
                        isMark: !isMark
                    })
                } else {
                    message.info('收藏失败,请再次点击')
                }
            })
        }
    }

    getTable () {
        ajax.getTable(this.queryParams).then(res => {
            if (res.code === 1) {
                const isMark = res.data.table.isCollect == '1';
                const applyButton = res.data.table.permissionStatus == '0';
                this.setState({
                    tableData: res.data,
                    isMark,
                    applyButton
                });
            }
        })
    }

    switchType (evt) {
        const showType = evt.target.value;
        this.setState({
            showType
        });
    }

    getPreview (key) {
        const { previewData } = this.state;
        if (previewData) return;
        if (+key === 3) {
            ajax.previewTable(this.queryParams).then(res => {
                if (res.code === 1 && res.data) {
                    this.setState({
                        previewData: this.formatPreviewData(res.data)
                    });
                }
            });
        }
        this.setState({ showTableRelation: true })
    }

    formatPreviewData (arr) {
        const cols = arr.shift();

        this.previewCols = cols;
        return arr.map(keyArr => {
            let o = {};
            for (let i = 0; i < keyArr.length; i++) {
                o[cols[i]] = keyArr[i]
            }
            return o
        });
    }

    getCreateCode () {
        !this.state.code ? ajax.getCreateTableCode(this.queryParams).then(res => {
            if (res.code === 1 && res.data) {
                this.setState({
                    visible: true,
                    code: res.data
                });
            } else {
                notification['error']({
                    message: '异常',
                    description: '从服务器获取数据失败！'
                });
            }
        })
            : this.setState({
                visible: true,
                code: this.state.code
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
        }, 1000)
    }

    onShowBloodRelation = (flag) => {
        this.setState({
            showTableRelation: flag
        })
    }

    apply = (applyData) => {
        const { applyModal } = this.state;
        const params = { ...applyData };
        params.applyResourceType = APPLY_RESOURCE_TYPE.TABLE;
        params.resourceId = applyModal.data.id;
        ajax.applyTable(params).then(res => {
            if (res.code === 1) {
                message.success('申请成功！')
                applyModal.visible = false;
                // applyModal.data = {};
                this.setState({ applyModal }, this.getTable)
            }
        })
    }

    showApply = () => {
        const { applyModal, tableData } = this.state;
        applyModal.visible = true;
        applyModal.data = tableData.table;
        this.setState({
            applyModal
        })
    }

    cancelApply = () => {
        const { applyModal } = this.state;
        applyModal.visible = false;
        // applyModal.data = {};
        this.setState({
            applyModal
        })
    }

    render () {
        const { showType, tableData, previewData, isMark, applyModal, applyButton } = this.state;

        const columns = [{
            title: '序号',
            dataIndex: 'columnIndex',
            key: 'columnIndex',
            render (index) {
                return ++index
            }
        }, {
            title: '字段名称',
            dataIndex: 'columnName',
            key: 'columnName'
        }, {
            title: '类型',
            dataIndex: 'columnType',
            key: 'columnType'
        }, {
            title: '注释',
            dataIndex: 'comment',
            key: 'comment',
            render (text) {
                return text
            }
        }];

        return <div className="box-1">
            <div className="box-card full-screen-table-40">
                <main>
                    <div >
                        <h1 className="card-title">
                            <GoBack type="textButton" autoClose={true} /> 查看表：{tableData && tableData.table.tableName}
                            <span className="right">
                                <Button className="button-top" type="primary" onClick={this.changeMark.bind(this)}>{isMark ? '取消收藏' : '收藏'}</Button>
                                {applyButton ? <Button className="button-top" type="primary" onClick={this.showApply}>申请授权</Button> : ''}
                                <Button
                                    type="primary"
                                    className="button-top"
                                    onClick={this.getCreateCode.bind(this)}
                                >
                                    生成建表语句
                                </Button>
                            </span>
                        </h1>
                    </div>
                    <Row className="m-tablebasic">
                        <Col span={12} className="col-sep" style={{ paddingLeft: 0 }}>
                            <h3> 基本信息 </h3>
                            {tableData && <table width="100%" cellPadding="0" cellSpacing="0">
                                <tbody>
                                    <tr>
                                        <th>所属项目</th>
                                        <td>{tableData.table.projectAlias}</td>
                                    </tr>
                                    <tr>
                                        <th>负责人</th>
                                        <td>{tableData.table.chargeUser}</td>
                                    </tr>
                                    <tr>
                                        <th>创建时间</th>
                                        <td>{moment(tableData.table.gmtCreate).format('YYYY-MM-DD HH:mm:ss')}</td>
                                    </tr>
                                    <tr>
                                        <th>表类型</th>
                                        <td>
                                            {tableData.table.tableType == 'EXTERNAL' ? (
                                                <span>外部表({tableData.table.location})</span>
                                            ) : (
                                                <span>内部表</span>
                                            )}
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>所属类目</th>
                                        <td>{tableData.table.catalogue}</td>
                                    </tr>
                                    <tr>
                                        <th>描述</th>
                                        <td style={{ height: '50px', width: '100%', border: 0, verticalAlign: 'middle' }} className="cell-overflow no-scroll-bar">
                                            <div className="vertical-middle">{tableData.table.tableDesc}</div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>}
                        </Col>
                        <Col span={12} className="col-sep" style={{ paddingRight: 0 }}>
                            <h3>存储信息</h3>
                            {tableData && <table width="100%" cellPadding="0" cellSpacing="0">
                                <tbody>
                                    <tr>
                                        <th>物理存储量</th>
                                        <td>{tableData.table.tableSize}</td>
                                    </tr>
                                    <tr>
                                        <th>生命周期</th>
                                        <td>{tableData.table.lifeDay}天</td>
                                    </tr>
                                    <tr>
                                        <th>是否分区</th>
                                        <td>{tableData.table.partition ? '是' : '否'}</td>
                                    </tr>
                                    <tr>
                                        <th>表结构最后变更时间</th>
                                        <td>{moment(tableData.table.lastDdlTime).format('YYYY-MM-DD HH:mm:ss')}</td>
                                    </tr>
                                    <tr>
                                        <th>数据最后变更时间</th>
                                        <td>{moment(tableData.table.lastDmlTime).format('YYYY-MM-DD HH:mm:ss')}</td>
                                    </tr>
                                    <tr>
                                        <th>存储格式</th>
                                        <td>{tableData.table.storedType}</td>
                                    </tr>
                                </tbody>
                            </table>}
                        </Col>
                    </Row>
                    <Row>
                        <div className="m-tabs m-card bd">
                            <Tabs
                                animated={false}
                                onChange={this.getPreview.bind(this)}
                                style={{ height: 'auto' }}
                            >
                                <TabPane tab="字段信息" key="1">
                                    <Card
                                        bordered={false}
                                        noHovering
                                        title={
                                            <RadioGroup
                                                defaultValue={showType}
                                                onChange={this.switchType.bind(this)}
                                                style={{ marginTop: 10 }}
                                            >
                                                <RadioButton value={0}>非分区字段</RadioButton>
                                                <RadioButton value={1}>分区字段</RadioButton>
                                            </RadioGroup>
                                        }
                                        extra={
                                            <p style={{ color: '#ccc' }}>
                                                共 {tableData ? (tableData[showType === 0 ? 'column' : 'partition'].length) : 0} 个字段
                                            </p>
                                        }
                                    >
                                        {tableData && <Table
                                            className="m-table"
                                            columns={columns}
                                            rowKey="id"
                                            dataSource={showType === 0 ? tableData.column : tableData.partition}
                                        />}
                                    </Card>
                                </TabPane>
                                <TabPane tab="分区信息" key="2">
                                    <TablePartition table={tableData && tableData.table} />
                                </TabPane>
                                <TabPane tab="数据预览" key="3">
                                    <div className="box">
                                        {previewData ? <Table
                                            columns={this.previewCols.map((str, i) => ({
                                                title: str,
                                                dataIndex: str,
                                                key: str + i,
                                                width: '200px'
                                            }))}
                                            className="m-table"
                                            dataSource={previewData}
                                            scroll={{ x: 200 * this.previewCols.length }}
                                        ></Table>
                                            : <p style={{
                                                marginTop: 20,
                                                textAlign: 'center',
                                                fontSize: 12,
                                                color: '#ddd'
                                            }}><Icon type="exclamation-circle-o" /> 此表中没有数据 </p>
                                        }
                                    </div>
                                </TabPane>
                                <TabPane tab="血缘信息" key="4">
                                    <TableRelation
                                        showTableRelation={this.state.showTableRelation}
                                        onShowBloodRelation={this.onShowBloodRelation}
                                        tableData={tableData && tableData.table}
                                    />
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
                maskClosable={false}
                closable
                onCancel={this.handleCancel.bind(this)}
                footer={[
                    <Button key="cancel" onClick={this.handleCancel.bind(this)}>取消</Button>,
                    <CopyToClipboard key="copy" text={this.state.code}
                        onCopy={this.handleOk.bind(this)}>
                        <Button type="primary">复制</Button>
                    </CopyToClipboard>
                ]}
            >
                <Editor value={this.state.code} language="dtsql" options={{ readOnly: false }} style={{ height: '400px' }} />
            </Modal>
            <TableApplyModal
                visible={applyModal.visible}
                table={applyModal.data}
                onOk={this.apply}
                onCancel={this.cancelApply}
            />
        </div>
    }
}
