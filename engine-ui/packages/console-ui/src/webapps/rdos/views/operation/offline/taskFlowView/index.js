import React, { Component } from 'react'

import {
    Tooltip, Spin,
    Modal, message, Icon,
} from 'antd'

import utils from 'utils'
import Api from '../../../../api'
import MyIcon from '../../../../components/icon'
import { getVertxtStyle } from '../../../../comm'
import { TASK_STATUS } from '../../../../comm/const'
import { taskTypeText, taskStatusText } from '../../../../components/display'
import { TaskInfo } from './taskInfo'
import { LogInfo } from '../taskLog'
import RestartModal from './restartModal'

const Mx = require('public/rdos/mxgraph')({
    mxImageBasePath: 'public/rdos/mxgraph/images',
    mxBasePath: 'public/rdos/mxgraph',
})

const {
    mxGraph,
    mxShape,
    mxConnectionConstraint,
    mxPoint,
    mxPolyline,
    mxEvent,
    mxRubberband,
    mxConstants,
    mxEdgeStyle,
    mxPopupMenu,
    mxPerimeter,
    mxCompactTreeLayout,
    mxGraphView,
    mxText,
} = Mx

const VertexSize = { // vertex大小
    width: 150,
    height: 36,
}

// 遍历树形节点，用新节点替换老节点
export function replaceTreeNode(treeNode, replace) {
    if (treeNode.id === parseInt(replace.id, 10)) {
        replace.jobVOS = treeNode.jobVOS ? [...treeNode.jobVOS] : []
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.jobVOS) {
        const children = treeNode.jobVOS
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

const mockData = {
    "code": 1,
    "message": null,
    "data": {
        "batchTask": {
            "id": 2,
            "gmtCreate": 1532327916000,
            "gmtModified": 1532327924000,
            "isDeleted": 0,
            "tenantId": 1,
            "projectId": 3,
            "name": "aa2hive_dq_data_source",
            "taskType": 2,
            "computeType": 1,
            "engineType": 0,
            "sqlText":
                "eyJqb2IiOiJ7IFwiam9iXCI6e1wiY29udGVudFwiOlt7XCJyZWFkZXJcIjp7XCJuYW1lXCI6XCJteXNxbHJlYWRlclwiLFwicGFyYW1ldGVyXCI6e1wicGFzc3dvcmRcIjpcImFiYzEyM1wiLFwiY29sdW1uVHlwZXNcIjpbXCJqYXZhLmxhbmcuSW50ZWdlclwiLFwiamF2YS5sYW5nLkludGVnZXJcIixcImphdmEubGFuZy5TdHJpbmdcIixcImphdmEubGFuZy5TdHJpbmdcIixcImphdmEubGFuZy5TdHJpbmdcIixcImphdmEubGFuZy5Cb29sZWFuXCIsXCJqYXZhLmxhbmcuQm9vbGVhblwiLFwiamF2YS5zcWwuVGltZXN0YW1wXCIsXCJqYXZhLnNxbC5UaW1lc3RhbXBcIixcImphdmEubGFuZy5JbnRlZ2VyXCIsXCJqYXZhLmxhbmcuSW50ZWdlclwiLFwiamF2YS5sYW5nLkJvb2xlYW5cIl0sXCJjb2x1bW5cIjpbXCJpZFwiLFwidGVuYW50X2lkXCIsXCJkYXRhX25hbWVcIixcImRhdGFfZGVzY1wiLFwiZGF0YV9qc29uXCIsXCJ0eXBlXCIsXCJhY3RpdmVcIixcImdtdF9jcmVhdGVcIixcImdtdF9tb2RpZmllZFwiLFwiY3JlYXRlX3VzZXJfaWRcIixcIm1vZGlmeV91c2VyX2lkXCIsXCJpc19kZWxldGVkXCJdLFwiY29ubmVjdGlvblwiOlt7XCJqZGJjVXJsXCI6W1wiamRiYzpteXNxbDovLzE3Mi4xNi44LjE5OTozMzA2L2RxXCJdLFwidGFibGVcIjpbXCJkcV9kYXRhX3NvdXJjZVwiXX1dLFwidXNlcm5hbWVcIjpcImR0c3RhY2tcIn19LFwid3JpdGVyXCI6e1wicGFyYW1ldGVyXCI6e1wiZmlsZU5hbWVcIjpcInB0PSR7YmRwLnN5c3RlbS5iaXpkYXRlfVwiLFwiY29sdW1uXCI6W3tcIm5hbWVcIjpcImlkXCIsXCJpbmRleFwiOjAsXCJ0eXBlXCI6XCJJTlRcIn0se1wibmFtZVwiOlwidGVuYW50X2lkXCIsXCJpbmRleFwiOjEsXCJ0eXBlXCI6XCJJTlRcIn0se1wibmFtZVwiOlwiZGF0YV9uYW1lXCIsXCJpbmRleFwiOjIsXCJ0eXBlXCI6XCJTVFJJTkdcIn0se1wibmFtZVwiOlwiZGF0YV9kZXNjXCIsXCJpbmRleFwiOjMsXCJ0eXBlXCI6XCJTVFJJTkdcIn0se1wibmFtZVwiOlwiZGF0YV9qc29uXCIsXCJpbmRleFwiOjQsXCJ0eXBlXCI6XCJTVFJJTkdcIn0se1wibmFtZVwiOlwidHlwZVwiLFwiaW5kZXhcIjo1LFwidHlwZVwiOlwiVElOWUlOVFwifSx7XCJuYW1lXCI6XCJhY3RpdmVcIixcImluZGV4XCI6NixcInR5cGVcIjpcIlRJTllJTlRcIn0se1wibmFtZVwiOlwiZ210X2NyZWF0ZVwiLFwiaW5kZXhcIjo3LFwidHlwZVwiOlwiU1RSSU5HXCJ9LHtcIm5hbWVcIjpcImdtdF9tb2RpZmllZFwiLFwiaW5kZXhcIjo4LFwidHlwZVwiOlwiU1RSSU5HXCJ9LHtcIm5hbWVcIjpcImNyZWF0ZV91c2VyX2lkXCIsXCJpbmRleFwiOjksXCJ0eXBlXCI6XCJJTlRcIn0se1wibmFtZVwiOlwibW9kaWZ5X3VzZXJfaWRcIixcImluZGV4XCI6MTAsXCJ0eXBlXCI6XCJJTlRcIn0se1wibmFtZVwiOlwiaXNfZGVsZXRlZFwiLFwiaW5kZXhcIjoxMSxcInR5cGVcIjpcIlRJTllJTlRcIn1dLFwid3JpdGVNb2RlXCI6XCJhcHBlbmRcIixcImZpZWxkRGVsaW1pdGVyXCI6XCJcXHUwMDAxXCIsXCJlbmNvZGluZ1wiOlwidXRmLThcIixcImZ1bGxDb2x1bW5OYW1lXCI6W1wiaWRcIixcInRlbmFudF9pZFwiLFwiZGF0YV9uYW1lXCIsXCJkYXRhX2Rlc2NcIixcImRhdGFfanNvblwiLFwidHlwZVwiLFwiYWN0aXZlXCIsXCJnbXRfY3JlYXRlXCIsXCJnbXRfbW9kaWZpZWRcIixcImNyZWF0ZV91c2VyX2lkXCIsXCJtb2RpZnlfdXNlcl9pZFwiLFwiaXNfZGVsZXRlZFwiXSxcInBhdGhcIjpcImhkZnM6Ly9uczEvdXNlci9oaXZlL3dhcmVob3VzZS9ycnIuZGIvZHFfZGF0YV9zb3VyY2VcIixcInBhcnRpdGlvblwiOlwicHQ9JHtiZHAuc3lzdGVtLmJpemRhdGV9XCIsXCJkZWZhdWx0RlNcIjpcImhkZnM6Ly9uczFcIixcImNvbm5lY3Rpb25cIjpbe1wiamRiY1VybFwiOlwiamRiYzpoaXZlMjovLzE3Mi4xNi44LjE4MjoxMDAwMC9ycnJcIixcInRhYmxlXCI6W1wiZHFfZGF0YV9zb3VyY2VcIl19XSxcImZpbGVUeXBlXCI6XCJvcmNcIixcImZ1bGxDb2x1bW5UeXBlXCI6W1wiaW50XCIsXCJpbnRcIixcInN0cmluZ1wiLFwic3RyaW5nXCIsXCJzdHJpbmdcIixcInRpbnlpbnRcIixcInRpbnlpbnRcIixcInN0cmluZ1wiLFwic3RyaW5nXCIsXCJpbnRcIixcImludFwiLFwidGlueWludFwiXX0sXCJuYW1lXCI6XCJoZGZzd3JpdGVyXCJ9fV0sXCJzZXR0aW5nXCI6e1wic3BlZWRcIjp7XCJjaGFubmVsXCI6MSxcImJ5dGVzXCI6MTA0ODU3Nn0sXCJlcnJvckxpbWl0XCI6e1wicmVjb3JkXCI6MTAwfX19IH0iLCJjcmVhdGVNb2RlbCI6MX0=",
            "taskParams":
                "##sql prefix is only useful for SQL ,mr prefix is only useful for MR ,no prefix is  useful for MR and SQL\r\n\r\n\r\n##The entire SQL job concurrency settings\r\n#sql.env.parallelism=1\r\n\r\n##The entire SQL job max concurrency settings\r\n#sql.max.env.parallelism=1\r\n\r\n\r\n##Sets the maximum time frequency (milliseconds) for the flushing of the\r\n##output buffers. By default the output buffers flush frequently to provide\r\n##low latency and to aid smooth developer experience. Setting the parameter\r\n##can result in three logical modes:\r\n#sql.buffer.timeout.millis=100\r\n\r\n#Time interval between state checkpoints in milliseconds\r\nsql.checkpoint.interval=60000\r\n\r\n#one of EXACTLY_ONCE,AT_LEAST_ONCE , the default val is EXACTLY_ONCE\r\n#sql.checkpoint.mode=EXACTLY_ONCE\r\n\r\n#The checkpoint timeout, in milliseconds,The default timeout of a checkpoint attempt: 10 minutes\r\n#sql.checkpoint.timeout=600000\r\n\r\n#The maximum number of concurrent checkpoint attempts,The default limit of concurrently happening checkpoints: one\r\n#sql.max.concurrent.checkpoints=1\r\n\r\n#Cleanup behaviour for persistent checkpoints. one of true or false\r\n#sql.checkpoint.cleanup.mode=true\r\n\r\n\r\n##The entire MR job concurrency settings \r\n##Only when the program itself does not specify a degree of parallelism parameter takes effect\r\n#mr.job.parallelism=1\r\n",
            "scheduleConf":
                "{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\"}",
            "periodType": null,
            "scheduleStatus": 1,
            "submitStatus": 1,
            "modifyUserId": 1,
            "createUserId": 1,
            "ownerUserId": 1,
            "version": 1,
            "nodePid": 171,
            "taskDesc": "",
            "mainClass": "",
            "exeArgs": "",
            "createUser": {
                "id": 1,
                "gmtCreate": 1532326548000,
                "gmtModified": 1532326548000,
                "isDeleted": 0,
                "userName": "admin@dtstack.com",
                "phoneNumber": "18825166170",
                "dtuicUserId": 1,
                "email": "admin@dtstack.com",
                "status": 0,
                "defaultProjectId": null
            },
            "modifyUser": null,
            "ownerUser": null,
            "taskPeriodId": 2,
            "taskPeriodType": "天任务",
            "nodePName": null,
            "readWriteLockVO": null,
            "userId": null,
            "lockVersion": null,
            "taskVariables": null,
            "forceUpdate": false,
            "createModel": 0,
            "taskVOS": null,
            "subTaskVOS": null,
            "resourceList": null,
            "taskVersions": null,
            "cron": "0 0 0 * * ?"
        },
        "id": 1,
        "gmtCreate": 1532327933000,
        "gmtModified": 1532327933000,
        "isDeleted": 0,
        "tenantId": null,
        "projectId": null,
        "jobId": "2b5a42ba",
        "jobKey":
            "fillData_P_aa2hive_dq_data_source_2018_07_23ss_2_20180723000000",
        "jobName":
            "P_aa2hive_dq_data_source_2018_07_23ss-aa2hive_dq_data_source-20180723000000",
        "status": 5,
        "taskId": 2,
        "createUserId": 1,
        "type": 1,
        "businessDate": "2018-07-22 ",
        "cycTime": "2018-07-23 00:00:00",
        "execStartTime": null,
        "execEndTime": null,
        "execTime": "10秒",
        "execStartDate": "2018-07-23 14:38:59",
        "execEndDate": "2018-07-23 14:39:09",
        "taskPeriodId": 2,
        "taskPeriodType": "天任务",
        "jobVOS": null,
        "batchEngineJob": {
            "id": 1,
            "gmtCreate": 1532327934000,
            "gmtModified": 1532327949000,
            "isDeleted": 0,
            "status": 5,
            "jobId": "2b5a42ba",
            "engineJobId": "92137cb9230595fd7cb36e1ecdaf95e6",
            "logInfo":
                "{\"jobid\":\"92137cb9230595fd7cb36e1ecdaf95e6\",\"msg_info\":\"submit job is success\"}",
            "engineLog":
                "{\"truncated\":false,\"perf\":\"读取记录数:\\t0\\n读取字节数:\\t21\\n读取速率(B/s):\\t12\\n写入记录数:\\t0\\n写入字节数:\\t25\\n写入速率(B/s):\\t2\\n错误记录数:\\t0\\n\",\"all-exceptions\":[]}",
            "execStartTime": 1532327939000,
            "execEndTime": 1532327949000,
            "execTime": 10
        }
    },
    "space": 12
};

class TaskFlowView extends Component {

    state = {
        selectedJob: '', // 选中的Job
        data: {}, // 数据
        loading: 'success',
        lastVertex: '',
        sort: 'children',
        taskLog: {},
        logVisible: false,
        visible: false,
        visibleRestart: false,
    }

    initGraph = (id) => {
        this.Container.innerHTML = ""; // 清理容器内的Dom元素
        this.graph = "";
        this._vertexCells = {} // 缓存创建的节点

        const editor = this.Container
        this.initEditor()
        this.loadEditor(editor)
        this.listenOnClick()
        this.listenDoubleClick();
        this.hideMenu();
        this.loadTaskChidren({
            jobId: id,
            level: 6,
        })
    }

    componentWillReceiveProps(nextProps) {
        const currentJob = this.props.taskJob
        const { taskJob, visibleSlidePane } = nextProps
        if (taskJob && visibleSlidePane && (!currentJob || taskJob.id !== currentJob.id)) {
            this.initGraph(taskJob.id)
        }
    }

    loadTaskChidren = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobChildren(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ selectedJob: data, data, sort: 'children' })
                ctx.doInsertVertex(data, 'children')
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadTaskParent = (params) => {
        const ctx = this
        this.setState({ loading: 'loading' })
        Api.getJobParents(params).then(res => {
            if (res.code === 1) {
                const data = res.data
                ctx.setState({ data, selectedJob: data, sort: 'parent' })
                ctx.doInsertVertex(res.data, 'parent')
            }
            ctx.setState({ loading: 'success' })
        })
    }

    loadEditor = (container) => {
        mxGraphView.prototype.optimizeVmlReflows = false;
        mxText.prototype.ignoreStringSize = true; //to avoid calling getBBox
        // Disable context menu
        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)
        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setConnectable(true)
        graph.setTooltips(true)
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(false);

        graph.setAllowDanglingEdges(false)
        // 禁止连接
        graph.setConnectable(false)
        // 禁止Edge对象移动
        graph.isCellsMovable = function (cell) {
            var cell = graph.getSelectionCell()
            return !(cell && cell.edge)
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false
        }
        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle)
        // 转换value显示的内容
        // graph.convertValueToString = this.corvertValueToString

        // 重置tooltip
        graph.getTooltipForCell = this.formatTooltip

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';
        mxConstants.VERTEX_SELECTION_COLOR = '#2491F7';
        mxConstants.STYLE_OVERFLOW = 'hidden';

        // enables rubberband
        new mxRubberband(graph)
        this.initContextMenu(graph)

    }

    formatTooltip = (cell) => {
        if (cell.vertex) {
            const currentNode = this._vertexCells[cell.id].data;
            return currentNode.batchTask.name;
        }
    }

    getShowStr = (data) => {
        const task = data.batchTask;
        const taskType = taskTypeText(task.taskType);
        const taskStatus = taskStatusText(data.status);
        const taskName = task.name.length > 12 ? `${task.name.substring(0, 10)}...` : task.name;
        const str = `${taskName || ''} \n ${taskType}(${taskStatus})`;
        return str;
    }

    corvertValueToString = (cell) => {
        if (cell.vertex && cell.value) {
            const dataParse = cell.value ? cell.value : {};
            const task = dataParse.batchTask || '';
            const taskType = taskTypeText(task.taskType);
            const taskStatus = taskStatusText(dataParse.status);
            if (task) {
                return `<div class="vertex"><span class="vertex-title"><span>${task.name || ''}</span>
                <span style="font-size:10px; color: #666666;">${taskType}(${taskStatus})</span></span>
                </div>`
            }
        }
    }

    insertEdge = (graph, type, parent, child) => {
        if (type === 'children') {
            graph.insertEdge(graph.getDefaultParent(), null, '', parent, child)
        } else {
            graph.insertEdge(graph.getDefaultParent(), null, '', child, parent)
        }
    }

    insertVertex = (graph, data, parent, type) => {
        if (data) {
            const style = getVertxtStyle(data.status)
            const defaultParent = graph.getDefaultParent();
            const exist = this._vertexCells[data.jobId];

            let newVertex = exist;

            if (exist) {
                const edges = graph.getEdgesBetween(parent, exist);
                if (edges.length === 0) {
                    this.insertEdge(graph, type, parent, exist);
                }
                return exist;
            } else if (!exist) {
                // 插入当前节点
                const str = this.getShowStr(data);
                newVertex = newVertex = graph.insertVertex(
                    defaultParent, data.jobId, str, this.cx, this.cy,
                    VertexSize.width, VertexSize.height, style
                );
                newVertex.data = data;
                this.insertEdge(graph, type, parent, newVertex);
                // 缓存节点
                this._vertexCells[data.jobId] = newVertex;
            }

            if (data.jobVOS) {
                const children = data.jobVOS
                for (let i = 0; i < children.length; i++) {
                    this.insertVertex(graph, children[i], newVertex, type)
                }
            }
        }
    }

    doInsertVertex = (data, type) => {

        const graph = this.graph
        const ctx = this;
        const parent = graph.getDefaultParent();
        const model = graph.getModel();
        this.cx = (graph.container.clientWidth - VertexSize.width) / 2
        this.cy = 200;

        const layout = new mxCompactTreeLayout(graph, false);
        layout.horizontal = false;
        layout.useBoundingBox = false;
        layout.edgeRouting = false;
        layout.levelDistance = 30;
        layout.nodeDistance = 10;

        this.executeLayout = function (change, post) {
            model.beginUpdate();
            try {
                if (change != null) { change(); }
                layout.execute(parent);
            } catch (e) {
                throw e;
            } finally {
                graph.getModel().endUpdate();
                if (post != null) { post(); }
            }
        }
        this.executeLayout(() => {
            ctx.insertVertex(graph, data, parent, type)
        })
    }

    initContextMenu = (graph) => {
        const ctx = this
        var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
        mxPopupMenu.prototype.showMenu = function () {
            var cells = this.graph.getSelectionCells()
            if (cells.length > 0 && cells[0].vertex) {
                mxPopupMenuShowMenu.apply(this, arguments);
            } else return false
        };
        graph.popupMenuHandler.autoExpand = true
        graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {

            if (!cell) return

            const currentNode = ctx._vertexCells[cell.id].data;

            menu.addItem('展开上游（6层）', null, function () {
                ctx.loadTaskParent({
                    jobId: currentNode.id,
                    level: 6,
                })
            })
            menu.addItem('展开下游（6层）', null, function () {
                ctx.loadTaskChidren({
                    jobId: currentNode.id,
                    level: 6,
                })
            })
            menu.addItem('查看任务日志', null, function () {
                ctx.showJobLog(currentNode.jobId)
            })
            menu.addItem('修改任务', null, function () {
                ctx.props.goToTaskDev(currentNode.taskId)
            })
            menu.addItem('查看任务属性', null, function () {
                ctx.setState({ visible: true })
            })
            // menu.addSeparator()
            menu.addItem('终止', null, function () {
                ctx.stopTask({
                    jobId: currentNode.id,
                })
            }, null, null,
                // 显示终止操作
                currentNode.status === TASK_STATUS.RUNNING || // 运行中
                currentNode.status === TASK_STATUS.RESTARTING || // 重启中
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 等待提交
                currentNode.status === TASK_STATUS.WAIT_RUN
            )

            menu.addItem('刷新任务实例', null, function () {
                ctx.resetGraph(cell)
            })

            menu.addItem('重跑并恢复调度', null, function () {
                ctx.restartAndResume({
                    jobId: currentNode.id,
                    justRunChild: false, // 只跑子节点
                    setSuccess: false, // 更新节点状态
                }, '重跑并恢复调度')

            }, null, null,
                // 重跑并恢复调度
                currentNode.status === TASK_STATUS.WAIT_SUBMIT || // 未运行
                currentNode.status === TASK_STATUS.FINISHED || // 已完成
                currentNode.status === TASK_STATUS.RUN_FAILED || // 运行失败
                currentNode.status === TASK_STATUS.SUBMIT_FAILED || // 提交失败
                currentNode.status === TASK_STATUS.SET_SUCCESS || // 手动设置成功
                currentNode.status === TASK_STATUS.STOPED) // 已停止

            menu.addItem('置成功并恢复调度', null, function () {
                ctx.restartAndResume({
                    jobId: currentNode.id,
                    justRunChild: true, // 只跑子节点
                    setSuccess: true,
                }, '置成功并恢复调度')
            }, null, null,
                //（运行失败、提交失败）重跑并恢复调度
                currentNode.status === TASK_STATUS.RUN_FAILED ||
                currentNode.status === TASK_STATUS.STOPED ||
                currentNode.status === TASK_STATUS.SUBMIT_FAILED)

            menu.addItem('重跑下游并恢复调度', null, function () {
                ctx.setState({ visibleRestart: true })
            })
        }
    }

    stopTask = (params) => {
        Api.stopJob(params).then(res => {
            if (res.code === 1) {
                message.success('任务终止运行命令已提交！')
            }
            this.refresh()
        })
    }

    restartAndResume = (params, msg) => { // 重跑并恢复任务
        const { reload } = this.props
        Api.restartAndResume(params).then(res => {
            if (res.code === 1) {
                message.success(`${msg}命令已提交!`)
                if (reload) reload();
            } else {
                message.error(`${msg}提交失败！`)
            }
            this.refresh()
        })
    }

    listenDoubleClick() {
        const ctx = this
        this.graph.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                const currentNode = ctx._vertexCells[cell.id].data;
                ctx.showJobLog(currentNode.jobId)

            }
        })
    }

    listenOnClick() {
        const ctx = this
        this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
            const cell = evt.getProperty('cell')
            if (cell && cell.vertex) {
                const currentNode = ctx._vertexCells[cell.id].data;
                ctx.setState({ selectedJob: currentNode })
            }
        })
    }

    resetGraph = () => {
        const { taskJob } = this.props
        if (taskJob) {
            this.loadTaskChidren({
                jobId: taskJob.id,
                level: 6,
            })
        }
    }

    showJobLog = (jobId) => {
        Api.getOfflineTaskLog({ jobId: jobId }).then((res) => {
            if (res.code === 1) {
                this.setState({ taskLog: res.data, logVisible: true, taskLogId:jobId })
            }
        })
    }

    graphEnable() {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }

    refresh = () => {
        this.initGraph(this.props.taskJob.id)
    }

    zoomIn = () => {
        this.graph.zoomIn()
    }

    zoomOut = () => {
        this.graph.zoomOut()
    }

    hideMenu = () => {
        document.addEventListener('click', (e) => {
            const popMenus = document.querySelector('.mxPopupMenu')
            if (popMenus) {
                document.body.removeChild(popMenus)
            }
        })
    }

    /* eslint-enable */
    render() {
        const { selectedJob, taskLog } = this.state;
        const { goToTaskDev, project, taskJob } = this.props
     
        return (
            <div className="graph-editor"
                style={{
                    position: 'relative',
                }}
            >
                <Spin
                    tip="Loading..."
                    size="large"
                    spinning={this.state.loading === 'loading'}
                >
                   <div
                        className="editor pointer"
                        ref={(e) => { this.Container = e }}
                        style={{
                            position: 'relative',
                            overflowX: 'auto',
                            paddingBottom: '20px',
                            height: '95%',
                        }}
                    >
                    </div>
                </Spin>
                <div className="graph-toolbar">
                    <Tooltip placement="bottom" title="刷新">
                        <Icon type="reload" onClick={this.refresh} />
                    </Tooltip>
                    <Tooltip placement="bottom" title="放大">
                        <MyIcon onClick={this.zoomIn} type="zoom-in" />
                    </Tooltip>
                    <Tooltip placement="bottom" title="缩小">
                        <MyIcon onClick={this.zoomOut} type="zoom-out" />
                    </Tooltip>
                </div>
                <div
                    className="box-title graph-info"
                    style={{
                        bottom: 0
                    }}
                >
                    <span>{taskJob && taskJob.batchTask && taskJob.batchTask.name || '-'}</span>
                    <span style={{ marginLeft: "15px" }}>{(taskJob && taskJob.batchTask && taskJob.batchTask.createUser && taskJob.batchTask.createUser.userName) || '-'}</span>&nbsp;
                    发布于&nbsp;
                    <span>{taskJob && taskJob.batchTask && utils.formatDateTime(taskJob.batchTask.gmtModified)}</span>&nbsp;
                    <a onClick={() => { goToTaskDev(taskJob && taskJob.batchTask.id) }}>查看代码</a>
                </div>
                <Modal
                    title="查看属性"
                    width="60%"
                    wrapClassName="vertical-center-modal"
                    visible={this.state.visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                    footer={null}
                >
                    <TaskInfo task={selectedJob} project={project} />
                </Modal>
                <Modal
                    width={600}
                    title={(
                        <span>
                            任务日志
                            <Tooltip placement="right" title="刷新">
                                <Icon style={{cursor:"pointer",marginLeft:"5px"}} onClick={()=>{this.showJobLog(this.state.taskLogId)}} type="reload" />
                            </Tooltip>
                        </span>
                    )}
                    wrapClassName="vertical-center-modal m-log-modal"
                    visible={this.state.logVisible}
                    onCancel={() => { this.setState({ logVisible: false }) }}
                    footer={null}
                >
                    <LogInfo
                        log={taskLog.logInfo}
                        syncJobInfo={taskLog.syncJobInfo}
                        height="520px"
                    />
                </Modal>
                <RestartModal
                    restartNode={selectedJob}
                    visible={this.state.visibleRestart}
                    onCancel={() => {
                        this.setState({ visibleRestart: false })
                    }}
                />
            </div>
        )
    }

    getDefaultVertexStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = '#90D5FF';
        style[mxConstants.STYLE_FILLCOLOR] = '#E6F7FF;';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333;';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        style[mxConstants.STYLE_OVERFLOW] = 'hidden';

        return style;
    }

    getDefaultEdgeStyle() {
        let style = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#9EABB2';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = true;
        return style
    }

    /* eslint-disable */
    initEditor() {
        // Overridden to define per-shape connection points
        mxGraph.prototype.getAllConnectionConstraints = function (terminal, source) {
            if (terminal != null && terminal.shape != null) {
                if (terminal.shape.stencil != null) {
                    if (terminal.shape.stencil != null) {
                        return terminal.shape.stencil.constraints;
                    }
                }
                else if (terminal.shape.constraints != null) {
                    return terminal.shape.constraints;
                }
            }
            return null;
        };
        // Defines the default constraints for all shapes
        mxShape.prototype.constraints = [new mxConnectionConstraint(new mxPoint(0.25, 0), true),
        new mxConnectionConstraint(new mxPoint(0.5, 0), true),
        new mxConnectionConstraint(new mxPoint(0.75, 0), true),
        new mxConnectionConstraint(new mxPoint(0, 0.25), true),
        new mxConnectionConstraint(new mxPoint(0, 0.5), true),
        new mxConnectionConstraint(new mxPoint(0, 0.75), true),
        new mxConnectionConstraint(new mxPoint(1, 0.25), true),
        new mxConnectionConstraint(new mxPoint(1, 0.5), true),
        new mxConnectionConstraint(new mxPoint(1, 0.75), true),
        new mxConnectionConstraint(new mxPoint(0.25, 1), true),
        new mxConnectionConstraint(new mxPoint(0.5, 1), true),
        new mxConnectionConstraint(new mxPoint(0.75, 1), true)];
        // Edges have no connection points
        mxPolyline.prototype.constraints = null;
    }
}
export default TaskFlowView;
