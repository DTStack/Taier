(window.webpackJsonp=window.webpackJsonp||[]).push([[18],{GNb5:function(e,t,a){"use strict";a.r(t);var n=a("QbLZ"),r=a.n(n),c=a("Yz+Y"),l=a.n(c),o=a("iCc5"),i=a.n(o),s=a("V7oC"),m=a.n(s),p=a("FYw3"),d=a.n(p),u=a("mRg0"),h=a.n(u),E=(a("ppZR"),a("d2CI")),f=a.n(E),y=a("sbe7"),k=a.n(y),v=a("17x9"),b=a.n(v),w=a("/MKj"),g=(a("FGdI"),a("Pbn2")),C=a.n(g),I=(a("PFYH"),a("Jv8k")),N=a.n(I),S=a("dtw8"),_=N.a.SubMenu,x=function(e){function t(e){i()(this,t);var a=d()(this,(t.__proto__||l()(t)).call(this,e));return a.updateSelected=function(){var e=a.props.router.routes;if(e.length>3){var t=e[3].path||"overview";t.indexOf("task-patch-data")>-1&&(t="task-patch-data"),a.setState({current:t})}},a.handleClick=function(e){a.setState({current:e.key})},a.state={current:"overview"},a}return h()(t,e),m()(t,[{key:"componentDidMount",value:function(){this.updateSelected()}},{key:"componentWillReceiveProps",value:function(){this.updateSelected()}},{key:"render",value:function(){var e="/operation";return k.a.createElement("div",{className:"sidebar m-ant-menu",style:{borderRight:"none"}},k.a.createElement(N.a,{onClick:this.handleClick,selectedKeys:[this.state.current],defaultOpenKeys:["offline","alarm"],defaultSelectedKeys:[this.state.current],style:{height:"100%"},mode:this.props.mode},k.a.createElement(N.a.Item,{key:"overview"},k.a.createElement(S.c,{to:""+e},k.a.createElement(C.a,{type:"line-chart"}),k.a.createElement("span",{className:"nav-text"},"运维总览"))),k.a.createElement(_,{key:"offline",title:k.a.createElement("span",null,k.a.createElement(C.a,{type:"usb"}),k.a.createElement("span",{className:"nav-text"},"离线任务"))},k.a.createElement(N.a.Item,{key:"offline-management"},k.a.createElement(S.c,{to:e+"/offline-management"},"任务管理")),k.a.createElement(N.a.Item,{key:"offline-operation"},k.a.createElement(S.c,{to:e+"/offline-operation"},"周期实例")),k.a.createElement(N.a.Item,{key:"task-patch-data"},k.a.createElement(S.c,{to:e+"/task-patch-data"},"补数据实例"))),k.a.createElement(N.a.Item,{key:"realtime"},k.a.createElement(S.c,{to:e+"/realtime"},k.a.createElement(C.a,{type:"link"}),k.a.createElement("span",{className:"nav-text"},"实时任务"))),k.a.createElement(_,{key:"alarm",title:k.a.createElement("span",null,k.a.createElement(C.a,{type:"exclamation-circle-o"}),k.a.createElement("span",{className:"nav-text"},"监控告警"))},k.a.createElement(N.a.Item,{key:"alarm-record"},k.a.createElement(S.c,{to:e+"/alarm-record"},"告警记录")),k.a.createElement(N.a.Item,{key:"alarm-config"},k.a.createElement(S.c,{to:e+"/alarm-config"},"告警配置"))),k.a.createElement(N.a.Item,{key:"dirty-data"},k.a.createElement(S.c,{to:e+"/dirty-data"},k.a.createElement(C.a,{type:"book"}),"脏数据管理"))))}}]),t}(y.Component),K=a("Nr44"),M=a("ttTi"),O=f.a.Sider,P=f.a.Content,R={children:b.a.node},Y=function(e){function t(){var e,a,n,r;i()(this,t);for(var c=arguments.length,o=Array(c),s=0;s<c;s++)o[s]=arguments[s];return a=n=d()(this,(e=t.__proto__||l()(t)).call.apply(e,[this].concat(o))),n.state={collapsed:!1,mode:"inline"},n.onCollapse=function(e){n.setState({collapsed:e,mode:e?"vertical":"inline"})},r=a,d()(n,r)}return h()(t,e),m()(t,[{key:"componentDidMount",value:function(){this.props.dispatch(K.a()),this.props.dispatch(Object(M.b)())}},{key:"render",value:function(){var e=this.props.children;this.state.collapsed;return k.a.createElement(f.a,{className:"dt-operation"},k.a.createElement(O,{className:"bg-w"},k.a.createElement(x,r()({},this.props,{mode:this.state.mode}))),k.a.createElement(P,null,e||"i'm container."))}}]),t}(y.Component);Y.propTypes=R,Y.defaultProps={children:[]};t.default=Object(w.b)()(Y)}}]);