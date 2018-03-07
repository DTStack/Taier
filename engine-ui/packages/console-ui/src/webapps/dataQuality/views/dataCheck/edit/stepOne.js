import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { Link } from 'react-router';
import {  Row, Col, Table, Button, Form, Select, Input, TreeSelect, Icon, message } from 'antd';
import { dataSourceTypes, formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

export default class StepOne extends Component {
    constructor(props) {
        super(props);
        this.state = {
            havePart: false
        }
    }
    
    componentDidMount() {
        
    }

    initColumns = (data) => {
        return data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item
            }
        });
    }

    renderSourceType = (data) => {
        return data.map((source) => {
            return (
                <Option key={source.id} value={source.id.toString()}>{source.dataName}（{dataSourceTypes[source.type]}）</Option>
            )
        })
    }

    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
        })
    }

    renderTreeSelect = (data) => {
        return data.map((item) => {
            let partTitle = this.getPartTitle(item.partName, item.partValue);

            if (item.children.length) {
                return (
                    <TreeNode key={item.nodeId} title={partTitle} value={partTitle} dataRef={item}>
                        {this.renderTreeSelect(item.children)}
                    </TreeNode>
                )
            } else {
                return (
                    <TreeNode key={item.nodeId} title={partTitle} value={partTitle} dataRef={item} isLeaf={true} />
                )
            }
            
        });
    }

    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`
        } else {
            return name
        }
    }

    /**
     * 获取数据源类型
     * @param {string} type 
     */
    getSourceType = (id) => {
        const { sourceList } = this.props.dataSource;
        
        sourceList.forEach((item) => {
            if (item.id == id) {
                this.setState({ havePart: item.type === 7 || item.type === 10 });
            }
        });
    } 

    onSourceTypeChange = (id) => {
        // 获取类型
        this.getSourceType(id);
        // 获取所有表
        this.props.getDataSourcesTable({ sourceId: id });

        this.props.form.setFieldsValue({ sourceTable: '' });

        this.props.changeParams({
            origin: { ...this.props.dataCheck.params.origin, dataSourceId: id },
            target: { ...this.props.dataCheck.params.target, dataSourceId: id }
        });

        let tableName = this.props.form.getFieldValue('sourceTable');

        if(id && tableName && this.state.havePart) {
            this.props.getDataSourcesPart({
                sourceId: id,
                table: tableName
            });
        }
    }

    onOriginTableChange = (name) => {
        this.props.changeParams({
            origin: { ...this.props.dataCheck.params.origin, table: name }
        });

        let sourceId = this.props.form.getFieldValue('sourceId');

        if(sourceId && name && this.state.havePart) {
            this.props.getDataSourcesPart({
                sourceId: sourceId,
                table: name
            });
        }
    }

    // 获取预览数据
    onSourcePreview = () => {
        let sourceId = this.props.form.getFieldValue('sourceId');
        let tableName = this.props.form.getFieldValue('sourceTable');

        if(!sourceId || !tableName) {
            message.error('未选择数据源或表名');
            return;
        }

        this.props.getDataSourcesPreview({
            sourceId: sourceId,
            tableName: tableName
        });
    }

    handlePartChange = (value, label, extra) => {
        this.props.changeParams({
            origin: { ...this.props.dataCheck.params.origin, 
                partitionColumn: extra.triggerNode.props.dataRef.partName, 
                partitionValue: extra.triggerNode.props.dataRef.partValue  
            }
        });
    }

    renderPartText = () => {
        return (
            <div className="preview-text">
                <p>分区支持系统函数，例如:</p>
                <p dangerouslySetInnerHTML={{ __html: '${bdp.system.bizdate}，业务日期变量' }}></p>
                <p>————此处需补充其他变量</p>
                <p>如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
            </div>
        )
    }

    prev = () => {
        this.props.navToStep(0);
    }

    next = () => {
        this.props.form.validateFields({ force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                this.props.navToStep(1);
            }
        })
    }

    render() {
        const { sourceList, sourceTable, sourcePreview, sourcePart } = this.props.dataSource;
        const { origin } = this.props.dataCheck.params;
        const { getFieldDecorator } = this.props.form;
        const { havePart } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {
                                getFieldDecorator('sourceId', {
                                    rules: [{ required: true, message: '请选择数据源' }],
                                    initialValue: origin.dataSourceId
                                })(
                                    <Select onChange={this.onSourceTypeChange}>
                                        {
                                            this.renderSourceType(sourceList)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        <FormItem {...formItemLayout} label="选择左侧表">
                            {
                                getFieldDecorator('sourceTable', {
                                    rules: [{ required: true, message: '请选择左侧表' }],
                                    initialValue: origin.table
                                })(
                                    <Select onChange={this.onOriginTableChange}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            (havePart || origin.partitionColumn)
                            &&
                            <FormItem {...formItemLayout} label="选择分区" style={{ marginBottom: 5 }} extra={this.renderPartText()}>
                                {
                                    getFieldDecorator('originPartitionColumn', {
                                        rules: [{ required: true, message: '请选择分区' }],
                                        initialValue: this.getPartTitle(origin.partitionColumn, origin.partitionValue) 
                                    })(
                                        <TreeSelect
                                            allowClear
                                            showSearch
                                            placeholder="分区列表"
                                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                                            onChange={this.handlePartChange}>
                                            {
                                                this.renderTreeSelect(sourcePart)
                                            }
                                        </TreeSelect>
                                    )
                                }
                                
                            </FormItem>
                        }

                        <Row>
                            <Col span={12} offset={6}>
                                <Button onClick={this.onSourcePreview}>数据预览<Icon type="down" /></Button>
                            </Col>
                        </Row>
                        
                        {
                            !isEmpty(sourcePreview)
                            &&
                            <Row>
                                <Col span={12} offset={6}>
                                    <Table 
                                        rowKey="key"
                                        className="m-table preview-table"
                                        columns={this.initColumns(sourcePreview.columnList)} 
                                        dataSource={sourcePreview.dataList}
                                        pagination={false}
                                    />
                                </Col>
                            </Row>
                        }

                    </Form>
                </div>
                <div className="steps-action">
                    <Button>
                        <Link to="/dq/dataCheck">取消</Link>
                    </Button>
                    <Button className="m-l-8" type="primary" onClick={this.next}>
                        下一步
                    </Button>
                </div>
            </div>
        )
    }
}
StepOne = Form.create()(StepOne);