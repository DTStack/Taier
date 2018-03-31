import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Table, Row, Col } from 'antd';
import { dataCheckActions } from '../../../actions/dataCheck';
import GoBack from 'main/components/go-back';
import TableCell from 'widgets/tableCell';

const mapStateToProps = state => {
    const { dataCheck, common } = state;
    return { dataCheck, common }
}

const mapDispatchToProps = dispatch => ({
    getCheckReport(params) {
        dispatch(dataCheckActions.getCheckReport(params));
    },
    getCheckReportTable(params) {
        dispatch(dataCheckActions.getCheckReportTable(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheckReport extends Component {
    constructor(props) {
        super(props);
        this.state = {
            params: {
                currentPage: 1,
                pageSize: 20
            }
        };
    }

    componentDidMount() {
        const { verifyRecordId } = this.props.routeParams;
        let params = {...this.state.params, verifyRecordId};

        this.props.getCheckReport({ verifyRecordId });
        this.props.getCheckReportTable(params);
        this.setState({ params });
    }

    initColumns = (data) => {
        return Object.keys(data).map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80,
                render: function(txt) {
                    return <TableCell 
                        className="no-scroll-bar"
                        value={txt} 
                        resize="none"
                        style={{ minWidth: '80px',width:'100%' }} 
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
        this.setState({ params });
        this.props.getCheckReportTable(params);
    }

    render() {
        const { dataCheck, common } = this.props;
        const { verifyVO, lAll, rAll, mapSuccess, mapFailure, rightUnfound, leftUnfound, dataSourceEN } = dataCheck.checkReport;
        const { reportTable } = dataCheck;
        const { params } = this.state;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: reportTable.totalCount
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
                    <h3 className="table-h3-title">
                        具体差异
                    </h3>
                    <Table 
                        // rowKey="key"
                        // bordered
                        className="m-cells m-table"
                        style={{ margin: '0 20px' }}
                        columns={this.initColumns(reportTable.data ? reportTable.data[0]: {})} 
                        dataSource={reportTable.data ? reportTable.data : []}
                        pagination={pagination}
                        scroll={{ x: 1000 }}
                        onChange={this.onTableChange}
                    />
                </div>
            </div>
        );
    }
}