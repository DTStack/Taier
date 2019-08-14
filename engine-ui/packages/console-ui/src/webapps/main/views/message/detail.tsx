import * as React from 'react'
import { Icon } from 'antd'

import utils from 'utils'

import GoBack from '../../components/go-back'

import Api from '../../api'

class MsgDetail extends React.Component<any, any> {
    state: any = {
        msgInfo: {},
        app: utils.getParameterByName('app')
    }

    componentDidMount () {
        const { msgId } = this.props.router.params

        this.loadMsg(msgId);
    }

    loadMsg = (msgId: any) => {
        Api.getMsgById(this.state.app, { notifyRecordId: msgId }).then((res: any) => {
            const data = res.data || []
            this.setState({
                msgInfo: data
            })

            if (data.readStatus !== 1) { // 如果未读，则标记为已读
                this.markAsRead(msgId);
            }
        })
    }

    markAsRead = (msgId: any) => {
        const { app } = this.state;

        Api.markAsRead(app, {
            notifyRecordIds: [msgId]
        });
    }

    render () {
        const { msgInfo, app } = this.state
        const msgView = this.props.router.location.query.app == 'dataApi' ? (
            <p dangerouslySetInnerHTML={{ __html: msgInfo.content }} >

            </p>
        ) : (
            <p >
                {msgInfo.content}
            </p>
        )

        return (
            <div className="box-1">
                <div className="box-card msg-box">
                    <main>
                        <h1 className="card-title"><GoBack history url={`message?app=${app}`}/> 消息详情 </h1>

                    </main>
                    {msgView}
                    <footer>
                        <span>
                            <Icon type="notification" />
                            发送于 {utils.formatDateTime(msgInfo.gmtCreate)}
                        </span>
                    </footer>
                </div>
            </div>
        )
    }
}

export default MsgDetail
