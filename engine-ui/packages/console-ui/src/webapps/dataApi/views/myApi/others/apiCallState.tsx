import * as React from 'react';
import { Radio } from 'antd';
import CountGraph from './countGraph';
import DelayGraph from './delayGraph';

const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;
const GRAPH_TYPE: any = {
    COUNT: 'count',
    DEALY: 'delay'
}

class ApiCallState extends React.Component<any, any> {
    state: any = {
        graphType: GRAPH_TYPE.COUNT
    }
    getGraph () {
        const { graphType } = this.state;
        switch (graphType) {
            case GRAPH_TYPE.COUNT: {
                return <CountGraph
                    {...this.props}
                />
            }
            case GRAPH_TYPE.DEALY: {
                return <DelayGraph
                    {...this.props}
                />
            }
            default: {
                return null;
            }
        }
    }
    render () {
        const { graphType } = this.state;
        return (
            <div style={{ paddingLeft: 30 }}>
                <div style={{ paddingTop: '20px' }}>
                    <RadioGroup onChange={(e: any) => {
                        this.setState({
                            graphType: e.target.value
                        })
                    }} value={graphType}>
                        <RadioButton value={GRAPH_TYPE.COUNT}>调用次数</RadioButton>
                        <RadioButton value={GRAPH_TYPE.DEALY}>调用耗时</RadioButton>
                    </RadioGroup>
                </div>
                {this.getGraph()}
            </div>
        )
    }
}
export default ApiCallState;
