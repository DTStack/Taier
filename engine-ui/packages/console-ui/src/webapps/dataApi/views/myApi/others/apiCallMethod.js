import React,{Component} from "react";
import { connect } from "react-redux";
import {Link} from "react-router";  
import Content from "../../market/detail/content";
import { apiMarketActions } from '../../../actions/apiMarket';


const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getApiDetail(apiId) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo(apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@connect(mapStateToProps, mapDispatchToProps)
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
        const apiId = this.props.showRecord.apiId;
        this.props.getApiDetail(apiId);
        this.props.getApiExtInfo(apiId);
        
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.showRecord&&this.props.showRecord.apiId!=nextProps.showRecord.apiId){
            if(nextProps.slidePaneShow){
                this.getApiCallUrl(nextProps.showRecord.apiId);
                this.props.getApiDetail(nextProps.showRecord.apiId);
                this.props.getApiExtInfo(nextProps.showRecord.apiId);
            }
            
        }
    }
    render(){
        return (
            <div>
                <p style={{lineHeight:"30px",margin: "10 20"}}>调用URL：{this.state.callUrl}</p>
                <div style={{paddingLeft:30}}>
                    <Content {...this.props} apiId={this.props.showRecord&&this.props.showRecord.apiId}   />
                </div>
            </div>
        )
    }
}
export default ApiCallMethod;