import * as React from 'react';

import { Card, Button } from 'antd';

import Source from './collectionSource'
import Target from './collectionTarget'
import ChannelControl from './channelControl';
function Mask () {
    return <div className="mask-lock-layer" />
}

class CollectionComplete extends React.Component<any, any> {
    navtoStep (step: any) {
        this.props.navtoStep(step)
    }
    prev () {
        this.props.navtoStep(2)
    }
    save () {
        this.props.saveJob();
    }
    render () {
        const { currentPage, collectionData } = this.props;
        return (
            <div className="g-step5">
                <div className="m-preview"
                    style={{ padding: '0px 20px' }}
                >
                    <Card bordered={false}
                        style={{ marginBottom: 10 }}
                        title={<div style={{ textAlign: 'center' }}>选择来源</div>}
                        extra={<a href="javascript:void(0)"
                            onClick={() => this.navtoStep(0)}>修改</a>
                        }
                    >
                        <Source collectionData={collectionData} readonly />
                        <Mask />
                    </Card>
                    <Card bordered={false}
                        style={{ marginBottom: 10 }}
                        title={<div style={{ textAlign: 'center' }}>选择目标</div>}
                        extra={<a href="javascript:void(0)"
                            onClick={() => this.navtoStep(1)}>修改</a>
                        }
                    >
                        <Target collectionData={collectionData} updateTargetMap={this.props.updateTargetMap} readonly />
                        <Mask />
                    </Card>
                    <Card bordered={false}
                        style={{ marginBottom: 10 }}
                        title={<div style={{ textAlign: 'center' }}>通道控制</div>}
                        extra={<a href="javascript:void(0)"
                            onClick={() => this.navtoStep(2)}>修改</a>
                        }
                    >
                        <ChannelControl collectionData={collectionData} updateChannelControlMap={this.props.updateChannelControlMap} readonly />
                        <Mask />
                    </Card>
                </div>
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                    <Button type="primary" disabled={!currentPage.notSynced} onClick={() => this.save()}>保存</Button>
                </div>
            </div>
        )
    }
}

export default CollectionComplete;
