/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
<<<<<<< HEAD
* @Last Modified time: 2018-09-25 15:00:44
=======
* @Last Modified time: 2018-09-25 14:24:11
>>>>>>> 930_console
*/
import React, { Component } from 'react';
import { Table, Tabs, Select, Card } from 'antd';
import moment from "moment";
import Api from "../../api/console";
import '../../styles/main.scss';
import TaskDetail from './TaskDeatil';

const PAGE_SIZE = 10;
const Option = Select.Option;
class QueueManage extends Component {
	state = {
		dataSource: [],
		table: {
            pageIndex: 1,
            total: 0,
            loading: true
        },
        activeKey: "1",
        clusterList: [],
        clusterId: undefined,
        // 查看明细所需信息
        detailInfo: {}
	}

	componentDidMount() {
		this.getClusterDetail();
		this.getClusterSelect();
	}
	// 渲染集群
	getClusterDetail() {
		const { table, clusterId } = this.state;
        const { pageIndex } = table;
       	Api.getClusterDetail({
       		currentPage: pageIndex,
            pageSize: PAGE_SIZE,
            clusterId: clusterId
       	}).then((res) => {
       		if (res.code == 1 ) {
       			this.setState({
       				dataSource: res.data.data,
       				table: {
                        ...table,
                        loading: false,
                        total: res.data.totalCount
                    }
       			})
       		} else {
       			this.setState({
       				table: {
       					...table,
       					loading: false
       				}
       			})
       		}
       	}) 
    }
    // 获取集群下拉数据
    getClusterSelect() {
    	return Api.getClusterSelect().then((res) => {
    		if (res.code == 1) {
    			const data = res.data;
    			this.setState({
    				clusterList: data || []
    			})
    			
    		}
    	})
    }
    // 获取集群下拉视图
    getClusterOptionView() {
    	const clusterList = this.state.clusterList;
    	return clusterList.map((item,index) => {
    		return <Option key={item.id} value={item.id}>{item.clusterName}</Option>
    	})
    }
    // option改变
    clusterOptionChange(clusterId) {
    	this.setState({
    		clusterId: clusterId
    	},this.getClusterDetail.bind(this))
    }
    // 页表
    getPagination() {
        const { pageIndex, total } = this.state.table;
        return {
            currentPage: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    // 表格换页
    onTableChange = (page,sorter) => {
    	this.setState({
    		table: {
    			pageIndex: page.current
    		}
    	},
    	() => {
    		this.getClusterDetail();
    	})
    }

	initTableColumns() {
		return [
			{
				title: "引擎",
				dataIndex: "engine",
				render(text,record) {
					return record.engineType;
				}
			},
			{
				title: "group名称",
				dataIndex: "groupName",
				render(text,record) {
					return record.groupName;
				}
			},
			{
				title: "头部等待时长",
				dataIndex: "headWait",
				sorter: true,
				render(text,record) {
          			// return new moment(record.generateTime).format("HH" +"小时" + "mm" + "分钟")
          			return record.generateTime
          		},
          		sorter: (a,b) => a.generateTime - b.generateTime
			},
			{
				title: "总任务数",
				dataIndex: "totalCount",
				render(text,record) {
					return record.groupSize;
				},
				sorter: (a,b) => a.groupSize - b.groupSize
			},
			{
				title: "操作",
				dataIndex: "deal",
				render: (text,record) => {
					return (
						<div>
	                        <a onClick={this.viewDetails.bind(this, record)}>查看明细</a>
                    	</div>
					)
				}
			}
		]
	}
	// 查看明细(需要传入参数 集群,引擎,group) detailInfo
	viewDetails(detailInfo) {
		this.setState({
			detailInfo: detailInfo,
			activeKey: "2"
		})
	}
	// 面板切换
	handleClick(e) {
		this.setState({
			activeKey: e
		})
	}
	render() {
		const columns = this.initTableColumns();
		const { dataSource, table } = this.state;
		const { loading } = table;
		const activeKey = this.state.activeKey;
		return (
			<div className=" api-mine nobackground m-card height-auto m-tabs" style={{marginTop: "20px"}}>
				<Card
					style={{marginTop:"0px"}}
                	className="box-1"
                	noHovering
				>
					<Tabs
			            style={{overflow:"unset"}}
			            animated={false}
			            activeKey={activeKey}
			            onChange={this.handleClick.bind(this)}
			            >
		                <Tabs.TabPane tab="概览" key="1">
		                   <div style={{margin: "20px"}}>
		                   		集群: <Select style={{ width: 150 }}
		                   		placeholder="选择集群"
		                   		onChange={this.clusterOptionChange.bind(this)}
		                   		value={this.state.clusterId} 
		                   		>
		                   			{
		                   				this.getClusterOptionView()
		                   			}
		                   		</Select>
		                   </div>
		                   <Table
		                   	rowKey={(record) => {
		                   		return record.clusterId
		                   	}}
		                   	className="m-table s-table"
		                   	loading={loading}
		                   	columns={columns}
		                   	dataSource={dataSource}
		                   	pagination={this.getPagination()}
		                   	onChange={this.onTableChange}
		                   >
		                   </Table>
		                </Tabs.TabPane>
		                <Tabs.TabPane tab="明细" key="2">
		                    <TaskDetail
		                    	// detailInfo={detailInfo}
		                    >
		                    </TaskDetail>
		                </Tabs.TabPane>
		            </Tabs>
	            </Card>
	        </div>
		)
	}
}	
export default QueueManage;