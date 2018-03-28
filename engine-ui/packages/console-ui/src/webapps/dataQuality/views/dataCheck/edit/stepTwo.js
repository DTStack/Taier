import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, TreeSelect, Icon, message } from 'antd';

import { dataCheckActions } from '../../../actions/dataCheck';
import { formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

const mapStateToProps = state => {
    const { dataCheck, dataSource } = state;
    return { dataCheck, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getSourcePart(params, type) {
        dispatch(dataCheckActions.getSourcePart(params, type));
    },
    resetSourcePart(params) {
        dispatch(dataCheckActions.resetSourcePart(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            sourcePreview: {},
            useInput: false
        };
    }

    componentDidMount() {}

    renderTargetTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
        })
    }

    onTargetTableChange = (name) => {
        const { editParams, form, changeParams } = this.props;
        let target = { ...editParams.target, table: name };

        this.setState({ sourcePreview: {} });

        // 重置分区表单和参数
        if (editParams.origin.partition) {
            this.props.resetSourcePart('target');
            form.setFieldsValue({ targetColumn: '' });
            target.partition = undefined;

            this.props.getSourcePart({
                sourceId: target.dataSourceId,
                table: name
            }, 'target');
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
            message.error('未选择数据表');
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
        if (!isEmpty(data)) {
            return data.children.map((item) => {
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
    }

    // 分区title显示
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`
        } else {
            return name
        }
    }

    // 分区变化回调
    handlePartChange = (value, label, extra) => {
        const { target } = this.props.editParams;
        let partition = value ? extra.triggerNode.props.dataRef.partColumn : undefined;

        this.props.changeParams({
            target: {...target,  partition}
        });
    }

    // 分区变化回调
    handleInputPartChange = (e) => {
        const { target } = this.props.editParams;
        let partition = e.target.value ? e.target.value : undefined;

        this.props.changeParams({
            target: {...target,  partition}
        });
    }

    renderPartText = () => {
        return (
            <p className="font-14">如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
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

    previewTableColumns = (data) => {
        return data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80
            }
        });
    }

    renderColumnPart = () => {
        const { editStatus, editParams, form, dataCheck } = this.props;
        const { useInput } = this.state;
        const { partition } = editParams.target;
        const { targetPart } = dataCheck;
        const { getFieldDecorator } = form;

        if (!useInput) {
            return <FormItem {...formItemLayout} label="选择分区" extra={this.renderPartText()}>
                {
                    getFieldDecorator('targetColumn', {
                        rules: [],
                        initialValue: partition
                    })(
                        <TreeSelect
                            allowClear
                            showSearch
                            placeholder="分区列表"
                            style={{ width: '85%', marginRight: 15 }} 
                            disabled={editStatus === 'edit'}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            onChange={this.handlePartChange}>
                            {
                                this.renderTreeSelect(targetPart)
                            }
                        </TreeSelect>
                    )
                }
                {
                    editStatus !== 'edit'
                    &&
                    <a onClick={this.onPartitionTypeChange}>手动输入</a>
                }
            </FormItem>
        } else {
            return <FormItem {...formItemLayout} label="选择分区" extra={this.renderPartText()}>
                {
                    getFieldDecorator('targetColumnInput', {
                        rules: [],
                        initialValue: ''
                    })(
                        <Input 
                            style={{ width: '85%', marginRight: 15 }} 
                            placeholder="手动输入分区的格式为：分区字段=分区值，具体的参数配置在帮助文档里说明" 
                            onChange={this.handleInputPartChange}
                            disabled={editStatus === 'edit'} />
                    )
                }
                {
                    editStatus !== 'edit'
                    &&
                    <a onClick={this.onPartitionTypeChange}>选择已有分区</a>
                }
            </FormItem>
        }
    }

    onPartitionTypeChange = () => {
        const { target } = this.props.editParams;
        const { useInput } = this.state;
        // form.setFieldsValue({ part: '' });
        // params.partition = undefined;
        this.props.changeParams({
            target: {...target,  partition: undefined}
        });
        this.setState({ useInput: !useInput });
    }

    render() {
        const { editStatus, editParams, dataSource, form, havePart } = this.props;
        const { sourceTable } = dataSource;
        const { getFieldDecorator } = form;
        const { origin, target } = editParams;
        const { sourcePreview } = this.state;

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
                                    <Select 
                                        showSearch
                                        style={{ width: '85%' }} 
                                        onChange={this.onTargetTableChange} 
                                        disabled={editStatus === 'edit'}>
                                        {
                                            this.renderTargetTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            (havePart || target.partition)
                            &&
                            this.renderColumnPart()
                        }
                        
                        <Row type="flex" justify="center" className="font-14">
                            <a onClick={this.onSourcePreview}>数据预览<Icon type="down" style={{ marginLeft: 5 }} /></a>
                        </Row>
                        
                        {
                            !isEmpty(sourcePreview)
                            &&
                            <Table 
                                rowKey="key"
                                className="m-table preview-table"
                                columns={this.previewTableColumns(sourcePreview.columnList)} 
                                dataSource={sourcePreview.dataList}
                                pagination={false}
                                scroll={{ x: '120%', y: 400 }}
                            />
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