(window.webpackJsonp=window.webpackJsonp||[]).push([[18],{T0AK:function(e,t,n){"use strict";n.r(t);var r,a=n("Yz+Y"),c=n.n(a),o=n("iCc5"),i=n.n(o),l=n("V7oC"),s=n.n(l),p=n("FYw3"),u=n.n(p),d=n("mRg0"),m=n.n(d),h=(n("ppZR"),n("d2CI")),f=n.n(h),v=n("sbe7"),k=n.n(v),y=n("/MKj"),E=n("dtw8"),w=n("17x9"),b=n.n(w),g=(n("PFYH"),n("Jv8k")),C=n.n(g),j=function(e){function t(e){i()(this,t);var n=u()(this,(t.__proto__||c()(t)).call(this,e));return n.updateSelected=function(){var e=n.props.router.routes;if(e.length>3){var t=e[3].path||"config";n.setState({current:t})}},n.handleClick=function(e){n.setState({current:e.key})},n.state={current:"config"},n}return m()(t,e),s()(t,[{key:"componentDidMount",value:function(){this.updateSelected()}},{key:"componentWillReceiveProps",value:function(){this.updateSelected()}},{key:"render",value:function(){var e="/project/"+this.props.params.pid;return k.a.createElement("div",{className:"sidebar m-ant-menu"},k.a.createElement(C.a,{onClick:this.handleClick,style:{width:200,height:"100%"},selectedKeys:[this.state.current],defaultSelectedKeys:[this.state.current],mode:"inline"},k.a.createElement(C.a.Item,{key:"config"},k.a.createElement(E.c,{to:e+"/config"},"项目配置")),k.a.createElement(C.a.Item,{key:"member"},k.a.createElement(E.c,{to:e+"/member"},"项目成员管理")),k.a.createElement(C.a.Item,{key:"role"},k.a.createElement(E.c,{to:e+"/role"},"角色管理"))))}}]),t}(v.Component),_=f.a.Sider,S=f.a.Content,I={children:b.a.node},K=Object(y.b)(function(e){return{project:e.project}})(r=function(e){function t(){return i()(this,t),u()(this,(t.__proto__||c()(t)).apply(this,arguments))}return m()(t,e),s()(t,[{key:"componentWillReceiveProps",value:function(e){var t=e.params,n=void 0===t?{}:t,r=e.project,a=void 0===r?{}:r;n.pid!=a.id&&E.g.push(location.hash.replace(/.*?(\/project\/)[^\/]+(.*)/i,"$1"+a.id+"$2"))}},{key:"render",value:function(){var e=this.props.children;return k.a.createElement(f.a,{className:"dt-dev-project"},k.a.createElement(_,{className:"bg-w"},k.a.createElement(j,this.props)),k.a.createElement(S,null,e||"i'm container."))}}]),t}(v.Component))||r;K.propTypes=I,K.defaultProps={children:[]};t.default=K}}]);