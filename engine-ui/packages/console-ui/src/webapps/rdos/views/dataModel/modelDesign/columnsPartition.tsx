import * as React from 'react';
import { Row, Col, Icon } from 'antd';

import RowItem from './rowItem';
import { TABLE_TYPE } from '../../../comm/const'
/**
 * @description step2:字段与分区
 * @export
 * @class ColumnsPartition
 * @extends {React.Component}
 */
export default class ColumnsPartition extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    addRow (type: any) {
        const isHiveTable = this.props.tableType == TABLE_TYPE.HIVE;
        this.props.addRow({
            columnName: '',
            columnType: isHiveTable ? 'STRING' : 'INTEGER',
            columnDesc: '',
            uuid: Date.now()
        }, type);
    }

    delRow (type: any, uuid: any) {
        this.props.delRow(uuid, type);
    }

    replaceRow (type: any, newCol: any) {
        this.props.replaceRow(newCol, type);
    }

    moveRow (type: any, uuid: any, isUp: any) {
        this.props.moveRow(uuid, type, isUp);
    }

    render () {
        const { columns, partition_keys, isEdit, columnFileds, tableType } = this.props;// eslint-disable-line
        const isHiveTable = tableType == TABLE_TYPE.HIVE;
        const isDisable = {
            disabled: isEdit
        }
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
                    { columns.map((col: any, i: any) => <RowItem
                        data={ col }
                        key={ col.uuid || i }
                        columnFileds={columnFileds}
                        delRow={ this.delRow.bind(this, 1) }
                        replaceRow={ this.replaceRow.bind(this, 1) }
                        moveRow={ this.moveRow.bind(this, 1) }
                        isHiveTable={isHiveTable}
                    />)}
                </div>
                <div className="fn">
                    <a href="javascript:void(0)" {...isDisable} onClick={ this.addRow.bind(this, 1) }>
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
                            { partition_keys.map((partition: any, i: any) => <RowItem
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
                                {...isDisable}
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
