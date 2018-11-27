
import React from "react";

import { Form, Select, Button } from "antd";

import ajax from "../../../../../api/index"
import { formItemLayout, DATA_SOURCE_TEXT, DATA_SOURCE } from "../../../../../comm/const"
import { isKafka } from "../../../../../comm"

const FormItem = Form.Item;
const Option = Select.Option;

class CollectionTarget extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            topicList: []
        }
    }

    componentDidMount() {
        const { collectionData } = this.props;
        const { targetMap = {} } = collectionData;
        if (targetMap.sourceId) {
            this.getTopicType(targetMap.sourceId)
        }
    }

    componentWillReceiveProps(nextProps) {
        const { collectionData } = nextProps;
        const { targetMap } = collectionData;
        const { collectionData: old_col } = this.props;
        const { targetMap: old_target } = old_col;
        if (targetMap.sourceId && old_target.sourceId != targetMap.sourceId) {
            this.getTopicType(targetMap.sourceId)
        }
    }

    prev() {
        this.props.navtoStep(0)
    }

    next() {
        this._form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.props.navtoStep(2)
            }
        })
    }

    getTopicType(sourceId) {
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

    render() {
        const { topicList } = this.state;
        return (
            <div>
                <WrapCollectionTargetForm ref={(f) => { this._form = f }} topicList={topicList}  {...this.props} />
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

    render() {
        const { collectionData, topicList } = this.props;
        const { dataSourceList = [], isEdit } = collectionData;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }],
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                style={{ width: "100%" }}
                            >
                                {dataSourceList.map((item) => {
                                    if (!isKafka(item.type)) {
                                        return null
                                    }
                                    return <Option key={item.id} value={item.id}>{item.dataName}({DATA_SOURCE_TEXT[item.type]})</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            rules: [{
                                required: true, message: "请选择topic"
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
                </Form>
            </div>
        )
    }
}

const WrapCollectionTargetForm = Form.create({
    onValuesChange(props, fields) {
        /**
         * sourceId改变,则清空表
         */
        let clear = false;
        if (fields.sourceId != undefined) {
            clear = true
        }
        props.updateTargetMap(fields, clear);
    },
    mapPropsToFields(props) {
        const { collectionData } = props;
        const targetMap = collectionData.targetMap;
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