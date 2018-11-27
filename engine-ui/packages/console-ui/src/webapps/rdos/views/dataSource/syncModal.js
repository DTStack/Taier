import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Table, Button, Tooltip, Icon } from 'antd';

import Api from '../../api';
import { workbenchActions } from '../../store/modules/offlineTask/offlineAction';

class dbSyncHistoryModal extends Component {
    state = {
    	listData: {},
    	showDetail: false,
    	syncDetail: {}
    }

    componentWillReceiveProps (nextProps) {
    	if (nextProps.source.id) {
    		Api.getSyncHistoryList({
    			dataSourceId: nextProps.source.id,
    			currentPage: 1,
    			pageSize: 5
    		}).then(res => {
    			if (res.code === 1) {
    				this.setState({ listData: res.data });
    			}
    		})
    	}
    }

    showConfigDetail = (id) => {
    	this.setState({ showDetail: true });

    	Api.getSyncDetail({ migrationId: id }).then(res => {
    		if (res.code === 1) {
    			this.setState({ syncDetail: res.data });
    		}
    	})
    }

 	// table设置
    initColumns = () => {
        return [{
            title: '配置时间',
            dataIndex: 'gmtCreateFormat',
            key: 'gmtCreateFormat',
            width: '30%'
        }, {
            title: '配置表数量',
            dataIndex: 'taskCount',
            key: 'taskCount',
            width: '30%'
        }, {
            title: '配置人',
            dataIndex: 'createUserName',
            key: 'createUserName',
            width: '30%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                return (
                    <a onClick={this.showConfigDetail.bind(this, record.id)}>
                        查看详情
                    </a>
                );
            }
        }];
    }

    // table设置
    initDbTableColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '40%'
        }, {
            title: 'DTinsight.IDE',
            dataIndex: 'ideTableName',
            key: 'ideTableName',
            width: '40%'
        }, {
            title: '任务状态',
            width: '20%',
            render: (text, record) => {
            	if (record.status) {
                    return record.status === 1
                        ? <div>
                            <span className="m-r-8">
	                        <Icon type="check-circle" style={{ color: 'green', marginRight: 8 }} />
	                        成功
                            </span>
                            <a className="m-l-8" onClick={this.checkTask.bind(this, record.taskId)}>
	                        查看任务
                            </a>
                        </div>
                        : <div>
                    	<span className="m-r-8">
	                        <Icon type="close-circle" style={{ color: 'red', marginRight: 8 }} />
	                        <Tooltip overlayClassName="sync-tooltip" placement="bottom" title={record.report}>
	                        	失败
	                        </Tooltip>
                            </span>
                            <a className="m-l-8" onClick={this.checkTask.bind(this, record.taskId)}>
	                        查看任务
                            </a>
                        </div>
                }
            }
        }];
    }

    checkTask = (taskId) => {
    	this.props.goToTaskDev(taskId);
    }

    back = () => {
    	this.setState({
    		showDetail: false,
    		syncDetail: {}
    	});
    }

    renderContent = () => {
    	const { showDetail, listData, syncDetail } = this.state;
    	const { source } = this.props;

    	if (!showDetail) {
 			return (
 				<div className="sync-list">
	 				<h2>{source.dataName}</h2>

		            <Table
	                    rowKey="id"
	                    className="m-table"
	                    style={{ marginTop: 10 }}
	                    columns={this.initColumns()}
	                    pagination={false}
	                    dataSource={listData.data}
	                />
                </div>
 			)
    	} else {
    		let scheduleConf = syncDetail.scheduleConf ? JSON.parse(syncDetail.scheduleConf) : {};

    		return (
    			<div className="sync-detail">
	            	<Icon
	            		type="left-circle"
	            		style={{ cursor: 'pointer', fontSize: 14 }}
	            		onClick={this.back} /> 返回

		            <Table
	                    rowKey="id"
	                    className="m-table m-v-10"
	                    columns={this.initDbTableColumns()}
	                    pagination={false}
	                    dataSource={syncDetail.migrationTasks}
	                />

	                <p>
	                	生效日期：{`${scheduleConf.beginDate} 到 ${scheduleConf.endDate}`}
	                </p>
	                <p>
	                	调度周期：天
	                </p>
	                <p>
	                	选择时间：{scheduleConf.hour < 10 ? `0${scheduleConf.hour} : 00` : `${scheduleConf.hour} : 00`}
	                </p>
	                <p>
	                	同步方式：{syncDetail.syncType === 1 ? '增量' : '全量'}
	                </p>
	                {
	                	syncDetail.syncType === 1 &&
	                	<p>
	                		根据日期字段：{syncDetail.timeFieldIdentifier}
	                	</p>
	                }
	                <p>
	                	并发配置：{syncDetail.parallelType === 1 ? '分批上传' : '整批上传'}
	                </p>
	                {
	                	syncDetail.parallelType === 1 &&
	                	<p>
	                		从启动时间开始，每{syncDetail.parallelConfig.hourTime}小时同步{syncDetail.parallelConfig.tableNum}张表
	                	</p>
	                }
	            </div>
    		)
    	}
    }

    closeModal = () => {
    	this.props.cancel();

    	this.setState({
    		syncDetail: {},
    		showDetail: false
    	});
    }

    render () {
    	const { visible } = this.props;

    	return (
    		<Modal
	            title="整库同步配置历史"
	            wrapClassName="sync-history-modal"
	            width={'50%'}
	            visible={visible}
	            maskClosable={false}
	            onCancel={this.closeModal}
	            footer={<Button
	            	key="back"
	            	type="primary"
	            	onClick={this.closeModal}>
	            	关闭
	            </Button>}
	        >
                {
                	this.renderContent()
                }
	        </Modal>
        )
    }
}

export default connect((state) => {
    return {
        project: state.project,
        projectUsers: state.projectUsers
    }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})(dbSyncHistoryModal)
