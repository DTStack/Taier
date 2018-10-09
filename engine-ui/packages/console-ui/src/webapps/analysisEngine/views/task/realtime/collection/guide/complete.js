import React from "react";

import {Card, Button} from "antd";

import Source from "./collectionSource"
import Target from "./collectionTarget"

function Mask() {
    return <div className="mask-lock-layer" />
}

class CollectionComplete extends React.Component {
    navtoStep(step){
        this.props.navtoStep(step)
    }
    prev() {
        this.props.navtoStep(1)
    }
    save(){
        this.props.saveJob();
    }
    render() {
        const {currentPage, collectionData}=this.props;
        return (
            <div className="g-step5">
                <div className="m-preview"
                    style={{ padding: '0 20' }}
                >
                    <Card bordered={false}
                        style={{ marginBottom: 10 }}
                        title="选择来源"
                        extra={<a href="javascript:void(0)"
                            onClick={() => this.navtoStep(0)}>修改</a>
                        }
                    >
                        <Source collectionData={collectionData} readonly />
                        <Mask />
                    </Card>
                    <Card bordered={false}
                        style={{ marginBottom: 10 }}
                        title="选择目标"
                        extra={<a href="javascript:void(0)"
                            onClick={() => this.navtoStep(1)}>修改</a>
                        }
                    >
                        <Target collectionData={collectionData} readonly />
                        <Mask />
                    </Card>
                </div>
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                    <Button type="primary"  disabled={!currentPage.notSynced} onClick={() => this.save()}>保存</Button>
                </div>
            </div>
        )
    }
}

export default CollectionComplete;