import React, { useState } from "react";
import Link from "@docusaurus/Link";
import useBaseUrl from "@docusaurus/useBaseUrl";
import {
  CreateFileIcon,
  DelimiterIcon,
  EditIcon,
  PartitionIcon,
  ExecuteIcon,
  BarChartIcon,
} from "./icon";
import styles from "./process.module.scss";
import clsx from "clsx";

const steps = [
  {
    title: "集群配置",
    icon: <PartitionIcon />,
    children: [
      {
        title: "组件配置",
        link: "docs/functions/component/sftp",
      },
    ],
  },
  {
    title: "新建任务",
    icon: <CreateFileIcon />,
    children: [
      {
        title: "数据源",
        link: "docs/functions/datasource",
      },
      {
        title: "任务类型",
        link: "docs/functions/task/sync",
      },
    ],
  },
  {
    title: "编辑任务",
    icon: <EditIcon />,
  },
  {
    title: "执行任务",
    icon: <ExecuteIcon />,
    children: [
      {
        title: "依赖配置",
        link: "docs/functions/depend",
      },
      {
        title: "环境参数",
        link: "docs/functions/env-param",
      },
      {
        title: "任务参数",
        link: "docs/functions/task-param",
      },
    ],
  },
  {
    title: "运维中心",
    icon: <BarChartIcon />,
    link: "docs/functions/maintenance",
  },
];

export default function Process() {
  const [current, setCurrent] = useState("");

  return (
    <div className={styles.wrapper}>
      <div className="taier__container">
        <div className="taier__title">开发流程</div>
        <div className={styles.steps}>
          {steps.map((step, i) => (
            <React.Fragment key={step.title}>
              <div
                className={styles.step}
                onMouseEnter={() => setCurrent(step.title)}
                onMouseLeave={() => setCurrent("")}
              >
                <div className={styles.tag}>{step.icon}</div>
                {step.link ? (
                  <Link to={useBaseUrl(step.link)}>{step.title}</Link>
                ) : (
                  step.title
                )}
                {Boolean(step.children?.length) && (
                  <section
                    className={clsx(
                      styles.processLine,
                      current !== step.title && styles.disabled
                    )}
                  >
                    <ul>
                      {step.children.map((i) => (
                        <li>
                          <Link to={useBaseUrl(i.link)}>
                            <div className={styles.lineTitle}>{i.title}</div>
                          </Link>
                        </li>
                      ))}
                    </ul>
                  </section>
                )}
              </div>
              {i !== steps.length - 1 && (
                <span className={styles.delimiter}>
                  <DelimiterIcon />
                </span>
              )}
            </React.Fragment>
          ))}
        </div>
      </div>
    </div>
  );
}
