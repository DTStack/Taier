import React from "react";
import clsx from "clsx";
import useThemeContext from "@theme/hooks/useThemeContext";
import MultipleUrl from "@site/static/img/assets/multiple.png";
import MultipleDarkUrl from "@site/static/img/assets/multiple_dark.png";
import SparkSqlUrl from "@site/static/img/assets/sparksql.png";
import WorkflowUrl from "@site/static/img/assets/workflow.png";
import FlinkSqlUrl from "@site/static/img/assets/flinksql.png";
import ScheduleUrl from "@site/static/img/assets/schedule.png";
import ScheduleDarkUrl from "@site/static/img/assets/schedule_dark.png";
import BatchUrl from "@site/static/img/assets/batch.png";
import StreamUrl from "@site/static/img/assets/stream.png";
import styles from "./case.module.scss";

export default function Case() {
  const { isDarkTheme } = useThemeContext();

  return (
    <>
      <div className={styles.container}>
        <div className={styles.row}>
          <div className={styles.information}>
            <div className={styles.logo}>
              <img
                src={isDarkTheme ? MultipleDarkUrl : MultipleUrl}
                width={120}
              />
            </div>
            <div className={styles.title}>Multiple Tasks</div>
            <div className={styles.description}>
              Taier 支持众多数据类型，包括
              SparkSQL、数据同步、FlinkSQL、工作流等任务
            </div>
          </div>
          <div className={styles.showcases}>
            <div>
              <img src={WorkflowUrl} />
            </div>
            <div>
              <img src={FlinkSqlUrl} />
            </div>
            <div>
              <img src={SparkSqlUrl} />
            </div>
          </div>
        </div>
      </div>
      <div className={clsx(styles.container, styles.reverse)}>
        <div className={styles.row}>
          <div className={styles.information}>
            <div className={styles.logo}>
              <img
                src={isDarkTheme ? ScheduleDarkUrl : ScheduleUrl}
                width={120}
              />
            </div>
            <div className={styles.title}>Schedule Information</div>
            <div className={styles.description}>
              Taier
              支持运维中心查看任务调度相关信息，包括任务上下游关系、错误日志、运行结果等信息
            </div>
          </div>
          <div className={styles.showcases}>
            <div>
              <img src={StreamUrl} />
            </div>
            <div>
              <img src={BatchUrl} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
