import React from "react";

import {Table,Modal} from "antd";

class RecommentTaskModal extends React.Component{
    state={
        choosetask:[],//选择的任务
    }
    resetState(){
        this.setState({
            choosetask:[]
        })
    }
    onOk(){
        this.resetState();
        this.props.onOk();
    }
    onCancel(){
        this.resetState();
        this.props.onCancel();
    }
    initColumns(){
        return [{
            title: '任务名称',
            dataIndex: 'name',
        }]
    }
    rowSelection(){
        return {
            selectedRowKeys:this.state.choosetask,
            onChange:(selectedRowKeys, selectedRows)=>{
                this.setState({
                    choosetask:selectedRowKeys
                })
            }
        }
    }
    render(){
        const {
            visible,
            taskList
        } = this.props;
        return ( 
            <Modal
                title="推荐上游依赖"
                maskClosable={false}
                visible={visible}

                onCancel={this.onCancel.bind(this)}
                onOk={this.onOk.bind(this)}
            >
            <p style={{margin:"10px 10px"}}>提示：该分析仅基于您已发布过的任务进行分析</p>
            <Table 
                className="m-table"
                columns={this.initColumns()}
                dataSource={taskList}
                rowSelection={this.rowSelection()}
            />
            </Modal>
        )
    }
}
export default RecommentTaskModal;