import * as React from 'react';
import { get } from 'lodash';
import { FormComponentProps } from 'antd/lib/form/Form';
import { UploadChangeParam } from 'antd/lib/upload/interface';
import { Form, Input, Tooltip, Icon, Upload, Select, Button, message } from 'antd';

import { formItemLayout, tailFormItemLayout } from '../../../../comm/const';
import { API } from '../../../../api/apiMap';
import GroupAPI from '../../../../api/group';
import { IEntity } from '../../../../model/entity';
import { IGroupType, IGroup } from '../../../../model/group';

interface IProps {
    handSubmit?: (e: any) => void;
    mode: 'edit' | 'create';
    formData?: IGroup;
    router?: any;
};

/**
 * 群组状态
 */
enum GROUP_STATUS {
    VALID = 2,
    SAVE = 3
}

interface IState {
    options: any[];
    entities: IEntity[];
    groupStatus: GROUP_STATUS;
    entityAttrs: any[];
}

const FormItem = Form.Item;
const Option = Select.Option;

class GroupUpload extends React.Component<IProps & FormComponentProps, IState> {
    constructor (props: any) {
        super(props);
    }

    private _responseData: any;
    private _validResult: any;

    state: IState = {
        options: [],
        entities: [],
        groupStatus: GROUP_STATUS.VALID,
        entityAttrs: []
    }

    componentDidMount () {
        this.loadEntityAttr();
        this.loadEntities();
    }

    loadEntityAttr = async () => {
        const { router } = this.props;
        const res = await API.selectEntityAttrs({
            entityId: get(router, 'params.entityId', '')
        });
        if (res.code === 1) {
            this.setState({
                options: res.data
            })
        }
    }

    loadEntities = () => {
        API.selectEntity().then(res => {
            const { code, data = [] } = res;
            if (code === 1) {
                this.setState({
                    entities: data
                });
            }
        })
    }

    handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const ctx = this;
        this.props.form.validateFields((err, values) => {
            if (!err) {
                values.groupType = IGroupType.UPLOAD;
                values.taskId = ctx._validResult.taskId;
                this.props.handSubmit(values);
            }
        });
    }

    validResult = async () => {
        const ctx = this;
        const { entityAttrs } = this.state;
        const { router, formData = {} } = this.props;
        if (!this._responseData) {
            message.error('请先上传样本文件！');
            return;
        }
        const res = await GroupAPI.analysisGroup({
            entityId: get(router, 'params.entityId', ''),
            groupId: formData.groupId,
            taskId: -1,
            uploadFileName: this._responseData,
            entityAttrList: entityAttrs
        });
        const { code, data = {} } = res;
        if (code === 1) {
            ctx._validResult = data;
            ctx.setState({
                groupStatus: GROUP_STATUS.SAVE
            })
            message.success('校验成功！')
        } else {
            message.error(data.failMsg)
        }
    }

    onFileUploadChange = ({ file }: UploadChangeParam) => {
        const ctx = this;
        const { status, response = {} } = file;
        if (status === 'done') {
            ctx.setState({
                groupStatus: GROUP_STATUS.VALID
            })
            message.success('上传文件成功！');
            ctx._responseData = response.data;
        } else if (status === 'error') {
            message.error('上传文件失败！');
        }
    }

    onAttrChange = (value, option) => {
        const { entityAttrs } = this.state;
        const newState = entityAttrs.slice();
        const res = newState.find((o) => { return o.entityAttr === value });
        if (!res) {
            newState.push({
                entityAttr: value,
                entityAttrCn: option.props['data-attr']
            })
            this.setState({
                entityAttrs: newState
            })
        }
    }

    onCancel = () => {
        this.props.router.push('/groupAnalyse');
    }

    normFile = (e: any) => {
        console.log('Upload event:', e);
        if (Array.isArray(e)) {
            return e;
        }
        return e && e.fileList;
    }

    render () {
        const { options, entities, groupStatus, entityAttrs } = this.state;
        const { form, mode, formData = {}, router } = this.props;
        const { getFieldDecorator } = form;
        const btnText = mode && mode === 'edit' ? '立即保存' : '立即创建';

        const downloadUrl = GroupAPI.downloadGroupTemplate({
            fileName: form.getFieldValue('groupName'),
            entityAttrList: entityAttrs
        });

        const initialEntityAttrs = entityAttrs && entityAttrs.map(o => o.entityAttr);

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label={(<span>
                        群组名称
                    </span>)}
                >
                    {getFieldDecorator('groupName', {
                        rules: [{
                            required: true, message: '请输入群组名称!'
                        }, {
                            pattern: /^[\w\u4e00-\u9fa5]+$/,
                            message: '群组名称仅支持中文字符!'
                        }, {
                            max: 20,
                            message: '群组名称20字以内的中文字符!'
                        }],
                        initialValue: get(formData, 'groupName', '')
                    })(
                        <Input placeholder="请输入群组中文名称，20字以内的中文字符" />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label={(<span>
                        群组描述
                    </span>)}
                >
                    {getFieldDecorator('groupDesc', {
                        rules: [{
                            max: 20,
                            message: '群组名称20字以内的中文字符!'
                        }],
                        initialValue: get(formData, 'groupDesc', '')
                    })(
                        <Input.TextArea placeholder="请输入群组描述信息，长度限制在20个字符以内" />
                    )}
                </FormItem>
                <FormItem
                    required
                    {...formItemLayout}
                    label="实体"
                >
                    {getFieldDecorator('entityId', {
                        initialValue: get(router, 'params.entityId', '')
                    })(
                        <Select
                            disabled
                            showSearch
                            placeholder="请选择实体"
                            style={{ width: 200 }}
                        >
                            {entities && entities.map((o: IEntity) => {
                                return <Option key={o.id} value={o.id}>{o.entityName}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择匹配维度"
                >
                    {getFieldDecorator('entityAttrList', {
                        rules: [{
                            required: true, message: '请选择匹配维度!'
                        }],
                        initialValue: initialEntityAttrs || []
                    })(
                        <Select
                            showSearch
                            mode="multiple"
                            onSelect={this.onAttrChange}
                            placeholder="请选择匹配维度"
                            style={{ width: 200 }}
                        >
                            {options && options.map((o: any) => {
                                return <Option key={o.entityAttr} value={o.entityAttr} data-attr={o.entityAttrCn}>{o.entityAttrCn}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                {
                    mode === 'edit' ? <React.Fragment>
                        <FormItem
                            {...formItemLayout}
                            label={(<span>
                                样本数量
                            </span>)}
                        >
                            <span>{get(formData, 'groupDataCount', '')}</span>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label={(<span>
                                数据更新模式
                            </span>)}
                        >
                            <span>
                                <Tooltip title="增量更新，对历史数据进行合并、去重">
                                    追加 <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>
                        </FormItem>
                    </React.Fragment> : null
                }
                <FormItem
                    {...formItemLayout}
                    label="上传文件"
                    hasFeedback
                >
                    <div>
                        <Tooltip title="用户选择的实体维度信息生成模板； 选中的匹配维度将作为映射的内容提供下载模版，表头为实体设置映射的属性名称（中文）">
                            <a href={ downloadUrl } download>
                                生成模板并下载 <Icon type="question-circle-o" />
                            </a>
                        </Tooltip>
                    </div>
                    <div className="dropbox" style={{ height: 200 }}>
                        {getFieldDecorator('dragger', {
                            rules: [{
                                required: true, message: '请选择上传文件!'
                            }],
                            valuePropName: 'file',
                            getValueFromEvent: this.normFile
                        })(
                            <Upload.Dragger accept=".csv,.xlsx" onChange={this.onFileUploadChange} name="files" action="/api/v1/group/uploadModule">
                                <p className="ant-upload-drag-icon">
                                    <Icon type="inbox" />
                                </p>
                                <p className="ant-upload-text">点击或将文件拖拽到此处上传</p>
                                <p className="ant-upload-hint">仅支持xlsx，文件大小≤10M</p>
                            </Upload.Dragger>
                        )}
                    </div>
                </FormItem>
                <FormItem
                    style={{ marginTop: 100 }}
                    {...tailFormItemLayout}
                >
                    { groupStatus === GROUP_STATUS.SAVE ? <Button type="primary" style={{ marginRight: 20 }} onClick={this.handleSubmit}>{btnText}</Button> : null }
                    { groupStatus === GROUP_STATUS.VALID ? <Button type="primary" style={{ marginRight: 20 }} onClick={this.validResult}>校验上传结果</Button> : null }
                    <Button onClick={this.onCancel}>取消</Button>
                </FormItem>
            </Form>
        )
    }
}

const GroupUploadForm = Form.create<IProps>()(GroupUpload);

export default GroupUploadForm;
