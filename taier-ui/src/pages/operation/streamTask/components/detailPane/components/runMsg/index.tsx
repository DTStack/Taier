import * as React from 'react';
import { Radio } from 'antd';
import Common from './common';
import { TASK_STATUS } from '@/constant';
import DetailTable from './detailTable';
import { IStreamTaskProps } from '@/interface';

const Api = {} as any

interface IProps {
    data: IStreamTaskProps | undefined;
    isShow?: boolean;
}

interface IState {
    flinkJson: IDataSource[]; 
    loading: boolean; 
    tabKey: string;
}

export interface IDataSource {
    jobVertexName: string;
    parallelism: number;
    bytesReceived: number;
    bytesSent: number;
    recordsReceived: number;
    recordsSent: number;
}

class RunMsg extends React.Component<IProps, IState> {
    constructor (props: IProps) {
        super(props);
        this.state = {
            flinkJson: [],
            loading: false,
            tabKey: 'vertex'
        }
    }

    _timeClock: NodeJS.Timeout | undefined;

    componentDidMount () {
        this.getFlinkJsonData()
    }

    componentWillUnmount () {
        this._timeClock && clearTimeout(this._timeClock);
    }

    getFlinkJsonData = async (isSlient?: boolean) => {
        const { data } = this.props;
        const { id, status } = data || {}
        if (!id || status != TASK_STATUS.RUNNING) return;
        if (!isSlient) this.setState({ loading: true })
        this._timeClock && clearTimeout(this._timeClock);
        let res = await Api.getTaskJson({ id })
        if (res.code === 1) {
            this.rollData(status);
            const resData = res.data || {};
            const { taskVertices } = resData
            this.setState({ flinkJson: taskVertices || [] })
        }
        this.setState({ loading: false })
    }

    rollData = (status: number) => {
        if (status == TASK_STATUS.RUNNING) {
            this._timeClock = setTimeout(() => {
                this.getFlinkJsonData(true)
            }, 15000)
        }
    }

    render () {
        const { flinkJson, loading, tabKey } = this.state;
        if (!this.props.isShow) {
            return null;
        }
        return (
            <div className="c-collapse-wrapper">
                <div className="graph-content-box">
                    <Radio.Group style={{ padding: '0 20px 12px' }} value={tabKey} onChange={e => { this.setState({ tabKey: e.target.value }) }}>
                        <Radio.Button value="vertex">Vertex 拓扑</Radio.Button>
                        <Radio.Button value="detail">详情列表</Radio.Button>
                    </Radio.Group>
                    {tabKey === 'vertex' && <Common {...this.state} refresh={this.getFlinkJsonData} />}
                    {tabKey === 'detail' && <DetailTable tableData={flinkJson} loading={loading} {...this.props} />}
                </div>
            </div>
        )
    }
}

export default RunMsg;
