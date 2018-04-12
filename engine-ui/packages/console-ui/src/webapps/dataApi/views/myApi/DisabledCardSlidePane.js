import React, { Component } from "react";
import { Menu, Card, Table, Input } from "antd"
import SlidePane from "widgets/slidePane";
const TextArea = Input.TextArea;
class DisabledCardSlidePane extends Component {
    state = {
        approvedText:"同意",
        applyText:"申请调用此接口，请批准"
    }
   
  
    

 
    
    render() {
        return (

           
                <SlidePane visible={this.props.slidePaneShow}
                    style={{ right: '-20px', width: '80%', minHeight: '300px' }}
                    onClose={this.props.closeSlidePane}>
                    <h1 className="box-title approved-card-pane-title">详情</h1>
                    <div style={{ paddingLeft: 30,paddingTop:20 }}>
                        <p>管理员已禁用此接口，您可以联系管理员咨询详细情况，管理员信息</p>
                        <p style={{marginTop:20}}>用户名：admin</p>
                        <p>手机号码：18888888888</p>
                        <p>邮箱：admin@163.com</p>
                    </div>

                </SlidePane>
                
           

        )
    }
}
export default DisabledCardSlidePane;