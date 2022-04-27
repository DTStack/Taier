import * as React from 'react';
import { TASK_STATUS } from '@/constant';
import { IStreamTaskProps } from '@/interface';
import TaskManagerList, { ITaskList } from './list';
import TaskManagerLog from './log';

interface IProps {
    data: IStreamTaskProps | undefined;
    isShow: boolean;
}

interface IState {
    taskDetail: ITaskList | null;
}

class TaskManager extends React.Component<IProps, IState> {
    state: IState = {
        taskDetail: null
    }
    
    toTaskDetail = (record: ITaskList | null) => {
        this.setState({
            taskDetail: record
        })
    }

    isFail = (status: any) => {
        return status == TASK_STATUS.RUN_FAILED || status == TASK_STATUS.STOPED;
    }

    render () {
        const { taskDetail } = this.state;
        const { data, isShow } = this.props;
        if (!isShow) return null
        return (
            <div style={{ height: '100%' }}>
                {taskDetail
                    ? <TaskManagerLog
                        data={data}
                        taskDetail={taskDetail}
                        toTaskDetail={this.toTaskDetail}
                        isFail={this.isFail(data?.status)} />
                    : <TaskManagerList
                        toTaskDetail={this.toTaskDetail}
                        data={data} />}
            </div>
        )
    }
}
export default TaskManager;
