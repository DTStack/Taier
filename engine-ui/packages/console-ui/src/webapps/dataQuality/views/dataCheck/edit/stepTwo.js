import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, TreeSelect, Icon, message } from 'antd';
import { formItemLayout } from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {

        };
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

    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
        })
    }

    onTargetTableChange = (name) => {
        this.props.changeParams({
            target: { ...this.props.dataCheck.params.target, table: name }
        });
    }

    onSourcePreview = () => {
        let sourceId = this.props.form.getFieldValue('sourceId');
        let tableName = this.props.form.getFieldValue('table');

        if(!sourceId || !tableName) {
            message.error('未选择数据源或表名');
            return;
        }

        this.props.getDataSourcesPreview({
            sourceId: sourceId,
            tableName: tableName
        });
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

    handlePartChange = (value, label, extra) => {
        console.log(value,label,extra)
        
        this.props.changeParams({
            target: { ...this.props.dataCheck.params.target, 
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
                this.props.navToStep(2);
            }
        })
    }

    render() {
        const { sourceList, sourceTable, sourcePreview, sourcePart } = this.props.dataSource;
        const { getFieldDecorator } = this.props.form;
        const { origin, target } = this.props.dataCheck.params;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择右侧表">
                            {
                                getFieldDecorator('targetTable', {
                                    rules: [{ required: true, message: '请选择右侧表' }],
                                    initialValue: target.table
                                })(
                                    <Select onChange={this.onTargetTableChange}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            origin.partitionColumn
                            &&
                            <FormItem {...formItemLayout} label="选择分区" style={{ marginBottom: 5 }} extra={this.renderPartText()}>
                                {
                                    getFieldDecorator('targetPart', {
                                        rules: [{ required: true, message: '请选择分区' }],
                                        initialValue: this.getPartTitle(target.partitionColumn, target.partitionValue) 
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
                    <Button onClick={this.prev}>上一步</Button>
                    <Button className="m-l-8" type="primary" onClick={this.next}>下一步</Button>
                </div>
            </div>
        );
    }
}
StepTwo = Form.create()(StepTwo);