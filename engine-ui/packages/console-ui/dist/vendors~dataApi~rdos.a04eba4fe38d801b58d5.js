(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{213:function(e,t,n){var a=n(71);n(879),n(878),n(877);var i=n(875),o=n(201);a.registerProcessor(i),o.registerSubTypeDefaulter("legend",function(){return"plain"})},254:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=h(n(26)),i=h(n(4)),o=h(n(5)),r=h(n(2)),l=h(n(6)),s=h(n(0)),c=h(n(47)),g=h(n(99)),u=h(n(24)),d=h(n(32));function h(e){return e&&e.__esModule?e:{default:e}}var p=function(e){function t(e){(0,i.default)(this,t);var n=(0,r.default)(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return n.handleClose=function(e){e.preventDefault();var t=c.default.findDOMNode(n);t.style.height=t.offsetHeight+"px",t.style.height=t.offsetHeight+"px",n.setState({closing:!1}),(n.props.onClose||function(){})(e)},n.animationEnd=function(){n.setState({closed:!0,closing:!0})},n.state={closing:!0,closed:!1},n}return(0,l.default)(t,e),(0,o.default)(t,[{key:"render",value:function(){var e,t=this.props,n=t.closable,i=t.description,o=t.type,r=t.prefixCls,l=void 0===r?"ant-alert":r,c=t.message,h=t.closeText,p=t.showIcon,f=t.banner,m=t.className,v=void 0===m?"":m,y=t.style;p=!(!f||void 0!==p)||p;var S="";switch(o=f&&void 0===o?"warning":o||"info"){case"success":S="check-circle";break;case"info":S="info-circle";break;case"error":S="cross-circle";break;case"warning":S="exclamation-circle";break;default:S="default"}i&&(S+="-o");var x=(0,d.default)(l,(e={},(0,a.default)(e,l+"-"+o,!0),(0,a.default)(e,l+"-close",!this.state.closing),(0,a.default)(e,l+"-with-description",!!i),(0,a.default)(e,l+"-no-icon",!p),(0,a.default)(e,l+"-banner",!!f),e),v);h&&(n=!0);var w=n?s.default.createElement("a",{onClick:this.handleClose,className:l+"-close-icon"},h||s.default.createElement(u.default,{type:"cross"})):null;return this.state.closed?null:s.default.createElement(g.default,{component:"",showProp:"data-show",transitionName:l+"-slide-up",onEnd:this.animationEnd},s.default.createElement("div",{"data-show":this.state.closing,className:x,style:y},p?s.default.createElement(u.default,{className:l+"-icon",type:S}):null,s.default.createElement("span",{className:l+"-message"},c),s.default.createElement("span",{className:l+"-description"},i),w))}}]),t}(s.default.Component);t.default=p,e.exports=t.default},565:function(e,t,n){"use strict";n(72),n(883)},659:function(e,t,n){var a=n(71),i=n(39);n(864),n(862);var o=n(861),r=n(860),l=n(859),s=n(857);o("pie",[{type:"pieToggleSelect",event:"pieselectchanged",method:"toggleSelected"},{type:"pieSelect",event:"pieselected",method:"select"},{type:"pieUnSelect",event:"pieunselected",method:"unSelect"}]),a.registerVisual(i.curry(r,"pie")),a.registerLayout(i.curry(l,"pie")),a.registerProcessor(i.curry(s,"pie"))},857:function(e,t){e.exports=function(e,t){var n=t.findComponents({mainType:"legend"});n&&n.length&&t.eachSeriesByType(e,function(e){var t=e.getData();t.filterSelf(function(e){for(var a=t.getName(e),i=0;i<n.length;i++)if(!n[i].isSelected(a))return!1;return!0},this)},this)}},858:function(e,t,n){var a=n(214);function i(e,t,n,a,i,o,r){function l(t,n,a,i){for(var o=t;o<n;o++)if(e[o].y+=a,o>t&&o+1<n&&e[o+1].y>e[o].y+e[o].height)return void s(o,a/2);s(n-1,a/2)}function s(t,n){for(var a=t;a>=0&&(e[a].y-=n,!(a>0&&e[a].y>e[a-1].y+e[a-1].height));a--);}function c(e,t,n,a,i,o){for(var r=t?Number.MAX_VALUE:0,l=0,s=e.length;l<s;l++)if("center"!==e[l].position){var c=Math.abs(e[l].y-a),g=e[l].len,u=e[l].len2,d=c<i+g?Math.sqrt((i+g+u)*(i+g+u)-c*c):Math.abs(e[l].x-n);t&&d>=r&&(d=r-10),!t&&d<=r&&(d=r+10),e[l].x=n+d*o,r=d}}e.sort(function(e,t){return e.y-t.y});for(var g,u=0,d=e.length,h=[],p=[],f=0;f<d;f++)(g=e[f].y-u)<0&&l(f,d,-g),u=e[f].y+e[f].height;r-u<0&&s(d-1,u-r);for(f=0;f<d;f++)e[f].y>=n?p.push(e[f]):h.push(e[f]);c(h,!1,t,n,a,i),c(p,!0,t,n,a,i)}e.exports=function(e,t,n,o){var r,l,s=e.getData(),c=[],g=!1;s.each(function(n){var i,o,u,d,h=s.getItemLayout(n),p=s.getItemModel(n),f=p.getModel("label.normal"),m=f.get("position")||p.get("label.emphasis.position"),v=p.getModel("labelLine.normal"),y=v.get("length"),S=v.get("length2"),x=(h.startAngle+h.endAngle)/2,w=Math.cos(x),b=Math.sin(x);r=h.cx,l=h.cy;var I="inside"===m||"inner"===m;if("center"===m)i=h.cx,o=h.cy,d="center";else{var M=(I?(h.r+h.r0)/2*w:h.r*w)+r,A=(I?(h.r+h.r0)/2*b:h.r*b)+l;if(i=M+3*w,o=A+3*b,!I){var _=M+w*(y+t-h.r),L=A+b*(y+t-h.r),P=_+(w<0?-1:1)*S;i=P+(w<0?-5:5),o=L,u=[[M,A],[_,L],[P,L]]}d=I?"center":w>0?"left":"right"}var D=f.getFont(),N=f.get("rotate")?w<0?-x+Math.PI:-x:0,k=e.getFormattedLabel(n,"normal")||s.getName(n),C=a.getBoundingRect(k,D,d,"top");g=!!N,h.label={x:i,y:o,position:m,height:C.height,len:y,len2:S,linePoints:u,textAlign:d,verticalAlign:"middle",rotation:N,inside:I},I||c.push(h.label)}),!g&&e.get("avoidLabelOverlap")&&function(e,t,n,a,o,r){for(var l=[],s=[],c=0;c<e.length;c++)e[c].x<t?l.push(e[c]):s.push(e[c]);for(i(s,t,n,a,1,0,r),i(l,t,n,a,-1,0,r),c=0;c<e.length;c++){var g=e[c].linePoints;if(g){var u=g[1][0]-g[2][0];e[c].x<t?g[2][0]=e[c].x+3:g[2][0]=e[c].x-3,g[1][1]=g[2][1]=e[c].y,g[1][0]=g[2][0]+u}}}(c,r,l,t,0,o)}},859:function(e,t,n){var a=n(112),i=a.parsePercent,o=a.linearMap,r=n(858),l=n(39),s=2*Math.PI,c=Math.PI/180;e.exports=function(e,t,n,a){t.eachSeriesByType(e,function(e){var t=e.get("center"),a=e.get("radius");l.isArray(a)||(a=[0,a]),l.isArray(t)||(t=[t,t]);var g=n.getWidth(),u=n.getHeight(),d=Math.min(g,u),h=i(t[0],g),p=i(t[1],u),f=i(a[0],d/2),m=i(a[1],d/2),v=e.getData(),y=-e.get("startAngle")*c,S=e.get("minAngle")*c,x=0;v.each("value",function(e){!isNaN(e)&&x++});var w=v.getSum("value"),b=Math.PI/(w||x)*2,I=e.get("clockwise"),M=e.get("roseType"),A=e.get("stillShowZeroSum"),_=v.getDataExtent("value");_[0]=0;var L=s,P=0,D=y,N=I?1:-1;if(v.each("value",function(e,t){var n;if(isNaN(e))v.setItemLayout(t,{angle:NaN,startAngle:NaN,endAngle:NaN,clockwise:I,cx:h,cy:p,r0:f,r:M?NaN:m});else{(n="area"!==M?0===w&&A?b:e*b:s/x)<S?(n=S,L-=S):P+=e;var a=D+N*n;v.setItemLayout(t,{angle:n,startAngle:D,endAngle:a,clockwise:I,cx:h,cy:p,r0:f,r:M?o(e,_,[f,m]):m}),D=a}},!0),L<s&&x)if(L<=.001){var k=s/x;v.each("value",function(e,t){if(!isNaN(e)){var n=v.getItemLayout(t);n.angle=k,n.startAngle=y+N*t*k,n.endAngle=y+N*(t+1)*k}})}else b=L/P,D=y,v.each("value",function(e,t){if(!isNaN(e)){var n=v.getItemLayout(t),a=n.angle===S?S:e*b;n.startAngle=D,n.endAngle=D+N*a,D+=N*a}});r(e,m,g,u)})}},860:function(e,t){e.exports=function(e,t){var n={};t.eachRawSeriesByType(e,function(e){var a=e.getRawData(),i={};if(!t.isSeriesFiltered(e)){var o=e.getData();o.each(function(e){var t=o.getRawIndex(e);i[t]=e}),a.each(function(t){var r=i[t],l=null!=r&&o.getItemVisual(r,"color",!0);if(l)a.setItemVisual(t,"color",l);else{var s=a.getItemModel(t).get("itemStyle.normal.color")||e.getColorFromPalette(a.getName(t),n);a.setItemVisual(t,"color",s),null!=r&&o.setItemVisual(r,"color",s)}})}})}},861:function(e,t,n){var a=n(71),i=n(39);e.exports=function(e,t){i.each(t,function(t){t.update="updateView",a.registerAction(t,function(n,a){var i={};return a.eachComponent({mainType:"series",subType:e,query:n},function(e){e[t.method]&&e[t.method](n.name,n.dataIndex);var a=e.getData();a.each(function(t){var n=a.getName(t);i[n]=e.isSelected(n)||!1})}),{name:n.name,selected:i}})})}},862:function(e,t,n){var a=n(39),i=n(91),o=n(574);function r(e,t,n,a){var i=t.getData(),o=this.dataIndex,r=i.getName(o),s=t.get("selectedOffset");a.dispatchAction({type:"pieToggleSelect",from:e,name:r,seriesId:t.id}),i.each(function(e){l(i.getItemGraphicEl(e),i.getItemLayout(e),t.isSelected(i.getName(e)),s,n)})}function l(e,t,n,a,i){var o=(t.startAngle+t.endAngle)/2,r=Math.cos(o),l=Math.sin(o),s=n?a:0,c=[r*s,l*s];i?e.animate().when(200,{position:c}).start("bounceOut"):e.attr("position",c)}function s(e,t){i.Group.call(this);var n=new i.Sector({z2:2}),a=new i.Polyline,o=new i.Text;function r(){a.ignore=a.hoverIgnore,o.ignore=o.hoverIgnore}function l(){a.ignore=a.normalIgnore,o.ignore=o.normalIgnore}this.add(n),this.add(a),this.add(o),this.updateData(e,t,!0),this.on("emphasis",r).on("normal",l).on("mouseover",r).on("mouseout",l)}var c=s.prototype;c.updateData=function(e,t,n){var o=this.childAt(0),r=e.hostModel,s=e.getItemModel(t),c=e.getItemLayout(t),g=a.extend({},c);(g.label=null,n)?(o.setShape(g),"scale"===r.getShallow("animationType")?(o.shape.r=c.r0,i.initProps(o,{shape:{r:c.r}},r,t)):(o.shape.endAngle=c.startAngle,i.updateProps(o,{shape:{endAngle:c.endAngle}},r,t))):i.updateProps(o,{shape:g},r,t);var u=s.getModel("itemStyle"),d=e.getItemVisual(t,"color");o.useStyle(a.defaults({lineJoin:"bevel",fill:d},u.getModel("normal").getItemStyle())),o.hoverStyle=u.getModel("emphasis").getItemStyle();var h=s.getShallow("cursor");function p(){o.stopAnimation(!0),o.animateTo({shape:{r:c.r+r.get("hoverOffset")}},300,"elasticOut")}function f(){o.stopAnimation(!0),o.animateTo({shape:{r:c.r}},300,"elasticOut")}h&&o.attr("cursor",h),l(this,e.getItemLayout(t),s.get("selected"),r.get("selectedOffset"),r.get("animation")),o.off("mouseover").off("mouseout").off("emphasis").off("normal"),s.get("hoverAnimation")&&r.isAnimationEnabled()&&o.on("mouseover",p).on("mouseout",f).on("emphasis",p).on("normal",f),this._updateLabel(e,t),i.setHoverStyle(this)},c._updateLabel=function(e,t){var n=this.childAt(1),a=this.childAt(2),o=e.hostModel,r=e.getItemModel(t),l=e.getItemLayout(t).label,s=e.getItemVisual(t,"color");i.updateProps(n,{shape:{points:l.linePoints||[[l.x,l.y],[l.x,l.y],[l.x,l.y]]}},o,t),i.updateProps(a,{style:{x:l.x,y:l.y}},o,t),a.attr({rotation:l.rotation,origin:[l.x,l.y],z2:10});var c=r.getModel("label.normal"),g=r.getModel("label.emphasis"),u=r.getModel("labelLine.normal"),d=r.getModel("labelLine.emphasis");s=e.getItemVisual(t,"color");i.setLabelStyle(a.style,a.hoverStyle={},c,g,{labelFetcher:e.hostModel,labelDataIndex:t,defaultText:e.getName(t),autoColor:s,useInsideStyle:!!l.inside},{textAlign:l.textAlign,textVerticalAlign:l.verticalAlign,opacity:e.getItemVisual(t,"opacity")}),a.ignore=a.normalIgnore=!c.get("show"),a.hoverIgnore=!g.get("show"),n.ignore=n.normalIgnore=!u.get("show"),n.hoverIgnore=!d.get("show"),n.setStyle({stroke:s,opacity:e.getItemVisual(t,"opacity")}),n.setStyle(u.getModel("lineStyle").getLineStyle()),n.hoverStyle=d.getModel("lineStyle").getLineStyle();var h=u.get("smooth");h&&!0===h&&(h=.4),n.setShape({smooth:h})},a.inherits(s,i.Group);var g=o.extend({type:"pie",init:function(){var e=new i.Group;this._sectorGroup=e},render:function(e,t,n,i){if(!i||i.from!==this.uid){var o=e.getData(),l=this._data,c=this.group,g=t.get("animation"),u=!l,d=e.get("animationType"),h=a.curry(r,this.uid,e,g,n),p=e.get("selectedMode");if(o.diff(l).add(function(e){var t=new s(o,e);u&&"scale"!==d&&t.eachChild(function(e){e.stopAnimation(!0)}),p&&t.on("click",h),o.setItemGraphicEl(e,t),c.add(t)}).update(function(e,t){var n=l.getItemGraphicEl(t);n.updateData(o,e),n.off("click"),p&&n.on("click",h),c.add(n),o.setItemGraphicEl(e,n)}).remove(function(e){var t=l.getItemGraphicEl(e);c.remove(t)}).execute(),g&&u&&o.count()>0&&"scale"!==d){var f=o.getItemLayout(0),m=Math.max(n.getWidth(),n.getHeight())/2,v=a.bind(c.removeClipPath,c);c.setClipPath(this._createClipPath(f.cx,f.cy,m,f.startAngle,f.clockwise,v,e))}this._data=o}},dispose:function(){},_createClipPath:function(e,t,n,a,o,r,l){var s=new i.Sector({shape:{cx:e,cy:t,r0:0,r:n,startAngle:a,endAngle:a,clockwise:o}});return i.initProps(s,{shape:{endAngle:a+(o?1:-1)*Math.PI*2}},l,r),s},containPoint:function(e,t){var n=t.getData().getItemLayout(0);if(n){var a=e[0]-n.cx,i=e[1]-n.cy,o=Math.sqrt(a*a+i*i);return o<=n.r&&o>=n.r0}}});e.exports=g},863:function(e,t,n){var a=n(39),i={updateSelectedMap:function(e){this._targetList=e.slice(),this._selectTargetMap=a.reduce(e||[],function(e,t){return e.set(t.name,t),e},a.createHashMap())},select:function(e,t){var n=null!=t?this._targetList[t]:this._selectTargetMap.get(e);"single"===this.get("selectedMode")&&this._selectTargetMap.each(function(e){e.selected=!1}),n&&(n.selected=!0)},unSelect:function(e,t){var n=null!=t?this._targetList[t]:this._selectTargetMap.get(e);n&&(n.selected=!1)},toggleSelected:function(e,t){var n=null!=t?this._targetList[t]:this._selectTargetMap.get(e);if(null!=n)return this[n.selected?"unSelect":"select"](e,t),n.selected},isSelected:function(e,t){var n=null!=t?this._targetList[t]:this._selectTargetMap.get(e);return n&&n.selected}};e.exports=i},864:function(e,t,n){var a=n(71),i=n(293),o=n(39),r=n(117),l=n(112).getPercentWithPrecision,s=n(571),c=n(863),g=a.extendSeriesModel({type:"series.pie",init:function(e){g.superApply(this,"init",arguments),this.legendDataProvider=function(){return this.getRawData()},this.updateSelectedMap(e.data),this._defaultLabelLine(e)},mergeOption:function(e){g.superCall(this,"mergeOption",e),this.updateSelectedMap(this.option.data)},getInitialData:function(e,t){var n=s(["value"],e.data),a=new i(n,this);return a.initData(e.data),a},getDataParams:function(e){var t=this.getData(),n=g.superCall(this,"getDataParams",e),a=[];return t.each("value",function(e){a.push(e)}),n.percent=l(a,e,t.hostModel.get("percentPrecision")),n.$vars.push("percent"),n},_defaultLabelLine:function(e){r.defaultEmphasis(e.labelLine,["show"]);var t=e.labelLine.normal,n=e.labelLine.emphasis;t.show=t.show&&e.label.normal.show,n.show=n.show&&e.label.emphasis.show},defaultOption:{zlevel:0,z:2,legendHoverLink:!0,hoverAnimation:!0,center:["50%","50%"],radius:[0,"75%"],clockwise:!0,startAngle:90,minAngle:0,selectedOffset:10,hoverOffset:10,avoidLabelOverlap:!0,percentPrecision:2,stillShowZeroSum:!0,label:{normal:{rotate:!1,show:!0,position:"outer"},emphasis:{}},labelLine:{normal:{show:!0,length:15,length2:15,smooth:!1,lineStyle:{width:1,type:"solid"}}},itemStyle:{normal:{borderWidth:1},emphasis:{}},animationType:"expansion",animationEasing:"cubicOut",data:[]}});o.mixin(g,c);var u=g;e.exports=u},875:function(e,t){e.exports=function(e){var t=e.findComponents({mainType:"legend"});t&&t.length&&e.filterSeries(function(e){for(var n=0;n<t.length;n++)if(!t[n].isSelected(e.name))return!1;return!0})}},876:function(e,t,n){var a=n(200),i=a.getLayoutRect,o=a.box,r=a.positionElement,l=n(152),s=n(91);t.layout=function(e,t,n){var a=t.getBoxLayoutParams(),l=t.get("padding"),s={width:n.getWidth(),height:n.getHeight()},c=i(a,s,l);o(t.get("orient"),e,t.get("itemGap"),c.width,c.height),r(e,a,s,l)},t.makeBackground=function(e,t){var n=l.normalizeCssArray(t.get("padding")),a=t.getItemStyle(["color","opacity"]);return a.fill=t.get("backgroundColor"),e=new s.Rect({shape:{x:e.x-n[3],y:e.y-n[0],width:e.width+n[1]+n[3],height:e.height+n[0]+n[2],r:t.get("borderRadius")},style:a,silent:!0,z2:-1})}},877:function(e,t,n){n(135).__DEV__;var a=n(71),i=n(39),o=n(267).createSymbol,r=n(91),l=n(876).makeBackground,s=n(200),c=i.curry,g=i.each,u=r.Group,d=a.extendComponentView({type:"legend.plain",newlineDisabled:!1,init:function(){this.group.add(this._contentGroup=new u),this._backgroundEl},getContentGroup:function(){return this._contentGroup},render:function(e,t,n){if(this.resetInner(),e.get("show",!0)){var a=e.get("align");a&&"auto"!==a||(a="right"===e.get("left")&&"vertical"===e.get("orient")?"right":"left"),this.renderInner(a,e,t,n);var o=e.getBoxLayoutParams(),r={width:n.getWidth(),height:n.getHeight()},c=e.get("padding"),g=s.getLayoutRect(o,r,c),u=this.layoutInner(e,a,g),d=s.getLayoutRect(i.defaults({width:u.width,height:u.height},o),r,c);this.group.attr("position",[d.x-u.x,d.y-u.y]),this.group.add(this._backgroundEl=l(u,e))}},resetInner:function(){this.getContentGroup().removeAll(),this._backgroundEl&&this.group.remove(this._backgroundEl)},renderInner:function(e,t,n,a){var o=this.getContentGroup(),r=i.createHashMap(),l=t.get("selectedMode");g(t.getData(),function(i,s){var g=i.get("name");if(this.newlineDisabled||""!==g&&"\n"!==g){var d=n.getSeriesByName(g)[0];if(!r.get(g))if(d){var m=d.getData(),v=m.getVisual("color");"function"==typeof v&&(v=v(d.getDataParams(0)));var y=m.getVisual("legendSymbol")||"roundRect",S=m.getVisual("symbol");this._createItem(g,s,i,t,y,S,e,v,l).on("click",c(h,g,a)).on("mouseover",c(p,d,null,a)).on("mouseout",c(f,d,null,a)),r.set(g,!0)}else n.eachRawSeries(function(n){if(!r.get(g)&&n.legendDataProvider){var o=n.legendDataProvider(),u=o.indexOfName(g);if(u<0)return;var d=o.getItemVisual(u,"color");this._createItem(g,s,i,t,"roundRect",null,e,d,l).on("click",c(h,g,a)).on("mouseover",c(p,n,g,a)).on("mouseout",c(f,n,g,a)),r.set(g,!0)}},this)}else o.add(new u({newline:!0}))},this)},_createItem:function(e,t,n,a,l,s,c,g,d){var h=a.get("itemWidth"),p=a.get("itemHeight"),f=a.get("inactiveColor"),m=a.isSelected(e),v=new u,y=n.getModel("textStyle"),S=n.get("icon"),x=n.getModel("tooltip"),w=x.parentModel;if(l=S||l,v.add(o(l,0,0,h,p,m?g:f,!0)),!S&&s&&(s!==l||"none"==s)){var b=.8*p;"none"===s&&(s="circle"),v.add(o(s,(h-b)/2,(p-b)/2,b,b,m?g:f))}var I="left"===c?h+5:-5,M=c,A=a.get("formatter"),_=e;"string"==typeof A&&A?_=A.replace("{name}",null!=e?e:""):"function"==typeof A&&(_=A(e)),v.add(new r.Text({style:r.setTextStyle({},y,{text:_,x:I,y:p/2,textFill:m?y.getTextColor():f,textAlign:M,textVerticalAlign:"middle"})}));var L=new r.Rect({shape:v.getBoundingRect(),invisible:!0,tooltip:x.get("show")?i.extend({content:e,formatter:w.get("formatter",!0)||function(){return e},formatterParams:{componentType:"legend",legendIndex:a.componentIndex,name:e,$vars:["name"]}},x.option):null});return v.add(L),v.eachChild(function(e){e.silent=!0}),L.silent=!d,this.getContentGroup().add(v),r.setHoverStyle(v),v.__legendDataIndex=t,v},layoutInner:function(e,t,n){var a=this.getContentGroup();s.box(e.get("orient"),a,e.get("itemGap"),n.width,n.height);var i=a.getBoundingRect();return a.attr("position",[-i.x,-i.y]),this.group.getBoundingRect()}});function h(e,t){t.dispatchAction({type:"legendToggleSelect",name:e})}function p(e,t,n){var a=n.getZr().storage.getDisplayList()[0];a&&a.useHoverLayer||e.get("legendHoverLink")&&n.dispatchAction({type:"highlight",seriesName:e.name,name:t})}function f(e,t,n){var a=n.getZr().storage.getDisplayList()[0];a&&a.useHoverLayer||e.get("legendHoverLink")&&n.dispatchAction({type:"downplay",seriesName:e.name,name:t})}e.exports=d},878:function(e,t,n){var a=n(71),i=n(39);function o(e,t,n){var a,o={},r="toggleSelected"===e;return n.eachComponent("legend",function(n){r&&null!=a?n[a?"select":"unSelect"](t.name):(n[e](t.name),a=n.isSelected(t.name));var l=n.getData();i.each(l,function(e){var t=e.get("name");if("\n"!==t&&""!==t){var a=n.isSelected(t);o.hasOwnProperty(t)?o[t]=o[t]&&a:o[t]=a}})}),{name:t.name,selected:o}}a.registerAction("legendToggleSelect","legendselectchanged",i.curry(o,"toggleSelected")),a.registerAction("legendSelect","legendselected",i.curry(o,"select")),a.registerAction("legendUnSelect","legendunselected",i.curry(o,"unSelect"))},879:function(e,t,n){var a=n(71),i=n(39),o=n(160),r=a.extendComponentModel({type:"legend.plain",dependencies:["series"],layoutMode:{type:"box",ignoreSize:!0},init:function(e,t,n){this.mergeDefaultAndTheme(e,n),e.selected=e.selected||{}},mergeOption:function(e){r.superCall(this,"mergeOption",e)},optionUpdated:function(){this._updateData(this.ecModel);var e=this._data;if(e[0]&&"single"===this.get("selectedMode")){for(var t=!1,n=0;n<e.length;n++){var a=e[n].get("name");if(this.isSelected(a)){this.select(a),t=!0;break}}!t&&this.select(e[0].get("name"))}},_updateData:function(e){var t=i.map(this.get("data")||[],function(e){return"string"!=typeof e&&"number"!=typeof e||(e={name:e}),new o(e,this,this.ecModel)},this);this._data=t;var n=i.map(e.getSeries(),function(e){return e.name});e.eachSeries(function(e){if(e.legendDataProvider){var t=e.legendDataProvider();n=n.concat(t.mapArray(t.getName))}}),this._availableNames=n},getData:function(){return this._data},select:function(e){var t=this.option.selected;if("single"===this.get("selectedMode")){var n=this._data;i.each(n,function(e){t[e.get("name")]=!1})}t[e]=!0},unSelect:function(e){"single"!==this.get("selectedMode")&&(this.option.selected[e]=!1)},toggleSelected:function(e){var t=this.option.selected;t.hasOwnProperty(e)||(t[e]=!0),this[t[e]?"unSelect":"select"](e)},isSelected:function(e){var t=this.option.selected;return!(t.hasOwnProperty(e)&&!t[e])&&i.indexOf(this._availableNames,e)>=0},defaultOption:{zlevel:0,z:4,show:!0,orient:"horizontal",left:"center",top:0,align:"auto",backgroundColor:"rgba(0,0,0,0)",borderColor:"#ccc",borderRadius:0,borderWidth:0,padding:5,itemGap:10,itemWidth:25,itemHeight:14,inactiveColor:"#ccc",textStyle:{color:"#333"},selectedMode:!0,tooltip:{show:!1}}}),l=r;e.exports=l},883:function(e,t,n){}}]);