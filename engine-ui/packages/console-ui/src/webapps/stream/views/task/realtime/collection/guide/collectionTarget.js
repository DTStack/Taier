
import React from 'react';
import { get } from 'lodash';

import {
    Form, Select,
    Button, Input, Radio
} from 'antd';

import ajax from '../../../../../api/index'
import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT } from '../../../../../comm/const'
import { isSupportedTargetSource, isKafka } from '../../../../../comm'
import HelpDoc from '../../../../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

function getSourceInitialField (sourceType) {
    const initialFields = { type: sourceType };
    switch (sourceType) {
        case DATA_SOURCE.HDFS: {
            initialFields.fileType = 'orc';
            initialFields.fieldDelimiter = ',';
            initialFields.encoding = 'utf-8';
            initialFields.writeMode = 'APPEND';
            return initialFields;
        }
    }
    return initialFields;
}

class CollectionTarget extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            topicList: []
        }
    }

    componentDidMount () {
        const { collectionData } = this.props;
        const { targetMap = {} } = collectionData;
        if (targetMap.sourceId && isKafka(targetMap.type)) {
            this.getTopicType(targetMap.sourceId)
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { collectionData } = nextProps;
        const { targetMap = {} } = collectionData;
        const { collectionData: oldCol } = this.props;
        const { targetMap: oldTarget } = oldCol;
        if (targetMap.sourceId && oldTarget.sourceId != targetMap.sourceId && isKafka(targetMap.type)) {
            this.getTopicType(targetMap.sourceId)
        }
    }

    onFormValuesChange = () => {
        const { updateCurrentPage } = this.props;
        setTimeout(() => {
            this._form.validateFields(null, {}, (err, values) => {
                let invalidSubmit = false;
                if (err) {
                    invalidSubmit = true;
                }
                updateCurrentPage({
                    invalidSubmit
                });
            });
        }, 200)
    }

    prev () {
        this.props.navtoStep(0)
    }

    next () {
        this._form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.props.navtoStep(2)
            }
        })
    }

    getTopicType (sourceId) {
        ajax.getTopicType({
            sourceId
        }).then((res) => {
            if (res.data) {
                this.setState({
                    topicList: res.data
                })
            }
        })
    }

    render () {
        const { topicList } = this.state;
        return (
            <div>
                <WrapCollectionTargetForm ref={(f) => { this._form = f }} onFormValuesChange={this.onFormValuesChange} topicList={topicList} {...this.props} />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                        <Button type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionTargetForm extends React.Component {
    onSelectSource = (value, option) => {
        const sourceType = option.props.data.type;
        const initialFields = getSourceInitialField(sourceType);
        /**
         * sourceId 改变,则清空表
         */
        let clearTargetData = false;
        this.props.updateTargetMap(initialFields, clearTargetData);
    }

    dynamicRender () {
        const { collectionData, topicList } = this.props;
        const { isEdit, targetMap = {} } = collectionData;
        const { getFieldDecorator } = this.props.form;
        if (!targetMap) return [];

        switch (targetMap.type) {
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10:
            case DATA_SOURCE.KAFKA : {
                return (
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            rules: [{
                                required: true, message: '请选择topic'
                            }]
                        })(
                            <Select
                                disabled={isEdit}
                                style={{ width: '100%' }}
                                placeholder="请选择topic"

                            >
                                {topicList.map(
                                    (topic) => {
                                        return <Option key={`${topic}`} value={topic}>
                                            {topic}
                                        </Option>
                                    }
                                )}
                            </Select>
                        )}
                    </FormItem>
                )
            }
            case DATA_SOURCE.HDFS: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [{
                                required: true
                            }],
                            initialValue: get(targetMap, 'path', '')
                        })(
                            <Input placeholder="例如: /app/batch"/>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件名"
                        key="fileName"
                    >
                        {getFieldDecorator('fileName', {
                            rules: [{
                                required: true
                            }],
                            initialValue: get(targetMap, 'fileName', '')
                        })(
                            <Input placeholder="文件名"/>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件类型"
                        key="fileType"
                    >
                        {getFieldDecorator('fileType', {
                            rules: [{
                                required: true
                            }],
                            initialValue: get(targetMap, 'fileType', 'orc')
                        })(
                            <Select>
                                <Option value="orc">orc</Option>
                                <Option value="text">text</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [],
                            initialValue: get(targetMap, 'fieldDelimiter', undefined)
                        })(
                            <Input
                                /* eslint-disable-next-line */
                                placeholder="例如: 目标为hive则 分隔符为\001"/>
                        )}
                        <HelpDoc doc="splitCharacter" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="编码"
                        key="encoding"
                    >
                        {getFieldDecorator('encoding', {
                            rules: [{
                                required: true
                            }],
                            initialValue: get(targetMap, 'encoding', undefined)
                        })(
                            <Select>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        className="txt-left"
                        key="writeMode-hdfs"
                    >
                        {getFieldDecorator('writeMode', {
                            rules: [{
                                required: true
                            }],
                            initialValue: get(targetMap, 'writeMode', 'APPEND')
                        })(
                            <RadioGroup>
                                <Radio disabled value="NONCONFLICT" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="APPEND" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
            }
            default: {
                return null;
            }
        }
    }

    render () {
        const { collectionData } = this.props;
        const { dataSourceList = [], isEdit, sourceMap = {} } = collectionData;
        const { getFieldDecorator } = this.props.form;
        const disableOption = (targetSourceType) => {
            // 源类型为Kafka时，目标仅能选择HDFS类型
            return (sourceMap.type === DATA_SOURCE.KAFKA_09 || sourceMap.type === DATA_SOURCE.KAFKA_10) && targetSourceType !== DATA_SOURCE.HDFS;
        }
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }]
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                onSelect={this.onSelectSource}
                                style={{ width: '100%' }}
                                allowClear
                            >
                                {dataSourceList.map((item) => {
                                    if (!isSupportedTargetSource(item.type)) {
                                        return null
                                    }
                                    return <Option
                                        key={item.id}
                                        data={item}
                                        value={item.id}
                                        disabled={disableOption(item.type)}
                                    >
                                        {item.dataName}({DATA_SOURCE_TEXT[item.type]})
                                    </Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    {this.dynamicRender()}
                </Form>
            </div>
        )
    }
}

const WrapCollectionTargetForm = Form.create({
    onValuesChange (props, fields) {
        props.updateTargetMap(fields, false);
        if (props.onFormValuesChange) {
            props.onFormValuesChange(props, fields);
        }
    },
    mapPropsToFields (props) {
        const { collectionData } = props;
        const targetMap = collectionData.targetMap;
        if (!targetMap) return {};

        return {
            sourceId: {
                value: targetMap.sourceId
            },
            topic: {
                value: targetMap.topic
            }
        }
    }
})(CollectionTargetForm);

export default CollectionTarget;
