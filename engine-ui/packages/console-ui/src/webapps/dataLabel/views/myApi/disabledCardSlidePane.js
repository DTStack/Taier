import React, { Component } from "react";
import { Menu, Card, Table, Input } from "antd"
import SlidePane from "widgets/slidePane";
const TextArea = Input.TextArea;
class DisabledCardSlidePane extends Component {
    state = {
        phone: "",
        dtuicUserId: "",
        email: ""
    }


    componentDidMount() {

        this.getApiCreatorInfo();

    }
    componentWillReceiveProps(nextProps) {
        if (
            (this.props.showRecord && this.props.showRecord.tagId !== nextProps.showRecord.tagId)
        ) {
            if (nextProps.slidePaneShow) {
                this.getApiCreatorInfo(nextProps.showRecord.tagId);
            }


        }
    }
    getApiCreatorInfo(tagId) {
        tagId = tagId || this.props.showRecord.tagId;
        if (!tagId) {
            return;
        }
        this.props.getApiCreatorInfo(tagId)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            phone: res.data.phoneNumber,
                            dtuicUserId: res.data.userName,
                            email: res.data.email
                        })
                    }
                }
            )
    }


    render() {
        return (


            <SlidePane visible={this.props.slidePaneShow}
                style={{ right: '-20px', width: '80%', minHeight: '300px' }}
                onClose={this.props.closeSlidePane}>
                <h1 className="box-title approved-card-pane-title">详情</h1>
                <div style={{ paddingLeft: 30, paddingTop: 20 }}>
                    <p>管理员已取消此接口的授权，您可以联系管理员咨询详细情况，管理员信息</p>
                    <p style={{ marginTop: 20 }}>用户名：{this.state.dtuicUserId}</p>
                    <p>手机号码：{this.state.phone}</p>
                    <p>邮箱：{this.state.email}</p>
                </div>

            </SlidePane>



        )
    }
}
export default DisabledCardSlidePane;