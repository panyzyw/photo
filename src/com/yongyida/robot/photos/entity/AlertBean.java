package com.yongyida.robot.photos.entity;

public class AlertBean {
	public String text;
	public Semantic semantic;
	public String operation;
	
	public class Semantic{
		public Slots slots;
	}
	
	public class Slots{
		public String content;
		public Datetime datetime;
		public String repeat;
		public String delword;
	}
	
	public class Datetime{
		public String date;		
		public String time;		
		public String dateOrig;
	}
}
