package com.nineShadow.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class MyTimerTask extends TimerTask{
	private Timer timer=new Timer();
	private Long delaytime=null;//延迟时间/ms
	private Long starttime=null;//开始时间
	private Long endtime=null;//结束时间
	public abstract void run();
	
	public MyTimerTask(Long delaytime) {
		super();
		this.delaytime = delaytime;
	}

	public void starttimer(){
		timer.schedule(this,delaytime);
		starttime=new Date().getTime();
		endtime=starttime+delaytime;
	}
	
	public void endtimer(){
		timer.cancel();
	}
	
	public int getLeftTime(){
		if(endtime==null)
			return (int) (delaytime/1000);
		Long now=new Date().getTime();
		if(now<=0){
			return 0;
		}
		return (int)((endtime-now)/1000);
	}
	

}
