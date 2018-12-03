import React, { Component } from 'react';
import { connect } from 'react-redux';
// import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Form, Table, Button, Select, TreeSelect, Icon, message } from 'antd';

import TableCell from 'widgets/tableCell';
import { dataCheckActions } from '../../../actions/dataCheck';
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
class StepTwo extends Component {
    constructor (props) {
        super(props);
        this.state = {
            showPreview: false,
            sourcePreview: {}
        };
    }

    // 右侧表下拉框
    renderTargetTable = data => {
        return data.map(tableName => {
            return (
                <Option key={tableName} value={tableName}>
                    {tableName}
                </Option>
            );
        });
    };

    // 右侧表变化回调
    onTargetTableChange = name => {
        const { form, havePart, editParams } = this.props;
        // let target = { ...editParams.target, table: name };
        let sourceId = editParams.target.dataSourceId;
        let target = {
            dataSourceId: sourceId,
            table: name
        };

        // 重置分区表单和参数
        if (havePart) {
            this.props.resetSourcePart('target');
            form.setFieldsValue({ targetPartition: undefined });

            // 请求分区数据
            this.props.getSourcePart(
                {
                    sourceId: sourceId,
                    table: name
                },
                'target'
            );
        }

        this.setState({
            showPreview: false,
            sourcePreview: {}
        });

        this.props.changeParams({
            target: target,
            mappedPK: {}
        });
    };

    // 预览数据源的数据
    onSourcePreview = () => {
        const { dataSourceId, table, partition } = this.props.editParams.target;
        const { showPreview } = this.state;

        if (!table) {
            message.error('未选择数据表');
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

                    this.setState({ sourcePreview: res.data });
                }
            });
        }

        this.setState({
            showPreview: !showPreview
        });
    };

    // 分区树形选择
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
                            {this.renderTreeSelect(item.children)}
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

    // 分区变化回调
    handlePartChange = (value, label, extra) => {
        const { target } = this.props.editParams;
        let partition = value
            ? extra.triggerNode.props.dataRef.partColumn
            : undefined;

        this.props.changeParams({
            target: { ...target, partition }
        });
    };

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    };

    next = () => {
        const { currentStep, navToStep, form } = this.props;
        form.validateFields({ force: true }, (err, values) => {
            console.log(err, values);
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
                    width: 80,
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

    render () {
        const {
            form,
            editStatus,
            editParams,
            dataSource,
            dataCheck,
            havePart
        } = this.props;
        const { targetPart } = dataCheck;
        const { sourceTable } = dataSource;
        const { getFieldDecorator } = form;
        const { table, partition } = editParams.target;
        const { sourcePreview, showPreview } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="选择右侧表">
                            {getFieldDecorator('table', {
                                rules: [
                                    { required: true, message: '请选择右侧表' }
                                ],
                                initialValue: table
                            })(
                                <Select
                                    showSearch
                                    style={{ width: '85%' }}
                                    onChange={this.onTargetTableChange}
                                    disabled={editStatus === 'edit'}
                                >
                                    {this.renderTargetTable(sourceTable)}
                                </Select>
                            )}
                        </FormItem>

                        {(havePart || partition) && (
                            <FormItem {...formItemLayout} label="选择分区">
                                {getFieldDecorator('targetPartition', {
                                    rules: [],
                                    initialValue: partition
                                })(
                                    <TreeSelect
                                        allowClear
                                        showSearch
                                        placeholder="分区列表"
                                        treeNodeLabelProp="value"
                                        disabled={editStatus === 'edit'}
                                        style={{
                                            width: '85%',
                                            marginRight: 15
                                        }}
                                        dropdownStyle={{
                                            maxHeight: 400,
                                            overflow: 'auto'
                                        }}
                                        onChange={this.handlePartChange}
                                    >
                                        {this.renderTreeSelect(targetPart)}
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
                                className="m-table m-cells"
                                columns={this.previewTableColumns(
                                    sourcePreview.columnList
                                )}
                                dataSource={sourcePreview.dataList}
                                pagination={false}
                                scroll={{ x: 1000 }}
                            />
                        )}
                    </Form>
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
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
export default Form.create()(StepTwo);
