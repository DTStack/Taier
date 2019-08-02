import * as React from 'react';
import { Icon, Button } from 'antd';
import { hashHistory } from 'react-router';

let timeClock: any;
class ManageComplete extends React.Component<any, any> {
    state={
        time: 3
    }

    countDown = (time: any) => {
        timeClock = setInterval(() => {
            time = time - 1;
            this.setState({ time });

            if (time === 0) {
                hashHistory.push('/dl/manage');
            }
        }, 1000);
    }

    componentDidMount () {
        this.countDown(this.state.time);
    }

    componentWillUnmount () {
        clearInterval(timeClock);
    }

    back = () => {
        hashHistory.push('/dl/manage');
    }

    render () {
        return (
            <div>
                <div className="txt-center">
                    <div style={{ fontSize: '30px', marginTop: '40px' }}>
                        <Icon type="check-circle" style={{ fontSize: '60px', color: '#00CD00' }} />
                        <br/>
                    操作成功
                        <br/>
                    </div>
                </div>
                <div className="txt-center" style={{ fontSize: '14px', marginTop: '30px' }}>
                    {this.state.time}秒后自动返回
                </div>
                <div className="txt-center" style={{ padding: '20px 0' }}>
                    {/* <Button size="large" type="primary" onClick={this.props.reDo}>继续创建</Button> */}
                    <Button size="large" style={{ marginLeft: '8px' }}><a onClick={this.back}>返回</a></Button>
                </div>
            </div>
        )
    }
}
export default ManageComplete
