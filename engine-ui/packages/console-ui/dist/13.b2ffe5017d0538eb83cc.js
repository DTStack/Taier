(window.webpackJsonp=window.webpackJsonp||[]).push([[13],{aYiL:function(e,t,n){"use strict";n.r(t);var r=n("Yz+Y"),l=n.n(r),a=n("iCc5"),i=n.n(a),o=n("V7oC"),u=n.n(o),s=n("FYw3"),c=n.n(s),d=n("mRg0"),h=n.n(d),g=n("sbe7"),p=n.n(g),m=(n("hJue"),n("YEIV")),E=n.n(m),f=(n("hkK3"),n("WydL")({mxBasePath:"public/rdos/mxgraph",mxImageBasePath:"public/rdos/mxgraph/images",mxLoadResources:!1,mxLanguage:"none",mxLoadStylesheets:!1})),v=f.mxGraph,y=f.mxShape,C=f.mxConnectionConstraint,w=f.mxPoint,b=f.mxPolyline,x=f.mxEvent,L=f.mxRubberband,S=f.mxCellState,k=f.mxConstants,T=f.mxEdgeStyle,I=f.mxPopupMenu,D=f.mxGraphHandler,_=f.mxCell,M=f.mxGeometry,R=f.mxPerimeter,O=f.mxUndoManager,N=f.mxCompactTreeLayout,A=f.mxUtils,Y=f.mxDragSource,P=91,V=17,G=8,H=65,U={width:120,height:60},z=function(e){function t(){var e,n,r,a;i()(this,t);for(var o=arguments.length,u=Array(o),s=0;s<o;s++)u[s]=arguments[s];return n=r=c()(this,(e=t.__proto__||l()(t)).call.apply(e,[this].concat(u))),r.insertItemVertex=function(e,t,n,r,l){var a=new _("new Cell",new M(0,0,U.width,U.height));a.vertex=!0;var i=e.importCells([a],r,l,n);null!=i&&i.length>0&&(e.scrollCellToVisible(i[0]),e.setSelectionCells(i))},r.getUnderMouseGraph=function(e){var t=x.getClientX(e),n=x.getClientY(e),l=document.elementFromPoint(t,n);return A.isAncestorNode(r.graph.container,l)?r.graph:null},a=n,c()(r,a)}return h()(t,e),u()(t,[{key:"componentDidMount",value:function(){var e=this.Container;this.initEditor(),this.loadEditor(e),this.listenKeyboard(),this.listenDoubleClick(),this.listenConnection(),this.undoManager()}},{key:"initEditor",value:function(){v.prototype.getAllConnectionConstraints=function(e,t){if(null!=e&&null!=e.shape)if(null!=e.shape.stencil){if(null!=e.shape.stencil)return e.shape.stencil.constraints}else if(null!=e.shape.constraints)return e.shape.constraints;return null},y.prototype.constraints=[new C(new w(.25,0),!0),new C(new w(.5,0),!0),new C(new w(.75,0),!0),new C(new w(0,.25),!0),new C(new w(0,.5),!0),new C(new w(0,.75),!0),new C(new w(1,.25),!0),new C(new w(1,.5),!0),new C(new w(1,.75),!0),new C(new w(.25,1),!0),new C(new w(.5,1),!0),new C(new w(.75,1),!0)],b.prototype.constraints=null}},{key:"loadEditor",value:function(e){x.disableContextMenu(e);var t=new v(e);this.graph=t,t.setConnectable(!0),t.setTooltips(!0),t.view.setScale(1),D.prototype.guidesEnabled=!0,t.isCellsMovable=function(){var e=t.getSelectionCell();return!(e&&e.edge)};var n=this.getDefaultVertexStyle();t.getStylesheet().putDefaultVertexStyle(n);var r=this.getDefaultEdgeStyle();t.getStylesheet().putDefaultEdgeStyle(r),t.isPart=function(e){};var l=new N(t,!1);l.horizontal=!1,l.useBoundingBox=!1,l.edgeRouting=!1,l.levelDistance=40,l.nodeDistance=20,new L(t);var a=t.getDefaultParent(),i=t.getModel();i.beginUpdate();try{var o=t.insertVertex(a,null,"block1",20,20,U.width,U.height),u=t.insertVertex(a,null,"block2",200,150,U.width,U.height),s=t.insertVertex(a,null,"block3",300,150,U.width,U.height),c=t.insertVertex(s,null,"block4",10,150,U.width,U.height);c.isPart=!0;var d=t.insertVertex(s,null,"block5",10,250,U.width,U.height);d.isPart=!0;t.insertEdge(a,null,"",o,s),t.insertEdge(a,null,"",u,s),t.insertEdge(a,null,"",c,d);l.execute(a),l.execute(s)}finally{i.endUpdate()}this.initContextMenu(t),this.initDragItem()}},{key:"getDefaultVertexStyle",value:function(){var e=[];return e[k.STYLE_SHAPE]=k.SHAPE_RECTANGLE,e[k.STYLE_PERIMETER]=R.RectanglePerimeter,e[k.STYLE_STROKECOLOR]="#fff",e[k.STYLE_ROUNDED]=!0,e[k.STYLE_FILLCOLOR]="#18a689",e[k.STYLE_FONTCOLOR]="#ffffff",e[k.STYLE_ALIGN]=k.ALIGN_CENTER,e[k.STYLE_VERTICAL_ALIGN]=k.ALIGN_MIDDLE,e[k.STYLE_FONTSIZE]="12",e[k.STYLE_FONTSTYLE]=1,e}},{key:"getDefaultEdgeStyle",value:function(){var e=[];return e[k.STYLE_SHAPE]=k.SHAPE_CONNECTOR,e[k.STYLE_STROKECOLOR]="#dddddd",e[k.STYLE_ALIGN]=k.ALIGN_CENTER,e[k.STYLE_VERTICAL_ALIGN]=k.ALIGN_MIDDLE,e[k.STYLE_EDGE]=T.ElbowConnector,e[k.STYLE_ENDARROW]=k.ARROW_CLASSIC,e[k.STYLE_FONTSIZE]="10",e}},{key:"initContextMenu",value:function(e){var t=I.prototype.showMenu;I.prototype.showMenu=function(){var e=this.graph.getSelectionCells();if(!(e.length>0&&e[0].vertex))return!1;t.apply(this,arguments)},e.popupMenuHandler.autoExpand=!0,e.popupMenuHandler.factoryMethod=function(t,n,r){t.addItem("Item1",null,function(){alert("item 1")}),t.addItem("移除",null,function(){e.removeCells([n])}),t.addSeparator();var l=t.addItem("SubMen",null,null);t.addItem("Item2",null,function(){alert("item 1")},l)}}},{key:"initDragItem",value:function(){var e=this,t=document.createElement("div");t.style.border="1px solid blue",t.style.width=U.width+"px",t.style.height=U.height+"px";var n=A.makeDraggable(this.btn1,this.getUnderMouseGraph,this.insertItemVertex,t,null,null,this.graph.autoscroll,!0),r=A.makeDraggable(this.btn2,this.getUnderMouseGraph,this.insertItemVertex,t,null,null,this.graph.autoscroll,!0);n.isGuidesEnabled=function(){return e.graph.graphHandler.guidesEnabled},n.createDragElement=Y.prototype.createDragElement,r.isGuidesEnabled=function(){return e.graph.graphHandler.guidesEnabled},r.createDragElement=Y.prototype.createDragElement}},{key:"zoomIn",value:function(){this.graph.zoomIn()}},{key:"zoomOut",value:function(){this.graph.zoomOut()}},{key:"removeCell",value:function(e){var t=e||this.graph.getSelectionCells();t&&t.length>0&&this.graph.removeCells(t)}},{key:"insert",value:function(){var e=this.graph.getDefaultParent();this.graph.insertVertex(e,null,(new Date).getTime(),null,150,80,30)}},{key:"outputRoot",value:function(){this.graph.getDefaultParent()}},{key:"disableConnection",value:function(){this.graph.setConnectable(!1)}},{key:"resetView",value:function(){this.graph.view.scaleAndTranslate(1,0,0)}},{key:"listenDoubleClick",value:function(){this.graph.addListener(x.DOUBLE_CLICK,function(e,t){t.getProperty("cell")&&window.open("http://www.google.com")})}},{key:"listenConnection",value:function(){var e=this.graph;e.connectionHandler.createEdgeState=function(t){var n=e.createEdge(null,null,null,null,null);return new S(e.view,n,e.getCellStyle(n))},e.connectionHandler.addListener(x.CONNECT,function(t,n){var r=n.getProperty("cell"),l=e.getModel().getTerminal(r,!0),a=e.getModel().getTerminal(r,!1);l&&a||e.removeCells([r])}),e.connectionHandler.addListener(x.RESET,function(t,n){var r=n.getProperty("cell"),l=e.getModel().getTerminal(r,!0),a=e.getModel().getTerminal(r,!1);l&&a||e.removeCells([r])})}},{key:"listenKeyboard",value:function(){var e,t=this,n=(e={},E()(e,P,!1),E()(e,H,!1),E()(e,V,!1),e);document.addEventListener("keydown",function(e){switch(e.keyCode){case G:t.removeCell();break;case P:n[P]=!0;break;case H:n[H]=!0;break;case V:n[V]=!0}(n[P]&&n[H]||n[H]&&n[V])&&(t.graph.selectAll(),n[P]=!1,n[H]=!1,n[V]=!1)})}},{key:"graphEnable",value:function(){var e=this.graph.isEnabled();this.graph.setEnabled(!e)}},{key:"undoManager",value:function(){var e=new O,t=this.graph;this.undoMana=e;var n=function(t,n){e.undoableEditHappened(n.getProperty("edit"))};t.getModel().addListener(x.UNDO,n),t.getView().addListener(x.UNDO,n)}},{key:"undo",value:function(){this.undoMana.undo()}},{key:"render",value:function(){var e=this;return p.a.createElement("div",{style:{height:"100%",width:"90%",marginLeft:"10%",position:"relative"}},p.a.createElement("div",{className:"editor",ref:function(t){e.Container=t}}),p.a.createElement("div",{style:{position:"absolute",zIndex:"2",right:"20px",top:"30px"}},p.a.createElement("button",{onClick:function(){return e.zoomIn()}},"放大"),p.a.createElement("button",{onClick:function(){return e.zoomOut()}},"缩小"),p.a.createElement("button",{onClick:function(){return e.resetView()}},"重置"),p.a.createElement("button",{onClick:function(){return e.insert()}},"添加"),p.a.createElement("button",{onClick:function(){return e.outputRoot()}},"Root节点"),p.a.createElement("button",{onClick:function(){return e.disableConnection()}},"禁止链接"),p.a.createElement("button",{onClick:function(){return e.currentState()}},"状态"),p.a.createElement("button",{onClick:function(){return e.graphEnable()}},"禁止编辑"),p.a.createElement("button",{onClick:function(){return e.undo()}},"撤销")),p.a.createElement("ul",{style:{position:"absolute",zIndex:"2",left:"-10%",top:"30px"}},p.a.createElement("li",null,p.a.createElement("button",{ref:function(t){return e.btn1=t},style:{padding:"10px"}},"Tool-1")),p.a.createElement("li",{style:{marginTop:"10px"}},p.a.createElement("button",{ref:function(t){return e.btn2=t},style:{padding:"10px"}},"Tool-2"))))}}]),t}(g.Component);n("moyJ");n.d(t,"default",function(){return F});var F=function(e){function t(){var e,n,r,a;i()(this,t);for(var o=arguments.length,u=Array(o),s=0;s<o;s++)u[s]=arguments[s];return n=r=c()(this,(e=t.__proto__||l()(t)).call.apply(e,[this].concat(u))),r.onEditorChange=function(e){},a=n,c()(r,a)}return h()(t,e),u()(t,[{key:"render",value:function(){return p.a.createElement("div",{style:{height:"100%",width:"100%"}},p.a.createElement(z,null))}}]),t}(g.Component)},hkK3:function(e,t,n){}}]);