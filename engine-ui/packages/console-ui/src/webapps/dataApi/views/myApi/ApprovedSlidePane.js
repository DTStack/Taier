import React, { Component } from "react";
import { Menu, Card, Table, Input } from "antd"
import SlidePane from "widgets/slidePane";
import utils from "utils"
const TextArea = Input.TextArea;
class ApprovedSlidePane extends Component {
    state = {
        
    }
   

    
    render() {
        const replyTime=this.props.showRecord&&this.props.showRecord.replyTime;
        let format_replyTime=null;
        if(replyTime){
            format_replyTime=utils.formatDateTime(format_replyTime);
        }
        return (

           
                <SlidePane visible={this.props.slidePaneShow}
                    style={{ right: '-20px', width: '80%', minHeight: '300px' }}
                    onClose={this.props.closeSlidePane}>
                    <h1 className="box-title approved-card-pane-title">审批情况</h1>
                    <div style={{ paddingLeft: 30 }}>
                        <p className="before-title-pane textarea-p" data-title="申请说明：">
                            <TextArea disabled value={this.props.showRecord&&this.props.showRecord.applyContent} style={{ width: 200,color:"#999",fontSize:14 }} rows={4} />
                        </p>
                        <p style={{paddingLeft:70}}>
                        {utils.formatDateTime(this.props.showRecord&&this.props.showRecord.applyTime)}
                        </p>
                        
                    </div>

                </SlidePane>
                
           

        )
    }
}
export default ApprovedSlidePane;