import React, { Component } from 'react';
import { connect } from 'react-redux';
import { browserHistory, hashHistory } from 'react-router';
import { Form, Table, Input, Icon, Button, Select, Card, Cascader, message, InputNumber } from 'antd';

import { tagConfigActions } from '../../../actions/tagConfig';
import { apiMarketActions } from '../../../actions/apiMarket';
import { dataSourceActions } from '../../../actions/dataSource';
import TableCell from 'widgets/tableCell';
import { halfFormItemLayout, TAG_TYPE } from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { dataSource, tagConfig, apiMarket } = state;
    return { dataSource, tagConfig, apiMarket }
}
const mapDispatchToProps = (dispatch) => ({
    getDataSourcesList (params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesPreview (params) {
        dispatch(dataSourceActions.getDataSourcesPreview(params));
    },
    getCatalogue (pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getAllIdentifyColumn (params) {
        dispatch(tagConfigActions.getAllIdentifyColumn(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)

class StepOne extends Component {
    state = {
        showPreview: false
    }

    componentDidMount () {
        this.props.getCatalogue(0);
        this.props.getDataSourcesList();
        this.props.getAllIdentifyColumn();
    }

    // 类目下拉框数据初始化
    initCatagoryOption = (data) => {
        if (data.some(item => item.api === true)) {
            return [];
        } else {
            return data.map((item) => {
                return {
                    value: item.id,
                    label: item.catalogueName,
                    children: this.initCatagoryOption(item.childCatalogue)
                }
            });
        }
    }

    // 获取已选取的类目array
    getCatalogueArray = (value) => {
        const { apiCatalogue } = this.props.apiMarket;
        let arr = [];

        if (!value) return [];

        const flat = (data) => {
            for (let i = 0; i < data.length; i++) {
                if (data[i].api) {
                    return
                }
                // 匹配节点
                if (data[i].id === value) {
                    arr.push(data[i].id);
                    return data[i].id;
                }
                // 若子节点含有对应的值，父节点入队
                if (flat(data[i].childCatalogue)) {
                    arr.push(data[i].id);
                    return data[i].id;
                }
            }
        }

        flat(apiCatalogue);
        return arr.reverse();
    }

    // 获取预览数据
    onSourcePreview = () => {
        const { basicInfo } = this.props;
        const { showPreview } = this.state;
        let dataSourceId = basicInfo.dataSourceId;
        let tableName = basicInfo.originTable;

        if (!dataSourceId || !tableName) {
            message.error('未选择数据源或数据表');
            return;
        }

        if (!showPreview) {
            this.props.getDataSourcesPreview({ dataSourceId, tableName });
        }

        this.setState({
            showPreview: !showPreview
        });
    }

    // 数据预览表格配置
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
                        /* eslint-disable-next-line */
                        value={value ? value : undefined}
                        readOnly
                        style={{ minWidth: 80, width: '100%', resize: 'none' }}
                    />
                }
            }
        });
    }

    getScroll = (width) => {
        const { sourcePreview } = this.props.dataSource;

        if (sourcePreview.columnList) {
            return width * sourcePreview.columnList.length;
        } else {
            return 0;
        }
    }

    next = () => {
        const { currentStep, navToStep, form } = this.props;

        form.validateFields({ force: true }, (err, values) => {
            if (!err) {
                values.catalogueId = [...values.catalogueId].pop();
                this.props.changeBasicInfo(values);
                navToStep(currentStep + 1);
            }
        });
    }

    cancel = () => {
        const { url, history } = this.props;

        if (url) {
            if (history) {
                browserHistory.push(url)
            } else {
                hashHistory.push(url)
            }
        } else {
            browserHistory.go(-1)
        }
    }

    render () {
        const { form, tagType, tagConfig, apiMarket, dataSource, basicInfo, editStatus } = this.props;
        const { getFieldDecorator } = form;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { sourceList, sourcePreview } = dataSource;
        const { showPreview } = this.state;
        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...halfFormItemLayout} label="所属分组">
                            {
                                getFieldDecorator('catalogueId', {
                                    rules: [{
                                        required: true,
                                        message: '请选择分组'
                                    }],
                                    initialValue: this.getCatalogueArray(basicInfo.catalogueId)
                                })(
                                    <Cascader
                                        showSearch
                                        popupClassName="noheight"
                                        options={this.initCatagoryOption(apiCatalogue)}
                                        placeholder="请选择分组"
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="标签名称">
                            {
                                getFieldDecorator('name', {
                                    rules: [{
                                        required: true,
                                        message: '请输入标签名称'
                                    }, {
                                        max: 20,
                                        message: '最大字数不能超过20'
                                    }, {
                                        pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/),
                                        message: '名称只能以字母，数字，下划线组成'
                                    }],
                                    initialValue: basicInfo.name
                                })(
                                    <Input disabled={editStatus === 'edit'} />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="标签描述">
                            {
                                getFieldDecorator('tagDesc', {
                                    rules: [{
                                        max: 200,
                                        message: '标签描述字符不能超过200'
                                    }],
                                    initialValue: basicInfo.tagDesc
                                })(
                                    <TextArea
                                        placeholder="标签描述"
                                        autosize={{ minRows: 2, maxRows: 6 }}
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="值域">
                            {
                                getFieldDecorator('tagRange', {
                                    rules: [{
                                        required: true,
                                        message: '值域不可为空'
                                    }],
                                    initialValue: basicInfo.tagRange
                                })(
                                    <TextArea
                                        placeholder="值域"
                                        autosize={{ minRows: 2, maxRows: 6 }}
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="调用限制">
                            {
                                getFieldDecorator('reqLimit', {
                                    rules: [{
                                        required: true,
                                        message: '请输入调用次数限制'
                                    }, {
                                        validator: function (rule, value, callback) {
                                            if (value && (value > 1000 || value < 1)) {
                                                const msg = `请输入不大于1000的正整数`
                                                callback(msg)
                                                return;
                                            }
                                            callback();
                                        }
                                    }],
                                    initialValue: basicInfo.reqLimit
                                })(
                                    <InputNumber
                                        min={1}
                                        step={1}
                                        max={1000}
                                        precision={0}
                                        style={{ width: '100%' }}
                                        placeholder="单用户每秒最高调用次数"
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="返回条数限制">
                            {
                                getFieldDecorator('respLimit', {
                                    rules: [{
                                        required: true,
                                        message: '请输入最大返回条数'
                                    }],
                                    initialValue: basicInfo.respLimit
                                })(
                                    <InputNumber
                                        min={1}
                                        step={1}
                                        max={2000}
                                        precision={0}
                                        style={{ width: '100%' }}
                                        placeholder="单次最大返回数据条数 (最高支持2000条)"
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="目标数据库">
                            {
                                getFieldDecorator('dataSourceId', {
                                    rules: [{
                                        required: true,
                                        message: '请选择数据源'
                                    }],
                                    initialValue: basicInfo.dataSourceId ? basicInfo.dataSourceId.toString() : undefined
                                })(
                                    <Select
                                        showSearch
                                        disabled
                                        placeholder="请选择数据源">
                                        {
                                            sourceList.map((source) => {
                                                let title = `${source.dataName}（${source.sourceTypeValue}）`;
                                                return (
                                                    <Option
                                                        key={source.id}
                                                        value={source.id.toString()}
                                                        title={title}>
                                                        {title}
                                                    </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                        {
                            TAG_TYPE[tagType] == '注册标签' &&
                            <FormItem {...halfFormItemLayout} label="来源表">
                                {
                                    getFieldDecorator('originTable', {
                                        rules: [{
                                            required: true,
                                            message: '不可为空'
                                        }],
                                        initialValue: basicInfo.originTable
                                    })(
                                        <Input disabled />
                                    )
                                }
                            </FormItem>
                        }
                        {
                            TAG_TYPE[tagType] == '注册标签' &&
                            <FormItem {...halfFormItemLayout} label="来源列">
                                {
                                    getFieldDecorator('originColumn', {
                                        rules: [{
                                            required: true,
                                            message: '不可为空'
                                        }],
                                        initialValue: basicInfo.originColumn
                                    })(
                                        <Input disabled />
                                    )
                                }
                            </FormItem>
                        }
                        <FormItem {...halfFormItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('identityColumn', {
                                    rules: [{
                                        required: true,
                                        message: '识别列ID不可为空'
                                    }],
                                    initialValue: basicInfo.identityColumn
                                })(
                                    <Input disabled />
                                )
                            }
                        </FormItem>
                        <FormItem {...halfFormItemLayout} label="识别列类型">
                            {
                                getFieldDecorator('identityId', {
                                    rules: [{
                                        required: true,
                                        message: '识别列类型不可为空'
                                    }],
                                    initialValue: basicInfo.identityId ? basicInfo.identityId.toString() : undefined
                                })(
                                    <Select
                                        showSearch
                                        disabled
                                        optionFilterProp="title"
                                        placeholder="选择识别列类型">
                                        {
                                            identifyColumn.map((item) => {
                                                return <Option
                                                    key={item.id}
                                                    value={item.id.toString()}
                                                    title={item.name}>
                                                    {item.name}
                                                </Option>
                                            })
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        <div className="txt-center font-14">
                            <a onClick={this.onSourcePreview.bind(this)}>
                                数据预览
                                <Icon type={showPreview ? 'up' : 'down'} style={{ marginLeft: 5 }} />
                            </a>
                        </div>

                        {
                            showPreview &&
                            <Card
                                className="box-2"
                                style={{ marginTop: '10px' }}
                                noHovering>
                                <Table
                                    rowKey={(record, index) => index}
                                    className="m-table m-cells"
                                    columns={ this.previewTableColumns(sourcePreview.columnList) }
                                    dataSource={sourcePreview.dataList}
                                    pagination={false}
                                    scroll={{ x: this.getScroll(80), y: 400 }}
                                />
                            </Card>
                        }
                    </Form>
                </div>

                <div className="steps-action">
                    <Button
                        className="m-r-8"
                        onClick={this.cancel}>
                        取消
                    </Button>
                    <Button
                        type="primary"
                        onClick={this.next}>
                        下一步
                    </Button>
                </div>
            </div>
        )
    }
}
export default (Form.create()(StepOne));
