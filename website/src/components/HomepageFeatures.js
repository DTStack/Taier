import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
    {
        title: '高稳定性',
        Svg: require('../../static/img/undraw_docusaurus_mountain.svg').default,
        description: (
            <>
                去中心化的分布式模式、水平扩容，数百家企业客户生产环境实战检验
            </>
        ),
    },
    {
        title: '易用性',
        Svg: require('../../static/img/undraw_docusaurus_tree.svg').default,
        description: (
            <>
                可视化任务开发配置、界面直接运行，
                调度属性、上下游依赖直接添加
            </>
        ),
    },
    {
        title: '多集群',
        Svg: require('../../static/img/undraw_docusaurus_react.svg').default,
        description: (
            <>
                多租户多集群隔离，能快速适配不同类型集群
            </>
        ),
    },
];

function Feature({Svg, title, description}) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                {/* <Svg className={styles.featureSvg} alt={title} /> */}
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
