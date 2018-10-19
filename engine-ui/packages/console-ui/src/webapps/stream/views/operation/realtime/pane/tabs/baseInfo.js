import React from "react"

import Editor from 'widgets/code-editor'
import { createLinkMark } from 'widgets/code-editor/utils'

import StreamDetailGraph from "./graph"
import { TASK_TYPE, TASK_STATUS } from "../../../../../comm/const";

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true,
}

class BaseInfo extends React.Component {
    componentDidMount(){
        console.log("BaseInfo")
    }
    getBaseInfo(){
        const {data={},isShow} = this.props;
        const {status} = data;
        /**
         * 不显示的时候这里不能渲染，
         * 因为Editor和echarts绘图的时候会计算当前dom大小
         * 不显示的时候大小为0，会造成显示错误
         */
        if(!isShow){
            return null;
        }
        switch(status){
            case TASK_STATUS.RUN_FAILED:
            case TASK_STATUS.SUBMIT_FAILED:{
                return (
                    <Editor sync value={data.taskDesc} options={editorOptions} />
                )
            }
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.FINISHED:{
                return (
                   <StreamDetailGraph data={data} />
                )
            }
            default:{
                return "该任务暂未运行"
            }
            
        }
    }
    render() {
        return (
           <div>
               {this.getBaseInfo()}
           </div>
        )
    }
}

export default BaseInfo;