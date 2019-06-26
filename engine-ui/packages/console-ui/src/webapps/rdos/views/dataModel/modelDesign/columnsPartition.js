import React from 'react';
import { Row, Col, Icon } from 'antd';

import RowItem from './rowItem';
import { TABLE_TYPE } from '../../../comm/const'
/**
 * @description step2:字段与分区
 * @export
 * @class ColumnsPartition
 * @extends {React.Component}
 */
export default class ColumnsPartition extends React.Component {
    constructor (props) {
        super(props);
    }

    addRow (type) {
        this.props.addRow({
            columnName: '',
            columnType: 'STRING',
            columnDesc: '',
            uuid: Date.now()
        }, type);
    }

    delRow (type, uuid) {
        this.props.delRow(uuid, type);
    }

    replaceRow (type, newCol) {
        this.props.replaceRow(newCol, type);
    }

    moveRow (type, uuid, isUp) {
        this.props.moveRow(uuid, type, isUp);
    }

    render () {
        const { columns, partition_keys, isEdit, columnFileds, tableType } = this.props;// eslint-disable-line
        const isHiveTable = tableType == TABLE_TYPE.HIVE;
        return <div className="m-columnspartition">
            <div className="columns box">
                <h3>字段信息</h3>
                <div className="table">
                    <Row className="title">
                        <Col span={4} className="cell">字段名</Col>
                        <Col span={8} className="cell">类型</Col>
                        <Col span={7} className="cell">注释</Col>
                        <Col span={5} className="cell">操作</Col>
                    </Row>
                    { columns.map((col, i) => <RowItem
                        data={ col }
                        key={ col.uuid || i }
                        columnFileds={columnFileds}
                        delRow={ this.delRow.bind(this, 1) }
                        replaceRow={ this.replaceRow.bind(this, 1) }
                        moveRow={ this.moveRow.bind(this, 1) }
                    />)}
                </div>
                <div className="fn">
                    <a href="javascript:void(0)" disabled={ isEdit } onClick={ this.addRow.bind(this, 1) }>
                        <Icon type="plus-circle-o" /> 新增字段
                    </a>
                </div>
            </div>
            {
                isHiveTable && (
                    <div className="partition box">
                        <h3>分区信息</h3>
                        <div className="table">
                            <Row className="title">
                                <Col span={4} className="cell">字段名</Col>
                                <Col span={8} className="cell">类型</Col>
                                <Col span={7} className="cell">注释</Col>
                                <Col span={5} className="cell">操作</Col>
                            </Row>
                            { partition_keys.map((partition, i) => <RowItem
                                columnFileds={columnFileds}
                                data={{ ...partition, isPartition: true }}
                                key={ partition.uuid || i }
                                delRow={ this.delRow.bind(this, 2) }
                                replaceRow={ this.replaceRow.bind(this, 2) }
                                moveRow={ this.moveRow.bind(this, 2) }
                            />)}
                        </div>
                        <div className="fn">
                            <a href="javascript:void(0)"
                                disabled={ isEdit }
                                onClick={ this.addRow.bind(this, 2) }>
                                <Icon type="plus-circle-o" /> 新增分区
                            </a>
                        </div>
                    </div>
                )
            }
        </div>
    }
}
