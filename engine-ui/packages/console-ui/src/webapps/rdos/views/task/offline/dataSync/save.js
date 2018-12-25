import React from 'react';
import { Card, Button } from 'antd';

import Source from './source';
import Target from './target';
import Keymap from './keymap';
import Channel from './channel';

function Mask () {
    return <div className="mask-lock-layer" />
}

export default class Save extends React.Component {
    constructor (props) {
        super(props);
        this.navtoStep = this.props.navtoStep;
    }

    render () {
        const { navtoStep } = this;
        const { notSynced, isIncrementMode } = this.props;

        return <div className="g-step5">
            <div className="m-preview"
                style={{ padding: '0 20' }}
            >
                <Card bordered={ false }
                    style={{ marginBottom: 10 }}
                    title="选择来源"
                    extra={ <a href="javascript:void(0)"
                        onClick={ () => this.navtoStep(0) }>修改</a>
                    }
                >
                    <Source
                        readonly
                        isIncrementMode={isIncrementMode}
                    />
                    <Mask />
                </Card>
                <Card bordered={ false }
                    style={{ marginBottom: 10 }}
                    title="选择目标"
                    extra={ <a href="javascript:void(0)"
                        onClick={ () => this.navtoStep(1) }>修改</a>
                    }
                >
                    <Target
                        readonly
                        isIncrementMode={isIncrementMode}
                    />
                    <Mask />
                </Card>
                <Card bordered={ false }
                    style={{ marginBottom: 10 }}
                    title="字段映射"
                    extra={ <a href="javascript:void(0)"
                        onClick={ () => this.navtoStep(2) }>修改</a>
                    }
                >
                    <Keymap readonly />
                    <Mask />
                </Card>
                <Card bordered={ false }
                    style={{ marginBottom: 10 }}
                    title="通道控制"
                    extra={ <a href="javascript:void(0)"
                        onClick={ () => this.navtoStep(3) }>修改</a>
                    }
                >
                    <Channel
                        readonly
                        isIncrementMode={isIncrementMode}
                    />
                    <Mask />
                </Card>
            </div>
            <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" disabled={!notSynced} onClick={() => this.save(navtoStep)}>保存</Button>
            </div>
        </div>
    }

    prev (cb) {
        /* eslint-disable */
        cb.call(null, 3);
        /* eslint-disable */

    }

    save (cb) {
        this.props.saveJob(cb);
    }
}
