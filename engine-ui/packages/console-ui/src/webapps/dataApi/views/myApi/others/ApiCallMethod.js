import React,{Component} from "react";
import {Link} from "react-router";  

class ApiCallMethod extends Component{
    state={
        callUrl:""
    }
    getApiCallUrl(){
        this.props.getApiCallUrl(this.props.showRecord.apiId)
        .then(
            (res)=>{
                if(res){
                    this.setState({
                        callUrl:res.data
                    })
                }
            }
        );
    }
    componentDidMount(){
        this.getApiCallUrl();
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.showRecord&&this.props.showRecord.apiId!=nextProps.showRecord.apiId){
            this.getApiCallUrl();
        }
    }
    render(){
        return (
            <div style={{paddingLeft:30}}>
                <p style={{lineHeight:"30px"}}>调用URL：{this.state.callUrl}</p>
                <Link to="/api/market">在Api市场中查看</Link>
            </div>
        )
    }
}
export default ApiCallMethod;