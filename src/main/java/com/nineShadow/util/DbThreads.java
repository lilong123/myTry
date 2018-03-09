package com.nineShadow.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
public class DbThreads {
	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
	
	public static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(30); 


	public static void executor(Runnable runnable) {
		
		executor.execute(runnable);
		int size = executor.getQueue().size();
		if (size > 500) {
			//LogManager.warn();
			System.out.println("DbThreads queue size：" + size);
		}
	}
}
