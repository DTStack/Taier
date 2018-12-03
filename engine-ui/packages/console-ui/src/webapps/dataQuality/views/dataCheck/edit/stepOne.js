import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Form, Table, Button, Select, TreeSelect, Icon, message } from 'antd';

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
    return { dataCheck, dataSource };
};

const mapDispatchToProps = dispatch => ({
    getDataSourcesList (params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesTable (params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable () {
        dispatch(dataSourceActions.resetDataSourcesTable());
    },
    getSourcePart (params, type) {
        dispatch(dataCheckActions.getSourcePart(params, type));
    },
    resetSourcePart (params) {
        dispatch(dataCheckActions.resetSourcePart(params));
    }
});

@connect(
    mapStateToProps,
    mapDispatchToProps
)
class StepOne extends Component {
    constructor (props) {
        super(props);
        this.state = {
            showPreview: false,
            sourcePreview: {}
        };
    }

    componentDidMount () {
        if (this.props.editStatus === 'new') {
            this.props.getDataSourcesList();
        }
    }

    // 数据源下拉框
    renderSourceType = data => {
        return data.map(source => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option
                    key={source.id}
                    value={source.id.toString()}
                    title={title}
                >
                    {title}
                </Option>
            );
        });
    };

    // 左侧表下拉框
    renderSourceTable = data => {
        return data.map(tableName => {
            return (
                <Option key={tableName} value={tableName}>
                    {tableName}
                </Option>
            );
        });
    };

    // 分区下拉框
    renderTreeSelect = data => {
        if (!isEmpty(data)) {
            return data.children.map(item => {
                let name = item.partName;

                let value = item.partValue;

                let partTitle = value
                    ? `分区字段：${name}  分区值：${value}`
                    : name;

                if (item.children.length) {
                    return (
                        <TreeNode
                            key={item.nodeId}
                            title={partTitle}
                            value={item.partColumn}
                            dataRef={item}
                        >
                            {this.renderTreeSelect(item)}
                        </TreeNode>
                    );
                } else {
                    return (
                        <TreeNode
                            key={item.nodeId}
                            title={partTitle}
                            value={item.partColumn}
                            dataRef={item}
                            isLeaf={true}
                        />
                    );
                }
            });
        }
    };

    /**
     * 是否有分区
     * @param {String} id
     */
    havePartition = id => {
        const { sourceList } = this.props.dataSource;

        sourceList.forEach(item => {
            if (item.id == id) {
                this.props.changeHavePart(item.type === 7 || item.type === 10);
            }
        });
    };

    /**
     * 数据源变化回调
     * @param {String} id 数据源id
     */
    onSourceTypeChange = id => {
        const { form, havePart } = this.props;
        let origin = { dataSourceId: id };

        this.havePartition(id);
        this.props.resetDataSourcesTable();
        this.props.getDataSourcesTable({ sourceId: id });
        form.setFieldsValue({ originTable: undefined });

        // 重置分区数据
        if (havePart) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originPartition: undefined });
        }

        // 重置预览数据
        this.setState({
            showPreview: false,
            sourcePreview: {}
        });

        this.props.changeParams({
            origin: origin,
            target: origin,
            mappedPK: {}
        });
    };

    /**
     * 左侧表变化回调
     * @param {String} name
     */
    onOriginTableChange = name => {
        const { form, havePart, editParams } = this.props;

        let sourceId = editParams.origin.dataSourceId;
        let origin = {
            dataSourceId: sourceId,
            table: name
        };

        // 重置分区表单和参数
        if (havePart) {
            this.props.resetSourcePart('origin');
            form.setFieldsValue({ originPartition: undefined });

            // 请求分区数据
            this.props.getSourcePart(
                {
                    sourceId: sourceId,
                    table: name
                },
                'origin'
            );
        }

        // 重置预览数据
        this.setState({
            showPreview: false,
            sourcePreview: {}
        });

        this.props.changeParams({
            origin: origin,
            mappedPK: {}
        });
    };

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
            }).then(res => {
                if (res.code === 1) {
                    let { columnList, dataList } = res.data;

                    res.data.dataList = dataList.map((arr, i) => {
                        let o = {};
                        arr.forEach((item, j) => {
                            o.key = i;
                            o[columnList[j]] = item;
                        });
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
    };

    // 分区变化回调
    handlePartChange = (value, label, extra) => {
        const { origin } = this.props.editParams;
        let partition = value
            ? extra.triggerNode.props.dataRef.partColumn
            : undefined;

        this.props.changeParams({
            origin: { ...origin, partition }
        });
    };

    next = () => {
        const { currentStep, navToStep, form } = this.props;

        form.validateFields({ force: true }, (err, values) => {
            if (!err) {
                navToStep(currentStep + 1);
            }
        });
    };

    // 数据预览表格配置
    previewTableColumns = data => {
        return (
            data &&
            data.map(item => {
                return {
                    title: item,
                    key: item,
                    dataIndex: item,
                    width: item.length * 8 + 28 + 'px',
                    render: value => {
                        return (
                            <TableCell
                                className="no-scroll-bar"
                                value={value || undefined}
                                readOnly
                                style={{
                                    minWidth: 80,
                                    width: '100%',
                                    resize: 'none'
                                }}
                            />
                        );
                    }
                };
            })
        );
    };

    getScroll () {
        let i = 100;
        const columnList =
            this.state.sourcePreview && this.state.sourcePreview.columnList;

        for (let j in columnList) {
            let item = columnList[j];
            i = i + item.length * 8 + 28;
        }
        return i + 'px';
    }

    render () {
        const {
            editStatus,
            editParams,
            form,
            dataSource,
            dataCheck,
            havePart
        } = this.props;
        const { originPart } = dataCheck;
        const { getFieldDecorator } = form;
        const { sourceList, sourceTable, tableLoading } = dataSource;
        const { dataSourceId, table, partition } = editParams.origin;
        const { sourcePreview, showPreview } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择数据源">
                            {getFieldDecorator('sourceId', {
                                rules: [
                                    {
                                        required: true,
                                        message: '请选择数据源'
                                    }
                                ],
                                initialValue: dataSourceId
                                    ? dataSourceId.toString()
                                    : undefined
                            })(
                                <Select
                                    showSearch
                                    optionFilterProp="title"
                                    style={{ width: '85%', marginRight: 15 }}
                                    onChange={this.onSourceTypeChange}
                                    disabled={
                                        editStatus === 'edit' || tableLoading
                                    }
                                >
                                    {this.renderSourceType(sourceList)}
                                </Select>
                            )}
                            <Link to="/dq/dataSource">添加数据源</Link>
                        </FormItem>

                        <FormItem {...formItemLayout} label="选择左侧表">
                            {getFieldDecorator('originTable', {
                                rules: [
                                    {
                                        required: true,
                                        message: '请选择左侧表'
                                    }
                                ],
                                initialValue: table
                            })(
                                <Select
                                    showSearch
                                    style={{ width: '85%' }}
                                    onChange={this.onOriginTableChange}
                                    disabled={editStatus === 'edit'}
                                >
                                    {this.renderSourceTable(sourceTable)}
                                </Select>
                            )}
                        </FormItem>

                        {(havePart || partition) && (
                            <FormItem {...formItemLayout} label="选择分区">
                                {getFieldDecorator('originPartition', {
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
                                        dropdownStyle={{
                                            maxHeight: 400,
                                            overflow: 'auto'
                                        }}
                                        onChange={this.handlePartChange}
                                    >
                                        {this.renderTreeSelect(originPart)}
                                    </TreeSelect>
                                )}
                            </FormItem>
                        )}

                        <div className="txt-center font-14">
                            <a onClick={this.onSourcePreview}>
                                数据预览
                                <Icon type="down" style={{ marginLeft: 5 }} />
                            </a>
                        </div>

                        {showPreview && (
                            <Table
                                rowKey="key"
                                // bordered
                                className="m-table m-cells"
                                columns={this.previewTableColumns(
                                    sourcePreview.columnList
                                )}
                                dataSource={sourcePreview.dataList}
                                pagination={false}
                                scroll={{ x: this.getScroll() }}
                            />
                        )}
                    </Form>
                </div>

                <div className="steps-action">
                    <Button>
                        <Link to="/dq/dataCheck">取消</Link>
                    </Button>
                    <Button
                        className="m-l-8"
                        type="primary"
                        onClick={this.next}
                    >
                        下一步
                    </Button>
                </div>
            </div>
        );
    }
}
export default Form.create()(StepOne);
