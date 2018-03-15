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
            havePart: false,
            sourcePreview: {}
        }
    }
    
    componentDidMount() {
        this.props.getDataSourcesList();
    }

    componentWillReceiveProps(nextProps) {
        // let oldParams = this.props.editParams,
        //     newParams = nextProps.editParams;

        // if (!oldParams.origin.dataSourceId && newParams.origin.dataSourceId) {
        //     this.props.getDataSourcesTable({ sourceId: newParams.origin.dataSourceId });

        //     if (oldParams.origin.partitionValue != newParams.origin.partitionValue && newParams.origin.partitionValue) {
        //         this.props.getDataSourcesPart({
        //             sourceId: newParams.origin.dataSourceId,
        //             table: newParams.origin.table
        //         });

        //         this.setState({ havePart: true });
        //     }
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

    /**
     * 分区显示的title
     * @param {String} name 
     * @param {String} value 
     */
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`;
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
                this.setState({ havePart: item.type === 7 || item.type === 10 });
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
        if (origin.partitionColumn) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originColumn: '' });

            origin.partitionColumn = undefined;
            origin.partitionValue  = undefined;
        }

        changeParams({
            origin: { ...editParams.origin, ...origin },
            target: { ...editParams.target, dataSourceId: id }
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
        if (origin.partitionColumn) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originColumn: '' });
            
            origin.partitionColumn = undefined;
            origin.partitionValue  = undefined;
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
        const { havePart } = this.state;

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

        if(!sourceId || !tableName) {
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

        this.props.changeParams({
            origin: { ...origin, 
                partitionColumn: value ? extra.triggerNode.props.dataRef.partName : undefined, 
                partitionValue: value ? extra.triggerNode.props.dataRef.partValue : undefined
            }
        });
    }

    renderPartText = () => {
        return (
            <p className="font-14">如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
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

    initColumns = (data) => {
        return data.map((item) => {
            return {
                title: item,
                key: item,
                dataIndex: item,
                width: 80,
            }
        });
    }

    render() {
        const { editStatus, editParams, form, dataSource, dataCheck } = this.props;
        const { dataSourceId, table, partitionColumn, partitionValue } = editParams.origin;
        const { sourceList, sourceTable } = dataSource;
        const { originPart } = dataCheck;
        const { havePart, sourcePreview } = this.state;
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
                                    <Select onChange={this.onSourceTypeChange} disabled={editStatus === 'edit'}>
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
                                    initialValue: table
                                })(
                                    <Select onChange={this.onOriginTableChange} disabled={editStatus === 'edit'}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            (havePart || partitionColumn)
                            &&
                            <FormItem {...formItemLayout} label="选择分区" extra={this.renderPartText()}>
                                {
                                    getFieldDecorator('originColumn', {
                                        rules: [{ required: true, message: '请选择分区' }],
                                        initialValue: this.getPartTitle(partitionColumn, partitionValue) 
                                    })(
                                        <TreeSelect
                                            disabled={editStatus === 'edit'}
                                            allowClear
                                            showSearch
                                            placeholder="分区列表"
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
                            !isEmpty(sourcePreview)
                            &&
                            <Table 
                                rowKey="key"
                                // bordered
                                className="m-table preview-table"
                                columns={this.initColumns(sourcePreview.columnList)} 
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