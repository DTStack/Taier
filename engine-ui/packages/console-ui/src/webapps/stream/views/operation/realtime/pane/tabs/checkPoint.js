import React from "react"

import { Table, DatePicker } from "antd"

const { RangePicker } = DatePicker;

class CheckPoint extends React.Component {

    state = {
        pagination: {
            total: 50,
            defaultPageSize: 15,
        }
    }

    componentDidMount(){
        console.log("CheckPoint")
    }
   
    initCheckPointColumns() {
        return [{
            title: 'ID',
            dataIndex: 'id',
        }, {
            title: 'StartTime',
            dataIndex: 'StartTime',
        }]
    }
    getTableTitle=()=>{
        return (
            <div style={{padding:"10px 10px 11px 0px"}}>
                <RangePicker showTime={{disabledSeconds:true,format:"HH:mm"}} style={{width:"250px"}} format="YYYY-MM-DD HH:mm" />
                <span className="checkpoint-overview">
                    <span>checkpoint总数：1个</span>
                    <span>成功：1个</span>
                    <span>失败：1个</span>
                </span>
            </div>
        )
    }
    render() {
        const { pagination } = this.state;
        return (
            <div style={{padding:"0px 20px 20px 25px"}}>
                {this.getTableTitle()}
                <Table
                    className="m-table"
                    columns={this.initCheckPointColumns()}
                    dataSource={[]}
                    pagination={pagination}
                />
            </div>
        )
    }
}

export default CheckPoint;