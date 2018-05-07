webpackJsonp([4],{1863:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=a(8),r=a.n(n),o=a(1),s=a.n(o),i=a(4),c=a.n(i),l=a(3),m=a.n(l),_=a(2),u=a.n(_),p=a(0),d=a.n(p),E=a(1884),T=a(1883),f=a(1882);a.d(t,"default",function(){return v});var v=function(e){function t(){return s()(this,t),m()(this,(t.__proto__||r()(t)).apply(this,arguments))}return u()(t,e),c()(t,[{key:"render",value:function(){return d.a.createElement("div",{className:"operation-overview",style:{background:"#f2f7fa"}},d.a.createElement(E.a,null),d.a.createElement(T.a,null),d.a.createElement(f.a,null))}}]),t}(p.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&__REACT_HOT_LOADER__.register(v,"Index","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/overview.js")}()},1882:function(e,t,a){"use strict";var n=a(52),r=(a.n(n),a(51)),o=a.n(r),s=a(70),i=(a.n(s),a(69)),c=a.n(i),l=a(32),m=(a.n(l),a(31)),_=a.n(m),u=a(142),p=(a.n(u),a(141)),d=a.n(p),E=a(37),T=(a.n(E),a(36)),f=a.n(T),v=a(8),y=a.n(v),g=a(1),h=a.n(g),N=a(4),A=a.n(N),k=a(3),D=a.n(k),R=a(2),O=a.n(R),C=a(0),L=a.n(C),b=(a(17),a(20)),H=a(13),x=a.n(H),w=a(23),I=(a.n(w),a(198)),S=a(35),U=a(50),j=a(304),z=a(44);a(205),a(669),a(200),a(199);var J=function(e){function t(){var e,a,n,r;h()(this,t);for(var o=arguments.length,s=Array(o),i=0;i<o;i++)s[i]=arguments[i];return a=n=D()(this,(e=t.__proto__||y()(t)).call.apply(e,[this].concat(s))),n.state={offline:"",topTiming:[],topError:[],handleTiming:x()(),lineChart:""},n.resize=function(){var e;return(e=n).__resize__REACT_HOT_LOADER__.apply(e,arguments)},n.loadChartData=function(){var e;return(e=n).__loadChartData__REACT_HOT_LOADER__.apply(e,arguments)},n.getTopTaskTime=function(){var e;return(e=n).__getTopTaskTime__REACT_HOT_LOADER__.apply(e,arguments)},n.getTopJobError=function(){var e;return(e=n).__getTopJobError__REACT_HOT_LOADER__.apply(e,arguments)},n.getSeries=function(){var e;return(e=n).__getSeries__REACT_HOT_LOADER__.apply(e,arguments)},n.changeHandleTiming=function(){var e;return(e=n).__changeHandleTiming__REACT_HOT_LOADER__.apply(e,arguments)},n.topTaskTiming=function(){var e;return(e=n).__topTaskTiming__REACT_HOT_LOADER__.apply(e,arguments)},n.topTaskError=function(){var e;return(e=n).__topTaskError__REACT_HOT_LOADER__.apply(e,arguments)},n.disabledDate=function(){var e;return(e=n).__disabledDate__REACT_HOT_LOADER__.apply(e,arguments)},r=a,D()(n,r)}return O()(t,e),A()(t,[{key:"componentDidMount",value:function(){this.loadChartData(),this.getTopTaskTime(),this.getTopJobError()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&(this.loadChartData(),this.getTopTaskTime(),this.getTopJobError())}},{key:"__resize__REACT_HOT_LOADER__",value:function(){this.state.lineChart&&this.state.lineChart.resize()}},{key:"__loadChartData__REACT_HOT_LOADER__",value:function(){var e=this;S.a.getJobGraph().then(function(t){1===t.code&&e.initLineChart(t.data)})}},{key:"__getTopTaskTime__REACT_HOT_LOADER__",value:function(e){var t=this,a=this.state.handleTiming,n={startTime:a.set({hour:0,minute:0,second:0}).unix(),endTime:a.set({hour:23,minute:59,second:59}).unix()};S.a.getJobTopTime(n).then(function(e){1===e.code&&t.setState({topTiming:e.data})})}},{key:"__getTopJobError__REACT_HOT_LOADER__",value:function(){var e=this;S.a.getJobTopError().then(function(t){1===t.code&&e.setState({topError:t.data})})}},{key:"__getSeries__REACT_HOT_LOADER__",value:function(e){var t=[];if(e&&e.y)for(var a=e&&e.type?e.type.data:[],n=0;n<a.length;n++)t.push({name:a[n],symbol:"none",smooth:!0,type:"line",data:e.y[n].data});return t}},{key:"initLineChart",value:function(e){var t=z.init(document.getElementById("TaskTrend")),n=a.i(w.cloneDeep)(U.d);n.title.text="",n.tooltip.axisPointer.label.formatter="{value}: 00",n.yAxis[0].minInterval=1,n.xAxis[0].axisLabel.formatter="{value} 时",n.legend.data=e&&e.type?e.type.data:[],n.xAxis[0].data=e&&e.x?e.x.data:[],n.series=this.getSeries(e),t.setOption(n),this.setState({lineChart:t})}},{key:"__changeHandleTiming__REACT_HOT_LOADER__",value:function(e){var t=this;this.setState({handleTiming:e},function(){t.getTopTaskTime()})}},{key:"__topTaskTiming__REACT_HOT_LOADER__",value:function(){var e=this;return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(t,a){return 1===a.isDeleted?t+"(已删除)":L.a.createElement("a",{onClick:e.props.goToTaskDev.bind(e,a.taskId)},t)}},{title:"任务实例类型",dataIndex:"type",key:"type",render:function(e,t){return 1===t.type?"补数据":"周期调度"}},{title:"调度时间",dataIndex:"cycTime",key:"cycTime"},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"执行时长",dataIndex:"runTime",key:"runTime"}]}},{key:"__topTaskError__REACT_HOT_LOADER__",value:function(){var e=this;return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(t,a){return 1===a.isDeleted?t+" (已删除)":L.a.createElement("a",{onClick:e.props.goToTaskDev.bind(e,a.taskId)},t)}},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"出错次数",dataIndex:"errorCount",key:"errorCount"}]}},{key:"__disabledDate__REACT_HOT_LOADER__",value:function(e){return e&&e.valueOf()>x()().add(1,"days").valueOf()}},{key:"render",value:function(){var e=this.state,t=(e.offline,e.topTiming),a=e.topError,n=e.handleTiming;return L.a.createElement("div",{className:"box-card",style:{marginTop:"20px"}},L.a.createElement(f.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"今日任务完成情况"},L.a.createElement(I.a,{onResize:this.resize},L.a.createElement("article",{id:"TaskTrend",style:{width:"100%",height:"300px"}}))),L.a.createElement(o.a,{className:"m-card",style:{marginTop:"20px"}},L.a.createElement(c.a,{span:"12",style:{paddingRight:"10px"}},L.a.createElement(f.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"执行时长排行",extra:L.a.createElement(d.a,{style:{width:100,float:"right",marginTop:"10px"},format:"YYYY-MM-DD",value:n,disabledDate:this.disabledDate,onChange:this.changeHandleTiming})},L.a.createElement(_.a,{rowKey:"id",pagination:!1,className:"m-table",style:{minHeight:"0"},columns:this.topTaskTiming(),dataSource:t||[]}))),L.a.createElement(c.a,{span:"12",style:{paddingLeft:"10px"}},L.a.createElement(f.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"近30天出错排行"},L.a.createElement(_.a,{rowKey:"id",className:"m-table",pagination:!1,style:{minHeight:"0"},columns:this.topTaskError(),dataSource:a||[]})))))}}]),t}(C.Component),M=a.i(b.connect)(function(e){return{project:e.project}},function(e){var t=a.i(j.a)(e);return{goToTaskDev:function(e){t.openTaskInDev(e)}}})(J);t.a=M;!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(J,"OfflineStatistics","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/offline/index.js"),__REACT_HOT_LOADER__.register(M,"default","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/offline/index.js"))}()},1883:function(e,t,a){"use strict";var n=a(37),r=(a.n(n),a(36)),o=a.n(r),s=a(52),i=(a.n(s),a(51)),c=a.n(i),l=a(70),m=(a.n(l),a(69)),_=a.n(m),u=a(24),p=(a.n(u),a(22)),d=a.n(p),E=a(8),T=a.n(E),f=a(1),v=a.n(f),y=a(4),g=a.n(y),h=a(3),N=a.n(h),A=a(2),k=a.n(A),D=a(0),R=a.n(D),O=a(17),C=a(20),L=a(13),b=(a.n(L),a(35)),H=function(e){function t(){var e,a,n,r;v()(this,t);for(var o=arguments.length,s=Array(o),i=0;i<o;i++)s[i]=arguments[i];return a=n=N()(this,(e=t.__proto__||T()(t)).call.apply(e,[this].concat(s))),n.state={data:""},n.loadOfflineData=function(){var e;return(e=n).__loadOfflineData__REACT_HOT_LOADER__.apply(e,arguments)},r=a,N()(n,r)}return k()(t,e),g()(t,[{key:"componentDidMount",value:function(){this.loadOfflineData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadOfflineData()}},{key:"__loadOfflineData__REACT_HOT_LOADER__",value:function(){var e=this;b.a.getJobStatistics().then(function(t){1===t.code&&e.setState({data:t.data})})}},{key:"render",value:function(){var e=this.state.data,t={flexGrow:1,flex:1};return R.a.createElement("div",{style:{marginTop:"10px"}},R.a.createElement("h1",{className:"box-title box-title-bolder"},"离线任务",R.a.createElement(d.a,{type:"primary",className:"right",style:{marginTop:"8px"}},R.a.createElement(O.Link,{to:"/operation/offline-operation"},"离线任务运维"))),R.a.createElement("div",{className:"box-4 m-card m-card-small"},R.a.createElement(o.a,{noHovering:!0,bordered:!1,loading:!1,title:"今日任务完成情况"},R.a.createElement(c.a,{className:"m-count",style:{display:"flex"}},R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"全部"),R.a.createElement("span",{className:"m-count-content font-black"},e.ALL||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"失败"),R.a.createElement("span",{className:"m-count-content font-red"},e.FAILED||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"运行"),R.a.createElement("span",{className:"m-count-content font-organge"},e.RUNNING||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"成功"),R.a.createElement("span",{className:"m-count-content font-green"},e.FINISHED||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"未提交"),R.a.createElement("span",{className:"m-count-content font-gray"},e.UNSUBMIT||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"提交中"),R.a.createElement("span",{className:"m-count-content font-organge"},e.SUBMITTING||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"待运行"),R.a.createElement("span",{className:"m-count-content font-organge"},e.WAITENGINE||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"冻结"),R.a.createElement("span",{className:"m-count-content font-blue"},e.FROZEN||0))),R.a.createElement(_.a,{style:t},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"取消"),R.a.createElement("span",{className:"m-count-content font-gray"},e.CANCELED||0)))))))}}]),t}(D.Component),x=a.i(C.connect)(function(e){return{project:e.project}})(H);t.a=x;!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(H,"OfflineCount","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/offline/offlineCount.js"),__REACT_HOT_LOADER__.register(x,"default","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/offline/offlineCount.js"))}()},1884:function(e,t,a){"use strict";var n=a(37),r=(a.n(n),a(36)),o=a.n(r),s=a(52),i=(a.n(s),a(51)),c=a.n(i),l=a(70),m=(a.n(l),a(69)),_=a.n(m),u=a(24),p=(a.n(u),a(22)),d=a.n(p),E=a(8),T=a.n(E),f=a(1),v=a.n(f),y=a(4),g=a.n(y),h=a(3),N=a.n(h),A=a(2),k=a.n(A),D=a(0),R=a.n(D),O=a(17),C=a(20),L=a(13),b=(a.n(L),a(35)),H=function(e){function t(){var e,a,n,r;v()(this,t);for(var o=arguments.length,s=Array(o),i=0;i<o;i++)s[i]=arguments[i];return a=n=N()(this,(e=t.__proto__||T()(t)).call.apply(e,[this].concat(s))),n.state={data:{},chart:""},n.getSeriesData=function(){var e;return(e=n).__getSeriesData__REACT_HOT_LOADER__.apply(e,arguments)},r=a,N()(n,r)}return k()(t,e),g()(t,[{key:"componentDidMount",value:function(){this.loadRealtimeData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadRealtimeData()}},{key:"loadRealtimeData",value:function(){var e=this;b.a.taskStatistics().then(function(t){1===t.code&&e.setState({data:t.data})})}},{key:"__getSeriesData__REACT_HOT_LOADER__",value:function(e){return e?[{name:"失败",value:e.FAILED||0},{name:"运行中",value:e.RUNNING||0},{name:"停止",value:e.CANCELED||0},{name:"等待运行",value:e.WAITENGINE||0},{name:"等待提交",value:e.UNSUBMIT||0}]:[]}},{key:"render",value:function(){var e=this.state.data;return R.a.createElement("div",null,R.a.createElement("h1",{className:"box-title box-title-bolder"},"实时任务",R.a.createElement(d.a,{type:"primary",className:"right",style:{marginTop:"8px"}},R.a.createElement(O.Link,{to:"/operation/realtime"},"实时任务运维"))),R.a.createElement("div",{className:"box-4 m-card m-card-small"},R.a.createElement(o.a,{noHovering:!0,bordered:!1,loading:!1,title:"任务数量"},R.a.createElement(c.a,{className:"m-count"},R.a.createElement(_.a,{span:4},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"全部"),R.a.createElement("span",{className:"m-count-content font-black"},e.ALL||0))),R.a.createElement(_.a,{span:5},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"失败"),R.a.createElement("span",{className:"m-count-content font-red"},e.FAILED||0))),R.a.createElement(_.a,{span:6},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"运行"),R.a.createElement("span",{className:"m-count-content font-organge"},e.RUNNING||0))),R.a.createElement(_.a,{span:5},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"未提交"),R.a.createElement("span",{className:"m-count-content font-darkgreen"},e.UNSUBMIT||0))),R.a.createElement(_.a,{span:4},R.a.createElement("section",{className:"m-count-section"},R.a.createElement("span",{className:"m-count-title"},"取消"),R.a.createElement("span",{className:"m-count-content font-green"},e.CANCELED||0)))))))}}]),t}(D.Component),x=a.i(C.connect)(function(e){return{project:e.project}})(H);t.a=x;!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(H,"RealtimeCount","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/realtime/realtimeCount.js"),__REACT_HOT_LOADER__.register(x,"default","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/operation/realtime/realtimeCount.js"))}()}});