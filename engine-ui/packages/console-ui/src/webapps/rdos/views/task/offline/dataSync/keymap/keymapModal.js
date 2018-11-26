import React from 'react';
import { 
    Modal, Form, Select, Input, InputNumber,
} from 'antd';

import { 
    formItemLayout, 
    DATA_SOURCE, 
} from '../../../../../comm/const';
import HelpDoc from '../../../../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;

// 添加字段表单.
class KeyForm extends React.Component{

    shouldComponentUpdate(nextProps) {
        if (this.props !== nextProps) {
            return true
        }
        return false
    }

    columnFamily = data => {
        return data && data.map(item => <Option value={item}>{item}</Option>)
    }

    renderFormItems = () => {

        const { 
            keyModal, dataType, 
            sourceColumnFamily, targetColumnFamily 
        } = this.props;

        const { isReader, fileType, editField } = keyModal;
        const { getFieldDecorator } = this.props.form;

        if(isReader) {// 数据源
            switch(dataType) {
                case DATA_SOURCE.FTP:
                case DATA_SOURCE.HDFS: {
                    return [
                        <FormItem
                            {...formItemLayout}
                            label="索引值"
                            key="key"
                        >
                        {getFieldDecorator('key', {
                            rules: [{
                                required: true,
                                type: 'integer',
                                message: '请按要求填写索引值！',
                            }],
                            initialValue: (editField && (editField.key || editField.index)) || ''
                        })(
                            <InputNumber placeholder="请输入索引值" style={{ width: '100%' }} min={0} />
                        )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="类型"
                            key="type"
                        >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.type) || 'STRING'
                        })(
                            <Select placeholder="请选择类型">
                                <Option value="STRING">STRING</Option>
                                <Option value="LONG">LONG</Option>
                                <Option value="BOOLEAN">BOOLEAN</Option>
                                <Option value="DOUBLE">DOUBLE</Option>
                                <Option value="DATE">DATE</Option>
                            </Select>
                        )}
                        </FormItem>
                    ]
                }
                case DATA_SOURCE.HBASE: {
                    const disabledEdit = editField && editField.key === 'rowkey'
                    return [
                        <FormItem
                            {...formItemLayout}
                            label="列名"
                            key="key"
                        >
                        {getFieldDecorator('key', {
                            rules: [{
                                required: true,
                                type: 'string'
                            }],
                            initialValue: (editField && editField.key) || ''
                        })(
                                <Input placeholder="请输入列名" style={{ width: '100%' }} disabled={disabledEdit}/>
                        )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="列族"
                            key="cf"
                        >
                            {getFieldDecorator('cf', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: (editField && editField.cf) || undefined,
                            })(
                                <Select placeholder="请选择列族" disabled={disabledEdit}>
                                    {this.columnFamily(sourceColumnFamily)}
                                </Select>
                            )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="类型"    
                            key="type"
                        >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.type) || 'STRING'
                        })(
                            <Select placeholder="请选择类型">
                                <Option value="STRING">STRING</Option>
                                <Option value="BOOLEAN">BOOLEAN</Option>
                                <Option value="SHORT">SHORT</Option>
                                <Option value="INT">INT</Option>
                                <Option value="LONG">LONG</Option>
                                <Option value="FLOAT">FLOAT</Option>
                                <Option value="DOUBLE">DOUBLE</Option>
                            </Select>
                        )}
                        </FormItem>
                    ];
                }
                default: {
                    return [
                        <FormItem
                            {...formItemLayout}
                            label="字段名"
                            key="key"
                        >
                        {getFieldDecorator('key', {
                            rules: [{
                                required: true,
                                message: '请按要求填写字段名！',
                            }],
                            initialValue: (editField && editField.key) || ''
                        })(
                            <Input disabled={true} placeholder="请输入字段名" style={{ width: '100%' }} />
                        )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="类型"
                            key="type"
                        >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.type) || 'STRING'
                        })(
                            <Input disabled={true} />
                        )}
                        </FormItem>,
                    ]
                }
            }
        }
        else {// 目标表
            switch(dataType) {
                case DATA_SOURCE.FTP:
                case DATA_SOURCE.HDFS: {
                    return [
                        <FormItem
                            {...formItemLayout}
                            label="字段名"
                            key="keyName"
                        >
                        {getFieldDecorator('key', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.key) || ''
                        })(
                            <Input placeholder="请输入字段名"/>
                        )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="选择类型"
                            key="type"
                        >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.type) || 'STRING'
                        })(
                            <Select placeholder="请选择类型">
                                <Option value="STRING">STRING</Option>
                                <Option value="BIGINT">BIGINT</Option>
                                <Option value="TIMESTAMP">TIMESTAMP</Option>
                                <Option value="VARCHAR">VARCHAR</Option>
                                <Option value="CHAR">CHAR</Option>
                                <Option value="TINYINT">TINYINT</Option>
                                <Option value="SMALLINT">SMALLINT</Option>
                                <Option value="DECIMAL">DECIMAL</Option>
                                <Option value="INT">INT</Option>
                                <Option value="FLOAT">FLOAT</Option>
                                <Option value="DOUBLE">DOUBLE</Option>
                                <Option value="DATE">DATE</Option>
                            </Select>
                        )}
                        </FormItem>,
                    ];
                }
                case DATA_SOURCE.HBASE: {
                    return [
                        <FormItem
                            {...formItemLayout}
                            label="列名"
                            key="key"
                        >
                        {getFieldDecorator('key', {
                            rules: [{
                                required: true,
                                type: 'string'
                            }],
                            initialValue: (editField && editField.key) || undefined,
                        })(
                            <Input placeholder="请输入列名" style={{ width: '100%' }}/>
                        )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="列族"
                            key="cf"
                        >
                            {getFieldDecorator('cf', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: (editField && editField.cf) || undefined,
                            })(
                                <Select placeholder="请选择列族">
                                    {this.columnFamily(targetColumnFamily)}
                                </Select>
                            )}
                        </FormItem>,
                        <FormItem
                            {...formItemLayout}
                            label="选择类型"
                            key="type"
                        >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true
                            }],
                            initialValue: (editField && editField.type) || 'STRING'
                        })(
                            <Select placeholder="请选择类型">
                                <Option value="STRING">STRING</Option>
                                <Option value="BOOLEAN">BOOLEAN</Option>
                                <Option value="SHORT">SHORT</Option>
                                <Option value="INT">INT</Option>
                                <Option value="LONG">LONG</Option>
                                <Option value="FLOAT">FLOAT</Option>
                                <Option value="DOUBLE">DOUBLE</Option>
                            </Select>
                        )}
                        </FormItem>
                    ]
                }
                default: break;
            }
        }
        return [];
    }

    render() {
        const { 
            keyModal,
        } = this.props;
        const { getFieldDecorator } = this.props.form;

        const { editField, isReader } = keyModal;
        // 如果源数据类型为字符串，则支持字符串格式化
        const canFormat = editField && editField.type && 
        (editField.type.toUpperCase() === 'STRING' || editField.type.toUpperCase() === 'VARCHAR');
        return <Form>
            { this.renderFormItems() }
            {
                canFormat && isReader && 
                <FormItem
                    {...formItemLayout}
                    label="格式化"
                    key="format"
                >
                {getFieldDecorator('format', {
                    rules: [],
                    initialValue: (editField && editField.format) || undefined,
                })(
                    <Input placeholder="格式化, 例如：YYYY-DD-MM" />
                )}
                <HelpDoc doc="stringColumnFormat"/>
                </FormItem>
            }
        </Form>
    }
}

const KeyFormWrapper = Form.create()(KeyForm);

class KeyMapModal extends React.Component {

    submit = () => {
        const { onOk } = this.props
        this.Form.validateFields((err, values) => {
            if (!err) {
                setTimeout(() => {this.Form.resetFields()}, 200)
                onOk(values)
            } else (
                onOk(null, err)
            )
        })
    }

    cancel = () => {
        const { onCancel } = this.props
        onCancel()
        this.Form.resetFields();
    }

    render() {
        const { 
            title, visible, keyModal, 
            dataType, sourceColumnFamily,
            targetColumnFamily
        } = this.props
        return (
            <Modal
                title={ title }
                visible={ visible }
                onOk={ this.submit }
                onCancel={ this.cancel }
            >
                <KeyFormWrapper 
                    sourceColumnFamily={sourceColumnFamily}
                    targetColumnFamily={targetColumnFamily}
                    dataType={dataType}
                    keyModal={keyModal}
                    ref={el => this.Form = el}
                />
            </Modal>
        )
    }
}

export default KeyMapModal;