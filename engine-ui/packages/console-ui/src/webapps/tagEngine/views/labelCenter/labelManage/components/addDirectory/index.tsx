import * as React from 'react';
import { Modal ,Button,Form,Input} from 'antd';
import './style.scss';

interface IProps {
    visible: boolean
    handleOk: Function
    handleCancel: Function
    id: string | number
    form: any
}

interface IState {}

const formItemLayout = {
    labelCol: {
        xs: { span: 4 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 18 },
        sm: { span: 16 }
    }
}
class AddDirectpry extends React.PureComponent<IProps, IState> {
    state: IState = {};
    componentDidMount () {}
    handleCancel =() => {}
    handleOk =() => {}
    render () {
        const {visible,form} = this.props;
        const { getFieldDecorator } = form
        return (
            <Modal
            visible={visible}
            title="Title"
            onOk={this.handleOk}
            onCancel={this.handleCancel}
            footer={[
              <Button key="back" size="large" onClick={this.handleCancel}>取消</Button>,
              <Button key="submit" type="primary" size="large"  onClick={this.handleOk}>
                保存
              </Button>,
            ]}
          >
                       <Form.Item {...formItemLayout}>
                        {getFieldDecorator('username', {
                            rules: [
                                {
                                    required: true,
                                    message: '请输入姓名'
                                },
                                {
                                    max: 20,
                                    message: '最多输入20个字符'
                                },
                                {
                                    pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/,
                                    message: '姓名只能包括汉字，字母、下划线、数字'
                                }
                            ]
                        })(<Input placeholder="请输入姓名、允许汉字" />)}
                    </Form.Item>
          </Modal>
        );
    }
}
export default Form.create<IProps>()(AddDirectpry)