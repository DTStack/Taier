webpackJsonp([3],{1864:function(e,a,t){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var n=t(52),r=(t.n(n),t(51)),s=t.n(r),i=t(5),c=t.n(i),o=t(70),l=(t.n(o),t(69)),_=t.n(l),u=t(24),d=(t.n(u),t(22)),p=t.n(d),m=t(105),E=t.n(m),h=t(56),k=t.n(h),v=t(1328),T=(t.n(v),t(1327)),f=t.n(T),g=t(19),R=(t.n(g),t(18)),O=t.n(R),A=t(8),C=t.n(A),y=t(1),L=t.n(y),D=t(4),b=t.n(D),w=t(3),H=t.n(w),P=t(2),x=t.n(P),N=t(48),S=(t.n(N),t(47)),U=t.n(S),V=t(0),j=t.n(V),z=t(20),I=t(17),F=t(23),M=(t.n(F),t(12)),K=t(35),B=t(665),W=t(969),q=t(705),Q=t(1098),G=t(687),J=t(50),X=t(1916),Y=U.a.confirm,Z=function(e){function a(){var e,n,r,s;L()(this,a);for(var i=arguments.length,c=Array(i),o=0;o<i;o++)c[o]=arguments[o];return n=r=H()(this,(e=a.__proto__||C()(a)).call.apply(e,[this].concat(c))),r.saveTask=function(){var e;return(e=r).__saveTask__REACT_HOT_LOADER__.apply(e,arguments)},r.editorChange=function(){var e;return(e=r).__editorChange__REACT_HOT_LOADER__.apply(e,arguments)},r.debounceChange=t.i(F.debounce)(r.editorChange,300,{maxWait:2e3}),r.editorParamsChange=function(){var e;return(e=r).__editorParamsChange__REACT_HOT_LOADER__.apply(e,arguments)},r.startTask=function(){var e;return(e=r).__startTask__REACT_HOT_LOADER__.apply(e,arguments)},r.loadTreeData=function(){var e;return(e=r).__loadTreeData__REACT_HOT_LOADER__.apply(e,arguments)},s=n,H()(r,s)}return x()(a,e),b()(a,[{key:"componentDidMount",value:function(){}},{key:"__saveTask__REACT_HOT_LOADER__",value:function(){var e=this.props,a=e.currentPage,t=e.dispatch,n=a.resourceList;n&&n.length>0&&(a.resourceIdList=n.map(function(e){return e.id})),a.lockVersion=a.readWriteLockVO.version,K.a.saveTask(a).then(function(e){var n=function(e){O.a.success("任务保存成功"),e.notSynced=!1,t(q.c(e)),a.taskType===J.b.MR&&t(Q.b({id:e.parentId,catalogueType:J.e.TASK}))};if(1===e.code){var r=e.data.readWriteLockVO,s=r.result;0===s?n(e.data):1===s?Y({title:"锁定提醒",content:j.a.createElement("span",null,"文件正在被",r.lastKeepLockUserName,"编辑中，开始编辑时间为",M.a.formatDateTime(r.gmtModified),"。 强制保存可能导致",r.lastKeepLockUserName,"对文件的修改无法正常保存！"),okText:"确定保存",okType:"danger",cancelText:"取消",onOk:function(){var e=function(e){1===e.code&&n(e.data)};K.a.forceUpdateTask(a).then(e)}}):2===s&&Y({title:"保存警告",content:j.a.createElement("span",null,"文件已经被",r.lastKeepLockUserName,"编辑过，编辑时间为",M.a.formatDateTime(r.gmtModified),"。 点击确认按钮会",j.a.createElement(f.a,{color:"orange"},"覆盖"),"您本地的代码，请您提前做好备份！"),okText:"确定覆盖",okType:"danger",cancelText:"取消",onOk:function(){var e={id:a.id,lockVersion:r.version};K.a.getTask(e).then(function(e){if(1===e.code){var a=e.data;a.merged=!0,n(a)}})}})}})}},{key:"__editorChange__REACT_HOT_LOADER__",value:function(e,a){var t=this.props,n=t.currentPage,r=t.dispatch;e!==a&&(n.sqlText=a,n.notSynced=!0,r(q.c(n)))}},{key:"__editorParamsChange__REACT_HOT_LOADER__",value:function(e,a){var t=this.props,n=t.currentPage,r=t.dispatch;e!==a&&(n.taskParams=a,r(q.c(n)))}},{key:"__startTask__REACT_HOT_LOADER__",value:function(){var e=this.props,a=e.currentPage,t=e.dispatch;K.a.startTask({id:a.id,isRestoration:0}).then(function(e){1===e.code&&(a.status=10,t(q.c(k()({},a))),O.a.success("任务已经成功提交！"))})}},{key:"autoSaveTask",value:function(){var e=this;this.timerID=setInterval(function(){if("edit"===e.state.editorState){var a=e.props.currentPage;K.a.saveTask(a).then(function(e){1===e.code&&O.a.success("任务已自动保存")})}},3e4)}},{key:"__loadTreeData__REACT_HOT_LOADER__",value:function(e){var a=this.props.dispatch,t=e.props.data;return new E.a(function(e){a(Q.b(t)),e()})}},{key:"render",value:function(){var e=this.props,a=e.dispatch,t=e.currentPage;t.status;return j.a.createElement(s.a,{className:"task-editor"},j.a.createElement("header",{className:"toolbar bd-bottom clear"},j.a.createElement(_.a,{className:"left"},j.a.createElement(p.a,{onClick:function(){a(W.b(G.d.ADD_TASK_VISIBLE))},title:"创建任务"},j.a.createElement(B.a,{className:"my-icon",type:"focus"})," 新建任务"),j.a.createElement(p.a,{disabled:t.invalid,onClick:this.saveTask,title:"保存任务"},j.a.createElement(B.a,{className:"my-icon",type:"save"}),"保存")),j.a.createElement(_.a,{className:"right"},j.a.createElement(I.Link,{to:"/operation/realtime?tname="+t.name},j.a.createElement(p.a,null,j.a.createElement(B.a,{className:"my-icon",type:"goin"})," 运维")))),j.a.createElement(X.a,c()({},this.props,{ayncTree:this.loadTreeData,editorParamsChange:this.editorParamsChange,editorChange:this.debounceChange})))}}]),a}(V.Component),$=t.i(z.connect)(function(e){var a=e.realtimeTask,t=a.resources,n=a.pages;return{currentPage:a.currentPage,pages:n,resources:t}})(Z);a.default=$;!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(Y,"confirm","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/index.js"),__REACT_HOT_LOADER__.register(Z,"TaskIndex","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/index.js"),__REACT_HOT_LOADER__.register($,"default","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/index.js"))}()},1866:function(e,a,t){"use strict";var n=t(108),r=(t.n(n),t(94)),s=t.n(r),i=t(8),c=t.n(i),o=t(1),l=t.n(o),_=t(4),u=t.n(_),d=t(3),p=t.n(d),m=t(2),E=t.n(m),h=t(0),k=t.n(h);t.d(a,"a",function(){return v});var v=function(e){function a(e){return l()(this,a),p()(this,(a.__proto__||c()(a)).call(this,e))}return E()(a,e),u()(a,[{key:"render",value:function(){return this.props.notSynced?k.a.createElement(s.a,{title:"修改已保存至本地存储但尚未同步到服务器，你可以点击上面的保存按钮立即同步。"},k.a.createElement("span",{style:{display:"inline-block",width:8,height:8,marginRight:8,borderRadius:"50%",border:"3px solid ",borderColor:"#EF5350",opacity:.8}})):k.a.createElement("span",{style:{display:"inline-block",width:8,height:8,marginRight:8,borderRadius:"50%",border:"3px solid ",borderColor:"#00a854",opacity:.6}})}}]),a}(k.a.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&__REACT_HOT_LOADER__.register(v,"SyncBadge","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/sync-badge/index.js")}()},1914:function(e,a,t){"use strict";var n=t(8),r=t.n(n),s=t(1),i=t.n(s),c=t(4),o=t.n(c),l=t(3),_=t.n(l),u=t(2),d=t.n(u),p=t(0),m=t.n(p),E=t(1915),h=t(50),k=t(664);t.d(a,"a",function(){return v});var v=function(e){function a(){return i()(this,a),_()(this,(a.__proto__||r()(a)).apply(this,arguments))}return d()(a,e),o()(a,[{key:"render",value:function(){var e=this.props,a=e.currentPage,t=e.editorChange,n=e.editorFocus,r=e.editorFocusOut,s=a.taskType===h.b.SQL?m.a.createElement(k.a,{key:"main-editor-"+a.id,value:a.sqlText,sync:a.merged||void 0,onFocus:n,focusOut:r,onChange:t}):m.a.createElement(E.a,this.props);return m.a.createElement("div",{className:"editor-container"},s)}}]),a}(p.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&__REACT_HOT_LOADER__.register(v,"EditorContainer","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/editorContainer.js")}()},1915:function(e,a,t){"use strict";var n=t(56),r=t.n(n),s=t(5),i=t.n(s),c=t(29),o=(t.n(c),t(32)),l=t.n(o),_=t(8),u=t.n(_),d=t(1),p=t.n(d),m=t(4),E=t.n(m),h=t(3),k=t.n(h),v=t(2),T=t.n(v),f=t(79),g=(t.n(f),t(71)),R=t.n(g),O=t(34),A=(t.n(O),t(33)),C=t.n(A),y=t(0),L=t.n(y),D=t(50),b=t(705),w=C.a.Item,H=R.a.Group,P=function(e){function a(){return p()(this,a),k()(this,(a.__proto__||u()(a)).apply(this,arguments))}return T()(a,e),E()(a,[{key:"render",value:function(){var e=this.props,a=e.form,t=e.currentPage,n=a.getFieldDecorator;return L.a.createElement("div",{style:{padding:"60px"}},L.a.createElement(C.a,null,L.a.createElement(w,i()({},D.f,{label:"任务名称",hasFeedback:!0}),n("name",{rules:[{required:!0,message:"任务名称不可为空！"},{max:64,message:"任务名称不得超过64个字符！"}],initialValue:t?t.name:""})(L.a.createElement(l.a,null))),L.a.createElement(w,i()({},D.f,{label:"任务类型"}),n("taskType",{rules:[],initialValue:t?t.taskType:0})(L.a.createElement(H,{disabled:!0},L.a.createElement(R.a,{value:0},"SQL任务"),L.a.createElement(R.a,{value:1},"MR任务")))),L.a.createElement(w,i()({},D.f,{label:"资源",hasFeedback:!0}),t&&t.resourceList&&t.resourceList.length>0?t.resourceList[0].resourceName:""),L.a.createElement(w,i()({},D.f,{label:"mainClass"}),n("mainClass",{rules:[{}],initialValue:t&&t.mainClass})(L.a.createElement(l.a,{placeholder:"请输入mainClass"}))),L.a.createElement(w,i()({},D.f,{label:"参数"}),n("exeArgs",{rules:[{}],initialValue:t&&t.exeArgs})(L.a.createElement(l.a,{placeholder:"请输入任务参数"}))),L.a.createElement(w,i()({},D.f,{label:"描述",hasFeedback:!0}),n("taskDesc",{rules:[],initialValue:t?t.taskDesc:""})(L.a.createElement(l.a,{type:"textarea",rows:4})))))}}]),a}(y.Component),x=function(e,a){var t=e.dispatch,n=e.currentPage,s=!1;""===a.name&&(s=!0),a.invalid=s;var i=r()(n,a);t(b.c(i)),t(b.e(i))},N=C.a.create({onValuesChange:x})(P),S=N;a.a=S;!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(w,"FormItem","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"),__REACT_HOT_LOADER__.register(H,"RadioGroup","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"),__REACT_HOT_LOADER__.register(P,"MrEditor","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"),__REACT_HOT_LOADER__.register(x,"taskValueChange","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"),__REACT_HOT_LOADER__.register(N,"wrappedForm","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"),__REACT_HOT_LOADER__.register(S,"default","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/mrEditor.js"))}()},1916:function(e,a,t){"use strict";var n=t(52),r=(t.n(n),t(51)),s=t.n(r),i=t(56),c=t.n(i),o=t(966),l=(t.n(o),t(965)),_=t.n(l),u=t(19),d=(t.n(u),t(18)),p=t.n(d),m=t(8),E=t.n(m),h=t(1),k=t.n(h),v=t(4),T=t.n(v),f=t(3),g=t.n(f),R=t(2),O=t.n(R),A=t(48),C=(t.n(A),t(47)),y=t.n(C),L=t(73),D=(t.n(L),t(72)),b=t.n(D),w=t(0),H=t.n(w),P=t(685),x=(t.n(P),t(12)),N=t(35),S=t(50),U=t(1866),V=t(664),j=(t(186),t(705)),z=t(1098),I=t(1914),F=t(1917);t.d(a,"a",function(){return B});var M=b.a.TabPane,K=y.a.confirm,B=function(e){function a(){var e,t,n,r;k()(this,a);for(var s=arguments.length,i=Array(s),c=0;c<s;c++)i[c]=arguments[c];return t=n=g()(this,(e=a.__proto__||E()(a)).call.apply(e,[this].concat(i))),n.state={selected:"",expanded:!1},n.onChange=function(){var e;return(e=n).__onChange__REACT_HOT_LOADER__.apply(e,arguments)},n.onEdit=function(){var e;return(e=n).__onEdit__REACT_HOT_LOADER__.apply(e,arguments)},n.mapPanels=function(){var e;return(e=n).__mapPanels__REACT_HOT_LOADER__.apply(e,arguments)},n.tabClick=function(){var e;return(e=n).__tabClick__REACT_HOT_LOADER__.apply(e,arguments)},n.unLock=function(){var e;return(e=n).__unLock__REACT_HOT_LOADER__.apply(e,arguments)},n.handleCustomParamsChange=function(){var e;return(e=n).__handleCustomParamsChange__REACT_HOT_LOADER__.apply(e,arguments)},r=t,g()(n,r)}return O()(a,e),T()(a,[{key:"__onChange__REACT_HOT_LOADER__",value:function(e){var a=this.props,t=a.dispatch,n=a.pages,r=parseInt(e,10),s=n&&n.find(function(e){return e.id===r});s&&t(j.c(s))}},{key:"__onEdit__REACT_HOT_LOADER__",value:function(e,a){var t=this.props,n=t.pages,r=t.currentPage,s=t.dispatch;switch(a){case"remove":r.notSynced?K({title:"部分任务修改尚未同步到服务器，是否强制关闭 ?",content:"强制关闭将丢弃这些修改数据",onOk:function(){s(j.d(parseInt(e,10),n,r))},onCancel:function(){}}):s(j.d(parseInt(e,10),n,r))}}},{key:"__mapPanels__REACT_HOT_LOADER__",value:function(e){return e&&e.length>0?e.map(function(e){var a=H.a.createElement("span",null,H.a.createElement(U.a,{notSynced:e.notSynced}),e.name);return H.a.createElement(M,{style:{height:"0px"},tab:a,key:e.id})}):[]}},{key:"__tabClick__REACT_HOT_LOADER__",value:function(e){var a=this.state,t=a.selected,n=a.expanded;e===t&&n?(this.setState({selected:"",expanded:!1}),this.SideBench.style.width="30px"):e!==t&&(this.SideBench.style.width="500px",this.setState({selected:e,expanded:!0}))}},{key:"__unLock__REACT_HOT_LOADER__",value:function(){var e=this.props,a=e.currentPage,n=e.dispatch,r=a.readWriteLockVO;K({title:"解锁提醒",content:"文件正在被"+r.lastKeepLockUserName+"编辑中，开始编辑时间\n                "+x.a.formatDateTime(r.gmtModified)+"。\n                强制编辑可能导致"+r.lastKeepLockUserName+"对该文件的修改无法保存！\n            ",okText:"确定",okType:"danger",cancelText:"取消",onOk:function(){var e={fileId:a.id,type:S.l.STREAM_TASK,lockVersion:r.version};N.a.unlockFile(e).then(function(e){if(1===e.code){var r=e.data;r.getLock?p.a.success("文件解锁成功！"):y.a.error({title:"解锁失败",content:"文件正在被"+r.lastKeepLockUserName+"编辑中!开始编辑时间\n                                "+x.a.formatDateTime(r.gmtModified)+"."});var s={id:a.id};N.a.getTask(s).then(function(e){if(1===e.code){var r=e.data;r.merged=!0;var s={id:a.id,readWriteLockVO:r.readWriteLockVO};n(j.c(r)),n(t.i(z.c)(s))}})}})},onCancel:function(){}})}},{key:"renderLock",value:function(e){return e.readWriteLockVO&&!e.readWriteLockVO.getLock?H.a.createElement("div",{className:"lock-layer"},H.a.createElement(_.a,{style:{position:"absolute",top:"10px",left:"35%",zIndex:"999"},showIcon:!0,message:H.a.createElement("span",null,"当前文件为只读状态！",H.a.createElement("a",{onClick:this.unLock},"解锁")),type:"warning"})):null}},{key:"__handleCustomParamsChange__REACT_HOT_LOADER__",value:function(e){var a=this.props,t=a.currentPage,n=a.dispatch,r=c()(t,e);n(j.c(r))}},{key:"render",value:function(){var e=this,a=this.props,t=a.currentPage,n=a.pages,r=a.router,i=a.editorFocus,c=a.editorFocusOut,o=a.editorParamsChange;0===n.length&&r.push("/realtime");var l=this.mapPanels(n);return H.a.createElement(s.a,{className:"task-browser"},H.a.createElement("div",{className:"browser-content"},H.a.createElement(b.a,{hideAdd:!0,onTabClick:this.onChange,activeKey:""+t.id,type:"editable-card",className:"browser-tabs",onEdit:this.onEdit},l),this.renderLock(t),H.a.createElement(I.a,this.props),H.a.createElement("div",{className:"m-siderbench bd-left",ref:function(a){e.SideBench=a}},H.a.createElement(b.a,{activeKey:this.state.selected,type:"card",className:"task-params",tabPosition:"right",onTabClick:this.tabClick},H.a.createElement(M,{tab:H.a.createElement("span",{className:"title-vertical"},"任务详情"),key:"params1"},H.a.createElement(F.a,this.props)),H.a.createElement(M,{tab:H.a.createElement("span",{className:"title-vertical"},"环境参数"),key:"params2"},H.a.createElement(V.a,{key:"params-editor",options:S.h,onFocus:i,focusOut:c,value:t.taskParams,onChange:o}))))))}}]),a}(w.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(M,"TabPane","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/taskBrowser.js"),__REACT_HOT_LOADER__.register(K,"confirm","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/taskBrowser.js"),__REACT_HOT_LOADER__.register(B,"TaskBrowser","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/taskBrowser.js"))}()},1917:function(e,a,t){"use strict";var n=t(48),r=(t.n(n),t(47)),s=t.n(r),i=t(52),c=(t.n(i),t(51)),o=t.n(c),l=t(70),_=(t.n(l),t(69)),u=t.n(_),d=t(1328),p=(t.n(d),t(1327)),m=t.n(p),E=t(19),h=(t.n(E),t(18)),k=t.n(h),v=t(8),T=t.n(v),f=t(1),g=t.n(f),R=t(4),O=t.n(R),A=t(3),C=t.n(A),y=t(2),L=t.n(y),D=t(26),b=(t.n(D),t(25)),w=t.n(b),H=t(0),P=t.n(H),x=t(12),N=t(35),S=t(705);t.d(a,"a",function(){return V});var U=w.a.Option,V=function(e){function a(){var e,t,n,r;g()(this,a);for(var s=arguments.length,i=Array(s),c=0;c<s;c++)i[c]=arguments[c];return t=n=C()(this,(e=a.__proto__||T()(a)).call.apply(e,[this].concat(i))),n.state={visibleAlterRes:!1,resList:[]},n.handleChange=function(){var e;return(e=n).__handleChange__REACT_HOT_LOADER__.apply(e,arguments)},n.alterRes=function(){var e;return(e=n).__alterRes__REACT_HOT_LOADER__.apply(e,arguments)},r=t,C()(n,r)}return L()(a,e),O()(a,[{key:"componentWillReceiveProps",value:function(e){var a=e.currentPage,t=this.props.currentPage;if(a.id!==t.id){var n=a.resourceList.length>0?a.resourceList.map(function(e){return e.id}):[];this.setState({resList:n})}}},{key:"__handleChange__REACT_HOT_LOADER__",value:function(e){this.setState({resList:e})}},{key:"__alterRes__REACT_HOT_LOADER__",value:function(){var e=this,a=this.props.currentPage,t=this.state.resList;if(t=Array.isArray(t)?t:[t],0===t.length)return void k.a.info("您没有选择任何资源！");a&&a.id&&t.length>0&&N.a.updateTaskRes({id:a.id,resources:t}).then(function(t){1===t.code&&(k.a.success("资源修改成功！"),e.reloadTask(a.id),e.setState({visibleAlterRes:!1,resList:[]}))})}},{key:"reloadTask",value:function(e){var a=this.props.dispatch;N.a.getTask({taskId:e}).then(function(e){1===e.code&&(a(S.c(e.data)),a(S.e(e.data)))})}},{key:"render",value:function(){var e=this,a=this.state,t=a.visibleAlterRes,n=a.resList,r=this.props,i=r.resources,c=r.currentPage,l=c.resourceList&&c.resourceList.map(function(e){return P.a.createElement(m.a,{key:e.id,color:"blue"},e.resourceName)}),_=i&&i.map(function(e){return P.a.createElement(U,{value:e.id,key:e.id,name:e.resourceName},e.resourceName)});return P.a.createElement(o.a,{className:"task-info"},P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"任务名称："),P.a.createElement(u.a,{span:"14"},c.name)),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"任务类型："),P.a.createElement(u.a,{span:"14"},0===c.taskType?"SQL任务":"MR任务")),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"资源："),P.a.createElement(u.a,{span:"14",style:{marginTop:"10px"}},l)),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"创建人员："),P.a.createElement(u.a,{span:"14"},c.createUserName)),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"创建时间："),P.a.createElement(u.a,{span:"14"},x.a.formatDateTime(c.gmtCreate))),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"最近修改时间："),P.a.createElement(u.a,{span:"14"},x.a.formatDateTime(c.gmtModified))),P.a.createElement(o.a,null,P.a.createElement(u.a,{span:"10",className:"txt-right"},"描述："),P.a.createElement(u.a,{span:"14"},c.taskDesc)),P.a.createElement(s.a,{title:"修改任务资源",wrapClassName:"vertical-center-modal",visible:t,onCancel:function(){e.setState({visibleAlterRes:!1})},onOk:this.alterRes},P.a.createElement(w.a,{mode:0===c.taskType?"multiple":"",style:{width:"100%"},showSearch:!0,value:n,placeholder:"请选择资源",optionFilterProp:"name",onChange:this.handleChange},_)))}}]),a}(H.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(U,"Option","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/taskDetail.js"),__REACT_HOT_LOADER__.register(V,"TaskDetail","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/task/realtime/taskDetail.js"))}()}});