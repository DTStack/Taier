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

export const sortScope = (
    <div>
        <p>数据加载期间指定排序范围</p>
        <p>LOCAL_SORT: 默认值，本地排序</p>
        <p>NO_SORT: 这将会以未经排序的方式加载数据，会显着提高负载性能。</p>
        <p>BATCH_SORT: 如果块个数 > 并行性，它将增加数据的加载性能，但会减少数据的查询性能。</p>
        <p>GLOBAL_SORT: 这会增加数据的查询性能，特别是高并发查询。如果你特别关心加载资源的隔离时使用，因为系统使用 Spark 的 GroupBy 对数据进行排序，我们可以通过 Spark 来控制资源。</p>
    </div>
)

export const blockSize = (
    <div>
        <p>设置表的块大小，默认值是 1024 MB，这个属性只能设置在 1MB ~ 2048MB 之间。</p>
    </div>
)
export const marjorCompactionSize = (
    <div>
        <p>segment大小总和低于此阈值的将会被合并，默认为512MB。</p>
        <p>segment：每次将数据插入表时，会产生一个segment。</p>
    </div>
)
export const autoLoadMerge = (
    <div>
        <p>数据加载的时候是否启用压缩，默认为关闭。</p>
    </div>
)
export const compactionLevelThreshold = (
    <div>
        <p>该属性在 minor compaction 时使用，决定要合并多少个 segment。比如：如果将这个属性设置为 2, 3，那么每 2 个 segment 会触发一次 Level 1 的 minor compaction。每 3 个 Level 1 的 compacted segment 将会进一步压缩成新的 segment。
默认值为4,3，2个数字中间用英文逗号隔开。</p>
        <p>segment：每次将数据插入表时，会产生一个segment。</p>
    </div>
)
export const compactionPreserveSegments = (
    <div>
        <p>如果需要避免一些 segment 被压缩，可以通过设置这个参数。比如设置为2，那么 2 个最新的 segment 总是被排除在压缩之外。默认为0，即没有 segment 被保留。</p>
        <p>segment：每次将数据插入表时，会产生一个segment。</p>
    </div>
)
export const allowedCompactionDays = (
    <div>
        <p>在指定的天数内加载的 segment 将被合并。如果配置为 2，仅在 2 天内加载的 segment 被合并，2 天之前的 segment 不会被合并。默认为0，即没有被启用。</p>
        <p>segment：每次将数据插入表时，会产生一个segment。</p>
    </div>
)
export const compressMode = (
    <div>
        <p>Major：在系统空闲时自动合并segment</p>
        <p>Minor：在每次插入数据时合并segment</p>
    </div>
)
export const decimalType = (
    <div>
        <p>type(precision,scale);presicion:数字总长度，最大为38；scale：小数点之后的位数</p>
    </div>
)