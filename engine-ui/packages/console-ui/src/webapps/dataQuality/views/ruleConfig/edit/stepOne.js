import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Row, Col, Table, 
    Button, Form, Select, 
    Input, TreeSelect, Icon, 
    message, Checkbox } from 'antd';

import TableCell from 'widgets/tableCell';

import { dataSourceActions } from '../../../actions/dataSource';
import { dataSourceTypes, formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

const mapStateToProps = state => {
    const { dataSource } = state;
    return { dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    getDataSourcesPart(params) {
        dispatch(dataSourceActions.getDataSourcesPart(params));
    },
    resetDataSourcesPart() {
        dispatch(dataSourceActions.resetDataSourcesPart());
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepOne extends Component {
    constructor(props) {
        super(props);
        this.state = {
            useInput: false,
            showPreview: false,
            sourcePreview: {}
        }
    }
    
    componentDidMount() {
        this.props.getDataSourcesList();
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

    // 数据表下拉框
    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option 
                    key={tableName} 
                    value={tableName}>
                    {tableName}
                </Option>
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

    // 分区显示title
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`
        } else {
            return name
        }
    }

    /**
     * 是否有分区
     * @param {string} id 
     */
    havePartition = (id) => {
        const { sourceList } = this.props.dataSource;
        
        sourceList.forEach((item) => {
            if (item.id == id) {
                this.props.changeHavePart(item.type === 7 || item.type === 10);
            }
        });
    } 

    // 数据源变化回调
    onSourceTypeChange = (id) => {
        const { 
            form, 
            editParams, 
            changeParams, 
            getDataSourcesTable,
            resetDataSourcesPart 
        } = this.props;
        let params = { dataSourceId: id };

        this.havePartition(id);
        form.setFieldsValue({ sourceTable: '' });
        getDataSourcesTable({ sourceId: id });

        // 重置分区表单和参数
        if (editParams.partition) {
            resetDataSourcesPart();
            form.setFieldsValue({ 
                originColumn: '',
                originColumnInput: ''
            });
            params.partition = undefined;
        }

        this.setState({ 
            showPreview: false,
            sourcePreview: {} 
        });

        // 重置规则
        if (editParams.rules.length) {
            params.rules = [];
        }

        changeParams(params);

        // 如果数据和表都有则请求分区数据
        let tableName = form.getFieldValue('sourceTable');
        this.getSourcesPart(id, tableName);
    }

    // 数据表变化回调
    onTableChange = (name) => {
        const { form, editParams, changeParams } = this.props;
        let params = { tableName: name };

        // 重置分区表单和参数
        if (editParams.partition) {
            this.props.resetDataSourcesPart();
            form.setFieldsValue({ 
                originColumn: '',
                originColumnInput: ''
            });
            params.partition = undefined;
        }

        this.setState({
            showPreview: false,
            sourcePreview: {} 
        });

        // 重置规则
        if (editParams.rules.length) {
            params.rules = [];
        }

        changeParams(params);

        // 如果数据和表都有则请求分区数据
        let sourceId = form.getFieldValue('sourceId');
        this.getSourcesPart(sourceId, name);
    }

    // 获取分区数据
    getSourcesPart = (id, name) => {
        const { havePart } = this.props;

        if (id && name && havePart) {
            this.props.getDataSourcesPart({
                sourceId: id,
                table: name
            });
        }
    }

    // 获取预览数据
    onSourcePreview = () => {
        const { dataSourceId, tableName, partition } = this.props.editParams;
        const { showPreview } = this.state;

        if(!dataSourceId || !tableName) {
            message.error('未选择数据源或表');
            return;
        }

        if (!showPreview) {
            DSApi.getDataSourcesPreview({
                sourceId: dataSourceId,
                tableName: tableName,
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
        let partition = value ? extra.triggerNode.props.dataRef.partColumn : undefined;
        this.props.changeParams({ partition });
    }

    // 分区变化回调
    handleInputPartChange = (e) => {
        let partition = e.target.value ? e.target.value : undefined;
        this.props.changeParams({ partition });
    }

    renderPartText = () => {
        return (
            <p className="font-14">如果分区还不存在，可以直接手动输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
        )
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
        if (data) {
            return data.map((item) => {
                return {
                    title: item,
                    key: item,
                    dataIndex: item,
                    width: 80,
                    render: function(txt) {
                        return <TableCell 
                            className="no-scroll-bar"
                            value={txt} 
                            resize="none"
                            style={{ minWidth: '80px', width:'100%' }} 
                        />
                    }
                }
            });
        }
    }

    onSubscribeChange = (e) => {
        let isSubscribe = e.target.checked ? 1 : 0;
        this.props.changeParams({ isSubscribe });
    }

    renderColumnPart = () => {
        const { editParams, form, dataSource } = this.props;
        const { useInput } = this.state;
        const { partition } = editParams;
        const { sourcePart } = dataSource;
        const { getFieldDecorator } = form;

        if (!useInput) {
            return <FormItem {...formItemLayout} label="选择分区" extra={this.renderPartText()}>
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
                            style={{ width: '85%', marginRight: 15 }} 
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            onChange={this.handlePartChange}>
                            {
                                this.renderTreeSelect(sourcePart)
                            }
                        </TreeSelect>
                    )
                }
                <a onClick={this.onPartitionTypeChange}>手动输入</a>
            </FormItem>
        } else {
            return <FormItem {...formItemLayout} label="选择分区" extra={this.renderPartText()}>
                {
                    getFieldDecorator('originColumnInput', {
                        rules: [],
                        initialValue: ''
                    })(
                        <Input
                            style={{ width: '85%', marginRight: 15 }} 
                            placeholder="手动输入分区的格式为：分区字段=分区值，如column=${sys.recentPart}，具体的参数配置在帮助文档里说明" 
                            onChange={this.handleInputPartChange} />
                    )
                }
                <a onClick={this.onPartitionTypeChange}>选择已有分区</a>
            </FormItem>
        }
    }

    onPartitionTypeChange = () => {
        const { useInput } = this.state;
        
        // form.setFieldsValue({ part: '' });
        // params.partition = undefined;

        this.props.changeParams({ partition: undefined });
        this.setState({ useInput: !useInput });
    }

    render() {
        const { editParams, form, dataSource, havePart } = this.props;
        const { useInput, showPreview, sourcePreview } = this.state;
        const { dataSourceId, tableName, partition } = editParams;
        const { sourceList, sourceTable, sourcePart } = dataSource;
        const { getFieldDecorator } = form;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {
                                getFieldDecorator('sourceId', {
                                    rules: [{ required: true, message: '请选择数据源' }],
                                    initialValue: dataSourceId ? dataSourceId.toString() : ''
                                })(
                                    <Select 
                                        showSearch
                                        optionFilterProp="title"
                                        style={{ width: '85%', marginRight: 15 }} 
                                        onChange={this.onSourceTypeChange}>
                                        {
                                            this.renderSourceType(sourceList)
                                        }
                                    </Select>
                                )
                            }
                            <Link to="/dq/dataSource">添加数据源</Link>
                        </FormItem>

                        <FormItem {...formItemLayout} label="选择数据表">
                            {
                                getFieldDecorator('sourceTable', {
                                    rules: [{ required: true, message: '请选择数据表' }],
                                    initialValue: tableName
                                })(
                                    <Select 
                                        showSearch
                                        style={{ width: '85%', marginRight: 15 }} 
                                        onChange={this.onTableChange}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                            {
                                tableName &&
                                <Checkbox onChange={this.onSubscribeChange}>订阅</Checkbox>
                            }
                        </FormItem>

                        {
                            havePart
                            &&
                            this.renderColumnPart()
                        }

                        <Row type="flex" justify="center" className="font-14">
                            <a onClick={this.onSourcePreview}>数据预览<Icon type="down" style={{ marginLeft: 5 }} /></a>
                        </Row>
                        
                        {
                            showPreview
                            &&
                            <Table 
                                rowKey="key"
                                className="m-table m-cells preview-table"
                                columns={this.initColumns(sourcePreview.columnList)} 
                                dataSource={sourcePreview.dataList}
                                pagination={false}
                                scroll={{ x: 1000 }}
                            />
                        }
                    </Form>
                </div>

                <div className="steps-action">
                    <Button>
                        <Link to="/dq/rule">取消</Link>
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