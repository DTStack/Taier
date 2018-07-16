import React from "react";
import {hashHistory} from "react-router";
import {Card,Table,Button} from "antd";
import moment from "moment";

import Api from "../../api/console"
const PAGE_SIZE=10;

class ClusterManage extends React.Component{
    state={
        dataSource:[],
        table:{
            pageIndex:1,
            total:0,
            loading:true
        }
    }
    componentDidMount(){
        this.getResourceList();
    }
    getResourceList(){
        const {table}=this.state;
        const {pageIndex} = table;
        Api.getClusterList({
            currentPage:pageIndex,
            pageSize:PAGE_SIZE
        })
        .then(
            (res)=>{
                if(res.code==1){
                    this.setState({
                        dataSource:res.data.data,
                        table:{
                            ...table,
                            loading:false,
                            total:res.data.totalCount
                        }
                    })
                }else{
                    this.setState({
                        table:{
                            ...table,
                            loading:false
                        }
                    })
                }
            }
        )
    }
    getPagination(){
        const {pageIndex,total} = this.state.table;
        return {
            current:pageIndex,
            pageSize:PAGE_SIZE,
            total:total
        }
    }
    initTableColumns(){
        return [
            {
                title:"集群名称",
                dataIndex:"clusterName"
            },
            {
                title:"节点数量",
                dataIndex:"totalNode"
            },
            {
                title:"总资源数",
                dataIndex:"totalCore",
                width:"200px",
                render(text,record){
                    const memory=record.totalMemory/1024;
                    const haveDot=Math.floor(memory)!=memory
                    return `${record.totalCore}核 ${haveDot?memory.toFixed(2):memory}GB`
                }
            },
            {
                title:"绑定租户",
                dataIndex:"name"
            },
            {
                title:"修改时间",
                dataIndex:"gmtModified",
                width:"200px",
                render(text){
                    return new moment(text).format("YYYY-MM-DD HH:mm:ss")
                }
            },
            {
                title:"操作",
                dataIndex:"deal",
                render:(text,record)=>{
                    return <a onClick={this.editCluster.bind(this,record)}>修改</a>
                }
            }
        ]
    }
    editCluster(item){
        hashHistory.push({
            pathname:"/console/clusterManage/editCluster",
            state:{
                cluster:item,
                mode:"edit"
            }
        })
    }
    render(){
        const {dataSource,table} = this.state;
        const {loading} = table;
        const columns=this.initTableColumns();

        const cardTitle=(
            <div>多集群管理 <Button type="primary" style={{float:"right",marginTop:"9px"}}>新增集群</Button></div>
        )
        return (
            <div className="contentBox m-card">
            <Card
            noHovering
            title={cardTitle}
            >
            <Table
                rowKey={(record)=>{
                    return record.id
                }}
                className="m-table"
                pagination={this.getPagination()}
                loading={loading}
                dataSource={dataSource}
                columns={columns}
                />
            </Card>       
            </div>
        )
    }
}

export default ClusterManage;