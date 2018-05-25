import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Input, Select, Cascader } from 'antd';
import { isEmpty } from 'lodash';

import { tagConfigActions } from '../../../actions/tagConfig';
import { dataSourceActions } from '../../../actions/dataSource';
import { formItemLayout, TAG_TYPE, TAG_PUBLISH_STATUS } from '../../../consts';

const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { tagConfig, dataSource, apiMarket } = state;
    return { tagConfig, dataSource, apiMarket }
}

const mapDispatchToProps = dispatch => ({
    getTagDataSourcesList(params) {
        dispatch(dataSourceActions.getTagDataSourcesList(params));
    },
    getAllIdentifyColumn(params) {
        dispatch(tagConfigActions.getAllIdentifyColumn(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable() {
        dispatch(dataSourceActions.resetDataSourcesTable());
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
    resetDataSourcesColumn() {
        dispatch(dataSourceActions.resetDataSourcesColumn());
    },
})
@connect(mapStateToProps, mapDispatchToProps)
export default class EditTagModal extends Component {

    state = {}

    componentDidMount() {
        this.props.getTagDataSourcesList();
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

    // 目标数据库变化
    onSourceChange = (id) => {
        const { form } = this.props;

        form.setFieldsValue({ 
            originTable: undefined,
            originColumn: undefined,
            identityColumn: undefined
        });

        this.props.resetDataSourcesTable();
        this.props.resetDataSourcesColumn();
        this.props.getDataSourcesTable({ sourceId: id });
    }

    // 来源表变化
    onSourceTableChange = (name) => {
        const { form } = this.props;

        form.setFieldsValue({ 
            originColumn: undefined,
            identityColumn: undefined
        });

        this.props.resetDataSourcesColumn();
        this.props.getDataSourcesColumn({ 
            sourceId: form.getFieldValue("dataSourceId"), 
            tableName: name
        });
    }

    render() {
    	const { form, visible, editData, apiMarket, dataSource, tagConfig } = this.props;
    	const { getFieldDecorator } = form;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { tagSourceList, sourceTable, sourceColumn } = dataSource;

        let publishStatus = TAG_PUBLISH_STATUS[editData.publishStatus] === '已发布';
        let titleName = (editData.name ? '编辑' : '新建') + TAG_TYPE[editData.type];

    	return (
    		<Modal
	            title={titleName}
	            wrapClassName="ruleTagModal"
	            width={'50%'}
	            visible={visible}
	            maskClosable={false}
	            okText="保存"
	            cancelText="取消"
	            onOk={this.props.saveTag.bind(this, form)}
	            onCancel={this.props.cancel.bind(this, form)}
	        >
	            <Form>
	                <FormItem {...formItemLayout} label="标签名称">
	                    {
	                        getFieldDecorator('name', {
	                            rules: [{ 
	                                required: true, 
	                                message: '标签名称不可为空' 
	                            }, { 
	                                max: 20,
	                                message: "最大字数不能超过20" 
	                            }, { 
	                                pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), 
	                                message: '名称只能以字母，数字，下划线组成' 
	                            }], 
	                            initialValue: editData.name
	                        })(
	                            <Input disabled={publishStatus} />
	                        )
	                    }
	                </FormItem>
	                <FormItem {...formItemLayout} label="标签描述">
	                    {
	                        getFieldDecorator('tagDesc', {
	                            rules: [], 
	                            initialValue: editData.tagDesc
	                        })(
	                            <TextArea 
	                                placeholder="标签描述" 
	                                autosize={{ minRows: 2, maxRows: 6 }} 
	                            />
	                        )
	                    }
	                </FormItem>
	                <FormItem {...formItemLayout} label="标签类目">
	                    {
	                        getFieldDecorator('catalogueId', {
	                            rules: [{ 
	                                required: true, 
	                                message: '标签类目不可为空' 
	                            }], 
	                            initialValue: editData.catalogueId
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
	                <FormItem {...formItemLayout} label="值域">
	                    {
	                        getFieldDecorator('tagRange', {
	                            rules: [{ 
	                                required: true, 
	                                message: '值域不可为空' 
	                            }], 
	                            initialValue: editData.tagRange
	                        })(
	                            <TextArea 
	                                placeholder="值域" 
	                                autosize={{ minRows: 2, maxRows: 6 }} 
	                            />
	                        )
	                    }
	                </FormItem>
	                <FormItem {...formItemLayout} label="目标数据库">
	                    {
	                        getFieldDecorator('dataSourceId', {
	                            rules: [{ 
	                                required: true, 
	                                message: '目标数据库不可为空' 
	                            }], 
	                            initialValue: editData.dataSourceId ? editData.dataSourceId.toString() : undefined
	                        })(
	                            <Select
	                                showSearch
	                                optionFilterProp="title"
	                                placeholder="选择目标数据库"
	                                disabled={publishStatus}
	                                onChange={this.onSourceChange}>
	                                {
	                                    tagSourceList.map((source) => {
	                                        let title = `${source.dataName}（${source.sourceTypeValue}）`;
	                                        return <Option 
	                                            key={source.id} 
	                                            value={source.id.toString()}
	                                            title={title}>
	                                            {title}
	                                        </Option>
	                                    })
	                                }
	                            </Select>
	                        )
	                    }
	                </FormItem>
	                {
	                	editData.type === 1 ?
	                	<div>
			                <FormItem {...formItemLayout} label="来源表">
		                        {
		                            getFieldDecorator('originTable', {
		                                rules: [{ 
		                                    required: true, 
		                                    message: '请选择来源表' 
		                                }], 
		                                initialValue: editData.originTable
		                            })(
		                                <Select
		                                    showSearch
		                                    placeholder="选择来源表"
		                                    disabled={publishStatus}
		                                    onChange={this.onSourceTableChange}>
		                                    {
		                                        sourceTable.map((tableName) => {
		                                            return <Option 
		                                                key={tableName} 
		                                                value={tableName}>
		                                                {tableName}
		                                            </Option>
		                                        })
		                                    }
		                                </Select>
		                            )
		                        }
		                    </FormItem>
		                    <FormItem {...formItemLayout} label="来源列">
		                        {
		                            getFieldDecorator('originColumn', {
		                                rules: [{ 
		                                    required: true, 
		                                    message: '请选择来源列' 
		                                }], 
		                                initialValue: editData.originColumn
		                            })(
		                                <Select
		                                    showSearch
		                                    disabled={publishStatus}
		                                    placeholder="选择来源列"
		                                    onChange={this.onUserSourceChange}>
		                                    {
		                                        sourceColumn.map((item) => {
		                                            return <Option 
		                                                key={item.key}
		                                                value={item.key}>
		                                                {item.key}
		                                            </Option>
		                                        })
		                                    }
		                                </Select>
		                            )
		                        }
		                    </FormItem>
		                    <FormItem {...formItemLayout} label="识别列ID">
		                        {
		                            getFieldDecorator('identityColumn', {
		                                rules: [{ 
		                                    required: true, 
		                                    message: '识别列ID不可为空' 
		                                }], 
		                                initialValue: editData.identityColumn
		                            })(
		                                <Select
		                                    showSearch
		                                    placeholder="选择识别列ID"
		                                    disabled={publishStatus}>
		                                    {
		                                        sourceColumn.map((item) => {
		                                            return <Option 
		                                                key={item.key}
		                                                value={item.key}>
		                                                {item.key}
		                                            </Option>
		                                        })
		                                    }
		                                </Select>
		                            )
		                        }
		                    </FormItem>
	                    </div>
	                    :
	                    <div>
			                <FormItem {...formItemLayout} label="来源表">
			                    {
			                        getFieldDecorator('originTable', {
			                            rules: [{ 
			                                required: true, 
			                                message: '来源表不可为空' 
			                            }], 
			                            initialValue: editData.originTable
			                        })(
			                            <Input disabled={publishStatus} />
			                        )
			                    }
			                </FormItem>
			                <FormItem {...formItemLayout} label="识别列ID">
			                    {
			                        getFieldDecorator('identityColumn', {
			                            rules: [{ 
			                                required: true, 
			                                message: '识别列ID不可为空' 
			                            }], 
			                            initialValue: editData.identityColumn
			                        })(
			                            <Input disabled={publishStatus} />
			                        )
			                    }
			                </FormItem>
		                </div>
	                }
	                <FormItem {...formItemLayout} label="识别列类型">
	                    {
	                        getFieldDecorator('identityId', {
	                            rules: [{ 
	                                required: true, 
	                                message: '识别列类型不可为空' 
	                            }], 
	                            initialValue: editData.identityId ? editData.identityId.toString() : undefined
	                        })(
	                            <Select
	                                showSearch
	                                optionFilterProp="title"
	                                placeholder="选择识别列类型"
	                                disabled={publishStatus}>
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
	            </Form>
	        </Modal>
        )
    }
}
EditTagModal = Form.create()(EditTagModal);
		