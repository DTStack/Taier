#!/bin/bash
#参考钉钉文档 https://open-doc.dingtalk.com/microapp/serverapi2/qf2nxq
 sonarreport=$(curl -s http://172.16.100.198:8082/?projectname=dt-insight-engine/DAGScheduleX)
 curl -s "https://oapi.dingtalk.com/robot/send?access_token=25f4c43f5ba889d406708f831fbf6be5c6ad352070b746140e424006b54deede" \
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