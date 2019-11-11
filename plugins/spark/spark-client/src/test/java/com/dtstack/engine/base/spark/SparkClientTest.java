package com.dtstack.engine.base.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class SparkClientTest {
	
	 private static  SparkSession sparkSession = null;
	 public static void main(String[] args){
		 SparkConf sparkConf = new SparkConf().setMaster("spark://172.16.1.65:7077");
		 sparkSession = SparkSession
	            .builder().config(sparkConf)
	            .appName("rdos_share_job11")
	            .enableHiveSupport()
	            .getOrCreate();
		sparkSession.sql("CREATE TABLE rdos_ysq.rdos_engine_test (a int, b int, c int)");
		sparkSession.close();
	}
}
