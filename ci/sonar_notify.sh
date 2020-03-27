#!/bin/bash
#参考钉钉文档 https://open-doc.dingtalk.com/microapp/serverapi2/qf2nxq
 sonarreport=$(curl -s http://172.16.100.198:8082/?projectname=dt-insight-engine/DAGScheduleX)
 curl -s "https://oapi.dingtalk.com/robot/send?access_token=343313befb1418a0b0d09774d0226d6210e9b03956e73a0ed012f78fb9d01578" \
   -H "Content-Type: application/json" \
   -d "{
     \"msgtype\": \"markdown\",
     \"markdown\": {
         \"title\":\"sonar代码质量\",
         \"text\": \"## sonar代码质量报告: \n
> [sonar地址](http://172.16.100.198:9000/dashboard?id=dt-insight-engine/DAGScheduleX) \n
> ${sonarreport} \n\"
     }
 }"