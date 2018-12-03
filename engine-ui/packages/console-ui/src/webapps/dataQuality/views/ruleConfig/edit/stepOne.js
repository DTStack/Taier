import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { isEmpty } from 'lodash';
import {
    Table,
    Button,
    Form,
    Select,
    Input,
    TreeSelect,
    Icon,
    message,
    Checkbox,
    Modal
} from 'antd';

import TableCell from 'widgets/tableCell';

import { dataSourceActions } from '../../../actions/dataSource';
import { formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';
import RCApi from '../../../api/ruleConfig';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

const mapStateToProps = state => {
    const { dataSource } = state;
    return { dataSource };
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
    getDataSourcesPart (params) {
        dispatch(dataSourceActions.getDataSourcesPart(params));
    },
    resetDataSourcesPart () {
        dispatch(dataSourceActions.resetDataSourcesPart());
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
            sourcePreview: {},
            loading: false
        };
    }

    componentDidMount () {
        this.props.getDataSourcesList();
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

    // 数据表下拉框
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
     * @param {string} id
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
     * 查看是否存在相同规则
     *
     */
    checkMonitor () {
        const { editParams, havePart } = this.props;
        const params = {
            tableName: editParams.tableName,
            dataSourceId: editParams.dataSourceId,
            partition: havePart ? editParams.partition : undefined
        };

        this.setState({
            loading: true
        });

        return RCApi.checkMonitor(params).then(res => {
            this.setState({
                loading: false
            });

            if (res && res.data) {
                return res.data;
            } else {
                if (res.code != 1) {
                    throw new Error(res.message);
                }
                return null;
            }
        });
    }

    // 数据源变化回调
    onSourceTypeChange = id => {
        const { form, havePart } = this.props;
        let params = {
            dataSourceId: id,
            rules: [],
            partition: undefined
        };

        this.havePartition(id);
        this.props.resetDataSourcesTable();
        this.props.getDataSourcesTable({ sourceId: id });
        form.setFieldsValue({ sourceTable: undefined });

        // 重置分区数据
        if (havePart) {
            this.props.resetDataSourcesPart();
            form.setFieldsValue({
                partition: undefined,
                partitionInput: 'column=${' + 'sys.recentPart}'
            });
        }

        // 重置预览数据
        this.setState({
            showPreview: false,
            sourcePreview: {}
        });

        this.props.changeParams(params);
    };

    // 数据表变化回调
    onTableChange = name => {
        const { form, havePart, editParams } = this.props;

        let params = {
            tableName: name,
            rules: [],
            partition: undefined
        };

        // 重置分区数据
        if (havePart) {
            this.props.resetDataSourcesPart();
            form.setFieldsValue({
                partition: undefined,
                partitionInput: 'column=${' + 'sys.recentPart}'
            });

            this.props.getDataSourcesPart({
                sourceId: editParams.dataSourceId,
                table: name
            });
        }

        // 重置预览数据
        this.setState({
            showPreview: false,
            sourcePreview: {}
        });

        this.props.changeParams(params);
    };

    // 获取预览数据
    onSourcePreview = () => {
        const { dataSourceId, tableName, partition } = this.props.editParams;
        const { showPreview } = this.state;

        if (!dataSourceId || !tableName) {
            message.error('未选择数据源或表');
            return;
        }

        if (!showPreview) {
            DSApi.getDataSourcesPreview({
                sourceId: dataSourceId,
                tableName: tableName,
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
        let partition = value
            ? extra.triggerNode.props.dataRef.partColumn
            : undefined;
        this.props.changeParams({ partition });
    };

    // 分区变化回调
    handleInputPartChange = e => {
        let partition = e.target.value ? e.target.value : undefined;
        this.props.changeParams({ partition });
    };

    // 分区提示文本
    partHintText = () => {
        return (
            <p className="font-14">
                {
                    '支持填写系统参数，格式为：column=${' + 'sys.recentPart}，column为分区字段名，需要您根据情况修改，${' + 'sys.recentPart}为系统参数，系统每次执行时会对最新的1个分区的数据做校验。除此之外还支持${' + 'bdp.system.premonth}、${' + 'bdp.system.cyctime}、${' + 'bdp.system.bizdate}、${' + 'bdp.system.currmonth}'
                }
            </p>
        );
    };

    jumpToEditRule (data, modal) {
        this.modal && this.modal.destroy();
        hashHistory.push({
            pathname: '/dq/rule',
            query: {
                tableName: data.tableName,
                tableId: data.tableId
            }
        });
    }

    next = () => {
        const { currentStep, navToStep, form } = this.props;

        form.validateFields({ force: true }, (err, values) => {
            if (!err) {
                this.checkMonitor().then(data => {
                    if (!data) {
                        navToStep(currentStep + 1);
                    } else {
                        const modal = Modal.warning({
                            title: '该规则配置已存在',
                            content: (
                                <span>
                                    该规则配置已存在，您可以直接前往
                                    <a
                                        onClick={this.jumpToEditRule.bind(
                                            this,
                                            data
                                        )}
                                    >
                                        {' '}
                                        编辑
                                    </a>
                                </span>
                            )
                        });
                        this.modal = modal;
                    }
                });
            }
        });
    };

    // 数据预览表格配置
    previewTableColumns = data => {
        if (data) {
            return data.map(item => {
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
            });
        }
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
    onSubscribeChange = e => {
        let isSubscribe = e.target.checked ? 1 : 0;
        this.props.changeParams({ isSubscribe });
    };

    // 分区不同输入方式
    renderColumnPart = () => {
        const { editParams, form, dataSource, useInput } = this.props;
        const { partition } = editParams;
        const { sourcePart } = dataSource;
        const { getFieldDecorator } = form;

        if (!useInput) {
            return (
                <FormItem {...formItemLayout} label="选择分区">
                    {getFieldDecorator('partition', {
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
                            onChange={this.handlePartChange}
                        >
                            {this.renderTreeSelect(sourcePart)}
                        </TreeSelect>
                    )}
                    <a onClick={this.onPartitionTypeChange}>手动输入</a>
                </FormItem>
            );
        } else {
            return (
                <FormItem
                    {...formItemLayout}
                    label="输入分区"
                    extra={this.partHintText()}
                >
                    {getFieldDecorator('partitionInput', {
                        rules: [],
                        initialValue: partition || 'column=${' + 'sys.recentPart}'
                        // ? partition : 'column=${sys.recentPart}'
                    })(
                        <Input
                            style={{ width: '85%', marginRight: 15 }}
                            placeholder={'手动输入分区的格式为：分区字段=分区值，如column=${' + 'sys.recentPart}'}
                            onChange={this.handleInputPartChange}
                        />
                    )}
                    <a onClick={this.onPartitionTypeChange}>选择已有分区</a>
                </FormItem>
            );
        }
    };

    // 改变分区输入方式
    onPartitionTypeChange = () => {
        const { useInput } = this.props;

        this.props.changeParams({ partition: undefined });
        this.props.changeUseInput(!useInput);
    };

    render () {
        const { form, editParams, dataSource, havePart } = this.props;
        const { getFieldDecorator } = form;
        const { dataSourceId, tableName, isSubscribe } = editParams;
        const { sourceList, sourceTable, tableLoading } = dataSource;
        const { showPreview, sourcePreview, loading } = this.state;

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
                                    : dataSourceId
                            })(
                                <Select
                                    showSearch
                                    optionFilterProp="title"
                                    style={{ width: '85%', marginRight: 15 }}
                                    onChange={this.onSourceTypeChange}
                                    disabled={tableLoading}
                                >
                                    {this.renderSourceType(sourceList)}
                                </Select>
                            )}
                        </FormItem>

                        <FormItem {...formItemLayout} label="选择数据表">
                            {getFieldDecorator('sourceTable', {
                                rules: [
                                    {
                                        required: true,
                                        message: '请选择数据表'
                                    }
                                ],
                                initialValue: tableName
                            })(
                                <Select
                                    mode="combobox"
                                    showSearch
                                    style={{ width: '85%', marginRight: 15 }}
                                    onChange={this.onTableChange}
                                >
                                    {this.renderSourceTable(sourceTable)}
                                </Select>
                            )}
                            {tableName && (
                                <Checkbox
                                    checked={isSubscribe == 1}
                                    onChange={this.onSubscribeChange}
                                >
                                    订阅
                                </Checkbox>
                            )}
                        </FormItem>

                        {havePart && this.renderColumnPart()}

                        <div className="txt-center font-14">
                            <a onClick={this.onSourcePreview}>
                                数据预览
                                <Icon type="down" style={{ marginLeft: 5 }} />
                            </a>
                        </div>

                        {showPreview && (
                            <Table
                                rowKey="key"
                                className="m-table m-cells preview-table"
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
                        <Link to="/dq/rule">取消</Link>
                    </Button>
                    <Button
                        loading={loading}
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
