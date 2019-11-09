import * as React from 'react';
import { Breadcrumb, Steps, Button } from 'antd';
import { Link } from 'react-router';

import BaseForm from './baseForm';
import DimensionInfor from './dimensionInfor';
import AtomicLabel from './atomicLabel';

const Step = Steps.Step;

export default class EntityEdit extends React.Component<any, any> {
    state: any = {
        current: 0
    }

    componentDidMount () {
        console.log('location:', this.props.location)
    }

    prev = () => {
        const { current } = this.state;
        this.setState({
            current: current - 1
        })
    }

    next = () => {
        const { current } = this.state;
        this.setState({
            current: current + 1
        })
    }

    handleSave = () => {

    }

    render () {
        const { current } = this.state;
        const steps = [{
            title: '编辑基础信息',
            content: <BaseForm />
        }, {
            title: '设置维度信息',
            content: <DimensionInfor />
        }, {
            title: '生成原子标签',
            content: <AtomicLabel />
        }];
        return (
            <div className="entity-edit bg-w" style={{ padding: '20px', margin: '20px' }}>
                <Breadcrumb>
                    <Breadcrumb.Item><Link to="/entityManage">实体管理</Link></Breadcrumb.Item>
                    <Breadcrumb.Item>新增实体</Breadcrumb.Item>
                </Breadcrumb>
                <Steps current={current}>
                    {steps.map(item => <Step key={item.title} title={item.title} />)}
                </Steps>
                <div className="steps-content">
                    {steps[current].content}
                </div>
                <div className="steps-action">
                    { current > 0 &&
                        <Button style={{ marginRight: 8 }}
                            onClick={() => this.prev()}
                        > 上一步 </Button>
                    }
                    { current < steps.length - 1 &&
                        <Button type="primary"
                            onClick={ () => this.next() }
                        >下一步</Button>
                    }
                    { current === steps.length - 1 && <Button type="primary"
                        onClick={this.handleSave}
                    >保存</Button>
                    }
                </div>
            </div>
        )
    }
}
