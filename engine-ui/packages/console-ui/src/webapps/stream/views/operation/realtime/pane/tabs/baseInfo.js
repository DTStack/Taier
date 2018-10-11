import React from "react"

import Editor from 'widgets/code-editor'
import { createLinkMark } from 'widgets/code-editor/utils'

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
        const {data={}} = this.props;
        const {status} = data;
        switch(status){
            case TASK_STATUS.RUN_FAILED:
            case TASK_STATUS.SUBMIT_FAILED:{
                return (
                    <Editor sync value={data.taskDesc} options={editorOptions} />
                )
            }
            case TASK_STATUS.RUNNING:
            case TASK_STATUS.SET_SUCCESS:{
                return (
                    "此处有图"
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