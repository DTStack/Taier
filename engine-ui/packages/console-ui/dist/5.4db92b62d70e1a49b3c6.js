webpackJsonp([5],{1867:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=n(8),r=n.n(o),a=n(1),s=n.n(a),i=n(4),l=n.n(i),c=n(3),p=n.n(c),d=n(2),u=n.n(d),A=n(625),g=(n.n(A),n(624)),C=n.n(g),m=n(0),_=n.n(m),E=n(685),w=n.n(E),h=n(1878);n.d(t,"default",function(){return I});var b=C.a.TreeNode,I=function(e){function t(){return s()(this,t),p()(this,(t.__proto__||r()(t)).apply(this,arguments))}return u()(t,e),l()(t,[{key:"render",value:function(){return _.a.createElement(w.a,{split:"vertical",minSize:300,maxSize:"80%",defaultSize:"60%",primary:"first"},_.a.createElement("div",{className:"leftSidebar"},_.a.createElement("h1",null,"Left.")),_.a.createElement(w.a,{split:"horizontal"},_.a.createElement("div",null,_.a.createElement("h1",null,"Right-Top")),_.a.createElement("div",null,_.a.createElement(h.a,null))))}}]),t}(m.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(b,"TreeNode","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/test.js"),__REACT_HOT_LOADER__.register(I,"Test","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/views/test.js"))}()},1878:function(e,t,n){"use strict";var o=n(9),r=n.n(o),a=n(8),s=n.n(a),i=n(1),l=n.n(i),c=n(4),p=n.n(c),d=n(3),u=n.n(d),A=n(2),g=n.n(A),C=n(0),m=n.n(C),_=n(1921);n.n(_);n.d(t,"a",function(){return B});var E=n(966)({mxImageBasePath:"public/rdos/mxgraph/images",mxBasePath:"public/rdos/mxgraph"}),w=E.mxGraph,h=E.mxShape,b=E.mxConnectionConstraint,I=E.mxPoint,x=E.mxPolyline,f=E.mxEvent,D=E.mxRubberband,T=E.mxCellState,k=E.mxConstants,v=E.mxEdgeStyle,y=E.mxPopupMenu,M=E.mxPerimeter,R=E.mxUndoManager,L={COMMAND:91,CTRL:17,BACKUP:8,A:65},S={width:120,height:60},B=function(e){function t(){return l()(this,t),u()(this,(t.__proto__||s()(t)).apply(this,arguments))}return g()(t,e),p()(t,[{key:"componentDidMount",value:function(){var e=this.Container;this.initEditor(),this.loadEditor(e),this.listenKeyboard(),this.listenDoubleClick(),this.listenConnection(),this.undoManager()}},{key:"initEditor",value:function(){w.prototype.getAllConnectionConstraints=function(e,t){if(null!=e&&null!=e.shape)if(null!=e.shape.stencil){if(null!=e.shape.stencil)return e.shape.stencil.constraints}else if(null!=e.shape.constraints)return e.shape.constraints;return null},h.prototype.constraints=[new b(new I(.25,0),!0),new b(new I(.5,0),!0),new b(new I(.75,0),!0),new b(new I(0,.25),!0),new b(new I(0,.5),!0),new b(new I(0,.75),!0),new b(new I(1,.25),!0),new b(new I(1,.5),!0),new b(new I(1,.75),!0),new b(new I(.25,1),!0),new b(new I(.5,1),!0),new b(new I(.75,1),!0)],x.prototype.constraints=null}},{key:"loadEditor",value:function(e){f.disableContextMenu(e);var t=new w(e);this.graph=t,t.setConnectable(!0),t.setTooltips(!0),t.view.setScale(1),t.isCellsMovable=function(e){var e=t.getSelectionCell();return!(e&&e.edge)};var n=this.getDefaultVertexStyle();t.getStylesheet().putDefaultVertexStyle(n);var o=this.getDefaultEdgeStyle();t.getStylesheet().putDefaultEdgeStyle(o),new D(t);var r=t.getDefaultParent(),a=t.getModel();a.beginUpdate();try{var s=t.insertVertex(r,null,"block1",20,20,S.width,S.height),i=t.insertVertex(r,null,"block2",200,150,S.width,S.height),l=t.insertVertex(r,null,"block3",300,150,S.width,S.height);t.insertEdge(r,null,"",s,l),t.insertEdge(r,null,"",i,l)}finally{a.endUpdate()}this.initContextMenu(t)}},{key:"getDefaultVertexStyle",value:function(){var e=[];return e[k.STYLE_SHAPE]=k.SHAPE_RECTANGLE,e[k.STYLE_PERIMETER]=M.RectanglePerimeter,e[k.STYLE_STROKECOLOR]="#fff",e[k.STYLE_ROUNDED]=!0,e[k.STYLE_FILLCOLOR]="#18a689",e[k.STYLE_FONTCOLOR]="#ffffff",e[k.STYLE_ALIGN]=k.ALIGN_CENTER,e[k.STYLE_VERTICAL_ALIGN]=k.ALIGN_MIDDLE,e[k.STYLE_FONTSIZE]="12",e[k.STYLE_FONTSTYLE]=1,e}},{key:"getDefaultEdgeStyle",value:function(){var e=[];return e[k.STYLE_SHAPE]=k.SHAPE_CONNECTOR,e[k.STYLE_STROKECOLOR]="#18a689",e[k.STYLE_ALIGN]=k.ALIGN_CENTER,e[k.STYLE_VERTICAL_ALIGN]=k.ALIGN_MIDDLE,e[k.STYLE_EDGE]=v.ElbowConnector,e[k.STYLE_ENDARROW]=k.ARROW_CLASSIC,e[k.STYLE_FONTSIZE]="10",e}},{key:"initContextMenu",value:function(e){var t=y.prototype.showMenu;y.prototype.showMenu=function(){var e=this.graph.getSelectionCells();if(!(e.length>0&&e[0].vertex))return!1;t.apply(this,arguments)},e.popupMenuHandler.autoExpand=!0,e.popupMenuHandler.factoryMethod=function(t,n,o){t.addItem("Item1",null,function(){alert("item 1")}),t.addItem("移除",null,function(){e.removeCells([n])}),t.addSeparator();var r=t.addItem("SubMen",null,null);t.addItem("Item2",null,function(){alert("item 1")},r)}}},{key:"zoomIn",value:function(){this.graph.zoomIn()}},{key:"zoomOut",value:function(){this.graph.zoomOut()}},{key:"removeCell",value:function(e){var t=e||this.graph.getSelectionCells();t&&t.length>0&&this.graph.removeCells(t)}},{key:"insert",value:function(){var e=this.graph.getDefaultParent();this.graph.insertVertex(e,null,(new Date).getTime(),null,150,80,30)}},{key:"outputRoot",value:function(){this.graph.getDefaultParent()}},{key:"disableConnection",value:function(){this.graph.setConnectable(!1)}},{key:"resetView",value:function(){this.graph.view.scaleAndTranslate(1,0,0)}},{key:"listenDoubleClick",value:function(){this.graph.addListener(f.DOUBLE_CLICK,function(e,t){t.getProperty("cell")&&window.open("http://www.google.com")})}},{key:"listenConnection",value:function(){var e=this.graph;e.connectionHandler.createEdgeState=function(t){var n=e.createEdge(null,null,null,null,null);return new T(e.view,n,e.getCellStyle(n))},e.connectionHandler.addListener(f.CONNECT,function(t,n){var o=n.getProperty("cell"),r=e.getModel().getTerminal(o,!0),a=e.getModel().getTerminal(o,!1);r&&a||e.removeCells([o])}),e.connectionHandler.addListener(f.RESET,function(t,n){var o=n.getProperty("cell"),r=e.getModel().getTerminal(o,!0),a=e.getModel().getTerminal(o,!1);r&&a||e.removeCells([o])})}},{key:"listenKeyboard",value:function(){var e,t=this,n=(e={},r()(e,L.COMMAND,!1),r()(e,L.A,!1),r()(e,L.CTRL,!1),e);document.addEventListener("keydown",function(e){switch(e.keyCode){case L.BACKUP:t.removeCell();break;case L.COMMAND:n[L.COMMAND]=!0;break;case L.A:n[L.A]=!0;break;case L.CTRL:n[L.CTRL]=!0}(n[L.COMMAND]&&n[L.A]||n[L.A]&&n[L.CTRL])&&(t.graph.selectAll(),n[L.COMMAND]=!1,n[L.A]=!1,n[L.CTRL]=!1)})}},{key:"graphEnable",value:function(){var e=this.graph.isEnabled();this.graph.setEnabled(!e)}},{key:"undoManager",value:function(){var e=new R,t=this.graph;this.undoMana=e;var n=function(t,n){e.undoableEditHappened(n.getProperty("edit"))};t.getModel().addListener(f.UNDO,n),t.getView().addListener(f.UNDO,n)}},{key:"undo",value:function(){this.undoMana.undo()}},{key:"render",value:function(){var e=this;return m.a.createElement("div",null,m.a.createElement("div",{className:"editor",ref:function(t){e.Container=t}}),m.a.createElement("div",{style:{position:"absolute",zIndex:"2",right:"20px",top:"30px"}},m.a.createElement("button",{onClick:function(){return e.zoomIn()}},"放大"),m.a.createElement("button",{onClick:function(){return e.zoomOut()}},"缩小"),m.a.createElement("button",{onClick:function(){return e.resetView()}},"重置"),m.a.createElement("button",{onClick:function(){return e.insert()}},"添加"),m.a.createElement("button",{onClick:function(){return e.outputRoot()}},"Root节点"),m.a.createElement("button",{onClick:function(){return e.disableConnection()}},"禁止链接"),m.a.createElement("button",{onClick:function(){return e.currentState()}},"状态"),m.a.createElement("button",{onClick:function(){return e.graphEnable()}},"禁止编辑"),m.a.createElement("button",{onClick:function(){return e.undo()}},"撤销")))}}]),t}(C.Component);!function(){"undefined"!=typeof __REACT_HOT_LOADER__&&(__REACT_HOT_LOADER__.register(E,"Mx","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(w,"mxGraph","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(h,"mxShape","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(b,"mxConnectionConstraint","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(I,"mxPoint","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(x,"mxPolyline","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(f,"mxEvent","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(D,"mxRubberband","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(T,"mxCellState","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(k,"mxConstants","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(v,"mxEdgeStyle","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(y,"mxPopupMenu","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(M,"mxPerimeter","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(R,"mxUndoManager","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(L,"KEY","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(S,"VertexSize","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"),__REACT_HOT_LOADER__.register(B,"Editor","/Users/ziv/Documents/workspace/data-stack/src/webapps/rdos/components/graph-editor/index.js"))}()},1921:function(e,t,n){var o=n(1924);"string"==typeof o&&(o=[[e.i,o,""]]);n(1857)(o,{});o.locals&&(e.exports=o.locals)},1924:function(e,t,n){t=e.exports=n(943)(),t.push([e.i,".editor{position:absolute;overflow:hidden;left:0;top:0;width:100%;height:100%;background-color:#fff;background-image:url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2UwZTBlMCIgb3BhY2l0eT0iMC4yIiBzdHJva2Utd2lkdGg9IjEiLz48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZTBlMGUwIiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=);background-position:-1px -1px}body div.mxPopupMenu{box-shadow:3px 3px 6px silver;background:#fff;position:absolute;border:3px solid #e7e7e7;padding:3px}body table.mxPopupMenu{border-collapse:collapse;margin:0}body tr.mxPopupMenuItem{color:#000;cursor:default}body td.mxPopupMenuItem{padding:6px 60px 6px 30px;font-family:Arial;font-size:10pt}body td.mxPopupMenuIcon{background-color:#fff;padding:0}body tr.mxPopupMenuItemHover{background-color:#eee;color:#000}table.mxPopupMenu hr{border-top:1px solid #ccc}table.mxPopupMenu tr{font-size:4pt}","",{version:3,sources:["/./src/webapps/rdos/components/graph-editor/style.scss"],names:[],mappings:"AAAA,QACI,kBAAkB,AAClB,gBAAgB,AAChB,OAAO,AACP,MAAM,AACN,WAAW,AACX,YAAY,AAIZ,sBAAoC,AACpC,6oBAA6oB,AAC7oB,6BAA8B,CACjC,AAED,qBAGI,8BAA+B,AAC/B,gBAAiB,AACjB,kBAAkB,AAClB,yBAAyB,AACzB,WAAY,CACf,AACD,uBACI,yBAAyB,AACzB,QAAW,CACd,AACD,wBACI,WAAY,AACZ,cAAe,CAClB,AACD,wBACI,0BAA0B,AAC1B,kBAAkB,AAClB,cAAe,CAClB,AACD,wBACI,sBAAuB,AACvB,SAAY,CACf,AACD,6BACI,sBAAyB,AACzB,UAAY,CACf,AACD,qBACI,yBAA6B,CAChC,AACD,qBACI,aAAc,CACjB",file:"style.scss",sourcesContent:[".editor {\n    position: absolute;\n    overflow: hidden;\n    left: 0;\n    top: 0;\n    width: 100%;\n    height: 100%;\n    // border-width: 1px;\n    // border-color: rgb(202, 202, 202);\n    // border-style: solid;\n    background-color: rgb(255, 255, 255);\n    background-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2UwZTBlMCIgb3BhY2l0eT0iMC4yIiBzdHJva2Utd2lkdGg9IjEiLz48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZTBlMGUwIiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=);\n    background-position: -1px -1px;\n}\n\nbody div.mxPopupMenu {\n    -webkit-box-shadow: 3px 3px 6px #C0C0C0;\n    -moz-box-shadow: 3px 3px 6px #C0C0C0;\n    box-shadow: 3px 3px 6px #C0C0C0;\n    background: white;\n    position: absolute;\n    border: 3px solid #e7e7e7;\n    padding: 3px;\n}\nbody table.mxPopupMenu {\n    border-collapse: collapse;\n    margin: 0px;\n}\nbody tr.mxPopupMenuItem {\n    color: black;\n    cursor: default;\n}\nbody td.mxPopupMenuItem {\n    padding: 6px 60px 6px 30px;\n    font-family: Arial;\n    font-size: 10pt;\n}\nbody td.mxPopupMenuIcon {\n    background-color: white;\n    padding: 0px;\n}\nbody tr.mxPopupMenuItemHover {\n    background-color: #eeeeee;\n    color: black;\n}\ntable.mxPopupMenu hr {\n    border-top: solid 1px #cccccc;\n}\ntable.mxPopupMenu tr {\n    font-size: 4pt;\n}"],sourceRoot:"webpack://"}])}});