import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, TreeSelect, Icon, message } from 'antd';
import { formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            sourcePart: [],
            sourcePreview: {}
        };
    }

    componentDidMount() {}

    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
        })
    }

    onTargetTableChange = (name) => {
        const { editParams, form, changeParams } = this.props;
        let target = { ...editParams.target, table: name };

        // 重置分区表单和参数
        if (editParams.origin.partitionColumn) {
            form.setFieldsValue({ targetColumn: '' });
            target.partitionColumn = undefined;
            target.partitionValue  = undefined;
            
            DSApi.getDataSourcesPart({
                sourceId: editParams.target.dataSourceId,
                table: name
            }).then((res) => {
                if (res.code === 1) {
                    this.setState({ sourcePart: res.data.children });
                }
            });
        }

        changeParams({
            target: { ...editParams.target, ...target }
        });

    }

    // 预览数据源的数据
    onSourcePreview = () => {
        const { form, editParams } = this.props;
        let tableName = form.getFieldValue('table');

        if(!tableName) {
            message.error('未选择右侧表');
            return;
        }

        DSApi.getDataSourcesPreview({
            sourceId: editParams.target.dataSourceId,
            tableName: tableName
        }).then((res) => {
            if (res.code === 1) {
                let { columnList, dataList } = res.data;
                
                res.data.dataList = dataList.map((arr, i) => {
                    let o = {};
                    arr.forEach((item, j) => {
                        o.key = i;
                        o[columnList[j]] = item;
                    })
                    return o;
                });

                this.setState({ sourcePreview: res.data });
            }
        });
    }

    // 分区树形选择
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

    // 分区title显示
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`
        } else {
            return name
        }
    }

    // 分区回调
    handlePartChange = (value, label, extra) => {
        const { target } = this.props.editParams;
        
        this.props.changeParams({
            target: { ...target, 
                partitionColumn: value ? extra.triggerNode.props.dataRef.partName : undefined, 
                partitionValue: value ? extra.triggerNode.props.dataRef.partValue : undefined
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
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    next = () => {
        const { currentStep, navToStep, form } = this.props;
        form.validateFields({ force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                navToStep(currentStep + 1);
            }
        })
    }

    initColumns = (data) => {
        return data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80
            }
        });
    }

    render() {
        const { sourceTable } = this.props.dataSource;
        const { getFieldDecorator } = this.props.form;
        const { origin, target } = this.props.editParams;
        const { sourcePreview, sourcePart } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择右侧表">
                            {
                                getFieldDecorator('table', {
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
                                    getFieldDecorator('targetColumn', {
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
                            <Col span={14} offset={6}>
                                <Button onClick={this.onSourcePreview}>数据预览<Icon type="down" /></Button>
                            </Col>
                        </Row>
                        
                        {
                            !isEmpty(sourcePreview)
                            &&
                            <Row>
                                <Col span={14} offset={6}>
                                    <Table 
                                        rowKey="key"
                                        className="m-table preview-table"
                                        columns={this.initColumns(sourcePreview.columnList)} 
                                        dataSource={sourcePreview.dataList}
                                        pagination={false}
                                        scroll={{ x: '200%', y: 400 }}
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