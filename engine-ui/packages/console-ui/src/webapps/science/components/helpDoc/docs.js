// 帮助文档
import React from 'react'

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
