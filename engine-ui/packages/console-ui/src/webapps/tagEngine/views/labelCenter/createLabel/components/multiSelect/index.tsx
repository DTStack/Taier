import * as React from 'react';
import { Input, Col, Row, Select, Icon, Tooltip, Modal } from 'antd';
import './style.scss';
const { Option } = Select;
const { TextArea } = Input;
interface IProps {
    tip?: string;
}

interface IState {
    visible: boolean;
}

export default class MultiSelect extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        visible: false
    };
    componentDidMount () { }

    onHandleEdit = () => {
        this.setState({
            visible: true
        })
    }
    handleOk = () => {
        this.setState({
            visible: false
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    render () {
        const { tip } = this.props
        const { visible } = this.state;
        
        return (
            <Row className="multi-select-Row" type='flex' gutter={8}>
                <Col>
                    <Select defaultValue="lucy"
                        mode="tags"
                        style={{ width: 120 }}
                        tokenSeparators={[',']}
                    >
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    <Icon type="edit" className="edit" onClick={this.onHandleEdit}/>
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
                    <TextArea rows={14} placeholder="请输入"/>
                </Modal>

            </Row>
        );
    }
}
