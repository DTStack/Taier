import * as React from 'react';

class SummaryPanel extends React.Component<any, any> {
    render () {
        return (
            <div className="summary-pannel">
                <section>
                    <h3>项目汇总</h3>
                    <div className="project-summary_flex">
                        <div>
                            总项目数
                            <p>5</p>
                        </div>
                        <div>
                            API创建数
                            <p>5</p>
                        </div>
                        <div>
                            API发布数
                            <p>5</p>
                        </div>
                    </div>
                    <p>最近24h累计调用次数</p>
                    <p>最近24h调用失败率</p>
                </section>
                <section>
                    <h3>常用操作</h3>
                    <p><a>API生成</a></p>
                    <p><a>API发布</a></p>
                    <p><a>API申请</a></p>
                    <p><a>API测试</a></p>
                    <p><a>API调用</a></p>
                </section>
                <section>
                    <h3>快速入门</h3>
                </section>
            </div>
        )
    }
}
export default SummaryPanel;

