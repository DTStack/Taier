package com.dtstack.rdos.base.spark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class SparkClientTest {
	
	 private static ExecutorService executor = Executors.newCachedThreadPool();
	
	public static void main(String[] args){
		 SparkConf sparkConf = new SparkConf().setMaster("spark://172.16.1.65:7077");
		 SparkSession sparkSession = SparkSession
	            .builder().config(sparkConf)
	            .appName("rdos_share_job")
	            .enableHiveSupport()
	            .getOrCreate();
		 executor.submit(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				 for(int i=3;i<5;i++){
					 sparkSession.sql(String.format("CREATE TABLE %s (a int, b int, c int)", "engine_test"+i));
				 }
			}
			 
		 });

		 
		 executor.submit(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				 for(int i=7;i<9;i++){
					 sparkSession.sql(String.format("CREATE TABLE %s (a int, b int, c int)", "engine_test"+i));
				 }
			}
			 
		 });
	}
	 
}
