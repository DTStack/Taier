import React, { Component } from "react";
import { Menu, Card, Table, Input } from "antd"

class NewApiDataSourceTable extends Component {
    state = {
        pageIndex: 1,
        loading:false
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            sortedInfo:sorter,
            loading:true
        });
        
        setTimeout(
            ()=>{
                this.setState({
                    loading:false
                })
            },1000
        )
    }
    initColumns() {
        const columns=this.props.data.columnList;
        if(!columns){
            return null;
        }
        let arr=[];
        for(let i=0;i<columns.length;i++){
            arr.push({
                key:i,
                dataIndex:i,
                title:columns[i],
                width:"100px"
            })
        }
        console.log(arr);
        return arr;
    }
    getSource() {
        const dataList=this.props.data.dataList;
        if(!dataList){
            return null;
        }
        let arr=[];
        for(let i=0;i<dataList.length;i++){
            let dic={
                
            };
            let item=dataList[i];
            for(let j=0;j<item.length;j++){
                dic[j]=item[j]
            }

            arr.push(dic);
            
           
        }
        console.log(arr);
        return arr;
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: 30,
        }
    }
    getScroll(){
        const max=120;
        const init=100;
        if(this.props.data.columnList){
            let x=this.props.data.columnList.length;
            if(x<5){
                return init+"%";
            }
            x=init+x*2;
            return (x>max?max:x)+"%";
        }
        return init+"%";
    }

    render() {
        return (


                <Table
                rowKey="0"
                loading={this.props.loading}
                    className="m-table monitor-table"
                    columns={this.initColumns()}
                    loading={this.state.loading}
                    pagination={false}
                    dataSource={this.getSource()}
                    onChange={this.onTableChange}
                    scroll={{x:this.getScroll(),y:400}}
                />

        )
    }
}
export default NewApiDataSourceTable;