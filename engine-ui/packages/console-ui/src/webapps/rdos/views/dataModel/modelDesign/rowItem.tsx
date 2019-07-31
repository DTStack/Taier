import * as React from 'react';
import assign from 'object-assign';
import { isEqual, range, isObject } from 'lodash';

import {
    message, Input,
    Row, Col, Icon, Select,
    Tooltip, InputNumber
} from 'antd';

const Option = Select.Option;

/**
 * @description 字段/分区 一行
 * @class RowItem
 * @extends {React.Component}
 */
export default class RowItem extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            editMode: false
        };
    }

    /**
     * @description
     * @param {any} selectName evt为value类型时用于确定表单name，可选
     * @param {any} evt value/event
     * @memberof RowItem
     */
    handleChange (selectName: any, evt: any) {
        const { data, replaceRow } = this.props;
        let iptName, value;

        if (isObject(evt)) {
            iptName = evt.target.name;
            value = evt.target.value;
        } else {
            iptName = selectName;
            value = evt;
        }

        const newData = assign({}, data, { [iptName]: value });
        const TYPE = newData.columnType.toUpperCase();

        if (TYPE === 'DECIMAL' || TYPE === 'NUMERIC') {
            if (!newData.precision) newData.precision = 10;
            if (!newData.scale) newData.scale = 0;
        }
        if (TYPE === 'CHAR' || TYPE === 'CHARACTER' || TYPE === 'CHARACTER VARYING') {
            if (!newData.charLen) newData.charLen = 10;
        }
        if (TYPE === 'VARCHAR') {
            if (!newData.varcharLen) newData.varcharLen = 10;
        }

        if (this.checkParams(newData)) {
            replaceRow(newData);
        }
    }

    checkParams (params: any) {
        const reg = /^[A-Za-z0-9_]+$/;
        if (params.name) {
            if (!reg.test(params.name)) {
                message.error('字段名称只能由字母、数字、下划线组成！')
                return false;
            }
            if (params.name.length > 20) {
                message.error('字段名称不可超过20个字符！')
                return false;
            }
        }
        if (params.comment && params.comment.length > 100) {
            message.error('字段备注不可超过100个字符！')
            return false;
        }
        return true;
    }

    shouldComponentUpdate (nextProps: any, nextState: any) {
        return !isEqual(this.props, nextProps);
    }

    render () {
        const { data, columnFileds, isHiveTable } = this.props;
        console.log('rowItem', this.props);
        console.log('rowItem', data);

        const options = columnFileds && columnFileds.map((field: any) => <Option title={field.columnName} value={field.columnName} key={field.columnName}>{field.columnName}</Option>)
        const { isSaved, isPartition, columnType } = data;
        const needExtra: any = ['DECIMAL', 'VARCHAR', 'CHAR', 'CHARACTER', 'CHARACTER VARYING', 'NUMERIC'].indexOf(columnType.toUpperCase()) !== -1;
        const TYPES = isHiveTable
            ? (isPartition ? ['STRING', 'BIGINT'] : ['TINYINT', 'SMALLINT', 'INT', 'BIGINT', 'BOOLEAN',
                'FLOAT', 'DOUBLE', 'STRING', 'BINARY', 'TIMESTAMP',
                'DECIMAL', 'DATE', 'VARCHAR', 'CHAR'
            ])
            : ['SMALLINT', 'INTEGER', 'BIGINT', 'BOOLEAN', 'CHARACTER', 'CHARACTER VARYING',
                'TEXT', 'NUMERIC', 'REAL', 'DOUBLE PRECISION', 'TIMESTAMP', 'DATE'];

        return <Row className="row">
            <Col span={4} className="cell">
                <Select
                    mode="combobox"
                    value={data.columnName}
                    placeholder={this.props.placeholder}
                    notFoundContent=""
                    name="columnName"
                    showArrow={true}
                    style={{ width: '100%' }}
                    defaultActiveFirstOption={false}
                    disabled={ isSaved }
                    filterOption={false}
                    onChange={ this.handleChange.bind(this, 'columnName') }
                >
                    {options}
                </Select>
            </Col>
            <Col span={8} className="cell">
                <Select name="columnType" defaultValue={ data.columnType }
                    onChange={ this.handleChange.bind(this, 'columnType') }
                    style={{ width: needExtra ? '40%' : '80%' }}
                    disabled={ isSaved }
                >
                    {TYPES.map((str: any) => <Option key={str} value={str}>{str}</Option>)}
                </Select>
                { needExtra && this.renderExtra(data.columnType) }
            </Col>
            <Col span={7} className="cell">
                <Input
                    name="comment"
                    defaultValue={ data.comment }
                    autoComplete="off"
                    onChange={ this.handleChange.bind(this, undefined) }
                    disabled={ isSaved && isPartition }
                />
            </Col>
            <Col span={5} className="cell" style={{ paddingTop: 13 }}>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.moveRow(data.uuid, true) }
                >上移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.moveRow(data.uuid, false) }
                >下移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.delRow(data.uuid) }
                >删除</a>
            </Col>
        </Row>
    }

    renderExtra (columnType: any) {
        const { data } = this.props;
        const { precision, scale, charLen, varcharLen, isSaved } = data;
        let result = '';

        columnType = columnType.toUpperCase();
        switch (columnType) {
            case 'DECIMAL':
            case 'NUMERIC':
                result = <span className="extra-ipt">
                    <Select name="precision"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={ `${precision}` || '10'}
                        onChange={ this.handleChange.bind(this, 'precision') }
                        placeholder="precision"
                        disabled={ isSaved }
                    >
                        {range(39).slice(1).map((n: any) => <Option value={`${n}`}
                            key={n}
                        >{n}</Option>)}
                    </Select>
                    <Select name="scale"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={ `${scale}` || '0'}
                        onChange={ this.handleChange.bind(this, 'scale') }
                        placeholder="scale"
                        disabled={ isSaved }
                    >
                        {range(precision || 10).map((n1: any) => <Option value={`${n1}`}
                            key={n1}
                        >{n1}</Option>)}
                    </Select>
                    <Tooltip title="type(precision,scale)；precision:数字总长度，最大为38；scale：小数点之后的位数">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
                    </Tooltip>
                </span>
                break;
            case 'CHAR':
            case 'CHARACTER':
            case 'CHARACTER VARYING':
                result = <span className="extra-ipt">
                    <InputNumber name="charLen" defaultValue={ charLen || 10 }
                        min={1}
                        max={255}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={ this.handleChange.bind(this, 'charLen') }
                        disabled={ isSaved }
                    />
                    <Tooltip title="type(char)；char的长度为1~255">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
                    </Tooltip>
                </span>
                break;
            case 'VARCHAR':
                result = <span className="extra-ipt">
                    <InputNumber name="varcharLen" defaultValue={ varcharLen || 10 }
                        min={1}
                        max={65535}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={ this.handleChange.bind(this, 'varcharLen') }
                        disabled={ isSaved }
                    />
                    <Tooltip title="type(varchar)；varchar的长度为1~65535">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
                    </Tooltip>
                </span>
                break;

            default: break;
        }

        return result;
    }
}
