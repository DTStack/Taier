import React, { Component } from "react";
import { Card, Steps,Button } from "antd";
import GoBack from 'main/components/go-back'
import BasicProperties from "./BasicProperties"
import ParamsConfig from "./ParamsConfig"
import Complete from "./Complete"
import { apiMarketActions } from '../../../actions/apiMarket';
import { apiManageActions } from '../../../actions/apiManage';
import {connect} from "react-redux"
const Step = Steps.Step;
const steps = [

    {
        key: "basicProperties",
        title: '基本属性',
        content: BasicProperties,
    },
    {
        key: "paramsConfig",
        title: '参数配置',
        content: ParamsConfig,
    },
    {
        key: "complete",
        title: '完成',
        content: Complete,
    }

]
const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue(pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getDataSourceList(type) {
        return dispatch(apiManageActions.getDataSourceByBaseInfo({ type: type }));
    },
    createApi(params) {
        return dispatch(apiManageActions.createApi(params));
    },
    tablelist(sourceId){
        return dispatch(apiManageActions.tablelist({sourceId}));
    },
    tablecolumn(sourceId,tableName){
        return dispatch(apiManageActions.tablecolumn({sourceId,tableName}));
    },
    previewData(dataSourceId,tableName){
        return dispatch(apiManageActions.previewData({dataSourceId,tableName}));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class NewApi extends Component {
    state = {
        current: 0,
        basicProperties: {},
        paramsConfig: {},
        complete: {}
    }
    basicProperties(data) {
        console.log(data)
        this.setState({
            basicProperties: data||{},
            current:1
        })
    }
    paramsConfig(data) {
        
        const params={}
        params.name=this.state.basicProperties.APIName;
        params.catalogueId=this.state.basicProperties.APIGroup[this.state.basicProperties.APIGroup.length-1];
        params.apiDesc=this.state.basicProperties.APIdescription;
        params.dataSrcId=this.state.basicProperties.dataSource;
        params.tableName=this.state.basicProperties.table;
        params.reqLimit=this.state.basicProperties.callLimit;
        params.respLimit=this.state.basicProperties.backLimit;
        params.inputParam=[];
        params.outputParam=[];
        for(let i in data.inputData){
            let item=data.inputData[i];

            params.inputParam.push({
                fieldName:item.param.key,
                paramName:item.paramName,
                paramType:item.param.type,
                operator:item.operators,
                required:item.isRequired,
                desc:item.instructions
            })
        }
        for(let i in data.outputData){
            let item=data.outputData[i];
            params.outputParam.push({
                fieldName:item.param.key,
                paramName:item.paramName,
                paramType:item.param.type,
                desc:item.instructions
            })
        }
       
        


        this.props.createApi(params)
        .then(
            (res)=>{
                if(res){
                    
                    this.setState({
                        paramsConfig: data||{},
                        current:2
                    })
                }
            }
        )
    }
    complete(data) {
        this.setState({
            complete: data
        })
    }
    reDo(){
        this.setState({
            current:0,
            basicProperties: {},
            paramsConfig: {},
            complete: {}
        })
    }
    next() {
        const { key} = steps[this.state.current];
        if(this.state[key]&&this.state[key].pass){
            const current = this.state.current + 1;
            this.setState({ current });
        }
       
    }
    prev() {
        const current = this.state.current - 1;
        this.setState({ current });
    }
    cancel(){
        this.props.router.goBack();
    }
    render() {
        const { key, content: Content } = steps[this.state.current];

        return (
            <div className="m-card g-datamanage">
                <h1 className="box-title"> <GoBack></GoBack> 新建API</h1>
                <Card
                    style={{ padding: "20px" }}
                    className="box-2"
                    noHovering
                >

                    <Steps current={this.state.current}>
                        <Step title="基本属性" />
                        <Step title="参数配置" />
                        <Step title="完成" />
                    </Steps>
                    
                        <Content 
                        dataSourceId={this.state.basicProperties.dataSource} 
                        tableId={this.state.basicProperties.table} 
                        {...this.props} 
                        {...this.state[key]} 
                        reDo={this.reDo.bind(this)} 
                        prev={this.prev.bind(this)} 
                        cancel={this.cancel.bind(this)} 
                        dataChange={this[key].bind(this)}></Content>
                    
                   
                </Card>
            </div>
        )
    }
}
export default NewApi;