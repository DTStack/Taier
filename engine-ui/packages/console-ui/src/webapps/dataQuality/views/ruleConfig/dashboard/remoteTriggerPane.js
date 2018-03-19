import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Table, message, Modal } from 'antd';
import moment from 'moment';

import RCApi from '../../../api/ruleConfig';

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
}

@connect(mapStateToProps)
export default class RemoteTriggerPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            rules: [],
            monitorPart: {},
            detail: {},
            visible: false,
            selectedIds: []
        };
    }

    componentDidMount() {
        const { data } = this.props;
        console.log(data, 'mount1')

        RCApi.getMonitorRule({
            monitorId: data.monitorPartVOS[0].monitorId
        }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    rules: res.data
                });
            }
        });

        this.setState({
            monitorPart: data.monitorPartVOS[0]
        });
    }

    initColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            key: 'columnName',
            render: (text, record) => this.renderColumns(text, record, 'columnName'),
            width: '17%',
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => this.renderColumns(text, record, 'functionId'),
            width: '17%',
        }, 
        {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => this.renderColumns(text, record, 'filter'),
            width: '16%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => this.renderColumns(text, record, 'verifyType'),
            width: '14%',
        }, {
            title: '阈值配置',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => this.renderColumns(text, record, 'threshold'),
            width: '10%'
        }, {
            title: '最近修改人',
            key: 'modifyUser',
            dataIndex: 'modifyUser',
            width: '13%',
            
        }, {
            title: '最近修改时间',
            key: 'gmtModified',
            dataIndex: 'gmtModified',
            width: '13%',
            render: (text) => (moment(text).format("YYYY-MM-DD HH:mm"))
        }]  

    }

    renderColumns(text, record, type) {
        const { currentRule } = this.state;
        let obj = {
            children: this.renderTD(text, record, type),
            props: {},
        };

        if (record.isCustomizeSql) {
            switch(type) {
                case 'columnName':
                    obj.props.colSpan = 3;
                    break;
                case 'functionId':
                    obj.props.colSpan = 0;
                    break;
                case 'filter':
                    obj.props.colSpan = 0;
                    break;
                default:
                    break;
            }
        }

        return obj;
    }

    renderTD = (text, record, type) => {
        const { ruleConfig, common } = this.props;
        const { monitorFunction } = ruleConfig;
        const { verifyType } = common.allDict;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return record.customizeSql;
                } else {
                    return text;
                }
            }

            case 'functionId': {
                return  record.functionName;
            }

            case 'verifyType': {
                return verifyType[text - 1].name || undefined;
            }

            case 'threshold': {
                return text ? `${record.operator}  ${text}` : 0;
            }

            default:
                return text;
        }
    }

    onMonitorIdChange = (value) => {

    }

    showRemoteModal = () => {
        this.setState({
            visible: true
        })
    }

    closeRemoteModal = () => {
        this.setState({
            visible: false
        })
    }

    render() {
        const { data, ruleConfig, common } = this.props;
        const { rules, monitorPart, visible, selectedIds } = this.state;

        let rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds, selectedRows) => {
                console.log(selectedIds,selectedRows)
                this.setState({
                    selectedIds: selectedIds,
                    selectedRows: selectedRows
                })
            },
        };

        return (
            <div className="trigger-box">
                <h2>什么是远程触发？</h2>
                <p>您可以在自建的大数据平台或公有云平台上通过接口调用的形式,触发本系统中的数据质量校验任务执行。</p>
                <p>典型的场景：您自建的大数据平台上对某张表进行ETL任务处理，需要在ETL任务处理前，
                对此表的数据质量进行校验，校验通过后再启动后续的计算任务
                （防止错误数据流入下一个计算环节，同时可以节约计算资源）。
                </p>

                <p style={{ margin: '10px 0' }}>配置的流程如下：</p>
                <p>1、在本平台配置表的数据质量检测任务，并在本页面开启远程触发</p>
                <p>2、在您自建的大数据平台上，新建一个脚本任务，并编写调用的代码，详细的使用教程请参考《帮助文档》；</p>
                <p>3、在您自建的大数据平台上，将数据处理任务作为脚本任务的下游任务；</p>

                <Button type="primary" onClick={this.showRemoteModal}>配置远程触发</Button>

                <Modal
                    title="配置远程调用"
                    wrapClassName="remoteTriggerModal"
                    maskClosable={false}
                    visible={visible}
                    width={'85%'}
                    okText="确认"
                    cancelText="取消"
                    onOk={this.closeRemoteModal}
                    onCancel={this.closeRemoteModal}>
                    <Table 
                        rowKey="id"
                        className="m-table rule-edit-table"
                        rowSelection={rowSelection}
                        columns={this.initColumns()}
                        pagination={false}
                        dataSource={rules}
                    />
                </Modal>
            </div>
        );
    }
}