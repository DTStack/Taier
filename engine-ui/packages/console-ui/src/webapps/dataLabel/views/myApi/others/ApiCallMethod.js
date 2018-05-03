import React,{Component} from "react";
import {Link} from "react-router";  

class ApiCallMethod extends Component{
    state={
        callUrl:""
    }
    getApiCallUrl(apiId){
      
        apiId=apiId||this.props.showRecord.apiId;
        this.props.getApiCallUrl(apiId)
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
            if(nextProps.slidePaneShow){
                this.getApiCallUrl(nextProps.showRecord.apiId);
            }
            
        }
    }
    render(){
        const url="/api/market/detail/"+this.props.showRecord.apiId;
        return (
            <div style={{paddingLeft:30}}>
                <p style={{lineHeight:"30px"}}>调用URL：{this.state.callUrl}</p>
                <Link to={url}>在Api市场中查看</Link>
            </div>
        )
    }
}
export default ApiCallMethod;