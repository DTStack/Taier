(self.webpackJsonp=self.webpackJsonp||[]).push([[26],{aKWa:function(e,t,a){"use strict";a.r(t);var n=a("Yz+Y"),r=a.n(n),c=a("iCc5"),l=a.n(c),i=a("V7oC"),o=a.n(i),s=a("FYw3"),p=a.n(s),u=a("mRg0"),m=a.n(u),d=(a("ppZR"),a("d2CI")),h=a.n(d),f=a("sbe7"),v=a.n(f),y=a("17x9"),E=a.n(y),k=a("/MKj"),b=(a("PFYH"),a("Jv8k")),g=a.n(b),C=(a("FGdI"),a("Pbn2")),w=a.n(C),_=a("dtw8"),S=function(e){function t(e){l()(this,t);var a=p()(this,(t.__proto__||r()(t)).call(this,e));return a.updateSelected=function(){var e=a.props.router.routes;if(e.length>3){var t=e[3].path;t&&(t=t.split("/")[0]),a.setState({current:t||"table"})}},a.handleClick=function(e){a.setState({current:e.key})},a.state={current:"table"},a}return m()(t,e),o()(t,[{key:"componentDidMount",value:function(){this.updateSelected()}},{key:"componentWillReceiveProps",value:function(){this.updateSelected()}},{key:"render",value:function(){this.props;var e="/data-model";return v.a.createElement("div",{className:"sidebar m-ant-menu"},v.a.createElement(g.a,{onClick:this.handleClick,style:{width:200,height:"100%"},selectedKeys:[this.state.current],defaultSelectedKeys:[this.state.current],mode:"inline"},v.a.createElement(g.a.Item,{key:"overview"},v.a.createElement(_.c,{to:e+"/overview"},v.a.createElement(w.a,{type:"pie-chart"}),"总览")),v.a.createElement(g.a.Item,{key:"check"},v.a.createElement(_.c,{to:e+"/check"},v.a.createElement(w.a,{type:"filter"}),"检测中心")),v.a.createElement(g.a.Item,{key:"table"},v.a.createElement(_.c,{to:e+"/table"},v.a.createElement(w.a,{type:"api"}),"模型设计")),v.a.createElement(g.a.Item,{key:"config"},v.a.createElement(_.c,{to:e+"/config"},v.a.createElement(w.a,{type:"tool"}),"配置中心"))))}}]),t}(f.Component),I=(a("HPaR"),h.a.Sider),P=h.a.Content,j={children:E.a.node},K=function(e){function t(){return l()(this,t),p()(this,(t.__proto__||r()(t)).apply(this,arguments))}return m()(t,e),o()(t,[{key:"render",value:function(){var e=this.props.children;return v.a.createElement(h.a,{className:"dt-dev-datamanagement g-datamanage"},v.a.createElement(I,{className:"bg-w"},v.a.createElement(S,this.props)),v.a.createElement(P,{style:{position:"relative"}},e||"概览"))}}]),t}(f.Component);K.propTypes=j,K.defaultProps={children:[]};t.default=Object(k.b)(function(e){return{project:e.project.id}},null)(K)}}]);