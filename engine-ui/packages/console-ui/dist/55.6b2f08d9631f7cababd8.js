(window.webpackJsonp=window.webpackJsonp||[]).push([[55],{1914:function(e,t,a){"use strict";a.r(t);var n=a(9),i=a.n(n),o=a(4),s=a.n(o),r=a(6),l=a.n(r),c=a(1),m=a.n(c),_=a(5),u=a.n(_),p=a(0),d=a.n(p),E=(a(79),a(41)),T=a.n(E),f=(a(97),a(40)),h=a.n(f),v=(a(106),a(31)),y=a.n(v),g=(a(70),a(27)),N=a.n(g),k=a(17),O=a(30),A=a(24),R=a.n(A),D=a(12),C=a(33),b=a(10),L=function(e){function t(){var e,a,n,o;s()(this,t);for(var r=arguments.length,l=Array(r),c=0;c<r;c++)l[c]=arguments[c];return a=n=m()(this,(e=t.__proto__||i()(t)).call.apply(e,[this].concat(l))),n.state={data:{},chart:""},n.getSeriesData=function(){var e;return(e=n).__getSeriesData__REACT_HOT_LOADER__.apply(e,arguments)},o=a,m()(n,o)}return u()(t,e),l()(t,[{key:"componentDidMount",value:function(){this.loadRealtimeData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadRealtimeData()}},{key:"loadRealtimeData",value:function(){var e=this;C.a.taskStatistics().then(function(t){1===t.code&&e.setState({data:t.data})})}},{key:"__getSeriesData__REACT_HOT_LOADER__",value:function(e){return e?[{name:"失败",value:e.FAILED||0},{name:"运行中",value:e.RUNNING||0},{name:"停止",value:e.CANCELED||0},{name:"等待运行",value:e.WAITENGINE||0},{name:"等待提交",value:e.UNSUBMIT||0}]:[]}},{key:"jumpToRealList",value:function(e){k.g.push({pathname:"/operation/realtime",query:{status:e||0==e?e:void 0}})}},{key:"render",value:function(){var e=this.state.data;return d.a.createElement("div",null,d.a.createElement("h1",{className:"box-title box-title-bolder"},"实时任务",d.a.createElement(N.a,{type:"primary",className:"right",style:{marginTop:"8px"}},d.a.createElement(k.c,{to:"/operation/realtime"},"实时任务运维"))),d.a.createElement("div",{className:"box-4 m-card m-card-small"},d.a.createElement(T.a,{noHovering:!0,bordered:!1,loading:!1,title:"任务数量"},d.a.createElement(h.a,{className:"m-count"},d.a.createElement(y.a,{span:4},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"全部"),d.a.createElement("a",{onClick:this.jumpToRealList.bind(this,b.J.ALL),className:"m-count-content font-black"},e.ALL||0))),d.a.createElement(y.a,{span:5},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"失败"),d.a.createElement("a",{onClick:this.jumpToRealList.bind(this,b.J.FAILED),className:"m-count-content font-red"},e.FAILED||0))),d.a.createElement(y.a,{span:6},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"运行中"),d.a.createElement("a",{onClick:this.jumpToRealList.bind(this,b.J.RUNNING),className:"m-count-content font-organge"},e.RUNNING||0))),d.a.createElement(y.a,{span:5},d.a.createElement("section",{className:"m-count-section",style:{width:"60px"}},d.a.createElement("span",{className:"m-count-title"},"等待提交"),d.a.createElement("a",{onClick:this.jumpToRealList.bind(this,b.J.UNSUBMIT),className:"m-count-content font-darkgreen"},e.UNSUBMIT||0))),d.a.createElement(y.a,{span:4},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"取消"),d.a.createElement("a",{onClick:this.jumpToRealList.bind(this,b.J.CANCELED),className:"m-count-content font-gray"},e.CANCELED||0)))))))}}]),t}(p.Component),x=Object(O.b)(function(e){return{project:e.project}})(L),H=x,j=("undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(L,"RealtimeCount","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/realtime/realtimeCount.js"),__REACT_HOT_LOADER__.register(x,"default","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/realtime/realtimeCount.js")),function(e){function t(){var e,a,n,o;s()(this,t);for(var r=arguments.length,l=Array(r),c=0;c<r;c++)l[c]=arguments[c];return a=n=m()(this,(e=t.__proto__||i()(t)).call.apply(e,[this].concat(l))),n.state={data:""},n.loadOfflineData=function(){var e;return(e=n).__loadOfflineData__REACT_HOT_LOADER__.apply(e,arguments)},o=a,m()(n,o)}return u()(t,e),l()(t,[{key:"componentDidMount",value:function(){this.loadOfflineData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadOfflineData()}},{key:"__loadOfflineData__REACT_HOT_LOADER__",value:function(){var e=this;C.a.getJobStatistics().then(function(t){1===t.code&&e.setState({data:t.data})})}},{key:"jumpToOfflineList",value:function(e){k.g.push({pathname:"/operation/offline-operation",query:{status:e||0==e?e:void 0}})}},{key:"render",value:function(){var e=this.state.data,t={flexGrow:1,flex:1};return d.a.createElement("div",{style:{marginTop:"10px"}},d.a.createElement("h1",{className:"box-title box-title-bolder"},"离线任务",d.a.createElement(N.a,{type:"primary",className:"right",style:{marginTop:"8px"}},d.a.createElement(k.c,{to:"/operation/offline-operation"},"离线任务运维"))),d.a.createElement("div",{className:"box-4 m-card m-card-small"},d.a.createElement(T.a,{noHovering:!0,bordered:!1,loading:!1,title:"今日周期实例完成情况"},d.a.createElement(h.a,{className:"m-count",style:{display:"flex"}},d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"全部"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.ALL),className:"m-count-content font-black"},e.ALL||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"失败"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.FAILED),className:"m-count-content font-red"},e.FAILED||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"运行中"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.RUNNING),className:"m-count-content font-organge"},e.RUNNING||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"成功"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.FINISHED),className:"m-count-content font-green"},e.FINISHED||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section",style:{width:"60px"}},d.a.createElement("span",{className:"m-count-title"},"等待提交"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.UNSUBMIT),className:"m-count-content font-gray"},e.UNSUBMIT||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"提交中"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.SUBMITTING),className:"m-count-content font-organge"},e.SUBMITTING||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section",style:{width:"60px"}},d.a.createElement("span",{className:"m-count-title"},"等待运行"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.WAITING_RUN),className:"m-count-content font-organge"},e.WAITENGINE||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"冻结"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.FROZEN),className:"m-count-content font-blue"},e.FROZEN||0))),d.a.createElement(y.a,{style:t},d.a.createElement("section",{className:"m-count-section"},d.a.createElement("span",{className:"m-count-title"},"取消"),d.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,b.J.CANCELED),className:"m-count-content font-gray"},e.CANCELED||0)))))))}}]),t}(p.Component)),I=Object(O.b)(function(e){return{project:e.project}})(j),w=I,U=("undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(j,"OfflineCount","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/offline/offlineCount.js"),__REACT_HOT_LOADER__.register(I,"default","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/offline/offlineCount.js")),a(68),a(35)),S=a.n(U),J=(a(158),a(98)),z=a.n(J),W=a(127),G=a(170),M=a(108);a(225),a(288),a(205),a(204);var F=function(e){function t(){var e,a,n,o;s()(this,t);for(var r=arguments.length,l=Array(r),c=0;c<r;c++)l[c]=arguments[c];return a=n=m()(this,(e=t.__proto__||i()(t)).call.apply(e,[this].concat(l))),n.state={offline:"",topTiming:[],topError:[],handleTiming:R()(),lineChart:""},n.resize=function(){var e;return(e=n).__resize__REACT_HOT_LOADER__.apply(e,arguments)},n.loadChartData=function(){var e;return(e=n).__loadChartData__REACT_HOT_LOADER__.apply(e,arguments)},n.getTopTaskTime=function(){var e;return(e=n).__getTopTaskTime__REACT_HOT_LOADER__.apply(e,arguments)},n.getTopJobError=function(){var e;return(e=n).__getTopJobError__REACT_HOT_LOADER__.apply(e,arguments)},n.getSeries=function(){var e;return(e=n).__getSeries__REACT_HOT_LOADER__.apply(e,arguments)},n.changeHandleTiming=function(){var e;return(e=n).__changeHandleTiming__REACT_HOT_LOADER__.apply(e,arguments)},n.topTaskTiming=function(){var e;return(e=n).__topTaskTiming__REACT_HOT_LOADER__.apply(e,arguments)},n.topTaskError=function(){var e;return(e=n).__topTaskError__REACT_HOT_LOADER__.apply(e,arguments)},n.disabledDate=function(){var e;return(e=n).__disabledDate__REACT_HOT_LOADER__.apply(e,arguments)},o=a,m()(n,o)}return u()(t,e),l()(t,[{key:"componentDidMount",value:function(){this.loadChartData(),this.getTopTaskTime(),this.getTopJobError()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&(this.loadChartData(),this.getTopTaskTime(),this.getTopJobError())}},{key:"__resize__REACT_HOT_LOADER__",value:function(){this.state.lineChart&&this.state.lineChart.resize()}},{key:"__loadChartData__REACT_HOT_LOADER__",value:function(){var e=this;C.a.getJobGraph().then(function(t){1===t.code&&e.initLineChart(t.data)})}},{key:"__getTopTaskTime__REACT_HOT_LOADER__",value:function(e){var t=this,a=this.state.handleTiming,n={startTime:a.set({hour:0,minute:0,second:0}).unix(),endTime:a.set({hour:23,minute:59,second:59}).unix()};C.a.getJobTopTime(n).then(function(e){1===e.code&&t.setState({topTiming:e.data})})}},{key:"__getTopJobError__REACT_HOT_LOADER__",value:function(){var e=this;C.a.getJobTopError().then(function(t){1===t.code&&e.setState({topError:t.data})})}},{key:"__getSeries__REACT_HOT_LOADER__",value:function(e){var t=[];if(e&&e.y)for(var a=e&&e.type?e.type.data:[],n=0;n<a.length;n++)t.push({name:a[n],symbol:"none",smooth:!0,type:"line",data:e.y[n].data});return t}},{key:"initLineChart",value:function(e){var t=M.init(document.getElementById("TaskTrend")),a=Object(D.cloneDeep)(b.z);a.title.text="",a.tooltip.axisPointer.label.formatter="{value}: 00",a.yAxis[0].minInterval=1,a.xAxis[0].axisLabel.formatter="{value} 时",a.legend.data=e&&e.type?e.type.data:[],a.xAxis[0].data=e&&e.x?e.x.data:[],a.series=this.getSeries(e),t.setOption(a),this.setState({lineChart:t})}},{key:"__changeHandleTiming__REACT_HOT_LOADER__",value:function(e){var t=this;this.setState({handleTiming:e},function(){t.getTopTaskTime()})}},{key:"jumpToOffline",value:function(e,t,a){k.g.push({pathname:"/operation/offline-operation",query:{job:e,status:a,date:t}})}},{key:"__topTaskTiming__REACT_HOT_LOADER__",value:function(){var e=this;return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(t,a){return 1===a.isDeleted?t+"(已删除)":d.a.createElement("a",{onClick:e.props.goToTaskDev.bind(e,a.taskId)},t)}},{title:"任务实例类型",dataIndex:"type",key:"type",render:function(e,t){return 1===t.type?"补数据":"周期调度"}},{title:"调度时间",dataIndex:"cycTime",key:"cycTime"},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"执行时长",dataIndex:"runTime",key:"runTime"}]}},{key:"__topTaskError__REACT_HOT_LOADER__",value:function(){var e=this;return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(t,a){var n=1===a.isDeleted?t+" (已删除)":t;return d.a.createElement("a",{onClick:e.jumpToOffline.bind(e,t,30,b.s.RUN_FAILED)},n)}},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"出错次数",dataIndex:"errorCount",key:"errorCount"}]}},{key:"__disabledDate__REACT_HOT_LOADER__",value:function(e){return e&&e.valueOf()>R()().add(1,"days").valueOf()}},{key:"render",value:function(){var e=this.state,t=(e.offline,e.topTiming),a=e.topError,n=e.handleTiming;return d.a.createElement("div",{className:"box-card",style:{marginTop:"20px"}},d.a.createElement(T.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"今日周期实例完成情况"},d.a.createElement(W.a,{onResize:this.resize},d.a.createElement("article",{id:"TaskTrend",style:{width:"100%",height:"300px"}}))),d.a.createElement(h.a,{className:"m-card",style:{marginTop:"20px"}},d.a.createElement(y.a,{span:"12",style:{paddingRight:"10px"}},d.a.createElement(T.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"执行时长排行",extra:d.a.createElement(z.a,{style:{width:100,float:"right",marginTop:"10px"},format:"YYYY-MM-DD",value:n,disabledDate:this.disabledDate,onChange:this.changeHandleTiming})},d.a.createElement(S.a,{rowKey:"id",pagination:!1,className:"m-table",style:{minHeight:"0"},columns:this.topTaskTiming(),dataSource:t||[]}))),d.a.createElement(y.a,{span:"12",style:{paddingLeft:"10px"}},d.a.createElement(T.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"近30天出错排行"},d.a.createElement(S.a,{rowKey:"id",className:"m-table",pagination:!1,style:{minHeight:"0"},columns:this.topTaskError(),dataSource:a||[]})))))}}]),t}(p.Component),B=Object(O.b)(function(e){return{project:e.project}},function(e){var t=Object(G.b)(e);return{goToTaskDev:function(e){t.openTaskInDev(e)}}})(F),P=B;"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(F,"OfflineStatistics","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/offline/index.js"),__REACT_HOT_LOADER__.register(B,"default","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/offline/index.js"));a.d(t,"default",function(){return Y});var Y=function(e){function t(){return s()(this,t),m()(this,(t.__proto__||i()(t)).apply(this,arguments))}return u()(t,e),l()(t,[{key:"render",value:function(){return d.a.createElement("div",{className:"operation-overview",style:{background:"#f2f7fa"}},d.a.createElement(H,null),d.a.createElement(w,null),d.a.createElement(P,null))}}]),t}(p.Component);"undefined"!=typeof __REACT_HOT_LOADER__&&__REACT_HOT_LOADER__.register(Y,"Index","/Users/ziv/Development/Workspace/data-stack/src/webapps/rdos/views/operation/overview.js")}}]);