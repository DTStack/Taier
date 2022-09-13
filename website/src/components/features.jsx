import React from "react";
import { FirstIcon, SecondIcon, ThirdIcon } from "./icon";
import StabilityUrl from "@site/static/img/assets/stability.png";
import ScalabilityUrl from "@site/static/img/assets/scalability.png";
import EasyUseUrl from "@site/static/img/assets/easyUse.png";
import styles from "./features.module.scss";

export default function Features() {
  return (
    <div className={styles.container}>
      <div className={styles.titleRow}>
        <span className={styles.title}>特性</span>
      </div>
      <div className={styles.cardGroups}>
        <div className={styles.card} role="card">
          <div className={styles.header}>
            <img className={styles.img} src={StabilityUrl} width={140} />
            <span className={styles.number}>
              <FirstIcon />
            </span>
          </div>
          <div className={styles.cardTitle}>稳定性</div>
          <div className={styles.description}>
            分布式扩展，多租户多集群隔离，对集群环境 0 侵入
          </div>
        </div>

        <div className={styles.card} role="card">
          <div className={styles.header}>
            <img className={styles.img} src={ScalabilityUrl} width={140} />
            <span className={styles.number}>
              <SecondIcon />
            </span>
          </div>
          <div className={styles.cardTitle}>可扩展性</div>
          <div className={styles.description}>
            自定义扩展任务插件，定义参数替换，任务多版本支持
          </div>
        </div>

        <div className={styles.card} role="card">
          <div className={styles.header}>
            <img className={styles.img} src={EasyUseUrl} width={140} />
            <span className={styles.number}>
              <ThirdIcon />
            </span>
          </div>
          <div className={styles.cardTitle}>易上手</div>
          <div className={styles.description}>
            可视化 DAG 配置，IDE 式开发平台，支持大量任务类型
          </div>
        </div>
      </div>
    </div>
  );
}
