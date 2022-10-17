import React from "react";
import Link from "@docusaurus/Link";
import useBaseUrl from "@docusaurus/useBaseUrl";
import useThemeContext from "@theme/hooks/useThemeContext";
import ThumbUrl from "@site/static/img/assets/thumb.png";
import ThumbDarkUrl from "@site/static/img/assets/thumb_dark.png";
import ComputerUrl from "@site/static/img/assets/computer.png";
import ComputerDarkUrl from "@site/static/img/assets/computer_dark.png";
import ScreenUrl from "@site/static/img/assets/screen.png";
import ScreenDarkUrl from "@site/static/img/assets/screen_dark.png";
import CloudUrl from "@site/static/img/assets/cloud.png";
import CloudDarkUrl from "@site/static/img/assets/cloud_dark.png";
import Widget1Url from "@site/static/img/assets/widget1.png";
import Widget2Url from "@site/static/img/assets/widget2.png";
import Widget3Url from "@site/static/img/assets/widget3.png";
import Decorator1Url from "@site/static/img/assets/decorator1.png";
import Decorator2Url from "@site/static/img/assets/decorator2.png";
import Decorator3Url from "@site/static/img/assets/decorator3.png";
import Decorator4Url from "@site/static/img/assets/decorator4.png";
import Decorator1DarkUrl from "@site/static/img/assets/decorator1_dark.png";
import Decorator2DarkUrl from "@site/static/img/assets/decorator2_dark.png";
import Decorator3DarkUrl from "@site/static/img/assets/decorator3_dark.png";
import Decorator4DarkUrl from "@site/static/img/assets/decorator4_dark.png";

import styles from "./intro.module.scss";

export default function Intro() {
  const { isDarkTheme } = useThemeContext();

  return (
    <div className={styles.introduction}>
      <div className={styles.leftContainer}>
        <div className={styles.title}>Taier — 分布式调度系统</div>
        <hr className={styles.divider} role="divider" />
        <div className={styles.description}>
          旨在降低 ETL
          开发成本，解决任务之间复杂的依赖关系和提交、调度、运维带来的上手成本
        </div>
        <div>
          <Link to={useBaseUrl("/docs/quickstart/start")}>
            <button className={styles.btn} type="button">
              快速开始
            </button>
          </Link>
        </div>
      </div>
      <div className={styles.rightContainer}>
        <img
          src={isDarkTheme ? ThumbDarkUrl : ThumbUrl}
          width={700}
          className={styles.bgImg}
        />
        <img
          src={isDarkTheme ? ComputerDarkUrl : ComputerUrl}
          width={72}
          className={styles.computerImg}
        />
        <img
          src={isDarkTheme ? ScreenDarkUrl : ScreenUrl}
          width={71}
          className={styles.screenImg}
        />
        <img
          src={isDarkTheme ? CloudDarkUrl : CloudUrl}
          width={69}
          className={styles.cloudImg}
        />
        <img src={Widget1Url} width={17} className={styles.widget1Img} />
        <img src={Widget2Url} width={19} className={styles.widget2Img} />
        <img src={Widget3Url} width={19} className={styles.widget3Img} />
        <img
          src={isDarkTheme ? Decorator1DarkUrl : Decorator1Url}
          width={98}
          className={styles.decorator1Img}
        />
        <img
          src={isDarkTheme ? Decorator2DarkUrl : Decorator2Url}
          width={57}
          className={styles.decorator2Img}
        />
        <img
          src={isDarkTheme ? Decorator3DarkUrl : Decorator3Url}
          width={54}
          className={styles.decorator3Img}
        />
        <img
          src={isDarkTheme ? Decorator4DarkUrl : Decorator4Url}
          width={37}
          className={styles.decorator4Img}
        />
      </div>
    </div>
  );
}
