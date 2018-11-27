import React, { Component } from 'react'
import { Row, Icon, Button } from 'antd';
let timeClock;
class ManageComplete extends Component {
    state={
        time: 5
    }
    componentDidMount () {
        function djs () {
            timeClock = setTimeout(
                () => {
                    const time = this.state.time - 1;

                    if (time > -1) {
                        this.setState({
                            time: time
                        })
                        djs.call(this);
                    } else {
                        history.back();
                    }
                }, 1000
            )
        }

        djs.call(this);
    }
    componentWillUnmount () {
        clearTimeout(timeClock)
    }
    render () {
        return (
            <div>
                <Row type="flex" justify="center" style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '30px', marginTop: '40px' }}>
                        <Icon type="check-circle" style={{ fontSize: '60px', color: '#00CD00' }} /><br/>
                    操作成功 <br/>
                    </div>
                </Row>
                <Row type="flex" justify="center" style={{ textAlign: 'center', fontSize: '14px', marginTop: '30px' }}>
                    {this.state.time || '0'}秒后自动返回
                </Row>
                <Row type="flex" justify="center" style={{ marginTop: '20px' }}>
                    <Button size="large" type="primary" onClick={this.props.reDo}>继续创建</Button>
                    <Button size="large" style={{ marginLeft: '8px' }} onClick={this.props.cancel}>返回</Button>
                </Row>
            </div>
        )
    }
}
export default ManageComplete
