import React from "react";
import { TagIcon } from "./icon";
import styles from "./features.module.scss";

export default function Features() {
  return (
    <div className={styles.wrapper}>
      <div className="taier__container">
        <div className="taier__title">主要功能</div>
        <div className={styles.cardGroups}>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;稳定性
            </div>
            <ul className={styles.cardContent}>
              <li>分布式扩展</li>
              <li>多租户多集群隔离</li>
              <li>对集群环境 0 侵入</li>
            </ul>
          </div>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;可扩展性
            </div>
            <ul className={styles.cardContent}>
              <li>自定义扩展任务插件</li>
              <li>自定义参数替换</li>
              <li>任务多版本支持</li>
            </ul>
          </div>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;兼容性
            </div>
            <ul className={styles.cardContent}>
              <li>向导、脚本多种模式</li>
              <li>兼容实时、离线任务</li>
              <li>支持对接不同版本的 Hadoop</li>
            </ul>
          </div>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;易上手
            </div>
            <ul className={styles.cardContent}>
              <li>可视化 DAG 配置</li>
              <li>IDE 式开发平台</li>
              <li>上下游依赖调度</li>
            </ul>
          </div>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;实时性
            </div>
            <ul className={styles.cardContent}>
              <li>集群资源实时监控</li>
              <li>数据指标实时获取</li>
            </ul>
          </div>
          <div className={styles.card} role="card">
            <div className={styles.cardTitle}>
              <TagIcon /> &nbsp;多版本支持
            </div>
            <ul className={styles.cardContent}>
              <li>支持对接不同版本的 Hadoop</li>
              <li>支持 Kerberos 认证</li>
              <li>支持大量任务类型</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
