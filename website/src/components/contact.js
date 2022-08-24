import React, { useState } from "react";
import Link from "@docusaurus/Link";
import useBaseUrl from "@docusaurus/useBaseUrl";
import { GithubIcon, DingTalkIcon } from "./icon";
import styles from "./contact.module.scss";
import clsx from "clsx";

export default function Contact() {
  const [isRotate, setRotate] = useState(false);

  return (
    <div className={styles.wrapper}>
      <div className="taier__container">
        <div className="taier__title">联系我们</div>
        <div className={styles.contactList}>
          <div className={styles.list}>
            <div className={styles.logoTitle}>
              <GithubIcon />
            </div>
            <div className={styles.desc}>在 Github 上联系我们</div>
            <Link to="https://github.com/DTStack/Taier/issues">
              <button className={styles.button}>Go</button>
            </Link>
          </div>
          <div
            className={clsx(styles.list, styles.rotateCard, isRotate && styles.active)}
          >
            <div className={clsx(styles.face, styles.front)}>
              <div className={styles.logoTitle}>
                <DingTalkIcon />
              </div>
              <div className={styles.desc}>加入我们的钉钉群</div>
              <button className={styles.button} onClick={() => setRotate(true)}>
                现在加入
              </button>
            </div>
            <div className={clsx(styles.face, styles.back)}>
              <img src={useBaseUrl("img/readme/ding.jpeg")} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
