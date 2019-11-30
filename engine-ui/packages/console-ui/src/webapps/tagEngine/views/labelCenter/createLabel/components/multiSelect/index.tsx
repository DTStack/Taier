import * as React from 'react';
import { Input, Col, Row, Select, Icon, Tooltip, Modal } from 'antd';
import { API } from '../../../../../api/apiMap';
import './style.scss';
const { Option } = Select;
const { TextArea } = Input;
interface IProps {
    tip?: string;
    tagId: string | number;
    type?: string;
    value?: any;
    data?: any[];
    onChangeData?: any;
}

interface IState {
    visible: boolean;
    atomTagValueList: any[];
    textArea: string;
}

export default class MultiSelect extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }
    state: IState = {
        visible: false,
        atomTagValueList: [],
        textArea: ''
    };
    componentDidMount () {
        const { tagId, type } = this.props;
        if (tagId && type != 'number') {
            this.getAtomTagValueList(tagId)
        }
    }
    componentDidUpdate (preProps) {
        const { tagId, type } = this.props;
        if(tagId!=preProps.tagId){
            this.setState({
                atomTagValueList: [],
                textArea: ''
            })
        }
        if (tagId && type != 'number' && tagId != preProps.tagId) {
            this.getAtomTagValueList(tagId)
        }
    }
    getAtomTagValueList = (tagId) => { // 获取原子值标签列表
        API.getAtomTagValueList({
            tagId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    atomTagValueList: data
                })
            }
        })
    }
    onChangeTags = (value) => {
        this.props.onChangeData({ values: value })
    }
    onHandleEdit = () => {
        const { data } = this.props
        let textArea = data && data ? data.join('\n') : ''
        this.setState({
            visible: true,
            textArea: textArea
        })
    }
    handleOk = () => {
        const { textArea } = this.state;
        this.props.onChangeData({ values: textArea.split('\n') })
        this.setState({
            visible: false,
            textArea: ''
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false,
            textArea: ''
        })
    }
    onChangeTextArea = (e) => {
        const value = e.target.value;
        this.setState({
            textArea: value
        })
    }
    render () {
        const { tip, data } = this.props
        const { visible, atomTagValueList, textArea } = this.state;
        return (
            <Row className="multi-select-Row" type='flex' gutter={8}>
                <Col>
                    <Select
                        mode="tags"
                        style={{ width: 120 }}
                        value={data}
                        onChange={this.onChangeTags}
                        tokenSeparators={[',']}
                    >
                        {
                            atomTagValueList.map(item => <Option key={item} value={item}>{item}</Option>)
                        }

                    </Select>
                </Col>
                <Col>
                    <Tooltip placement="top" title="点击可以批量复制粘贴">
                        <Icon type="edit" className="edit" onClick={this.onHandleEdit}/>
                    </Tooltip>
                </Col>
                <Col>
                    <Tooltip placement="top" title={tip}>
                        <Icon type="question-circle-o" className="tip"/>
                    </Tooltip>
                </Col>
                <Modal
                    title="等于"
                    visible={visible}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                >
                    <div>注意：按换行符分隔，每行一个值</div>
                    <TextArea rows={14} value={textArea} onChange={this.onChangeTextArea} placeholder="请输入"/>
                </Modal>

            </Row>
        );
    }
}
