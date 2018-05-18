import React,{ Component } from 'react';
import { Icon, Button } from 'antd';
import { Link, browserHistory, hashHistory } from 'react-router';

// let timeClock;
class ManageComplete extends Component{

    state={
        time: 3
    }

    countDown = (time) => {
        for (let i = time; i > -1; --i) {
            setTimeout(() => {
                if (i == 0) {
                    const { url, history } = this.props;

                    if (url) {
                        if (history) 
                            browserHistory.push(url)
                        else
                            hashHistory.push(url)
                    } else {
                        browserHistory.go(-1)
                    }
                }
                this.setState({ time: i });
            }, (time - i + 1) * 1000);
        }
    } 

    componentDidMount() {
        this.countDown(this.state.time);
    }


    render() {
        return (
            <div>
                <div className="txt-center">
                    <div style={{fontSize:"30px",marginTop:"40px"}}>
                    <Icon type="check-circle" style={{fontSize:"60px",color:"#00CD00"}} />
                    <br/>
                    操作成功 
                    <br/>
                    </div>
                </div>
                <div className="txt-center" style={{ fontSize: "14px", marginTop: "30px" }}>
                    {this.state.time}秒后自动返回
                </div>
                <div className="txt-center" style={{ padding: "20px 0" }}>
                    {/*<Button size="large" type="primary" onClick={this.props.reDo}>继续创建</Button>*/}
                    <Button size="large" style={{marginLeft:"8px"}}><Link to="/dl/tagConfig">返回</Link></Button>
                </div>
            </div>
        )
    }
}
export default ManageComplete