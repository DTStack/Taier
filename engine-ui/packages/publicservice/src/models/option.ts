export const pieOption:any = {
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b}: {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    x: "left",
    data: ["直接访问", "邮件营销", "联盟广告", "视频广告", "搜索引擎"]
  },
  series: [
    {
      name: "访问来源",
      type: "pie",
      radius: ["100%", "70%"],
      avoidLabelOverlap: false,
      label: {
        normal: {
          show: false,
          position: "center"
        },
        emphasis: {
          show: true,
          textStyle: {
            fontSize: "30",
            fontWeight: "bold"
          }
        }
      },
      labelLine: {
        normal: {
          show: false
        }
      },
      data: [
        { value: 335, name: "直接访问" },
        { value: 310, name: "邮件营销" },
        { value: 234, name: "联盟广告" },
        { value: 135, name: "视频广告" },
        { value: 1548, name: "搜索引擎" }
      ]
    }
  ]
};

// //柱状图数据
// export const barOption :any= {
//   color: ["#3398DB"],
//   tooltip: {
//     trigger: "axis",
//     axisPointer: {
//       // 坐标轴指示器，坐标轴触发有效
//       type: "shadow" // 默认为直线，可选为：'line' | 'shadow'
//     }
//   },
//   grid: {
//     left: "3%",
//     right: "4%",
//     bottom: "3%",
//     containLabel: true
//   },
//   xAxis: [
//     {
//       type: "category",
//       data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
//       axisTick: {
//         alignWithLabel: true
//       }
//     }
//   ],
//   yAxis: [
//     {
//       type: "value"
//     }
//   ],
//   series: [
//     {
//       name: "直接访问",
//       type: "bar",
//       barWidth: "60%",
//       data: [10, 52, 200, 334, 390, 330, 220]
//     }
//   ]
// };

// //折线图数据
// export const lineOption :any= {
//   title: {
//     text: "一天用电量分布",
//     subtext: "纯属虚构"
//   },
//   tooltip: {
//     trigger: "axis",
//     axisPointer: {
//       type: "cross"
//     }
//   },
//   toolbox: {
//     show: true,
//     feature: {
//       saveAsImage: {}
//     }
//   },
//   xAxis: {
//     type: "category",
//     boundaryGap: false,
//     data: [
//       "00:00",
//       "01:15",
//       "02:30",
//       "03:45",
//       "05:00",
//       "06:15",
//       "07:30",
//       "08:45",
//       "10:00",
//       "11:15",
//       "12:30",
//       "13:45",
//       "15:00",
//       "16:15",
//       "17:30",
//       "18:45",
//       "20:00",
//       "21:15",
//       "22:30",
//       "23:45"
//     ]
//   },
//   yAxis: {
//     type: "value",
//     axisLabel: {
//       formatter: "{value} W"
//     },
//     axisPointer: {
//       snap: true
//     }
//   },
//   visualMap: {
//     show: false,
//     dimension: 0,
//     pieces: [
//       {
//         lte: 6,
//         color: "green"
//       },
//       {
//         gt: 6,
//         lte: 8,
//         color: "red"
//       },
//       {
//         gt: 8,
//         lte: 14,
//         color: "green"
//       },
//       {
//         gt: 14,
//         lte: 17,
//         color: "red"
//       },
//       {
//         gt: 17,
//         color: "green"
//       }
//     ]
//   },
//   series: [
//     {
//       name: "用电量",
//       type: "line",
//       smooth: true,
//       data: [
//         300,
//         280,
//         250,
//         260,
//         270,
//         300,
//         550,
//         500,
//         400,
//         390,
//         380,
//         390,
//         400,
//         500,
//         600,
//         750,
//         800,
//         700,
//         600,
//         400
//       ],
//       markArea: {
//         data: [
//           [
//             {
//               name: "早高峰",
//               xAxis: "07:30"
//             },
//             {
//               xAxis: "10:00"
//             }
//           ],
//           [
//             {
//               name: "晚高峰",
//               xAxis: "17:30"
//             },
//             {
//               xAxis: "21:15"
//             }
//           ]
//         ]
//       }
//     }
//   ]
// };

