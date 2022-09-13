import React from "react";
import Link from "@docusaurus/Link";
import useBaseUrl from "@docusaurus/useBaseUrl";
import styles from "./intro.module.scss";

export default function Intro() {
  return (
    <div className={styles.introduction}>
      <div className={styles.leftContainer}>
        <div className={styles.title}>Taier — 分布式调度系统</div>
        <hr className={styles.divider} role="divider" />
        <div className={styles.description}>
          旨在降低 ETL
          开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本
        </div>
        <Link to={useBaseUrl("/docs/quickstart/deploy/docker")}>
          <button className={styles.btn} type="button">
            快速开始
          </button>
        </Link>
      </div>
      <div className={styles.rightContainer}>
        <img src="https://streamxhub.com/home/streamx-banner.png" />
      </div>
    </div>
  );
}
