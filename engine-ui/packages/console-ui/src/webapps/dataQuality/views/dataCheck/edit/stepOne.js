import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Row, Table, Button, Form, Select, Input, TreeSelect, Icon, message } from 'antd';

import TableCell from 'widgets/tableCell';
import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
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
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    getSourcePart(params, type) {
        dispatch(dataCheckActions.getSourcePart(params, type));
    },
    resetSourcePart(params) {
        dispatch(dataCheckActions.resetSourcePart(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepOne extends Component {
    constructor(props) {
        super(props);
        this.state = {
            showPreview: false,
            sourcePreview: {},
        }
    }
    
    componentDidMount() {
    }

    componentWillReceiveProps(nextProps) {
        let oldParams = this.props.editParams,
            newParams = nextProps.editParams;

        // if (oldParams.origin.partition != newParams.origin.partition && newParams.origin.partition) {
        //     this.props.getDataSourcesPart({
        //         sourceId: newParams.origin.dataSourceId,
        //         table: newParams.origin.table
        //     });

        //     this.setState({ havePart: true });
        // }
    }

    // 数据源下拉框
    renderSourceType = (data) => {
        return data.map((source) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option 
                    key={source.id} 
                    value={source.id.toString()}
                    title={title}>
                    {title}
                </Option>
            )
        });
    }

    // 左侧表下拉框
    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
        });
    }

    // 分区下拉框
    renderTreeSelect = (data) => {
        if (!isEmpty(data)) {
            return data.children.map((item) => {
                let partTitle = this.getPartTitle(item.partName, item.partValue);

                if (item.children.length) {
                    return (
                        <TreeNode 
                            key={item.nodeId} 
                            title={partTitle} 
                            value={item.partColumn} 
                            dataRef={item}>
                            {this.renderTreeSelect(item)}
                        </TreeNode>
                    )
                } else {
                    return (
                        <TreeNode 
                            key={item.nodeId} 
                            title={partTitle} 
                            value={item.partColumn} 
                            dataRef={item} 
                            isLeaf={true}
                        />
                    )
                }
            });
        }
    }

    /**
     * 分区显示的title
     * @param {String} name 
     * @param {String} value 
     */
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}   分区值：${value}`;
        } else {
            return name;
        }
    }

    /**
     * 是否是Hive或MaxCompute
     * @param {String} id 
     */
    havePartition = (id) => {
        const { sourceList } = this.props.dataSource;
        
        sourceList.forEach((item) => {
            if (item.id == id) {
                this.props.changeHavePart(item.type === 7 || item.type === 10);
                // this.setState({ havePart: item.type === 7 || item.type === 10 });
            }
        });
    } 

    /**
     * 数据源变化回调
     * @param {String} id 数据源id
     */
    onSourceTypeChange = (id) => {
        const { 
            form, 
            editParams, 
            changeParams,
            resetSourcePart,
            getDataSourcesTable } = this.props;
        let origin = { ...editParams.origin, dataSourceId: id };

        this.havePartition(id);
        form.setFieldsValue({ sourceTable: '' });
        getDataSourcesTable({ sourceId: id });

        // 重置分区表单和参数
        if (origin.partition) {
            resetSourcePart('origin');
            form.setFieldsValue({ 
                originColumn: ''
            });
            origin.partition = undefined;
        }

        this.setState({ 
            showPreview: false,
            sourcePreview: {} 
        });

        changeParams({
            origin: { ...editParams.origin, ...origin },
            target: { ...editParams.target, dataSourceId: id, table: '' }
        });
    }

    /**
     * 左侧表变化回调
     * @param {String} name 
     */
    onOriginTableChange = (name) => {
        const { form, havePart, editParams, changeParams,
            getSourcePart, resetSourcePart } = this.props;
        let origin = { ...editParams.origin, table: name };

        // 重置分区表单和参数
        if (havePart) {
            resetSourcePart('origin');
            form.setFieldsValue({ 
                originColumn: ''
            });
            origin.partition = undefined;
        }

        this.setState({ 
            showPreview: false,
            sourcePreview: {} 
        });

        changeParams({
            origin: { ...editParams.origin, ...origin }
        });

        // 请求分区数据
        let sourceId = form.getFieldValue('sourceId');
        if (sourceId && name && havePart) {
            getSourcePart({
                sourceId,
                table: name
            }, 'origin');
        }
    }

    // 获取预览数据
    onSourcePreview = () => {
        const { dataSourceId, table, partition } = this.props.editParams.origin;
        const { showPreview } = this.state;

        if (!dataSourceId || !table) {
            message.error('未选择数据源或数据表');
            return;
        }

        if (!showPreview) {
            DSApi.getDataSourcesPreview({ 
                sourceId: dataSourceId,
                tableName: table,
                partition: partition
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

                    this.setState({
                        sourcePreview: res.data 
                    });
                }
            });
        }

        this.setState({ 
            showPreview: !showPreview
        });
    }

    // 分区变化回调
    handlePartChange = (value, label, extra) => {
        const { origin } = this.props.editParams;
        let partition = value ? extra.triggerNode.props.dataRef.partColumn : undefined;

        this.props.changeParams({
            origin: {...origin,  partition}
        });
    }

    next = () => {
        const { currentStep, navToStep, form } = this.props;

        form.validateFields({ force: true }, (err, values) => {
            console.log(err,values)
            if (!err) {
                navToStep(currentStep + 1);
            }
        })
    }

    previewTableColumns = (data) => {
        return data && data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80,
                render: (value) => {
                    return <TableCell 
                        className="no-scroll-bar"
                        value={value ? value : undefined}
                        readOnly
                        style={{ minWidth: 80, width: '100%', resize: 'none' }} 
                    />
                }
            }
        });
    }

    render() {
        const { editStatus, editParams, form, dataSource, dataCheck, havePart } = this.props;
        const { originPart } = dataCheck;
        const { getFieldDecorator } = form;
        const { sourceList, sourceTable } = dataSource;
        const { dataSourceId, table, partition } = editParams.origin;
        const { sourcePreview, showPreview } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {
                                getFieldDecorator('sourceId', {
                                    rules: [{ required: true, message: '请选择数据源' }],
                                    initialValue: dataSourceId ? dataSourceId.toString() : undefined
                                })(
                                    <Select 
                                        showSearch
                                        optionFilterProp="title"
                                        style={{ width: '85%', marginRight: 15 }} 
                                        onChange={this.onSourceTypeChange} 
                                        disabled={editStatus === 'edit'}>
                                        {
                                            this.renderSourceType(sourceList)
                                        }
                                    </Select>
                                )
                            }
                            <Link to="/dq/dataSource">添加数据源</Link>
                        </FormItem>

                        <FormItem {...formItemLayout} label="选择左侧表">
                            {
                                getFieldDecorator('sourceTable', {
                                    rules: [{ required: true, message: '请选择左侧表' }],
                                    initialValue: table
                                })(
                                    <Select 
                                        showSearch
                                        style={{ width: '85%' }} 
                                        onChange={this.onOriginTableChange} 
                                        disabled={editStatus === 'edit'}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            (havePart || partition)
                            &&
                            <FormItem {...formItemLayout} label="选择分区">
                                {
                                    getFieldDecorator('originColumn', {
                                        rules: [],
                                        initialValue: partition
                                    })(
                                        <TreeSelect
                                            allowClear
                                            showSearch
                                            placeholder="分区列表"
                                            treeNodeLabelProp="value"
                                            disabled={editStatus === 'edit'}
                                            style={{ width: '85%' }} 
                                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                                            onChange={this.handlePartChange}>
                                            {
                                                this.renderTreeSelect(originPart)
                                            }
                                        </TreeSelect>
                                    )
                                }
                            </FormItem>
                        }

                        <Row type="flex" justify="center" className="font-14">
                            <a onClick={this.onSourcePreview}>数据预览<Icon type="down" style={{ marginLeft: 5 }} /></a>
                        </Row>
                        
                        {
                            showPreview
                            &&
                            <Table 
                                rowKey="key"
                                // bordered
                                className="m-table m-cells preview-table"
                                columns={this.previewTableColumns(sourcePreview.columnList)} 
                                dataSource={sourcePreview.dataList}
                                pagination={false}
                                scroll={{ x: 1000 }}
                            />
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