import * as React from 'react';
import { Card, Table, Form, Icon, Input, Select, Checkbox, Tooltip, InputNumber } from 'antd';
// eslint-disable-next-line
import classnames from 'classnames';
import { API_MODE } from '../../../../consts'
import { generateFormItemKey } from './helper';

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;

class ColumnsConfig extends React.Component<any, any> {
    renderEdit (dataIndex: any, id: any, type: any, initialValue: any, disabled?: boolean) {
        const { charOption, form } = this.props;
        const { getFieldDecorator } = form;
        const key = generateFormItemKey(dataIndex, id, type);
        switch (dataIndex) {
            case 'paramsName': {
                return (<FormItem
                    style={{ marginBottom: '0px' }}
                >
                    {getFieldDecorator(key, {
                        initialValue: initialValue,
                        rules: [{
                            required: true, message: '请输入参数名称'
                        }]
                    })(
                        <Input />
                    )}
                </FormItem>);
            }
            case 'type': {
                return (<FormItem
                    style={{ marginBottom: '0px' }}
                >
                    {getFieldDecorator(key, {
                        initialValue: initialValue
                    })(
                        <Select style={{ width: '100%' }}>
                            {
                                charOption.map((item: any) => <Option key={item} value={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </FormItem>);
            }
            case 'operator': {
                return (<FormItem
                    style={{ marginBottom: '0px' }}
                >
                    {getFieldDecorator(key, {
                        initialValue: initialValue
                    })(
                        <Select style={{ width: '100%' }}>
                            <Option value="=">=</Option>
                            <Option value=">">&gt;</Option>
                            <Option value=">=">&gt;=</Option>
                            <Option value="<"> &lt;</Option>
                            <Option value="<=">&lt;=</Option>
                            <Option value="!=">!=</Option>
                            <Option value="in">in</Option>
                            <Option value="not in">not in</Option>
                            <Option value="like">like</Option>
                            <Option value="not like">not like</Option>
                        </Select>
                    )}
                </FormItem>);
            }
            case 'required': {
                return (<FormItem
                    style={{ marginBottom: '0px' }}
                >
                    {getFieldDecorator(key, {
                        initialValue: initialValue,
                        valuePropName: 'checked'
                    })(
                        <Checkbox disabled={disabled}></Checkbox>
                    )}
                </FormItem>);
            }
            case 'desc': {
                return (<FormItem
                    style={{ marginBottom: '0px' }}
                >
                    {getFieldDecorator(key, {
                        initialValue: initialValue
                    })(
                        <TextArea placeholder="参数描述" autosize={{ minRows: 2, maxRows: 4 }} />
                    )}
                </FormItem>);
            }
        }
    }
    initColumns (type: any) {
        const { mode } = this.props;
        const isGuideMode = mode == API_MODE.GUIDE; // 模板向导模式
        if (type == 'in') {
            return [
                {
                    title: '参数名称',
                    dataIndex: 'paramsName',
                    width: '150px',
                    render: (text: any, record: any) => {
                        return isGuideMode ? this.renderEdit('paramsName', record.id, type, text) : text;
                    }
                },
                {
                    title: '绑定字段',
                    dataIndex: 'columnName',
                    width: '130px'
                },
                {
                    title: '字段类型',
                    dataIndex: 'type',
                    width: '120px',
                    render: (text: any, record: any) => {
                        return (isGuideMode || text != 'OBJECT') ? text : this.renderEdit('type', record.id, type, 'VARCHAR');
                    }
                },
                {
                    title: '操作符',
                    dataIndex: 'operator',
                    width: '85px',
                    render: (text: any, record: any) => {
                        return isGuideMode ? this.renderEdit('operator', record.id, type, text) : text;
                    }
                },
                {
                    title: '必填',
                    dataIndex: 'required',
                    render: (text: any, record: any) => {
                        let disabled = false;
                        if (!isGuideMode) {
                            const { groupId } = record;
                            disabled = groupId == -1
                        }
                        return this.renderEdit('required', record.id, type, text, disabled)
                    },
                    width: '60px'
                },
                {
                    title: '说明',
                    dataIndex: 'desc',
                    render: (text: any, record: any) => {
                        return this.renderEdit('desc', record.id, type, text)
                    }
                }
            ]
        } else if (type == 'out') {
            return [
                {
                    title: '参数名称',
                    dataIndex: 'paramsName',
                    width: '150px',
                    render: (text: any, record: any) => {
                        return isGuideMode ? this.renderEdit('paramsName', record.id, type, text) : text;
                    }
                },
                {
                    title: '绑定字段',
                    dataIndex: 'columnName',
                    width: '130px'
                },
                {
                    title: '字段类型',
                    dataIndex: 'type',
                    width: '120px'
                },
                {
                    title: '说明',
                    dataIndex: 'desc',
                    render: (text: any, record: any) => {
                        return this.renderEdit('desc', record.id, type, text)
                    }
                }
            ]
        }
    }
    apiParamsConfig = () => {
        const {
            mode,
            selectedRows,
            addColumns,
            removeColumns,
            filterSelectRow,
            InputSelectedRows
        } = this.props;

        const inputAdd = classnames('params_exchange_button', {
            'params_exchange_button_disable': !selectedRows || selectedRows.length == 0
        })
        const inputRemove = classnames('params_exchange_button', {
            'params_exchange_button_disable': !InputSelectedRows || InputSelectedRows.length == 0
        })
        return <div className="middle-title middle-header">
            输入参数：
            {mode == API_MODE.GUIDE && (
                <div className="params_exchange_box">
                    <Icon
                        type="right-square-o"
                        className={inputAdd}
                        onClick={addColumns.bind(null, 'in')}
                    />
                    <Icon
                        type="left-square-o"
                        className={inputRemove}
                        onClick={() => {
                            filterSelectRow(InputSelectedRows, 'in');
                            removeColumns(InputSelectedRows, 'in')
                        }}
                    />
                </div>
            )}
        </div>
    }

    outputParams = () => {
        const {
            addColumns,
            removeColumns,
            selectedRows,
            resultPageChecked,
            resultPage,
            resultPageCheckedChange,
            resultPageChange,
            mode,
            filterSelectRow,
            OutSelectedRows,
            maxPageSize
        } = this.props;
        const outAdd = classnames('params_exchange_button', {
            'params_exchange_button_disable': !selectedRows || selectedRows.length == 0
        })
        const outRemove = classnames('params_exchange_button', {
            'params_exchange_button_disable': !OutSelectedRows || OutSelectedRows.length == 0
        })
        return <div className="required-tip middle-title middle-header">
            输出参数：
            <span className="params_result_check">
                <Checkbox checked={resultPageChecked} onChange={resultPageCheckedChange} >返回结果分页</Checkbox>
                <Tooltip title={`当查询结果大于${maxPageSize}条时，请选择分页查询，每页最大返回${maxPageSize}条结果。若没有选择，默认分页查询。`}>
                    <Icon type="question-circle-o" />
                </Tooltip>
                {resultPageChecked ? <InputNumber placeholder="请输入分页大小" style={{ marginLeft: '8px', width: '115px' }} min={1} max={maxPageSize} value={resultPage} onChange={resultPageChange} /> : null}
            </span>
            {mode == API_MODE.GUIDE && (
                <div className="params_exchange_box">
                    <Icon
                        type="right-square-o"
                        className={outAdd}
                        onClick={addColumns.bind(null, 'out')}
                    />
                    <Icon
                        type="left-square-o"
                        className={outRemove}
                        onClick={() => {
                            filterSelectRow(OutSelectedRows, 'out');
                            removeColumns(OutSelectedRows, 'out')
                        }} />
                </div>
            )}
        </div>
    }

    render () {
        const {
            InputColumns,
            OutputColums,
            rowSelection
        } = this.props;
        const inputTableColumns = this.initColumns('in');
        const outputTableColumns = this.initColumns('out');
        return (
            <Form>
                <Card title={this.apiParamsConfig()} style={{ marginTop: 10 }}>
                    <Table
                        rowKey="id"
                        className="m-table m-table--border m-table-showselect"
                        style={{ background: '#fff' }}
                        columns={inputTableColumns}
                        dataSource={InputColumns}
                        pagination={false}
                        rowSelection={rowSelection('in')}
                        scroll={{ y: 175 }}
                    />
                </Card>
                <Card title={this.outputParams()} style={{ marginTop: 20 }}>
                    <Table
                        rowKey="id"
                        className="m-table m-table--border m-table-showselect"
                        style={{ background: '#fff' }}
                        columns={outputTableColumns}
                        dataSource={OutputColums}
                        pagination={false}
                        rowSelection={rowSelection('out')}
                        scroll={{ y: 175 }}
                    />
                </Card>
            </Form>
        )
    }
}
const formConfig: any = {
    onValuesChange (props: any, value: any) {
        props.updateValue(value);
    }
}
const WrapColumnsConfigForm = Form.create(formConfig)(ColumnsConfig);

export default WrapColumnsConfigForm;
