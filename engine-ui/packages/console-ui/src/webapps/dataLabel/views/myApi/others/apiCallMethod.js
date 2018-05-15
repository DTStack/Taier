import React,{Component} from "react";
import {Link} from "react-router";  

class ApiCallMethod extends Component{
    state={
        callUrl:""
    }
    getApiCallUrl(tagId){
      
        tagId=tagId||this.props.showRecord.tagId;
        this.props.getApiCallUrl(tagId)
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
        if(nextProps.showRecord&&this.props.showRecord.tagId!=nextProps.showRecord.tagId){
            if(nextProps.slidePaneShow){
                this.getApiCallUrl(nextProps.showRecord.tagId);
            }
            
        }
    }
    render(){
        const url="/dl/market/detail/"+this.props.showRecord.tagId;
        return (
            <div style={{paddingLeft:30}}>
                <p style={{lineHeight:"30px"}}>调用URL：{this.state.callUrl}</p>
                <Link to={url}>在Api市场中查看</Link>
            </div>
        )
    }
}
export default ApiCallMethod;