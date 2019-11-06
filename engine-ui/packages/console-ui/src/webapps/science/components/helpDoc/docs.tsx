// 帮助文档
import * as React from 'react'

export const modelSavePath = () => {
    return (<div>
        <p>
            可在模型存储路径下查看模型数据
        </p>
    </div>)
}
export const updateModelDeal = () => {
    return (<div>
        <p>
            LOAD表示模型将加载此组参数，供外部调用。
        </p>
    </div>)
}
export const modelPath = () => {
    return (<div>
        <p>
            填写模型部署的 HDFS 路径，需与代码中的 HDFS 路径地址保持一致。
        </p>
    </div>)
}
export const schedulingEnd = () => {
    return (
        <div>任务结束包括成功、失败、取消3种情况</div>
    )
}

export const additionalColumn = (
    <div>
        <p>可选项。附加列指可将输入表的哪些列输出至输出聚类结果表，列名以逗号分隔。</p>
    </div>
)
export const missRate = (
    <div>
        <p>若选择删除，编码后的数据将线性无关。</p>
    </div>
)
