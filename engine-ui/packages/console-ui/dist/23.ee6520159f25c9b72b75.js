(self.webpackJsonp=self.webpackJsonp||[]).push([[23],{ASaT:function(e,t,a){"use strict";a.r(t);a("hr7U");var n=a("9xET"),i=a.n(n),o=(a("fv9D"),a("ZPTe")),r=a.n(o),l=a("Yz+Y"),s=a.n(l),c=a("iCc5"),m=a.n(c),u=a("V7oC"),d=a.n(u),p=a("FYw3"),h=a.n(p),f=a("mRg0"),E=a.n(f),N=a("sbe7"),T=a.n(N),g=(a("mN36"),a("N9UN")),y=a.n(g),v=(a("MaXC"),a("4IMT")),b=a.n(v),k=a("dtw8"),I=a("/MKj"),D=a("wd/R"),C=a.n(D),x=a("LvDl"),j=a("sVOr"),L=a("djHj"),S=function(e){function t(){var e,a,n,i;m()(this,t);for(var o=arguments.length,r=Array(o),l=0;l<o;l++)r[l]=arguments[l];return a=n=h()(this,(e=t.__proto__||s()(t)).call.apply(e,[this].concat(r))),n.state={data:{},chart:""},n.getSeriesData=function(e){return e?[{name:"失败",value:e.FAILED||0},{name:"运行中",value:e.RUNNING||0},{name:"停止",value:e.CANCELED||0},{name:"等待运行",value:e.WAITENGINE||0},{name:"等待提交",value:e.UNSUBMIT||0}]:[]},i=a,h()(n,i)}return E()(t,e),d()(t,[{key:"componentDidMount",value:function(){this.loadRealtimeData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadRealtimeData()}},{key:"loadRealtimeData",value:function(){var e=this;j.a.taskStatistics().then(function(t){1===t.code&&e.setState({data:t.data})})}},{key:"jumpToRealList",value:function(e){k.g.push({pathname:"/operation/realtime",query:{status:e||0==e?e:void 0}})}},{key:"render",value:function(){var e=this.state.data;return T.a.createElement("div",null,T.a.createElement("h1",{className:"box-title box-title-bolder",style:{padding:"0 20 0 10"}},"实时任务",T.a.createElement(b.a,{type:"primary",className:"right",style:{marginTop:"8px",fontWeight:200}},T.a.createElement(k.c,{to:"/operation/realtime"},"实时任务运维"))),T.a.createElement("div",{className:"box-4 m-card m-card-small",style:{margin:"0 20 0 10"}},T.a.createElement(y.a,{noHovering:!0,bordered:!1,loading:!1,title:"任务数量"},T.a.createElement(i.a,{className:"m-count"},T.a.createElement(r.a,{span:6},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"失败"),T.a.createElement("a",{onClick:this.jumpToRealList.bind(this,L.I.FAILED),className:"m-count-content font-red"},e.FAILED||0))),T.a.createElement(r.a,{span:6},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"运行中"),T.a.createElement("a",{onClick:this.jumpToRealList.bind(this,L.I.RUNNING),className:"m-count-content font-blue"},e.RUNNING||0))),T.a.createElement(r.a,{span:6},T.a.createElement("section",{className:"m-count-section",style:{width:"60px"}},T.a.createElement("span",{className:"m-count-title"},"等待提交"),T.a.createElement("a",{onClick:this.jumpToRealList.bind(this,L.I.UNSUBMIT),className:"m-count-content font-organge"},e.UNSUBMIT||0))),T.a.createElement(r.a,{span:6},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"取消"),T.a.createElement("a",{onClick:this.jumpToRealList.bind(this,L.I.CANCELED),className:"m-count-content font-gray"},e.CANCELED||0)))))))}}]),t}(N.Component),U=Object(I.b)(function(e){return{project:e.project}})(S),R=(a("93XW"),a("d1El")),A=a.n(R),O=(a("FGdI"),a("Pbn2")),_=a.n(O),w=function(e){function t(){var e,a,n,i;m()(this,t);for(var o=arguments.length,r=Array(o),l=0;l<o;l++)r[l]=arguments[l];return a=n=h()(this,(e=t.__proto__||s()(t)).call.apply(e,[this].concat(r))),n.state={data:""},n.loadOfflineData=function(){j.a.getJobStatistics().then(function(e){1===e.code&&n.setState({data:e.data})})},i=a,h()(n,i)}return E()(t,e),d()(t,[{key:"componentDidMount",value:function(){this.loadOfflineData()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&this.loadOfflineData()}},{key:"jumpToOfflineList",value:function(e){var t=e&&e.join(",");k.g.push({pathname:"/operation/offline-operation",query:{status:t}})}},{key:"render",value:function(){var e=this.state.data,t=(e.UNSUBMIT||0)+(e.SUBMITTING||0)+(e.WAITENGINE||0);return T.a.createElement("div",null,T.a.createElement("h1",{className:"box-title box-title-bolder",style:{padding:"0 10 0 20"}},"离线任务",T.a.createElement(b.a,{type:"primary",className:"right",style:{marginTop:"8px",fontWeight:200}},T.a.createElement(k.c,{to:"/operation/offline-operation"},"离线任务运维"))),T.a.createElement("div",{className:"box-4 m-card m-card-small",style:{margin:"0 10 0 20"}},T.a.createElement(y.a,{noHovering:!0,bordered:!1,loading:!1,title:"今日周期实例完成情况"},T.a.createElement(i.a,{className:"m-count"},T.a.createElement(r.a,{span:4},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"失败"),T.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,[L.I.FAILED]),className:"m-count-content font-red"},e.FAILED||0))),T.a.createElement(r.a,{span:5},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"运行中"),T.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,[L.I.RUNNING]),className:"m-count-content font-blue"},e.RUNNING||0))),T.a.createElement(r.a,{span:5},T.a.createElement("section",{className:"m-count-section",style:{width:60}},T.a.createElement("span",{className:"m-count-title"},"未运行 ",T.a.createElement(A.a,{title:"包括等待提交、提交中、等待运行3种状态"},T.a.createElement(_.a,{type:"question-circle-o"}))),T.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,[L.I.UNSUBMIT,L.I.SUBMITTING,L.I.WAITING_RUN]),className:"m-count-content font-organge"},t))),T.a.createElement(r.a,{span:5},T.a.createElement("section",{className:"m-count-section"},T.a.createElement("span",{className:"m-count-title"},"成功"),T.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,[L.I.FINISHED]),className:"m-count-content font-green"},e.FINISHED||0))),T.a.createElement(r.a,{span:5},T.a.createElement("section",{className:"m-count-section",style:{width:60}},T.a.createElement("span",{className:"m-count-title"},"冻结/取消"),T.a.createElement("a",{onClick:this.jumpToOfflineList.bind(this,[L.I.FROZEN,L.I.CANCELED]),className:"m-count-content font-gray"},(e.FROZEN||0)+(e.CANCELED||0))))))))}}]),t}(N.Component),M=Object(I.b)(function(e){return{project:e.project}})(w),F=(a("zmYW"),a("DtFj")),G=a.n(F),H=(a("GBD3"),a("wXtC")),W=a.n(H),B=a("zXZS"),J=a("5T+6"),Y=a("ProS");a("75ce"),a("0o9m"),a("AH3D"),a("Ynxi");var z=function(e){function t(){var e,a,n,i;m()(this,t);for(var o=arguments.length,r=Array(o),l=0;l<o;l++)r[l]=arguments[l];return a=n=h()(this,(e=t.__proto__||s()(t)).call.apply(e,[this].concat(r))),n.state={offline:"",topTiming:[],topError:[],handleTiming:C()(),lineChart:""},n.resize=function(){n.state.lineChart&&n.state.lineChart.resize()},n.loadChartData=function(){var e=n;j.a.getJobGraph().then(function(t){1===t.code&&e.initLineChart(t.data)})},n.getTopTaskTime=function(e){var t=n,a=n.state.handleTiming,i={startTime:a.set({hour:0,minute:0,second:0}).unix(),endTime:a.set({hour:23,minute:59,second:59}).unix()};j.a.getJobTopTime(i).then(function(e){1===e.code&&t.setState({topTiming:e.data})})},n.getTopJobError=function(){var e=n;j.a.getJobTopError().then(function(t){1===t.code&&e.setState({topError:t.data})})},n.getSeries=function(e){var t=[];if(e&&e.y)for(var a=e&&e.type?e.type.data:[],n=0;n<a.length;n++)t.push({name:a[n],symbol:"none",smooth:!0,type:"line",data:e.y[n].data});return t},n.changeHandleTiming=function(e){n.setState({handleTiming:e},function(){n.getTopTaskTime()})},n.topTaskTiming=function(){return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(e,t){return 1===t.isDeleted?e+"(已删除)":T.a.createElement("a",{onClick:n.props.goToTaskDev.bind(n,t.taskId)},e)}},{title:"任务实例类型",dataIndex:"type",key:"type",render:function(e,t){return 1===t.type?"补数据":"周期调度"}},{title:"调度时间",dataIndex:"cycTime",key:"cycTime"},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"执行时长",dataIndex:"runTime",key:"runTime"}]},n.topTaskError=function(){return[{title:"任务名称",dataIndex:"taskName",key:"taskName",render:function(e,t){var a=1===t.isDeleted?e+" (已删除)":e;return T.a.createElement("a",{onClick:n.jumpToOffline.bind(n,e,30,L.u.RUN_FAILED)},a)}},{title:"责任人",dataIndex:"createUser",key:"createUser"},{title:"出错次数",dataIndex:"errorCount",key:"errorCount"}]},n.disabledDate=function(e){return e&&e.valueOf()>C()().add(1,"days").valueOf()},i=a,h()(n,i)}return E()(t,e),d()(t,[{key:"componentDidMount",value:function(){this.loadChartData(),this.getTopTaskTime(),this.getTopJobError()}},{key:"componentWillReceiveProps",value:function(e){var t=e.project,a=this.props.project;a&&t&&a.id!==t.id&&(this.loadChartData(),this.getTopTaskTime(),this.getTopJobError())}},{key:"initLineChart",value:function(e){var t=Y.init(document.getElementById("TaskTrend")),a=Object(x.cloneDeep)(L.A);a.title.text="",a.tooltip.axisPointer.label.formatter="{value}: 00",a.yAxis[0].minInterval=1,a.xAxis[0].axisLabel.formatter="{value} 时",a.legend.data=e&&e.type?e.type.data:[],a.xAxis[0].data=e&&e.x?e.x.data:[],a.series=this.getSeries(e),t.setOption(a),this.setState({lineChart:t})}},{key:"jumpToOffline",value:function(e,t,a){k.g.push({pathname:"/operation/offline-operation",query:{job:e,status:a,date:t}})}},{key:"render",value:function(){var e=this.state,t=(e.offline,e.topTiming),a=e.topError,n=e.handleTiming;return T.a.createElement("div",{className:"box-card",style:{marginTop:"20px"}},T.a.createElement(y.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"今日周期实例完成情况"},T.a.createElement(B.a,{onResize:this.resize},T.a.createElement("article",{id:"TaskTrend",style:{width:"100%",height:"300px"}}))),T.a.createElement(i.a,{className:"m-card",style:{marginTop:"20px"}},T.a.createElement(r.a,{span:"14",style:{paddingRight:"10px"}},T.a.createElement(y.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"执行时长排行",extra:T.a.createElement(W.a,{style:{width:100,float:"right",marginTop:"10px"},format:"YYYY-MM-DD",value:n,disabledDate:this.disabledDate,onChange:this.changeHandleTiming})},T.a.createElement(G.a,{rowKey:"id",pagination:!1,className:"m-table",style:{minHeight:"0"},columns:this.topTaskTiming(),dataSource:t||[]}))),T.a.createElement(r.a,{span:"10",style:{paddingLeft:"10px"}},T.a.createElement(y.a,{noHovering:!0,bordered:!1,loading:!1,className:"shadow",title:"近30天出错排行"},T.a.createElement(G.a,{rowKey:"id",className:"m-table",pagination:!1,style:{minHeight:"0"},columns:this.topTaskError(),dataSource:a||[]})))))}}]),t}(N.Component),P=Object(I.b)(function(e){return{project:e.project}},function(e){var t=Object(J.b)(e);return{goToTaskDev:function(e){t.openTaskInDev(e)}}})(z);a.d(t,"default",function(){return q});var q=function(e){function t(){return m()(this,t),h()(this,(t.__proto__||s()(t)).apply(this,arguments))}return E()(t,e),d()(t,[{key:"render",value:function(){return T.a.createElement("div",{className:"operation-overview",style:{background:"#f2f7fa"}},T.a.createElement(i.a,{style:{marginTop:10}},T.a.createElement(r.a,{span:12},T.a.createElement(M,null)),T.a.createElement(r.a,{span:12},T.a.createElement(U,null))),T.a.createElement(P,null))}}]),t}(N.Component)}}]);