// //散点图数据
// export const scatterOption :any = {
//   tooltip: {
//     trigger: "axis",
//     showDelay: 0,
//     axisPointer: {
//       show: true,
//       type: "cross",
//       lineStyle: {
//         type: "dashed",
//         width: 1
//       }
//     },
//     zlevel: 1
//   },
//   legend: {
//     data: ["sin", "cos"]
//   },
//   toolbox: {
//     show: true,
//     feature: {
//       mark: { show: true },
//       dataZoom: { show: true },
//       dataView: { show: true, readOnly: false },
//       restore: { show: true },
//       saveAsImage: { show: true }
//     }
//   },
//   xAxis: [
//     {
//       type: "value",
//       scale: true
//     }
//   ],
//   yAxis: [
//     {
//       type: "value",
//       scale: true
//     }
//   ],
//   series: [
//     {
//       name: "sin",
//       type: "scatter",
//       large: true,
//       symbolSize: 3,
//       data: (function() {
//         var d :any = [];
//         var len :number = 10000;
//         var x :number= 0;
//         while (len--) {
//           x = (Math.random() * 10).toFixed(3) - 0;
//           d.push([
//             x,
//             //Math.random() * 10
//             (Math.sin(x) - x * (len % 2 ? 0.1 : -0.1) * Math.random()).toFixed(
//               3
//             ) - 0
//           ]);
//         }
//         //console.log(d)
//         return d;
//       })()
//     },
//     {
//       name: "cos",
//       type: "scatter",
//       large: true,
//       symbolSize: 2,
//       data: (function() {
//         var d = [];
//         var len = 20000;
//         var x = 0;
//         while (len--) {
//           x = (Math.random() * 10).toFixed(3) - 0;
//           d.push([
//             x,
//             //Math.random() * 10
//             (Math.cos(x) - x * (len % 2 ? 0.1 : -0.1) * Math.random()).toFixed(
//               3
//             ) - 0
//           ]);
//         }
//         //console.log(d)
//         return d;
//       })()
//     }
//   ]
// };
// // 地图
// export const mapOption :any = {
//   title: {
//     show: false
//   },
//   tooltip: {
//     backgroundColor: "white",
//     textStyle: {
//       color: "#666"
//     }
//   },
//   legend: {
//     show: false
//   },
//   grid: {
//     top: 0,
//     left: 0,
//     right: 0,
//     bottom: 0
//   },
//   toolbox: {
//     show: false,
//     orient: "vertical",
//     left: "right",
//     top: "center"
//   },
//   series: [
//     {
//       name: "热销指数",
//       type: "map",
//       mapType: "china",
//       layoutCenter: ["50%", "50%"],
//       layoutSize: "320px",
//       roam: false,
//       itemStyle: {
//         normal: { label: { show: true } },
//         emphasis: { label: { show: true } }
//       },
//       showLegendSymbol: false,
//       // 地图标识点去掉
//       label: {
//         normal: {
//           show: false
//         },
//         // 是否显示省市名
//         emphasis: {
//           show: true
//         }
//       },
//       data: [{ name: "广东", selected: true }]
//     }
//   ]
// };
// export const wordOption :any={"grid":{"top":0,"left":0,"right":0,"bottom":0},"tooltip":{"position":[10,5],"textStyle":{"color":"#63697B"},"backgroundColor":"#fff","hideDelay":1000,"triggerOn":"click"},"size":["100%","100%"],"series":[{"width":"120%","height":"120%","left":"center","top":"center","name":"热点分析","shape":"circle","type":"wordCloud","gridSize":20,"drawOutOfBound":false,"sizeRange":[12,26],"rotationRange":[0,0],"textPadding":20,"autoSize":{"enable":true,"minSize":6},"textStyle":{"normal":{"lineHeight":"30px"},"emphasis":{"shadowBlur":10,"shadowColor":"#999"}},"data":[{"name":"小说","value":66,"id":8729553,"bookModels":null},{"name":"当代","value":46,"id":8721726,"bookModels":null},{"name":"推理","value":43,"id":8718080,"bookModels":null},{"name":"悬疑","value":42,"id":8702636,"bookModels":null},{"name":"童书","value":25,"id":8700349,"bookModels":null},{"name":"阴暗","value":20,"id":8743571,"bookModels":null},{"name":"犯罪","value":20,"id":8726492,"bookModels":null},{"name":"哥伦比亚","value":20,"id":8724805,"bookModels":null},{"name":"侦探","value":19,"id":8698699,"bookModels":null},{"name":"社会","value":17,"id":8732391,"bookModels":null},{"name":"文学","value":15,"id":8709921,"bookModels":null},{"name":"散文集","value":14,"id":8745084,"bookModels":null},{"name":"美国","value":14,"id":8705077,"bookModels":null},{"name":"图画书","value":12,"id":8740461,"bookModels":null},{"name":"动漫","value":12,"id":8732975,"bookModels":null},{"name":"儿童文学","value":12,"id":8731043,"bookModels":null},{"name":"欧美","value":11,"id":8744988,"bookModels":null},{"name":"卡通","value":11,"id":8717893,"bookModels":null},{"name":"作品集","value":10,"id":8709950,"bookModels":null},{"name":"绘本","value":9,"id":8718783,"bookModels":null}]}]}
