import React, { Component } from 'react';
import { Table, Row, Col, Icon } from 'antd';
import GoBack from 'main/components/go-back';
import TableCell from 'widgets/tableCell';
import DCApi from '../../../api/dataCheck';

export default class DataCheckReport extends Component {
    constructor(props) {
        super(props);
        this.state = {
            params: {
                currentPage: 1,
                pageSize: 20,
                verifyRecordId: this.props.routeParams.verifyRecordId
            },
            tableData: {},
            reportData: {}
        };
    }

    componentDidMount() {
        const { params } = this.state;
        let verifyRecordId = params.verifyRecordId;

        DCApi.getCheckReport({ verifyRecordId }).then((res) => {
            if (res.code === 1) {
                this.setState({ reportData: res.data });
            }
        });
        DCApi.getCheckReportTable(params).then((res) => {
            if (res.code === 1) {
                console.log(res.data)
                this.setState({ tableData: res.data });
            }
        });
    }

    initColumns = (data) => {
        return data.length && data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80,
                render: (value) => {
                    return <TableCell 
                        className="no-scroll-bar"
                        value={value ? value : undefined}
                        readOnly
                        style={{ minWidth: 80, width: '100%', resize: 'none' }} 
                    />
                }
            }
        });
    }

    // 表格换页
    onTableChange = (page, filter, sorter) => {
        let params = {
            ...this.state.params,
            currentPage: page.current,
        }
        // this.props.getCheckReportTable(params);
        DCApi.getCheckReportTable(params).then((res) => {
            if (res.code === 1) {
                this.setState({ 
                    params,
                    tableData: res.data 
                });
            }
        });
    }

    handleDownload = () => {
        const { params } = this.state;
        let getParams = `verifyRecordId=${params.verifyRecordId}&currentPage=${params.currentPage}&pageSize=${params.pageSize}`;
        window.open(`/api/dq/export/verify/doExport?${getParams}`);
    }

    render() {
        const { params, tableData, reportData } = this.state;
        const { verifyVO, lAll, rAll, mapSuccess, mapFailure, rightUnfound, leftUnfound, dataSourceEN } = reportData;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: tableData.totalCount
        }

        return (
            <div className="box-1">
                <h1 className="box-title">
                    <GoBack /> 
                    <span className="m-l-8">查看报告</span>
                </h1>
                <div className="m-report-table">
                    <h3>
                        整体校验
                    </h3>
                    <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th></th>
                                <th>左侧表</th>
                                <th>右侧表</th>
                            </tr>
                            <tr>
                                <th>表名</th>
                                <td>{verifyVO ? verifyVO.originTableName : ''}</td>
                                <td>{verifyVO ? verifyVO.targetTableName : ''}</td>
                            </tr>
                            <tr>
                                <th>分区名</th>
                                <td>{verifyVO ? verifyVO.originPartition : ' -- '}</td>
                                <td>{verifyVO ? verifyVO.targetPartition : ' -- '}</td>
                            </tr>
                            <tr>
                                <th>类型</th>
                                <td>{dataSourceEN}</td>
                                <td>{dataSourceEN}</td>
                            </tr>
                            <tr>
                                <th>总记录数</th>
                                <td>{lAll}</td>
                                <td>{rAll}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div className="m-report-table">
                    <h3>
                        未匹配数据报告
                    </h3>
                    <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>匹配成功</th>
                                <td className="width-3">{mapSuccess}</td>
                                <th>左表数据在右表未找到</th>
                                <td className="width-3">{rightUnfound}</td>
                            </tr>
                            <tr>
                                <th>逻辑主键匹配，但数据不匹配</th>
                                <td className="width-3">{mapFailure}</td>
                                <th>右表数据在左表未找到</th>
                                <td className="width-3">{leftUnfound}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div>
                    <h3 className="table-h3-title flex" style={{ justifyContent: 'space-between' }}>
                        具体差异
                        <Icon 
                            type="download" 
                            onClick={this.handleDownload}
                            style={{ fontSize: 16, marginRight: 25, cursor: 'pointer' }} />
                    </h3>
                    <Table 
                        // rowKey="key"
                        // bordered
                        className="m-cells m-table"
                        style={{ margin: '0 20px' }}
                        scroll={{ x: 1000 }}
                        pagination={pagination}
                        dataSource={tableData.data ? tableData.data : []}
                        columns={this.initColumns(tableData.attachment ? tableData.attachment: [])} 
                        onChange={this.onTableChange}
                    />
                </div>
            </div>
        );
    }
}