import React from "react";
import Link from "@docusaurus/Link";
import styles from "./intro.module.scss";

export default function Intro() {
  return (
    <div className={styles.introduction}>
      <div className={styles.leftContainer}>
        <div className={styles.title}>Taier</div>
        <div className={styles.description}>A Distributed Dispatching System</div>
        <hr className={styles.divider} role="divider" />
        <div className={styles.verbose}>
          Taier 是一个开源的分布式 DAG
          调度系统，专注不同任务的提交和调度。旨在降低 ETL
          开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本
        </div>
        <div className={styles.buttonGroups}>
          <button className={styles.startBtn} type="button">
            Get Started
          </button>
          <Link to="https://github.com/DTStack/Taier">
            <button className={styles.githubBtn} type="button">
              Github
            </button>
          </Link>
          <Link to="https://gitee.com/dtstack_dev_0/taier">
            <button className={styles.giteeBtn} type="button">
              Gitee
            </button>
          </Link>
        </div>
        <div className={styles.shields}>
          <img src="https://img.shields.io/github/release/Dtstack/Taier.svg" />
          <img src="https://img.shields.io/github/stars/Dtstack/Taier" />
          <img src="https://img.shields.io/github/forks/Dtstack/Taier" />
          <img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg" />
        </div>
      </div>
      <div className={styles.rightContainer}>
        <img src="https://streamxhub.com/home/streamx-banner.png" />
      </div>
    </div>
  );
}
