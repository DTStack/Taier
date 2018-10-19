// 帮助文档
import React from 'react'

export const dataMapTypeSummary = (
    <div>
        <p>
            可针对主表的不同特性建立不同的DataMap类型。
            Preaggregate针对预聚合表，Timeseries针对时间序列表，BloomFilter适合在高基数列（如名称/ID）上进行精确匹配的查询，详情请参考帮助文档；
        </p>
    </div>
)


export const bloomSizeSummary = (
    <div>
        <p>
            BloomSize：该值由 BloomFilter 内部使用，用于预期插入的数据数量，它将影响 BloomFilter 索引的大小。
        </p>
        <p>
            由于每个 blocklet 都有一个 BloomFilter，所以该值是 blocklet 中记录近似的个数。 换句话说，应该是 32000 * #noOfPagesInBlocklet。必须填写整数。
        </p>
    </div>
)

export const bloomFPPSummary = (
    <div>
        <p>
            BloomFPP：该值在 BloomFilter 内部被用作为假阳性概率（False-Positive Probability），它将影响 bloomfilter 索引的大小以及 BloomFilter 的哈希函数的数量。该值的范围应该在 (0, 100) 之间的整数。       
        </p>
    </div>
)

export const isCompressIndex = (
    <div>
        <p>是否压缩索引文件：是否压缩 BloomFilter 索引文件。</p>
    </div>
)

