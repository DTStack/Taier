import React, { Component } from 'react';
import { Input } from 'antd'
import SlidePane from 'widgets/slidePane';
import utils from 'utils'
const TextArea = Input.TextArea;
class ApprovedSlidePane extends Component {
    state = {

    }

    render () {
        return (

            <SlidePane visible={this.props.slidePaneShow}
                style={{ right: '0px', width: '80%', minHeight: '600px', height: '100%' }}
                onClose={this.props.closeSlidePane}>
                <h1 className="box-title approved-card-pane-title">审批详情</h1>
                <div style={{ paddingLeft: 30, marginBottom: 30 }}>
                    <p className="before-title-pane textarea-p" data-title="申请说明：">
                        <TextArea disabled value={this.props.showRecord && this.props.showRecord.applyContent} style={{ width: 200, color: '#999', fontSize: 14 }} rows={4} />
                    </p>
                    <p style={{ paddingLeft: 70 }}>
                        {utils.formatDateTime(this.props.showRecord && this.props.showRecord.applyTime)}
                    </p>

                    {this.props.isApproved ? (
                        <div>
                            <p className="before-title-pane textarea-p" data-title="审批说明：">
                                <TextArea disabled value={this.props.showRecord && this.props.showRecord.replyContent} style={{ width: 200, color: '#999', fontSize: 14 }} rows={4} />
                            </p>
                            <p style={{ paddingLeft: 70 }}>
                                {utils.formatDateTime(this.props.showRecord && this.props.showRecord.replyTime)}
                            </p>
                        </div>
                    ) : null}
                </div>
            </SlidePane>

        )
    }
}
export default ApprovedSlidePane;
