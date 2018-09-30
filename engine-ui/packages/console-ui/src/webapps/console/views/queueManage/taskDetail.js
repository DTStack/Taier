/*
* @Author: 12574
* @Date:   2018-09-17 15:22:48
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 17:13:12
*/

import React, { Component } from 'react';
import { Input, Select, Card, Table, Row, Col, Tooltip, Icon, Button, Pagination, message, Spin } from "antd"
import moment from "moment";
import '../../styles/main.scss'
import ViewDetail from '../../components/viewDetail';
import KillTask from '../../components/killTask';
import Reorder from '../../components/reorder';
import Resource from '../../components/resource';
import Api from "../../api/console";
import {TASK_STATE} from '../../consts/index.js';
import utils from 'utils';
const PAGE_SIZE = 10;
// const Search = Input.Search;
const Option = Select.Option;

class TaskDetail extends Component {
	state = {
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: true
        },
        taskList: [],
        computeType: "batch",
        jobName: "",
        engineList: [],
        groupList: [],
        engineType: undefined,
        groupName: undefined,
        clusterName: undefined,
        // 执行顺序
        queueNum:undefined,
        // 剩余资源
        isShowResource: false,
        // 查看详情
        isShowViewDetail: false,
        resource: {},
        // 杀任务
        // 多个任务id
        killIds: [],
        isShowKill: false,
        killResource: {},
        // 顺序调整
        priorityResource: {},
        // 置顶
        // 一键展现此任务所在group下的所有任务
        isShowAll: false,
        isShowReorder: false
    }

    // componentWillReceiveProps(nextProps){
    // 	const {query={}} = this.props;
    // 	const {query:next_query={}}=nextProps;
    // 	if ( query.engineType != next_query.engineType || 
    // 		query.groupName != next_query.groupName) {
    // 		this.setState({
    // 			engineType: next_query.engineType,
    // 			groupName: next_query.groupName
    // 		}, () => {
    // 			this.getDetailTaskList();
    // 		})
    // 	}
    // }
    componentDidMount() {
    	const {query={}} = this.props;
    	this.searchTaskFuzzy();
    	this.getEngineList();
    	this.getGroupList();
    	this.setState({
			engineType: query.engineType,
			groupName: query.groupName
    	},this.getDetailTaskList.bind(this))
    	this.getDetailTaskList();
    	this.searchTaskList();
    }
    // 获取计算类型
    changeComputeValue(value) {
    	this.setState({
    		computeType: value
    	},this.searchTaskFuzzy.bind(this))
    }
    // 获取改变模糊任务值
    changeTaskName(value) {
    	if (!value) {
    		this.setState({
    			dataSource: [],
    			jobName: "",
    			isShowAll: false
    		},this.searchTaskFuzzy.bind(this))
    	}else {
    		this.setState({
    			jobName: value
    		},this.searchTaskFuzzy.bind(this))
    	}
    }
    // 模糊查询任务
    searchTaskFuzzy() {
    	const computeType = this.state.computeType;
    	const jobName = this.state.jobName;
    	this.setState({
    		taskList: []
    	})
    	if (jobName) {
    		return Api.searchTaskFuzzy({
    		computeType: computeType,
    		jobName: jobName
	    	}).then(res => {
	    		if (res.code == 1) {
	    			const data = res.data;
	    			this.setState({
	    				taskList: data
	    			})
	    		}
	    	})
    	}
    }
   	// 渲染任务下拉列表
   	getTaskNameListView() {
   		const taskList = this.state.taskList;
   		return taskList.map((item,index) => {
    		return <Option key={index} value={item}>{item}</Option>
    	})
   	}

   	// 选中某个值
   	changeSelectValue(value) {
   		this.setState({
   			jobName: value,
   			engineType: undefined,
   			groupName: undefined,
   			clusterName: undefined
   		},this.searchTaskList.bind(this))
   	}
   	// 显示更多任务
   	handleGroupClick() {
   		const { moreTask, dataSource } = this.state;
   		this.setState({
   			dataSource: [...dataSource,...moreTask]
   		})
   	}
   	// 根据任务名搜索任务
   	searchTaskList() {
   		const computeType = this.state.computeType;
   		const jobName = this.state.jobName;
   		const { table } = this.state;
        const { pageIndex } = table;
        this.setState({
        	dataSource: []
        })
        if (jobName) {
        	Api.searchTaskList({
   			jobName: jobName,
   			computeType: computeType,
   			pageSize: PAGE_SIZE,
   			currentPage: pageIndex
	   		}).then(res => {
	   			if (res.code == 1) {
					this.setState({
						dataSource: res.data.theJob,
						// 获取执行顺序
						queueNum: res.data,
						// 展示更多任务
						moreTask: res.data.topN,
						table: {
	                        ...table,
	                        loading: false,
	                        total: res.data.queueSize
                   	 	},
                   	 	isShowAll: true
					})
					console.log(res);
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
   		
   	}

   	// 获取集群下拉
   	getClusterListOptionView() {
   		const {clusterList} = this.props;
   		return clusterList.map((item,index) => {
    		return <Option key={item.id} value={item.clusterName}>{item.clusterName}</Option>
    	})
   	}
   	// 改变集群值
   	changeClusterValue(value) {
   		const engineType = this.state.engineType;
   		this.setState({
   			clusterName: value,
   			engineType: engineType,
   			groupName: undefined
   		},this.getGroupList.bind(this))
   	}
   	// 获取引擎下拉数据
   	getEngineList() {
   		return Api.getEngineList().then((res) => {
   			if ( res.code == 1 ) {
   				const data = res.data;
   				this.setState({
   					engineList: data
   				})
   			}
   		})
   	}
   	getEngineListOptionView() {
   		const engineList = this.state.engineList;
   		return engineList.map((item,index) => {
    		return <Option key={index} value={item}>{item}</Option>
    	})
   	}
   	// 改变引擎option值
   	changeEngineValue(value) {
   		const engineType = this.state.engineType;
   		if (!value) {
   			this.setState({
   				dataSource: [],
   				engineType: undefined,
   				groupName:undefined
   			},this.getGroupList.bind(this))
   		}else {
	   		this.setState({
	   			jobName: undefined,
	   			engineType: value,
	   			groupName:undefined
	   		},this.getGroupList.bind(this))
   		}
   		
   	}
   	getGroupList() {
   		const { engineType, clusterName } = this.state;
   		this.setState({
   			groupList:[]
   		})
	   		if(engineType){
	   			return Api.getGroupList({
	   			engineType: engineType,
	   			clusterName: clusterName
	   		})
	   		.then((res) => {
	   			if ( res.code == 1 ) {
	   				const data = res.data;
	   				this.setState({
	   					groupList: data
	   				})
	   			}
	   		})
   		}
   	}
   	// 获取group下拉视图
   	getGroupOptionView() {
   		const groupList = this.state.groupList;
   		return groupList.map((item,index) => {
    		return <Option key={index} value={item}>{item}</Option>
    	})
   	}
   	changeGroupValue(value) {
   		this.setState({
   			groupName: value
   		},this.getDetailTaskList.bind(this))
   	}

	getInitialState() {
		
	}
	// 获取详细任务
	getDetailTaskList() {
		const { engineType, groupName } = this.state;
		const { table } = this.state;
		const { pageIndex } = table;
		this.setState({
        	dataSource: []
        })
		if (engineType && groupName) {
			Api.getViewDetail({
			engineType: engineType,
			groupName: groupName,
			pageSize: PAGE_SIZE,
			currentPage: pageIndex
			// clusterName: 
			}).then((res) => {
				if (res.code == 1) {
					this.setState({
						dataSource: res.data.topN,
						table: {
	                        ...table,
	                        loading: false,
	                        total: res.data.queueSize
                   	 	}
					})
					console.log(res);
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
	}

	// 请求置顶调整接口
	changeJobPriority(record) {
		// 获取集群
		var groupName,clusterName,computeTypeInt;
		const arr = (record.groupName || "").split("_");
		if (arr.length == 1) {
			clusterName = record.groupName
		} else {
			for (var i = 0; i<=arr.length;i++) {
				clusterName = arr[0];
				groupName = arr[1];
			}
		}

		Api.changeJobPriority({
			engineType: record.engineType,
			groupName: record.groupName,
			jobId: record.taskId,
			jobIndex: 1
		}).then((res) => {
			if (res.code == 1) {
				message.success("置顶成功");
				this.getDetailTaskList();
			}
		})
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
	// 表格换页
	onTableChange = (page,sorter) => {
    	this.setState({
    		table: {
    			pageIndex: page.current
    		}
    	},
    	() => {
    		this.getDetailTaskList();
    	})
    }

	initTableColumns() {
		const {queueNum} = this.state;
		const { pageIndex, total } = this.state.table;
		return [
			{
				title: "执行顺序",
				dataIndex: "theJobIdx",
				render(text,record,index) {
					// 执行顺序排列
					if (queueNum) {
						return queueNum.theJobIdx
					}else {
						return ((++index) + PAGE_SIZE*(pageIndex - 1));
					}
				}
			},
			{
				title: "任务名称",
				dataIndex: "taskName",
				render(text,record) {
					return record.jobName;
				}
			},
			{
				title: "状态",
				dataIndex: "status",
				render(text,record) {
					switch(text) {
						case TASK_STATE.UNSUBMIT: 
							return "未提交";
						case TASK_STATE.CREATED: 
							return "已创建";
						case TASK_STATE.SCHEDULED: 
							return "已调度";
						case TASK_STATE.DEPLOYING: 
							return "DEPLOYING";
						case TASK_STATE.RUNNING: 
							return "运行中";
						case TASK_STATE.FINISHED: 
							return "运行完成";
						case TASK_STATE.CANCELLING: 
							return "取消中";
						case TASK_STATE.CANCELED: 
							return "已取消";
						case TASK_STATE.FAILED: 
							return "运行失败";
						case TASK_STATE.SUBMITFAILD: 
							return "提交失败";
						case TASK_STATE.SUBMITTING: 
							return "提交中";
						case TASK_STATE.RESTARTING: 
							return "重启中";
						case TASK_STATE.MANUALSUCCESS: 
							return "MANUALSUCCESS";
						case TASK_STATE.KILLED: 
							return "已停止";
						case TASK_STATE.SUBMITTED: 
							return "已提交";
						case TASK_STATE.NOTFOUND: 
							return "NOTFOUND";
						case TASK_STATE.WAITENGINE: 
							return "WAITENGINE";
						case TASK_STATE.WAITCOMPUTE: 
							return "等待运行";
						case TASK_STATE.FROZEN: 
							return "已冻结";
						case TASK_STATE.ENGINEACCEPTED: 
							return "ENGINEACCEPTED";
						case TASK_STATE.ENGINEDISTRIBUTE: 
							return "ENGINEDISTRIBUTE";
						default:
							return null;
					}
				}
			},
			{
				title: "已等待",
				dataIndex: "waitTime",
				render(text,record) {
					return record.waitTime;
				}
			},
			{
				title: "提交时间",
				dataIndex: "generateTime",
                render(text) {
                    return new moment(text).format("YYYY-MM-DD HH:mm:ss")
                }
			},
			{
				title: "集群",
				dataIndex: "clusterName",
				render(text,record) {
					const arr = (record.groupName || "").split("_");
					if (arr.length == 1) {
						return record.groupName
					} else {
						for( var i = 0; i<=arr.length;i++)
						{
							return arr[0]
						}
					}
					// return record.groupName;
				}
			},
			{
				title: "引擎",
				dataIndex: "engine",
				render(text,record) {
					return record.engineType;
				}
			},
			{
				title: "group",
				dataIndex: "groupName",
				render(text,record) {
					return record.groupName;
				}
			},
			{
				title: "租户",
				dataIndex: "tenement",
				render(text,record) {
					return record.tenantName;
				}
			},
			{
				title: "操作",
				dataIndex: "deal",
				render: (text,record,index) => {
					return (
						<div>
	                        <a onClick={this.viewDetails.bind(this,record)}>查看详情</a>
	                        <span className="ant-divider" ></span>
	                        <a onClick={this.killTask.bind(this,record)}>杀任务</a>
	                        <span className="ant-divider" ></span>
	                        <a onClick={this.sequentialAdjustment.bind(this,record)}>顺序调整</a>
	                        
	                        { ((++index) + PAGE_SIZE*(pageIndex - 1))!==1?(
	                        	<span>
	                        		<span className="ant-divider" ></span>
	                        		<a onClick={this.stickTask.bind(this,record,index)}>置顶</a>
	                        	</span>
	                        	):null}
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
	// 刷新
	handleClickRefresh() {
		// 刷新根据任务搜索
		this.searchTaskList();
		// 刷新根据引擎,group搜索出的任务
		this.getDetailTaskList();
	}


	// 查看详情
	viewDetails(record) {
		this.setState({
			isShowViewDetail: true,
			resource:record
		})
	}
	handleCloseViewDetail() {
		this.setState({
			isShowViewDetail: false
		})
	}
	// 杀任务
	killTask(record) {
		this.setState({
			isShowKill: true,
			killResource: record
		})
	}
	handleCloseKill() {
		this.setState({
			isShowKill: false
		})
	}
	// kill
	killSuccess(killId) {
		this.setState({
			killIds: [...this.state.killIds,killId]
		})
	}
	// 顺序调整
	sequentialAdjustment(record) {
		this.setState({
			isShowReorder: true,
			priorityResource: record
		})
	}
	handleCloseReorder() {
		this.setState({
			isShowReorder: false
		})
	}
	// 置顶
	stickTask(record) {
		this.changeJobPriority(record);
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
		const { isShowResource, isShowViewDetail, isShowKill, isShowReorder, isShowStick } = this.state;
		const columns = this.initTableColumns();
		const { dataSource, table } = this.state;
		const { loading } = table;
		const { resource } = this.state;
		const { killResource, priorityResource } = this.state;
		const isShowAll = this.state.isShowAll ? "inline-block" : "none";
		const style = {
			display: isShowAll
		}
		const {total} = this.state.table;
		const {clusterList} = this.props;
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
						style={{width:"250px"}}
						// notFoundContent="没有搜索到该任务"
						filterOption={false}
						onChange={this.changeTaskName.bind(this)}
						onSelect={this.changeSelectValue.bind(this)}
						allowClear
						// onPressEnter={this.searchTask}
						placeholder="输入任务名称搜索">
						{
							this.getTaskNameListView()
						}
					</Select>
					<span style={style}>查找此任务所在<a onClick={this.handleGroupClick.bind(this)}>group</a>的所有任务</span>
					<div style={{float: "right"}}>
						<Button type="primary" style={{marginRight: "9px"}} onClick={this.handleClickResource.bind(this)}>剩余资源</Button>
						<Button onClick={this.handleClickRefresh.bind(this)}>刷新</Button>
					</div>
				</div>
				<div className="select">
					集群：
					<Select
						placeholder="请选择集群"
						style={{width: "150px",marginRight: "10px"}}
						value={this.state.clusterName}
						onChange={this.changeClusterValue.bind(this)}
						allowClear 
					>
						{this.getClusterListOptionView()}
					</Select>
					引擎：
					<Select
						placeholder="请选择引擎"
						style={{width: "150px",marginRight: "10px"}} 
						onChange={this.changeEngineValue.bind(this)}
						value={this.state.engineType}
						allowClear
					>
						{this.getEngineListOptionView()}
					</Select>
					group：
					<Select
						value={this.state.groupName}
						placeholder="请选择group"
						style={{width: "150px",marginRight: "10px"}}
						onChange={this.changeGroupValue.bind(this)}
						allowClear 
					>
						{this.getGroupOptionView()}
					</Select>
				</div>
				<Table
					rowKey={(record) => {
	                    return record.taskId
	                }}
	                // loading={loading}
					// className="m-table no-card-table q-table"
					className="m-table s-table q-table"
					pagination={this.getPagination()}
					rowClassName={(record, index) => {
						if (this.state.killIds.indexOf(record.taskId) > -1) {
							return "killTask"
						}
					}}
					dataSource={dataSource}
					columns={columns}
					onChange={this.onTableChange}
					footer={() => {
						return (
							<div style={{lineHeight: "40px",paddingLeft: "18px"}}>
								任务总数<span>{total}</span>个
							</div>
						)
					}}
				/>
				<Resource
					visible={isShowResource}
					onCancel={this.handleCloseResource.bind(this)}
					clusterList={clusterList}
				/>
				<ViewDetail
					visible={isShowViewDetail}
					onCancel={this.handleCloseViewDetail.bind(this)}
					resource={resource}
				/>
				<KillTask
					visible={isShowKill}
					onCancel={this.handleCloseKill.bind(this)}
					killResource={killResource}
					killSuccess={this.killSuccess.bind(this)}
				/>
				<Reorder
					visible={isShowReorder}
					onCancel={this.handleCloseReorder.bind(this)}
					priorityResource={priorityResource}
					autoRefresh={this.getDetailTaskList.bind(this)}
				/>
			</div>	
		)
	}
}
export default TaskDetail;