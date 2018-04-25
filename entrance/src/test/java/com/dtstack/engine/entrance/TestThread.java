package com.dtstack.engine.entrance;

public class TestThread {
	
	public static void main(String[] args) throws InterruptedException{
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(!Thread.currentThread().isInterrupted()){
					System.out.println("ysqsqs");
				}
			}
			
		});
		
		t.start();
		
		
		Thread.sleep(5000);
		
		t.interrupt();
		
		
	}

}
