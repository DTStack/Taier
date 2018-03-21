import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty, cloneDeep } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm, Row, Col, Card } from 'antd';
import { dataCheckActions } from '../../../actions/dataCheck';
import moment from 'moment';
import GoBack from 'main/components/go-back';

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
        const { routeParams } = this.props;

        this.props.getCheckReport({ 
            verifyRecordId: routeParams.verifyRecordId 
        });
        this.props.getCheckReportTable({ 
            ...this.state.params,
            verifyRecordId: routeParams.verifyRecordId 
        });
    }

    initColumns = (data) => {
        return Object.keys(data).map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80,
            }
        });
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
                    查看报告
                </h1>
                <Row className="m-report-table">
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
                                <td>{verifyVO ? `${verifyVO.originPartitionColumn} -- ${verifyVO.originPartitionValue}` : ' -- '}</td>
                                <td>{verifyVO ? `${verifyVO.targetPartitionColumn} -- ${verifyVO.targetPartitionValue}` : ' -- '}</td>
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
                </Row>
                <Row className="m-report-table">
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
                                <th>id匹配，但数据不匹配</th>
                                <td className="width-3">{mapFailure}</td>
                                <th>右表数据在左表未找到</th>
                                <td className="width-3">{leftUnfound}</td>
                            </tr>
                        </tbody>
                    </table>
                </Row>
                <Row>
                    <h3 className="table-h3-title">
                        具体差异
                    </h3>
                    <Table 
                        // rowKey="key"
                        // bordered
                        className="m-table"
                        style={{ margin: '0 20px' }}
                        columns={this.initColumns(reportTable.data ? reportTable.data[0]: [])} 
                        dataSource={reportTable.data ? reportTable.data : []}
                        pagination={pagination}
                        scroll={{ x: '120%' }}
                    />
                </Row>
            </div>
        );
    }
}