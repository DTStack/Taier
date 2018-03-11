import React, { Component } from 'react';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Row, Col, Table, Button, Form, Select, Input, TreeSelect, Icon, message } from 'antd';
import { dataSourceTypes, formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

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
        let oldParams = this.props.editParams,
            newParams = nextProps.editParams;

        if (oldParams.origin.dataSourceId != newParams.origin.dataSourceId) {
            this.props.getDataSourcesTable({ sourceId: newParams.origin.dataSourceId });

            if (oldParams.origin.partitionValue != newParams.origin.partitionValue) {
                this.props.getDataSourcesPart({
                    sourceId: newParams.origin.dataSourceId,
                    table: newParams.origin.table
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

    // 左侧表下拉框
    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return (
                <Option key={tableName} value={tableName}>{tableName}</Option>
            )
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

    /**
     * 是否是Hive或MaxCompute
     * @param {string} id 
     */
    isHiveOrMaxCompute = (id) => {
        const { sourceList } = this.props.dataSource;
        
        sourceList.forEach((item) => {
            if (item.id == id) {
                console.log(item)
                this.setState({ havePart: item.type === 7 || item.type === 10 });
            }
        });
    } 

    onSourceTypeChange = (id) => {
        const { editParams, form, changeParams, getDataSourcesPart } = this.props;
        const { havePart } = this.state;
        let origin = { ...editParams.origin, dataSourceId: id };

        this.isHiveOrMaxCompute(id);
        form.setFieldsValue({ sourceTable: '' });

        // 重置分区表单和参数
        if (havePart) {
            form.setFieldsValue({ originColumn: '' });
            origin.partitionColumn = undefined;
            origin.partitionValue  = undefined;
        }

        changeParams({
            origin: { ...editParams.origin, ...origin },
            target: { ...editParams.target, dataSourceId: id }
        });

        // 如果id和表都有则请求分区数据
        let tableName = form.getFieldValue('sourceTable');
        this.getColumnAndValue(id, tableName);
    }

    // 左侧表变化回调
    onOriginTableChange = (name) => {
        const { editParams, form, changeParams, getDataSourcesPart } = this.props;
        const { havePart } = this.state;
        let origin = { ...editParams.origin, table: name };

        // 重置分区表单和参数
        if (havePart) {
            form.setFieldsValue({ originColumn: '' });
            origin.partitionColumn = undefined;
            origin.partitionValue  = undefined;
        }

        changeParams({
            origin: { ...editParams.origin, ...origin }
        });

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
            message.error('未选择数据源或左侧表');
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
            <div className="preview-text">
                <p>分区支持系统函数，例如:</p>
                <p dangerouslySetInnerHTML={{ __html: '${bdp.system.bizdate}，业务日期变量' }}></p>
                <p>————此处需补充其他变量</p>
                <p>如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
            </div>
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
        const { sourceList, sourceTable } = this.props.dataSource;
        const { origin } = this.props.editParams;
        const { getFieldDecorator } = this.props.form;
        const { havePart, sourcePreview, sourcePart } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {
                                getFieldDecorator('sourceId', {
                                    rules: [{ required: true, message: '请选择数据源' }],
                                    initialValue: origin.dataSourceId ? origin.dataSourceId.toString() : ''
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
                                    getFieldDecorator('originColumn', {
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
                            <Col span={14} offset={6}>
                                <Button onClick={this.onSourcePreview}>数据预览<Icon type="down" /></Button>
                            </Col>
                        </Row>
                        
                        {
                            !isEmpty(sourcePreview)
                            &&
                            <Row>
                                <Col span={14} offset={6}>
                                <div>
                                    <Table 
                                        rowKey="key"
                                        // bordered
                                        className="m-table preview-table"
                                        columns={this.initColumns(sourcePreview.columnList)} 
                                        dataSource={sourcePreview.dataList}
                                        pagination={false}
                                        scroll={{ x: '200%', y: 400 }}
                                    />
                                    </div>
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