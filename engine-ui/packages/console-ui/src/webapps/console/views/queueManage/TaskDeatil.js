/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
<<<<<<< HEAD
* @Last Modified time: 2018-09-25 15:00:08
=======
* @Last Modified time: 2018-09-25 13:17:41
>>>>>>> 930_console
*/

import React, { Component } from 'react';
import { Input, Select, Card, Table, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from "antd"
import moment from "moment";
import ViewDetail from '../../components/ViewDetail';
import KillTask from '../../components/KillTask';
import Reorder from '../../components/Reorder';

import Resource from '../../components/Resource'
import Api from "../../api/console";

const PAGE_SIZE = 10;
// const Search = Input.Search;
const Option = Select.Option;

class TaskDeatil extends Component {
	state = {
        dataSource: [
	        {
	        	"executionSequence": 1, "clusterName": "clusterName1", 
	        	"engine": "Spark2.1", "groupName": "groupName1", "tenement": "dtstack租户", 
	        	"taskName": "jobName1", "state": "等待提交1", "submissionTime": "", "beenWaiting": "12分钟"
	        },
	        {
	        	"executionSequence": 2, "clusterName": "clusterName2", 
	        	"engine": "Flink1.4", "groupName": "groupName2", "tenement": "dtstack租户", 
	        	"taskName": "jobName2", "state": "等待提交2", "submissionTime": "", "beenWaiting": "2分钟"
	        },
	        {
	        	"executionSequence": 3, "clusterName": "clusterName1", 
	        	"engine": "Spark2.1", "groupName": "groupName1", "tenement": "dtstack租户", 
	        	"taskName": "jobName1", "state": "等待提交1", "submissionTime": "", "beenWaiting": "12分钟"
	        },
	        {
	        	"executionSequence": 4, "clusterName": "clusterName2", 
	        	"engine": "Flink1.4", "groupName": "groupName2", "tenement": "dtstack租户", 
	        	"taskName": "jobName2", "state": "等待提交2", "submissionTime": "", "beenWaiting": "2分钟"
	        }
		],
        // dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: true
        },
        TaskList: [],
        computeType: "batch",
        jobName: "",
        // 剩余资源
        isShowResource: false,
        // 查看详情
        isShowViewDetail: false,
        // 杀任务
        isShowKill: false,
        // 让点击杀任务背景变红
        isBgc: true,
        // 一键展现此任务所在group下的所有任务
        isShowAll: false,
        isShowReorder: false
    }

    componentDidMount() {
    	this.searchTaskFuzzy();
    }
    // 获取计算类型
    changeComputeValue(value) {
    	this.setState({
    		computeType: value
    	},this.searchTaskFuzzy.bind(this))
    }
    // 获取改变模糊任务值
    changeTaskName(value) {
    	this.setState({
    		jobName: value
    	},this.searchTaskFuzzy.bind(this))
    }
    // 模糊查询任务
    searchTaskFuzzy() {
    	const computeType = this.state.computeType;
    	const jobName = this.state.jobName;
    	return Api.searchTaskFuzzy({
    		computeType: computeType,
    		jobName: jobName
    	}).then(res => {
    		if (res.code == 1) {
    			const data = res.data;
    			this.setState({
    				TaskList: data
    			})
    		}
    	})
    }
   	// 渲染任务下拉列表
   	getTaskNameListView() {
   		const TaskList = this.state.TaskList;
   		return TaskList.map((item,index) => {
    		return <Option value={item}>{item}</Option>
    	})
   	}

   	// 根据任务名搜索任务
   	searchTaskList() {
   		const computeType = this.state.computeType;
   		const jobName = this.state.jobName;
   		const { table } = this.state;
        const { pageIndex } = table;
   		Api.searchTaskList({
   			jobName: jobName,
   			computeType: computeType,
   			pageSize: PAGE_SIZE,
   			currentPage: pageIndex
   		}).then(res => {
   			this.setState({
   				isShowAll: true
   			})
   			// console.log(res);
   		})
   	}


	getInitialState() {
		
	}

	// 获取分页信息
	getPagination() {
		const { pageIndex, total } = this.state.table;
		return {
			currentPage: pageIndex,
			pageSize: PAGE_SIZE,
			total: total
		}
	}
	initTableColumns() {
		return [
			{
				title: "执行顺序",
				dataIndex: "executionSequence"
			},
			{
				title: "任务名称",
				dataIndex: "taskName"
			},
			{
				title: "状态",
				dataIndex: "state"
			},
			{
				title: "已等待",
				dataIndex: "beenWaiting"
			},
			{
				title: "提交时间",
				dataIndex: "submissionTime",
				// render(text) {
    //                 return new moment(text).format("YYYY-MM-DD HH:mm:ss")
    //             },
                render() {
                	return(moment().format('YYYY-MM-DD HH:mm:ss'))
                }
			},
			{
				title: "集群",
				dataIndex: "clusterName"
			},
			{
				title: "引擎",
				dataIndex: "engine"
			},
			{
				title: "队列名称",
				dataIndex: "groupName"
			},
			{
				title: "租户",
				dataIndex: "tenement"
			},
			{
				title: "操作",
				dataIndex: "deal",
				render: (text,record) => {
					return (
						<div>
	                        <a onClick={this.viewDetails.bind(this, record)}>查看详情</a>
	                        <span className="ant-divider" ></span>
	                        <a onClick={this.killTask.bind(this, record)}>杀任务</a>
	                        <span className="ant-divider" ></span>
	                        <a onClick={this.sequentialAdjustment.bind(this, record)}>顺序调整</a>
	                        <span className="ant-divider" ></span>
	                        <a onClick={this.stick.bind(this, record)}>置顶</a>
                    	</div>
					)
				}
			}
		]
	}

	// 剩余资源
	handleClickResource() {
		this.setState({
			isShowResource: true
		})
	}
	handleCloseResource() {
		this.setState({
			isShowResource: false
		})
	}
	// 查看详情
	viewDetails() {
		this.setState({
			isShowViewDetail: true
		})
	}
	handleCloseViewDetail() {
		this.setState({
			isShowViewDetail: false
		})
	}
	// 杀任务
	killTask() {
		this.setState({
			isShowKill: true
		})
	}
	handleCloseKill() {
		this.setState({
			isShowKill: false
		})
	}
	// 顺序调整
	sequentialAdjustment() {
		this.setState({
			isShowReorder: true
		})
	}
	handleCloseReorder() {
		this.setState({
			isShowReorder: false
		})
	}
	// 置顶
	stick() {
		
	}
	// input方法
	searchTask() {
		this.setState({
			isShowAll: true
		})
	}
	// 集群筛选
	onClusterChange() {
		
	}
	render() {
		const { isShowResource, isShowViewDetail, isShowKill, isShowReorder } = this.state;
		const columns = this.initTableColumns();
		const { dataSource, table } = this.state;
		const { loading } = table;
		const isShowAll = this.state.isShowAll ? "inline-block" : "none";
		const style = {
			display: isShowAll
		}
		const isBgc = this.state.isBgc ?"red" : "none";
		const taskBgc = {
			background: isBgc
		}
		return (
			<div>
				<div style={{margin: "20px"}}>
					计算类型: <Select 
						style={{width: "80px",marginRight: "10px"}} 
						value={this.state.computeType}
						onChange={this.changeComputeValue.bind(this)}
					>
						<Option value="batch">离线</Option>
						<Option value="stream">实时</Option>
					</Select>

					<Select className="task-search"
						mode="combobox"
						value={this.state.jobName}
						notFoundContent=""
						filterOption={false}
						onChange={this.changeTaskName.bind(this)}
						onSelect={this.searchTaskList.bind(this)}
						allowClear
						onPressEnter={this.searchTask}
						placeholder="输入任务名称搜索">
						{
							this.getTaskNameListView()
						}
					</Select>
					<span style={style}>查找此任务所在<a>group</a>的所有任务</span>
					<div style={{float: "right"}}>
						<Button type="primary" style={{marginRight: "9px"}} onClick={this.handleClickResource.bind(this)}>剩余资源</Button>
						<Button>刷新</Button>
					</div>
				</div>
				<div className="select">
					集群:
					<Select
						placeholder="请选择集群"
						style={{width: "150px",marginRight: "10px"}} 
					>

					</Select>
					引擎:
					<Select
						placeholder="请选择集群"
						style={{width: "150px",marginRight: "10px"}} 
					>
						
					</Select>
					group:
					<Select
						placeholder="请选择集群"
						style={{width: "150px",marginRight: "10px"}} 
					>
						
					</Select>
				</div>
				<Table
					rowKey={(record) => {
	                    return record.dataIndex
	                }}
	                // loading={loading}
					// className="m-table no-card-table q-table"
					className="m-table s-table q-table"
					pagination={this.getPagination()}
					dataSource={dataSource}
					columns={columns}
					footer={() => {
						return (
							<div style={{lineHeight: "40px",paddingLeft: "18px"}}>
								任务总数<span>12</span>个
							</div>
						)
					}}
				/>
				<Resource
					visible={isShowResource}
					onCancel={this.handleCloseResource.bind(this)}
				/>
				<ViewDetail
					visible={isShowViewDetail}
					onCancel={this.handleCloseViewDetail.bind(this)}
				/>
				<KillTask
					visible={isShowKill}
					onCancel={this.handleCloseKill.bind(this)}
					
				/>
				<Reorder
					visible={isShowReorder}
					onCancel={this.handleCloseReorder.bind(this)}
				/>
			</div>	
		)
	}
}
export default TaskDeatil;