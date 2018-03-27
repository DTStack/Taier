import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Row, Col, Table, Button, Form, Select, Input, TreeSelect, Icon, message } from 'antd';

import { dataSourceTypes, formItemLayout } from '../../../consts';
import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

const convertNum2Str = (num) => {
    let str = '';
    if (num) {
        str = num.toString();
    }
    return str;
}

const mapStateToProps = state => {
    const { dataCheck, dataSource } = state;
    return { dataCheck, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
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
            sourcePreview: {},
            useInput: false
        }
    }
    
    componentDidMount() {
        this.props.getDataSourcesList();
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
            return (
                <Option key={source.id} value={source.id.toString()}>{source.dataName}（{dataSourceTypes[source.type]}）</Option>
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
                        <TreeNode key={item.nodeId} title={partTitle} value={partTitle} dataRef={item}>
                            {this.renderTreeSelect(item)}
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
    isHiveOrMaxCompute = (id) => {
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
        const { editParams, form, changeParams } = this.props;
        let origin = { ...editParams.origin, dataSourceId: id };

        this.isHiveOrMaxCompute(id);
        this.props.getDataSourcesTable({ sourceId: id });

        form.setFieldsValue({ sourceTable: '' });
        this.setState({ sourcePreview: {} });

        // 重置分区表单和参数
        if (origin.partition) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originColumn: '' });
            origin.partition = undefined;
        }

        changeParams({
            origin: { ...editParams.origin, ...origin },
            target: { ...editParams.target, dataSourceId: id, table: '' }
        });

        // 如果数据源和表都有则请求分区数据
        let tableName = form.getFieldValue('sourceTable');
        this.getDataSourcesPart(id, tableName);
    }

    /**
     * 左侧表变化回调
     * @param {String} name 
     */
    onOriginTableChange = (name) => {
        const { editParams, form, changeParams } = this.props;
        let origin = { ...editParams.origin, table: name };

        this.setState({ sourcePreview: {} });

        // 重置分区表单和参数
        if (origin.partition) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originColumn: '' });
            origin.partition = undefined;
        }

        changeParams({
            origin: { ...editParams.origin, ...origin }
        });

        // 如果数据源和表都有则请求分区数据
        let sourceId = form.getFieldValue('sourceId');
        this.getDataSourcesPart(sourceId, name);
    }

    /**
     * 获取分区数据
     * @param {String} id 
     * @param {String} name
     */
    getDataSourcesPart = (id, name) => {
        const { havePart } = this.props;

        if (id && name && havePart) {
            this.props.getSourcePart({
                sourceId: id,
                table: name
            }, 'origin');
        }
    }

    // 获取预览数据
    onSourcePreview = () => {
        const { form } = this.props;
        let sourceId   = form.getFieldValue('sourceId'),
            tableName  = form.getFieldValue('sourceTable');

        if (!sourceId || !tableName) {
            message.error('未选择数据源或数据表');
            return;
        }

        DSApi.getDataSourcesPreview({
            sourceId: sourceId,
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

    // 分区变化回调
    handlePartChange = (value, label, extra) => {
        const { origin } = this.props.editParams;
        let partition = value ? extra.triggerNode.props.dataRef.partColumn : undefined;

        this.props.changeParams({
            origin: {...origin,  partition}
        });
    }

    // 分区变化回调
    handleInputPartChange = (e) => {
        const { origin } = this.props.editParams;
        let partition = e.target.value ? e.target.value : undefined;

        this.props.changeParams({
            origin: {...origin,  partition}
        });
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
            if (!err) {
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
                width: 80,
            }
        });
    }

    renderColumnPart = () => {
        const { editStatus, editParams, form, dataCheck } = this.props;
        const { useInput } = this.state;
        const { partition } = editParams.origin;
        const { originPart } = dataCheck;
        const { getFieldDecorator } = form;
        console.log(originPart,'renderColumnPart')

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
                            style={{ width: '85%', marginRight: 15 }} 
                            disabled={editStatus === 'edit'}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            onChange={this.handlePartChange}>
                            {
                                this.renderTreeSelect(originPart)
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
                    getFieldDecorator('originColumnInput', {
                        rules: [],
                        initialValue: ''
                    })(
                        <Input
                            style={{ width: '85%', marginRight: 15 }} 
                            placeholder="手动输入分区" 
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
        const { origin } = this.props.editParams;
        const { useInput } = this.state;
        // form.setFieldsValue({ part: '' });
        // params.partition = undefined;
        this.props.changeParams({
            origin: {...origin,  partition: undefined}
        });
        this.setState({ useInput: !useInput });
    }

    render() {
        const { editStatus, editParams, form, dataSource, havePart } = this.props;
        const { dataSourceId, table, partition } = editParams.origin;
        const { sourceList, sourceTable } = dataSource;
        const { sourcePreview } = this.state;
        const { getFieldDecorator } = form;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {
                                getFieldDecorator('sourceId', {
                                    rules: [{ required: true, message: '请选择数据源' }],
                                    initialValue: convertNum2Str(dataSourceId)
                                })(
                                    <Select 
                                        showSearch
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
                                // bordered
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