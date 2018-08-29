(window.webpackJsonp=window.webpackJsonp||[]).push([[23],{pZSc:function(e,a,t){"use strict";t.r(a);var n=t("Yz+Y"),l=t.n(n),r=t("iCc5"),i=t.n(r),o=t("V7oC"),s=t.n(o),d=t("FYw3"),c=t.n(d),u=t("mRg0"),h=t.n(u),p=t("sbe7"),m=t.n(p),f=t("5sfi"),E=(t("fwXI"),t("CC+v")),y=t.n(E),v=(t("MaXC"),t("4IMT")),k=t.n(v),g=(t("tL+A"),t("QpBz")),T=t.n(g),b=t("P2sY"),C=t.n(b),D=(t("93XW"),t("d1El")),F=t.n(D),S=(t("FGdI"),t("Pbn2")),_=t.n(S),M=t("QbLZ"),O=t.n(M),I=(t("7kJ1"),t("qPIi")),N=t.n(I),R=(t("cUip"),t("iJl9")),L=t.n(R),P=(t("ek7I"),t("FAat")),V=t.n(P),A=(t("nTym"),t("qu0K")),w=t.n(A),x=t("/MKj"),U=t("sVOr"),q=t("vbTb"),Y=t("68PS"),z=t("5T+6"),H=t("djHj"),j=t("ZQcs"),G=w.a.Item,W=V.a.Option,Z=L.a.TextArea,K=N.a.Group,J=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.loadTaskTypes=function(){U.a.getTaskTypes().then(function(e){1===e.code&&t.setState({taskTypes:e.data||[]})})},t.handleTaskTypeChange=t.handleTaskTypeChange.bind(t),t.isEditExist=!1,t.state={value:0,operateModel:"",taskTypes:[]},t._resChange=!1,t}return h()(a,e),s()(a,[{key:"componentWillMount",value:function(){var e=this.props.defaultData;this.loadTaskTypes(),this.setState({operateModel:e&&e.operateModel?e.operateModel:H.f.RESOURCE})}},{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"handleResSelectTreeChange",value:function(e){this._resChange=!0,this.props.form.setFieldsValue({resourceIdList:e}),this.props.form.validateFields(["resourceIdList"])}},{key:"handleTaskTypeChange",value:function(e){this.setState({value:e})}},{key:"handleOperateModel",value:function(e){this.setState({operateModel:e.target.value})}},{key:"render",value:function(){var e=this.props.form,a=e.getFieldDecorator,t=(e.getFieldValue,this.props.defaultData),n=this.state,l=n.taskTypes,r=n.operateModel,i=void 0===t,o=!i&&void 0===t.id;this.isEditExist=!i&&!o;var s=i?this.state.value:o?this.state.value:t.taskType,d=l.map(function(e){return m.a.createElement(W,{key:e.key,value:e.key},e.value)}),c=m.a.createElement("div",null,"功能释义：",m.a.createElement("br",null),"向导模式：便捷、简单，可视化字段映射，快速完成同步任务配置",m.a.createElement("br",null),"脚本模式：全能 高效，可深度调优，支持全部数据源",m.a.createElement("br",null),m.a.createElement("a",{href:H.h.DATA_SOURCE,target:"blank"},"查看支持的数据源")),u=s===H.v.MR,h=s===H.v.PYTHON,p=s==H.v.SYNC,f=s==H.v.DEEP_LEARNING,E=s==H.v.PYTHON_23,y=s==H.v.ML,v=s==H.v.HAHDOOPMR,k=y||v||u?H.o.JAR:h||E||f?H.o.PY:"",g=i?this.props.treeData.id:o?t.parentId:t.nodePid;return m.a.createElement(w.a,null,m.a.createElement(G,O()({},H.y,{label:"任务名称",hasFeedback:!0}),a("name",{rules:[{required:!0,message:"任务名称不可为空！"},{max:64,message:"任务名称不得超过64个字符！"},{pattern:/^[A-Za-z0-9_]+$/,message:"任务名称只能由字母、数字、下划线组成!"}],initialValue:i?void 0:o?void 0:t.name})(m.a.createElement(L.a,{placeholder:"请输入任务名称"}))),m.a.createElement(G,O()({},H.y,{label:"任务类型"}),a("taskType",{rules:[{required:!0,message:"请选择任务类型"}],initialValue:this.isEditExist?t.taskType:l.length>0&&l[0].key})(m.a.createElement(V.a,{disabled:!i&&!o,onChange:this.handleTaskTypeChange},d)),u&&m.a.createElement(F.a,{title:m.a.createElement("div",null,m.a.createElement("p",null,"支持基于Spark API的Java、Scala处理程序"))},m.a.createElement(_.a,{className:"formItem_inline_icon",type:"question-circle-o"})),y&&m.a.createElement(F.a,{title:m.a.createElement("div",null,m.a.createElement("p",null,"支持基于Spark MLLib的机器学习任务"))},m.a.createElement(_.a,{className:"formItem_inline_icon",type:"question-circle-o"}))),f&&m.a.createElement(G,O()({},H.y,{label:"框架类型"}),a("learningType",{rules:[{required:!0,message:"请选择框架类型"}],initialValue:this.isEditExist?t.learningType:H.i.TENSORFLOW})(m.a.createElement(K,{disabled:!i&&!o},m.a.createElement(N.a,{key:H.i.TENSORFLOW,value:H.i.TENSORFLOW},"TensorFlow"),m.a.createElement(N.a,{key:H.i.MXNET,value:H.i.MXNET},"MXNet")))),(f||E)&&m.a.createElement("div",null,m.a.createElement(G,O()({},H.y,{label:"python版本"}),a("pythonVersion",{rules:[{required:!0,message:"请选择python版本"}],initialValue:this.isEditExist?t.pythonVersion:H.m.PYTHON2})(m.a.createElement(K,{disabled:!i&&!o},m.a.createElement(N.a,{key:H.m.PYTHON2,value:H.m.PYTHON2},"python2.x"),m.a.createElement(N.a,{key:H.m.PYTHON3,value:H.m.PYTHON3},"python3.x")))),m.a.createElement(G,O()({},H.y,{label:"操作模式"}),a("operateModel",{rules:[{required:!0,message:"请选择操作模式"}],initialValue:r})(m.a.createElement(K,{disabled:!i&&!o,onChange:this.handleOperateModel.bind(this)},m.a.createElement(N.a,{key:H.f.RESOURCE,value:H.f.RESOURCE},"资源上传"),m.a.createElement(N.a,{key:H.f.EDIT,value:H.f.EDIT},"WEB编辑"))))),f&&m.a.createElement("div",null,m.a.createElement(G,O()({},H.y,{label:"数据输入路径"}),a("input",{initialValue:this.isEditExist?t.input:""})(m.a.createElement(L.a,{placeholder:"请输入数据输入路径"})),m.a.createElement(Y.a,{doc:"inputTaskHelp"})),m.a.createElement(G,O()({},H.y,{label:"模型输出路径"}),a("output",{initialValue:this.isEditExist?t.output:""})(m.a.createElement(L.a,{placeholder:"请输入模型输出路径"})),m.a.createElement(Y.a,{doc:"outputTaskHelp"}))),(f||E)&&m.a.createElement(G,O()({},H.y,{label:"参数"}),a("options",{initialValue:this.isEditExist?t.options:""})(m.a.createElement(Z,{autosize:{minRows:2,maxRows:4},placeholder:"\b输入命令行参数，多个参数用空格隔开"}))),(v||y||u||h||(f||E)&&r==H.f.RESOURCE)&&m.a.createElement("span",null,m.a.createElement(G,O()({},H.y,{label:"资源",hasFeedback:!0}),a("resourceIdList",{rules:[{required:!0,message:"请选择关联资源"},{validator:this.checkNotDir.bind(this)}],initialValue:i?void 0:o?void 0:t.resourceList[0]&&t.resourceList[0].id})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.RESOURCE,ispicker:!0,placeholder:"请选择关联资源",isFilepicker:!0,acceptRes:k,treeData:this.props.resTreeData,onChange:this.handleResSelectTreeChange.bind(this),defaultNode:i?void 0:o?void 0:t.resourceList[0]&&t.resourceList[0].resourceName})),(v||y||u)&&m.a.createElement(G,O()({},H.y,{label:"mainClass",hasFeedback:!0}),a("mainClass",{rules:[{required:!0,message:"mainClass 不可为空！"},{pattern:/^[A-Za-z0-9_.-]+$/,message:"mainClass 只能由字母、数字、下划线、分隔点组成!"}],initialValue:i?void 0:o?void 0:t.mainClass})(m.a.createElement(L.a,{placeholder:"请输入 mainClass"}))),(v||y||u||h)&&m.a.createElement(G,O()({},H.y,{label:"参数",hasFeedback:!0}),a("exeArgs",{rules:[{pattern:/^[A-Za-z0-9_\/-]+$/,message:"任务参数只能由字母、数字、下划线、斜杠组成!"}],initialValue:i?void 0:o?void 0:t.exeArgs})(m.a.createElement(L.a,{placeholder:"请输入任务参数"})))),p&&m.a.createElement(G,O()({},H.y,{label:"配置模式"}),a("createModel",{rules:[{required:!0,message:"请选择配置模式"}],initialValue:this.isEditExist?t.createModel:H.e.GUIDE})(m.a.createElement(K,{disabled:!i&&!o},m.a.createElement(N.a,{key:H.e.GUIDE,value:H.e.GUIDE},"向导模式"),m.a.createElement(N.a,{key:H.e.SCRIPT,value:H.e.SCRIPT},"脚本模式"))),m.a.createElement(F.a,{placement:"right",title:c},m.a.createElement(_.a,{type:"question-circle-o"}))),m.a.createElement(G,O()({},H.y,{label:"存储位置"}),a("nodePid",{rules:[{required:!0,message:"存储位置必选！"}],initialValue:g})(m.a.createElement(j.a,{ispicker:!0,id:"Task_dev_catalogue",type:H.k.TASK_DEV,treeData:this.props.treeData,onChange:this.handleSelectTreeChange.bind(this),defaultNode:i?this.props.treeData.name:o?this.getFolderName(t.parentId):this.getFolderName(t.nodePid)}))),m.a.createElement(G,O()({},H.y,{label:"描述",hasFeedback:!0}),a("taskDesc",{rules:[{max:200,message:"描述请控制在200个字符以内！"}],initialValue:i?void 0:o?void 0:t.taskDesc})(m.a.createElement(L.a,{type:"textarea",rows:4,placeholder:"请输入任务描述"}))),m.a.createElement(G,{style:{display:"none"}},a("computeType",{initialValue:1})(m.a.createElement(L.a,{type:"hidden"}))))}},{key:"checkNotDir",value:function(e,a,t){var n=void 0;!function e(t){t.forEach(function(t,l){t.id===a?n=t.type:e(t.children||[])})}([this.props.resTreeData]),"folder"===n&&t("请选择具体文件, 而非文件夹"),t()}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.treeData]),a}}]),a}(m.a.Component),$=w.a.create()(J),X=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.state={loading:!1},t.handleSubmit=t.handleSubmit.bind(t),t.handleCancel=t.handleCancel.bind(t),t.dtcount=0,t}return h()(a,e),s()(a,[{key:"handleSubmit",value:function(){var e=this,a=this.props,t=a.addOfflineTask,n=a.defaultData,l=this.form,r=void 0===n,i=!r&&void 0===n.id,o=!r&&!i;l.validateFields(function(a,r){a||(r.lockVersion=0,r.version=0,r.resourceIdList&&(r.resourceIdList=[r.resourceIdList]),n&&n.id&&(r.id=n.id,r.version=n.version,r.readWriteLockVO=C()({},n.readWriteLockVO)),e.setState({loading:!0}),t(r,o,n).then(function(a){e.setState({loading:!1}),a&&(T.a.success("操作成功"),l.resetFields(),e.closeModal())}))})}},{key:"handleCancel",value:function(){this.closeModal()}},{key:"closeModal",value:function(){this.props.toggleCreateTask(),this.props.emptyModalDefault(),this.dtcount++}},{key:"render",value:function(){var e=this,a=this.props,t=a.isModalShow,n=a.taskTreeData,l=a.resourceTreeData,r=a.defaultData,i=this.state.loading,o=!0;return r&&r.name&&(o=!1),m.a.createElement("div",null,m.a.createElement(y.a,{title:o?"新建离线任务":"编辑离线任务",key:this.dtcount,visible:t,maskClosable:!1,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.handleCancel},"取消"),m.a.createElement(k.a,{key:"submit",type:"primary",size:"large",loading:i,onClick:this.handleSubmit.bind(this)}," 确认 ")],onCancel:this.handleCancel},m.a.createElement($,{ref:function(a){return e.form=a},treeData:n,resTreeData:l,defaultData:r})))}}]),a}(m.a.Component),Q=Object(x.b)(function(e){return{isModalShow:e.offlineTask.modalShow.createTask,taskTreeData:e.offlineTask.taskTree,currentTab:e.offlineTask.workbench.currentTab,defaultData:e.offlineTask.modalShow.defaultData,resourceTreeData:e.offlineTask.resourceTree}},function(e){var a=Object(z.b)(e);return{toggleCreateTask:function(){a.toggleCreateTask()},addOfflineTask:function(t,n,l){return U.a.addOfflineTask(t).then(function(t){if(1===t.code){if(n){var r=C()(l,t.data);r.originPid=l.nodePid,e({type:q.n.EDIT_FOLDER_CHILD,payload:r}),U.a.getOfflineTaskDetail({id:l.id}).then(function(e){1===e.code&&a.updateTabData(e.data)})}else a.openTaskInDev(t.data.id);return a.loadTreeNode(t.data.parentId,H.k.TASK_DEV),!0}})},emptyModalDefault:function(){e({type:q.f.EMPTY_MODAL_DEFAULT})}}})(X),B=t("MgzW"),ee=t.n(B),ae=w.a.Item,te=V.a.Option,ne=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.renderFormItem=function(){var e=t.state.file,a=t.props.form.getFieldDecorator,n=t.props,l=n.defaultData,r=n.isEditExist,i=n.isCreateFromMenu,o=n.isCreateNormal;return n.isCoverUpload?[m.a.createElement(ae,O()({},H.y,{label:"选择目标替换资源",key:"id",hasFeedback:!0}),a("id",{rules:[{required:!0,message:"替换资源为必选！"},{validator:t.checkNotDir.bind(t)}],initialValue:o?t.props.treeData.id:i?l.parentId:r?l.id:void 0})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.RESOURCE,ispicker:!0,isFilepicker:!0,treeData:t.props.treeData,onChange:t.handleCoverTargetChange.bind(t),defaultNode:o?t.props.treeData.name:i?t.getFolderName(l.parentId):r?l.name:void 0})),m.a.createElement(ae,O()({},H.y,{label:"上传",key:"file",hasFeedback:!0}),a("file",{rules:[{required:!0,message:"请选择上传文件"},{validator:t.validateFileType}]})(m.a.createElement("div",null,m.a.createElement("label",{style:{lineHeight:"28px"},className:"ant-btn",htmlFor:"myOfflinFile"},"选择文件"),m.a.createElement("span",null," ",e.files&&e.files[0].name),m.a.createElement("input",{name:"file",type:"file",id:"myOfflinFile",onChange:t.fileChange,style:{display:"none"}})))),m.a.createElement(ae,O()({},H.y,{label:"描述",key:"resourceDesc",hasFeedback:!0}),a("resourceDesc",{rules:[{max:200,message:"描述请控制在200个字符以内！"}],initialValue:""})(m.a.createElement(L.a,{type:"textarea",rows:4})))]:[m.a.createElement(ae,O()({},H.y,{label:"资源名称",hasFeedback:!0,key:"resourceName"}),a("resourceName",{rules:[{required:!0,message:"资源名称不可为空!"},{pattern:/^[A-Za-z0-9_-]+$/,message:"资源名称只能由字母、数字、下划线组成!"},{max:20,message:"资源名称不得超过20个字符!"}]})(m.a.createElement(L.a,{placeholder:"请输入资源名称"}))),m.a.createElement(ae,O()({},H.y,{label:"资源类型",key:"resourceType",hasFeedback:!0}),a("resourceType",{rules:[{required:!0,message:"资源类型必选！"}],initialValue:H.o.JAR})(m.a.createElement(V.a,{onChange:t.changeFileType},m.a.createElement(te,{key:H.o.JAR,value:H.o.JAR},"jar"),m.a.createElement(te,{key:H.o.PY,value:H.o.PY},"python")))),m.a.createElement(ae,O()({},H.y,{label:"上传",key:"file",hasFeedback:!0}),a("file",{rules:[{required:!0,message:"请选择上传文件"},{validator:t.validateFileType}]})(m.a.createElement("div",null,m.a.createElement("label",{style:{lineHeight:"28px"},className:"ant-btn",htmlFor:"myOfflinFile"},"选择文件"),m.a.createElement("span",null," ",e.files&&e.files[0].name),m.a.createElement("input",{name:"file",type:"file",id:"myOfflinFile",accept:t.state.accept,onChange:t.fileChange,style:{display:"none"}})))),m.a.createElement(ae,O()({},H.y,{label:"选择存储位置",key:"nodePid",hasFeedback:!0}),a("nodePid",{rules:[{required:!0,message:"存储位置必选！"}],initialValue:o?t.props.treeData.id:i?l.parentId:void 0})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.RESOURCE,ispicker:!0,treeData:t.props.treeData,onChange:t.handleSelectTreeChange.bind(t),defaultNode:o?t.props.treeData.name:i?t.getFolderName(l.parentId):void 0})),m.a.createElement(ae,O()({},H.y,{label:"描述",key:"resourceDesc",hasFeedback:!0}),a("resourceDesc",{rules:[{max:200,message:"描述请控制在200个字符以内！"}],initialValue:""})(m.a.createElement(L.a,{type:"textarea",rows:4}))),m.a.createElement(ae,{key:"computeType",style:{display:"none"}},a("computeType",{initialValue:1})(m.a.createElement(L.a,{type:"hidden"})))]},t.changeFileType=t.changeFileType.bind(t),t.fileChange=t.fileChange.bind(t),t.state={file:"",accept:".jar",fileType:H.o.JAR},t}return h()(a,e),s()(a,[{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"handleCoverTargetChange",value:function(e){this.props.form.setFieldsValue({id:e}),this.props.form.validateFields(["id"])}},{key:"validateFileType",value:function(e,a,t){a&&!/\.(jar|sql|py|egg|zip)$/.test(a.toLocaleLowerCase())&&t("资源文件只能是Jar、SQL、egg、Zip或者Python文件!"),t()}},{key:"changeFileType",value:function(e){var a="";switch(e){case H.o.JAR:a=".jar";break;case H.o.PY:a=".py,.zip,.egg";break;default:a=""}this.setState({accept:a,fileType:e})}},{key:"fileChange",value:function(e){var a=e.target;this.setState({file:a}),this.props.handleFileChange(a)}},{key:"render",value:function(){return m.a.createElement(w.a,null,this.renderFormItem())}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.treeData]),a}},{key:"checkNotDir",value:function(e,a,t){var n=void 0;!function e(t){t.forEach(function(t,l){t.id===a?n=t.type:e(t.children||[])})}([this.props.treeData]),"folder"===n&&t("请选择具体文件, 而非文件夹"),t()}}]),a}(m.a.Component),le=w.a.create()(ne),re=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.handleSubmit=t.handleSubmit.bind(t),t.handleCancel=t.handleCancel.bind(t),t.state={file:""},t.dtcount=0,t}return h()(a,e),s()(a,[{key:"handleSubmit",value:function(){var e=this,a=this.form;a.validateFields(function(t,n){t||(n.file=e.state.file.files[0],e.setState({loading:!0}),e.props.addResource(n).then(function(t){e.setState({loading:!1}),t&&(e.closeModal(),e.setState({file:""}),a.resetFields())}))})}},{key:"handleCancel",value:function(){var e=this.props;e.isModalShow,e.toggleUploadModal;this.closeModal()}},{key:"closeModal",value:function(){this.dtcount++,this.props.toggleUploadModal(),this.props.emptyModalDefault()}},{key:"handleFileChange",value:function(e){this.setState({file:e})}},{key:"render",value:function(){var e=this,a=this.props,t=a.isModalShow,n=(a.toggleUploadModal,a.resourceTreeData),l=a.defaultData,r=a.isCoverUpload,i=this.state.loading,o=void 0===l,s=!o&&void 0===l.id,d=!o&&!s;return m.a.createElement("div",null,m.a.createElement(y.a,{title:r?"替换离线资源":d?"编辑资源":"上传离线计算资源",visible:t,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.handleCancel},"取消"),m.a.createElement(k.a,{key:"submit",loading:i,type:"primary",size:"large",onClick:this.handleSubmit}," 确认 ")],key:this.dtcount,onCancel:this.handleCancel},m.a.createElement(le,{ref:function(a){return e.form=a},treeData:n,handleFileChange:this.handleFileChange.bind(this),defaultData:l,isCreateNormal:o,isCreateFromMenu:s,isCoverUpload:r,isEditExist:d})))}}]),a}(m.a.Component),ie=Object(x.b)(function(e){return{isModalShow:e.offlineTask.modalShow.upload,isCoverUpload:e.offlineTask.modalShow.isCoverUpload,resourceTreeData:e.offlineTask.resourceTree,defaultData:e.offlineTask.modalShow.defaultData}},function(e){return{toggleUploadModal:function(){e({type:q.f.TOGGLE_UPLOAD})},addResource:function(a){return U.a.addOfflineResource(a).then(function(a){var t=a.data;if(1===a.code)return T.a.success("资源上传成功！"),e({type:q.g.ADD_FOLDER_CHILD,payload:t}),!0})},emptyModalDefault:function(){e({type:q.f.EMPTY_MODAL_DEFAULT})}}})(re),oe=w.a.Item,se=function(e){function a(e){return i()(this,a),c()(this,(a.__proto__||l()(a)).call(this,e))}return h()(a,e),s()(a,[{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"render",value:function(){var e=this.props.form.getFieldDecorator,a=this.props.defaultData,t=void 0===a;return m.a.createElement(w.a,null,m.a.createElement(oe,O()({label:"目录名称"},H.y,{hasFeedback:!0}),e("nodeName",{rules:[{max:20,message:"项目名称不得超过20个字符！"},{required:!0,message:"文件夹名称不能为空"}],initialValue:t?void 0:a.name})(m.a.createElement(L.a,{type:"text",placeholder:"文件夹名称"}))),m.a.createElement(oe,O()({label:"选择目录位置"},H.y),e("nodePid",{rules:[{required:!0,message:"请选择目录位置"}],initialValue:t?this.props.treeData.id:a.parentId})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:this.props.cateType,ispicker:!0,treeData:this.props.treeData,onChange:this.handleSelectTreeChange.bind(this),defaultNode:t?this.props.treeData.name:this.getFolderName(a.parentId)})))}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.treeData]),a}}]),a}(m.a.Component),de=w.a.create()(se),ce=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.handleSubmit=t.handleSubmit.bind(t),t.handleCancel=t.handleCancel.bind(t),t.dtcount=0,t}return h()(a,e),s()(a,[{key:"handleSubmit",value:function(){var e=this,a=this.props,t=a.cateType,n=a.defaultData,l=this.form;l.validateFields(function(a,r){a||(e.isCreate?e.props.addOfflineCatalogue(r,t).then(function(a){a&&(e.closeModal(),l.resetFields())}):e.props.editOfflineCatalogue(ee()(r,{id:n.id}),n,t).then(function(a){a&&(e.closeModal(),l.resetFields())}))})}},{key:"handleCancel",value:function(){var e=this.props;e.isModalShow,e.toggleCreateFolder;this.closeModal()}},{key:"closeModal",value:function(){var e=this.props,a=e.toggleCreateFolder,t=e.emptyModalDefault;this.dtcount++,t(),a()}},{key:"getTreeData",value:function(e){switch(e){case H.k.TASK:case H.k.TASK_DEV:return this.props.taskTreeData;case H.k.RESOURCE:return this.props.resourceTreeData;case H.k.COSTOMFUC:case H.k.FUNCTION:case H.k.SYSFUC:return this.props.functionTreeData;case H.k.SCRIPT:return this.props.scriptTreeData;default:return this.props.taskTreeData}}},{key:"render",value:function(){var e=this,a=this.props,t=a.isModalShow,n=(a.toggleCreateFolder,a.cateType),l=a.defaultData;return l&&l.name?this.isCreate=!1:this.isCreate=!0,m.a.createElement("div",null,m.a.createElement(y.a,{title:this.isCreate?"新建文件夹":"编辑文件夹",visible:t,key:this.dtcount,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.handleCancel},"取消"),m.a.createElement(k.a,{key:"submit",type:"primary",size:"large",onClick:this.handleSubmit}," 确认 ")],onCancel:this.handleCancel},m.a.createElement(de,{ref:function(a){return e.form=a},treeData:this.getTreeData(n),defaultData:l})))}}]),a}(m.a.Component),ue=Object(x.b)(function(e){var a=e.offlineTask;return{isModalShow:e.offlineTask.modalShow.createFolder,cateType:e.offlineTask.modalShow.cateType,defaultData:e.offlineTask.modalShow.defaultData,taskTreeData:a.taskTree,resourceTreeData:a.resourceTree,functionTreeData:a.functionTree,scriptTreeData:a.scriptTree}},function(e){var a=Object(z.b)(e);return{toggleCreateFolder:function(){e({type:q.f.TOGGLE_CREATE_FOLDER})},addOfflineCatalogue:function(e,t){return U.a.addOfflineCatalogue(e).then(function(n){if(1===n.code)return a.loadTreeNode(e.nodePid,t),!0})},editOfflineCatalogue:function(e,t,n){return U.a.editOfflineCatalogue(e).then(function(l){if(1===l.code){var r=t;switch(n){case H.k.TASK:case H.k.TASK_DEV:q.n;break;case H.k.RESOURCE:q.g;break;case H.k.FUNCTION:case H.k.SYSFUC:case H.k.COSTOMFUC:q.d;break;case H.k.SCRIPT:q.h;break;default:q.n}return r.name=e.nodeName,r.originPid=t.parentId,r.parentId=e.nodePid,a.loadTreeNode(e.nodePid,H.k.TASK_DEV),!0}})},emptyModalDefault:function(){e({type:q.f.EMPTY_MODAL_DEFAULT})}}})(ce),he=w.a.Item,pe=(V.a.Option,function(e){function a(e){return i()(this,a),c()(this,(a.__proto__||l()(a)).call(this,e))}return h()(a,e),s()(a,[{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"handleResSelectTreeChange",value:function(e){this.props.form.setFieldsValue({resources:e}),this.props.form.validateFields(["resources"])}},{key:"render",value:function(){var e=this.props.form.getFieldDecorator,a=this.props,t=a.defaultData,n=(a.isEditExist,a.isCreateFromMenu),l=a.isCreateNormal,r={labelCol:{xs:{span:24},sm:{span:6}},wrapperCol:{xs:{span:24},sm:{span:14}}};return m.a.createElement(w.a,null,m.a.createElement(he,O()({},r,{label:"函数名称",hasFeedback:!0}),e("name",{rules:[{required:!0,message:"函数名称不可为空！"},{pattern:/^[A-Za-z0-9_-]+$/,message:"函数名称只能由字母、数字、下划线组成!"},{max:20,message:"函数名称不得超过20个字符！"}]})(m.a.createElement(L.a,{placeholder:"请输入函数名称"}))),m.a.createElement(he,O()({},r,{label:"类名",hasFeedback:!0}),e("className",{rules:[{required:!0,message:"类名不能为空"},{pattern:/^[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,message:"请输入有效的类名!"}]})(m.a.createElement(L.a,{placeholder:"请输入类名"}))),m.a.createElement(he,O()({},r,{label:"资源",hasFeedback:!0}),e("resources",{rules:[{required:!0,message:"请选择关联资源"},{validator:this.checkNotDir.bind(this)}]})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.RESOURCE,ispicker:!0,isFilepicker:!0,treeData:this.props.resTreeData,onChange:this.handleResSelectTreeChange.bind(this)})),m.a.createElement(he,O()({},r,{label:"用途",hasFeedback:!0}),e("purpose")(m.a.createElement(L.a,{placeholder:""}))),m.a.createElement(he,O()({},r,{label:"命令格式",hasFeedback:!0}),e("commandFormate",{rules:[{max:200,message:"描述请控制在200个字符以内！"}]})(m.a.createElement(L.a,{type:"textarea",rows:4,placeholder:"请输入函数的命令格式，例如：datetime dateadd(datetime date, bigint delta, string datepart)"}))),m.a.createElement(he,O()({},r,{label:"参数说明",hasFeedback:!0}),e("paramDesc",{rules:[{max:200,message:"描述请控制在200个字符以内！"}]})(m.a.createElement(L.a,{type:"textarea",rows:4,placeholder:"请输入函数的参数说明"}))),m.a.createElement(he,O()({},r,{label:"选择存储位置",hasFeedback:!0}),e("nodePid",{rules:[{required:!0,message:"存储位置必选！"}],initialValue:l?this.props.functionTreeData.id:n?t.parentId:void 0})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.FUNCTION,ispicker:!0,treeData:this.props.functionTreeData,onChange:this.handleSelectTreeChange.bind(this),defaultNode:l?this.props.functionTreeData.name:n?this.getFolderName(t.parentId):void 0})))}},{key:"checkNotDir",value:function(e,a,t){var n=void 0;!function e(t){t.forEach(function(t,l){t.id===a?n=t.type:e(t.children||[])})}([this.props.resTreeData]),"folder"===n&&t("请选择具体文件, 而非文件夹"),t()}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.functionTreeData]),a}}]),a}(m.a.Component)),me=w.a.create()(pe),fe=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.handleSubmit=t.handleSubmit.bind(t),t.handleCancel=t.handleCancel.bind(t),t.dtcount=0,t}return h()(a,e),s()(a,[{key:"shouldComponentUpdate",value:function(e,a){return this.props!==e}},{key:"handleSubmit",value:function(){var e=this,a=this.props,t=(a.isModalShow,a.toggleCreateFn,a.addFn),n=this.form;n.validateFields(function(a,l){a||t(l).then(function(a){a&&(e.closeModal(),n.resetFields())})})}},{key:"handleCancel",value:function(){this.props.toggleCreateFn;this.closeModal()}},{key:"closeModal",value:function(){this.dtcount++,this.props.emptyModalDefault(),this.props.toggleCreateFn()}},{key:"render",value:function(){var e=this,a=this.props,t=a.isModalShow,n=(a.toggleCreateFn,a.functionTreeData),l=a.resTreeData,r=a.defaultData,i=void 0===r,o=!i&&void 0===r.id,s=!i&&!o;return m.a.createElement("div",null,m.a.createElement(y.a,{title:"创建函数",visible:t,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.handleCancel},"取消"),m.a.createElement(k.a,{key:"submit",type:"primary",size:"large",onClick:this.handleSubmit}," 确认 ")],key:this.dtcount,onCancel:this.handleCancel},m.a.createElement(me,{ref:function(a){return e.form=a},functionTreeData:n,resTreeData:l,isCreateFromMenu:o,isCreateNormal:i,isEditExist:s,defaultData:r})))}}]),a}(m.a.Component),Ee=Object(x.b)(function(e){return{isModalShow:e.offlineTask.modalShow.createFn,functionTreeData:e.offlineTask.functionTree,resTreeData:e.offlineTask.resourceTree,defaultData:e.offlineTask.modalShow.defaultData}},function(e){return{toggleCreateFn:function(){e({type:q.f.TOGGLE_CREATE_FN})},addFn:function(a){return U.a.addOfflineFunction(a).then(function(a){var t=a.data;if(1===a.code)return e({type:q.d.ADD_FOLDER_CHILD,payload:t}),!0})},emptyModalDefault:function(){e({type:q.f.EMPTY_MODAL_DEFAULT})}}})(fe),ye=w.a.Item,ve=(V.a.Option,function(e){function a(e){return i()(this,a),c()(this,(a.__proto__||l()(a)).call(this,e))}return h()(a,e),s()(a,[{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"render",value:function(){var e=this.props.form.getFieldDecorator,a=this.props,t=a.originFn,n=a.isVisible,l=t.parentId,r=t.name;t.functionId;return n?m.a.createElement(w.a,null,m.a.createElement(ye,O()({},H.y,{label:"函数名称",hasFeedback:!0}),e("name",{rules:[{required:!0,message:"函数名称不可为空！"},{max:20,message:"函数名称不得超过20个字符！"}],initialValue:r})(m.a.createElement(L.a,{disabled:!0,placeholder:"请输入函数名称"}))),m.a.createElement(ye,O()({},H.y,{label:"选择存储位置",hasFeedback:!0}),e("nodePid",{rules:[{required:!0,message:"存储位置必选！"}],initialValue:l})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.FUNCTION,ispicker:!0,treeData:this.props.functionTreeData,onChange:this.handleSelectTreeChange.bind(this),defaultNode:this.getFolderName(l)}))):null}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.functionTreeData]),a}}]),a}(m.a.Component)),ke=w.a.create()(ve),ge=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.handleSubmit=t.handleSubmit.bind(t),t.handleCancel=t.handleCancel.bind(t),t.dtcount=0,t}return h()(a,e),s()(a,[{key:"shouldComponentUpdate",value:function(e,a){return this.props!==e}},{key:"handleSubmit",value:function(){var e=this,a=this.props,t=a.doMoveFn,n=a.moveFnData,l=n.originFn.id;this.form.validateFields(function(a,r){a||(r.functionId=l,delete r.name,t(r,n.originFn).then(function(a){a&&e.closeModal()}))})}},{key:"handleCancel",value:function(){this.closeModal()}},{key:"closeModal",value:function(){this.dtcount++,this.props.toggleMoveFn()}},{key:"render",value:function(){var e=this,a=this.props,t=a.moveFnData,n=(a.toggleMoveFn,a.functionTreeData),l=void 0!==t;return m.a.createElement("div",null,m.a.createElement(y.a,{title:"移动函数",visible:l,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.handleCancel},"取消"),m.a.createElement(k.a,{key:"submit",type:"primary",size:"large",onClick:this.handleSubmit}," 确认 ")],key:this.dtcount,onCancel:this.handleCancel},m.a.createElement(ke,O()({ref:function(a){return e.form=a},functionTreeData:n},t))))}}]),a}(m.a.Component),Te=Object(x.b)(function(e){return{moveFnData:e.offlineTask.modalShow.moveFnData,functionTreeData:e.offlineTask.functionTree}},function(e){return{toggleMoveFn:function(){e({type:q.f.TOGGLE_MOVE_FN})},doMoveFn:function(a,t){return U.a.moveOfflineFn(a).then(function(n){if(1===n.code){var l=t;return l.originPid=t.parentId,l.parentId=a.nodePid,e({type:q.d.EDIT_FOLDER_CHILD,payload:l}),!0}})}}})(ge),be=(t("nT0F"),t("MM9K")),Ce=t.n(be),De=t("wd/R"),Fe=t.n(De),Se=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.state={loading:!0,data:void 0},t}return h()(a,e),s()(a,[{key:"componentWillReceiveProps",value:function(e){e.fnId!==this.props.fnId&&this.getFnDetail(e.fnId)}},{key:"getFnDetail",value:function(e){var a=this;e&&U.a.getOfflineFn({functionId:e}).then(function(e){1===e.code&&a.setState({data:e.data}),a.setState({loading:!1})})}},{key:"render",value:function(){var e=this.props,a=e.visible,t=e.fnId,n=e.closeModal,l=this.state,r=l.data,i=l.loading;return m.a.createElement(y.a,{title:"函数详情",visible:a,onCancel:n,key:t,footer:[m.a.createElement(k.a,{size:"large",onClick:n,key:"cancel"},"取消")]},i?m.a.createElement(Ce.a,null):null===r?"系统异常":m.a.createElement("table",{className:"ant-table ant-table-bordered bd-top bd-left",style:{width:"100%"}},m.a.createElement("tbody",{className:"ant-table-tbody"},m.a.createElement("tr",null,m.a.createElement("td",{width:"15%"},"函数名称"),m.a.createElement("td",null,r.name)),m.a.createElement("tr",null,m.a.createElement("td",null,"用途"),m.a.createElement("td",null,r.purpose)),m.a.createElement("tr",null,m.a.createElement("td",null,"命令格式"),m.a.createElement("td",null,r.commandFormate||"/")),m.a.createElement("tr",null,m.a.createElement("td",null,"参数说明"),m.a.createElement("td",null,r.paramDesc||"/")),m.a.createElement("tr",null,m.a.createElement("td",null,"创建"),m.a.createElement("td",null,r.createUser.userName," 于 ",Fe()(r.gmtCreate).format("YYYY-MM-DD hh:mm:ss"))),m.a.createElement("tr",null,m.a.createElement("td",null,"最后修改"),m.a.createElement("td",null,r.modifyUser.userName," 于 ",Fe()(r.gmtModified).format("YYYY-MM-DD hh:mm:ss"))))))}}]),a}(m.a.Component),_e=Object(x.b)(function(e){var a=e.offlineTask.modalShow;return{visible:a.fnViewModal,fnId:a.fnId}},function(e){return{closeModal:function(){e({type:q.f.HIDE_FNVIEW_MODAL})}}})(Se),Me=t("7Qib"),Oe=t("TH1f"),Ie=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.state={loading:!0,data:void 0},t}return h()(a,e),s()(a,[{key:"componentWillReceiveProps",value:function(e){e.resId!==this.props.resId&&this.getResDetail(e.resId)}},{key:"getResDetail",value:function(e){var a=this;e&&U.a.getOfflineRes({resourceId:e}).then(function(e){1===e.code&&a.setState({loading:!1,data:e.data})})}},{key:"render",value:function(){var e=this.props,a=e.visible,t=e.resId,n=e.closeModal,l=this.state,r=l.data,i=l.loading;return m.a.createElement(y.a,{title:"资源详情",visible:a,onCancel:n,key:t,footer:[m.a.createElement(k.a,{size:"large",onClick:n,key:"cancel"},"取消")]},i?m.a.createElement(Ce.a,null):null===r?"系统异常":m.a.createElement("table",{className:"ant-table ant-table-bordered bd-top bd-left",style:{width:"100%"}},m.a.createElement("tbody",{className:"ant-table-tbody"},m.a.createElement("tr",null,m.a.createElement("td",{width:"15%"},"资源名称"),m.a.createElement("td",null,r.resourceName)),m.a.createElement("tr",null,m.a.createElement("td",null,"资源描述"),m.a.createElement("td",null,r.resourceDesc)),m.a.createElement("tr",null,m.a.createElement("td",null,"资源类型"),m.a.createElement("td",null," ",m.a.createElement(Oe.f,{value:r.resourceType}))),m.a.createElement("tr",null,m.a.createElement("td",null,"创建"),m.a.createElement("td",null,r.createUser.userName," 于 ",Me.a.formatDateTime(r.gmtCreate))),m.a.createElement("tr",null,m.a.createElement("td",null,"修改时间"),m.a.createElement("td",null,Me.a.formatDateTime(r.gmtModified))))))}}]),a}(m.a.Component),Ne=Object(x.b)(function(e){var a=e.offlineTask.modalShow;return{visible:a.resViewModal,resId:a.resId}},function(e){return{closeModal:function(){e({type:q.f.HIDE_RESVIEW_MODAL})}}})(Ie),Re=w.a.Item,Le=(V.a.Optioin,N.a.Group),Pe=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.loadScritTypes=function(){U.a.getScriptTypes().then(function(e){t.setState({types:e.data||[]})})},t.handleRadioChange=t.handleRadioChange.bind(t),t.isEditExist=!1,t.state={value:0,types:[]},t}return h()(a,e),s()(a,[{key:"componentDidMount",value:function(){this.loadScritTypes()}},{key:"handleSelectTreeChange",value:function(e){this.props.form.setFieldsValue({nodePid:e})}},{key:"handleRadioChange",value:function(e){this.setState({value:e.target.value})}},{key:"render",value:function(){var e=this.props.form.getFieldDecorator,a=this.props.defaultData,t=this.state.types,n=void 0===a,l=!n&&void 0===a.id;this.isEditExist=!n&&!l;var r=n?this.state.value:l?this.state.value:a.taskType,i=t.map(function(e){return m.a.createElement(N.a,{key:e.value,value:e.value},e.name)});return m.a.createElement(w.a,null,m.a.createElement(Re,O()({},H.y,{label:"脚本名称",hasFeedback:!0}),e("name",{rules:[{required:!0,message:"脚本名称不可为空！"},{max:64,message:"脚本名称不得超过20个字符！"},{pattern:/^[A-Za-z0-9_-]+$/,message:"脚本名称只能由字母、数字、下划线组成!"}],initialValue:n?void 0:l?void 0:a.name})(m.a.createElement(L.a,{placeholder:"请输入脚本名称"}))),m.a.createElement(Re,O()({},H.y,{label:"脚本类型"}),e("type",{rules:[{required:!0,message:"请选择任务类型"}],initialValue:t.length>0?t[0].value:r})(m.a.createElement(Le,{disabled:!n&&!l,onChange:this.handleRadioChange},i))),m.a.createElement(Re,O()({},H.y,{label:"存储位置"}),e("nodePid",{rules:[{required:!0,message:"存储位置必选！"}],initialValue:n?this.props.treeData.id:l?a.parentId:a.nodePid})(m.a.createElement(L.a,{type:"hidden"})),m.a.createElement(j.a,{type:H.k.SCRIPT,ispicker:!0,treeData:this.props.treeData,onChange:this.handleSelectTreeChange.bind(this),defaultNode:n?this.props.treeData.name:l?this.getFolderName(a.parentId):this.getFolderName(a.nodePid)})),m.a.createElement(Re,O()({},H.y,{label:"描述",hasFeedback:!0}),e("scriptDesc",{rules:[{max:200,message:"描述请控制在200个字符以内！"}],initialValue:n?void 0:l?void 0:a.scriptDesc})(m.a.createElement(L.a,{type:"textarea",rows:4,placeholder:"请输入脚本描述"}))))}},{key:"checkNotDir",value:function(e,a,t){var n=void 0;!function e(t){t.forEach(function(t,l){t.id===a?n=t.type:e(t.children||[])})}([this.props.resTreeData]),"folder"===n&&t("请选择具体文件, 而非文件夹"),t()}},{key:"getFolderName",value:function(e){var a=void 0;return function t(n){n.forEach(function(n,l){n.id===e?a=n.name:t(n.children||[])})}([this.props.treeData]),a}}]),a}(m.a.Component),Ve=w.a.create()(Pe),Ae=function(e){function a(e){i()(this,a);var t=c()(this,(a.__proto__||l()(a)).call(this,e));return t.handleSubmit=t.handleSubmit.bind(t),t.closeModal=t.closeModal.bind(t),t.dtcount=0,t}return h()(a,e),s()(a,[{key:"handleSubmit",value:function(){var e=this,a=this.props.defaultData,t=this.form,n=void 0===a,l=!n&&void 0===a.id,r=!n&&!l;t.validateFields(function(n,l){n||(l.resourceIdList&&(l.resourceIdList=[l.resourceIdList]),void 0!==a&&void 0!==a.id&&(l.id=a.id,l.version=a.version,l.readWriteLockVO=C()({},a.readWriteLockVO)),e.props.createScript(l,r,a).then(function(a){a&&(e.closeModal(),t.resetFields())}))})}},{key:"closeModal",value:function(){this.dtcount++,this.props.emptyModalDefault(),this.props.toggleCreateScript()}},{key:"render",value:function(){var e=this,a=this.props,t=a.isModalShow,n=a.scriptTreeData,l=a.defaultData;return l&&l.name?this.isCreate=!1:this.isCreate=!0,m.a.createElement("div",null,m.a.createElement(y.a,{title:this.isCreate?"新建脚本":"编辑脚本",visible:t,footer:[m.a.createElement(k.a,{key:"back",size:"large",onClick:this.closeModal},"取消"),m.a.createElement(k.a,{key:"submit",type:"primary",size:"large",onClick:this.handleSubmit}," 确认 ")],onCancel:this.closeModal},m.a.createElement(Ve,{ref:function(a){return e.form=a},treeData:n,defaultData:l})))}}]),a}(m.a.Component),we=Object(x.b)(function(e){return{isModalShow:e.offlineTask.modalShow.createScript,scriptTreeData:e.offlineTask.scriptTree,defaultData:e.offlineTask.modalShow.defaultData}},function(e){return{toggleCreateScript:function(){e({type:q.f.TOGGLE_CREATE_SCRIPT})},createScript:function(a,t,n){return U.a.saveScript(a).then(function(a){if(1===a.code)return U.a.getScriptById({id:a.data.id}).then(function(l){if(t){var r=C()(n,a.data);r.originPid=n.nodePid,e({type:q.h.EDIT_FOLDER_CHILD,payload:r}),e({type:q.o.SET_TASK_FIELDS_VALUE,payload:l.data})}else{var i=a.data;i.catalogueType&&(i.catalogueType=H.k.SCRIPT),e({type:q.h.ADD_FOLDER_CHILD,payload:i}),e({type:q.o.LOAD_TASK_DETAIL,payload:l.data}),e({type:q.o.OPEN_TASK_TAB,payload:l.data.id})}}),!0})},emptyModalDefault:function(){e({type:q.f.EMPTY_MODAL_DEFAULT})}}})(Ae);t.d(a,"default",function(){return xe});var xe=function(e){function a(){return i()(this,a),c()(this,(a.__proto__||l()(a)).apply(this,arguments))}return h()(a,e),s()(a,[{key:"render",value:function(){return m.a.createElement("div",null,m.a.createElement(Q,null),m.a.createElement(ie,null),m.a.createElement(ue,null),m.a.createElement(Ee,null),m.a.createElement(Te,null),m.a.createElement(_e,null),m.a.createElement(Ne,null),m.a.createElement(we,null),m.a.createElement(f.default,null))}}]),a}(m.a.Component)}}]);