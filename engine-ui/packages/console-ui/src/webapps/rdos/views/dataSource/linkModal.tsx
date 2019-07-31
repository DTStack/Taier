import * as React from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import { uniqBy } from 'lodash';
import Api from '../../api'
import { formItemLayout } from '../../comm/const';
import { ExtTableCell } from './extDataSourceMsg'

const FormItem = Form.Item;
const Option = Select.Option;

class LinkModal extends React.Component<any, any> {
    state: any = {
        confirmLoading: false,
        targetList: []
    }

    getTargetList (sourceId: any) {
        const { type } = this.props;
        Api.getLinkSourceList({
            dataSourceId: sourceId
        }, type)
            .then(
                (res: any) => {
                    if (res.code == 1) {
                        this.setState({
                            targetList: [].concat(res.data.linkProjectSources).concat([res.data.linkSource]).filter(Boolean)
                        })
                    }
                }
            )
    }

    componentDidMount () {
        if (this.props.sourceData && this.props.sourceData.id) {
            this.getTargetList(this.props.sourceData.id);
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { visible, sourceData } = nextProps
        const { visible: oldVisible } = this.props;
        if (oldVisible != visible && visible) {
            this.getTargetList(sourceData.id);
        }
    }

    onCancel () {
        this.props.form.resetFields();
        this.setState({
            confirmLoading: false
        })
        this.props.onCancel();
    }
    linkSource () {
        const { sourceData, type } = this.props;
        this.props.form.validateFields(null, (err: any, values: any) => {
            if (!err) {
                this.setState({
                    confirmLoading: true
                })
                Api.linkSource({
                    sourceId: sourceData.id,
                    linkSourceId: values.linkSourceId
                }, type)
                    .then(
                        (res: any) => {
                            this.setState({
                                confirmLoading: false
                            })
                            if (res.code == 1) {
                                message.success('操作成功')
                                this.props.form.resetFields();
                                this.props.onOk();
                            }
                        }
                    )
            }
        })
    }
    render () {
        const { confirmLoading, targetList } = this.state;
        const { visible, form, sourceData } = this.props;
        const { getFieldDecorator } = form;
        return <Modal
            title={`映射配置(${sourceData.dataName || ''})`}
            maskClosable={false}
            visible={visible}
            onCancel={this.onCancel.bind(this)}
            onOk={this.linkSource.bind(this)}
            confirmLoading={confirmLoading}
        >
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="本项目"
                >
                    {getFieldDecorator('sourceName', {
                        initialValue: sourceData.dataName
                    })(

                        <Input disabled />

                    )}

                    <ExtTableCell style={{ marginTop: '8px' }} sourceData={sourceData} />
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="发布目标"
                >
                    {getFieldDecorator('linkSourceId', {
                        rules: [{
                            required: true,
                            message: '请选择发布目标数据源'
                        }],
                        initialValue: sourceData.linkSourceId
                    })(

                        <Select style={{ width: '100%' }} placeholder="目标数据源">
                            {uniqBy(targetList, 'dataName').map(
                                (target: any) => {
                                    if (target.type != sourceData.type) {
                                        return null;
                                    }
                                    return <Option key={target.id} value={target.id}>{target.dataName}</Option>
                                }
                            ).filter(Boolean)}
                        </Select>

                    )}
                </FormItem>
            </Form>
        </Modal>
    }
}

const WrapLinkModal = Form.create({})(LinkModal);

export default WrapLinkModal;
