import React from "react";
import Layout from "@theme/Layout";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import Intro from "../components/intro";
import Features from "../components/features";
import Case from '../components/case';
import "./index.scss";

export default function Home() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout title={siteConfig.title} description={siteConfig.tagline}>
      <Intro />
      <Features />
      <Case />
    </Layout>
  );
}
