import * as React from 'react';
import { HELP_DOC_URL } from '../../consts';

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
                    <p><a target="blank" href={HELP_DOC_URL.MAKE_API}>API生成</a></p>
                    <p><a target="blank" href={HELP_DOC_URL.RELEASE_API}>API发布</a></p>
                    <p><a target="blank" href={HELP_DOC_URL.APPLY_API}>API申请</a></p>
                    <p><a target="blank" href={HELP_DOC_URL.TEST_API}>API测试</a></p>
                    <p><a target="blank" href={HELP_DOC_URL.CALL_API}>API调用</a></p>
                </section>
                <section>
                    <h3>快速入门</h3>
                </section>
            </div>
        )
    }
}
export default SummaryPanel;
