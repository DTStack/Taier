import * as React from 'react';
import { Icon } from 'antd'

export default class StepFour extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            num: 3,
            siv: null
        }
    }
    siv: any;
    componentDidMount () {
        console.log('MOunt')
        this.siv = setInterval(() => {
            console.log(this.state.num)
            this.setState((preState: any) => ({
                num: preState.num - 1
            }), () => {
                if (this.state.num === 0) {
                    clearInterval(this.state.siv);
                    console.log(this.props.tabData.tableItem)
                    this.props.toTableDetail({ databaseId: this.props.tabData.tableItem.databaseId, id: this.props.tabData.tableItem.newTableId });
                }
            })
        }, 1000)
    }
    componentWillUnMount () {
        this.siv && clearInterval(this.siv)
    }
    render () {
        return (
            <div className="step-four-container step-container">
                <Icon className="icon-finished status-icon" style={{ color: '#00A755' }} type="check-circle"/>
                <p className="result">创建成功</p>
                <p className="inter"><span>{this.state.num}秒后自动返回</span></p>
            </div>
        )
    }
}
