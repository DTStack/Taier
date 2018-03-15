import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Row, Col, Table, Button, Form, Select, Input, TreeSelect, Icon, message } from 'antd';
import { dataSourceTypes, formItemLayout } from '../../../consts';
import { dataSourceActions } from '../../../actions/dataSource';
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
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepOne extends Component {
    constructor(props) {
        super(props);
        this.state = {
            havePart: false,
            sourcePart: [],
            sourcePreview: {}
        }
    }
    
    componentDidMount() {
        this.props.getDataSourcesList();
    }

    componentWillReceiveProps(nextProps) {
        console.log(this.props, 'prev')
        console.log(nextProps, 'next')
        let newParams = nextProps.editParams,
            oldParams = this.props.editParams;

        if (newParams.dataSourceId != oldParams.dataSourceId) {
            this.props.getDataSourcesTable({ sourceId: newParams.dataSourceId });

            if (oldParams.partitionValue != newParams.partitionValue) {
                this.props.getDataSourcesPart({
                    sourceId: newParams.dataSourceId,
                    table: newParams.tableName
                });

                this.setState({ havePart: true });
            }
        }
    }

    // 数据源下拉框
    renderSourceType = (data) => {
        return data.map((source) => {
            return (
                <Option key={source.id} value={source.id.toString()}>{source.dataName}（{dataSourceTypes[source.type]}）</Option>
            )
        });
    }

    // 数据表下拉框
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

    // 分区显示title
    getPartTitle = (name, value) => {
        if (value) {
            return `分区字段：${name}  分区值：${value}`
        } else {
            return name
        }
    }

    /**
     * 是否是Hive或MaxCompute
     * @param {string} id 
     */
    isHiveOrMaxCompute = (id) => {
        const { sourceList } = this.props.dataSource;
        
        sourceList.forEach((item) => {
            if (item.id == id) {
                this.setState({ havePart: item.type === 7 || item.type === 10 });
            }
        });
    } 

    // 数据源变化回调
    onSourceTypeChange = (id) => {
        const { editParams, form, changeParams, getDataSourcesPart } = this.props;
        const { havePart } = this.state;
        let params = { dataSourceId: id };

        this.isHiveOrMaxCompute(id);
        form.setFieldsValue({ sourceTable: '' });

        // 重置分区表单和参数
        if (havePart) {
            form.setFieldsValue({ part: '' });
            params.partitionColumn = undefined;
            params.partitionValue  = undefined;
        }

        changeParams(params);

        // 如果id和表都有则请求分区数据
        let tableName = form.getFieldValue('sourceTable');
        this.getColumnAndValue(id, tableName);
    }

    // 数据表变化回调
    onTableChange = (name) => {
        const { editParams, form, changeParams, getDataSourcesPart } = this.props;
        const { havePart } = this.state;
        let params = { tableName: name };

        // 重置分区表单和参数
        if (havePart) {
            form.setFieldsValue({ part: '' });
            params.partitionColumn = undefined;
            params.partitionValue  = undefined;
        }

        changeParams(params);

        // 如果id和表都有则请求分区数据
        let sourceId = form.getFieldValue('sourceId');
        this.getColumnAndValue(sourceId, name);
    }

    // 获取分区数据
    getColumnAndValue = (id, name) => {
        const { havePart } = this.state;

        if (id && name && havePart) {
            DSApi.getDataSourcesPart({
                sourceId: id,
                table: name
            }).then((res) => {
                if (res.code === 1) {
                    this.setState({ sourcePart: res.data.children });
                }
            });
        }
    }

    // 重置预览数据
    resetSourcePreview = () => {
        this.setState({ sourcePreview: {} });
    }

    // 获取预览数据
    onSourcePreview = () => {
        const { form } = this.props;
        let sourceId = form.getFieldValue('sourceId');
        let tableName = form.getFieldValue('sourceTable');

        if(!sourceId || !tableName) {
            message.error('未选择数据源或表');
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

    handlePartChange = (value, label, extra) => {
        this.props.changeParams({
            partitionColumn: value ? extra.triggerNode.props.dataRef.partName : undefined, 
            partitionValue: value ? extra.triggerNode.props.dataRef.partValue : undefined
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
        const { editStatus, editParams, form, dataSource } = this.props;
        const { havePart, sourcePreview, sourcePart } = this.state;
        const { dataSourceId, tableName, partitionColumn, partitionValue } = editParams;
        const { sourceList, sourceTable } = dataSource;
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

                        <FormItem {...formItemLayout} label="选择数据表">
                            {
                                getFieldDecorator('sourceTable', {
                                    rules: [{ required: true, message: '请选择数据表' }],
                                    initialValue: tableName
                                })(
                                    <Select onChange={this.onTableChange} disabled={editStatus === 'edit'}>
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
                                    getFieldDecorator('part', {
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
                                                this.renderTreeSelect(sourcePart)
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