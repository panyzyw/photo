package com.yongyida.robot.photos.utils;

public class StringToDate {
	public static String[] parse(String text){
		String result[] = new String[2];
		String result1 = "";
		String result2 = "";
		char[] c = text.toCharArray();
		
		String year = "";
		String month = "";
		String date = "";
		int yearEnd = 0;
		boolean isContinue1 = true;
		for(int i = 0; i < c.length; i++){
			if(c[i] == '年' && isContinue1){
				yearEnd = i;
				isContinue1 = false;
			}
		}
		if(yearEnd - 4 >= 0){
			year = text.substring(yearEnd - 4, yearEnd);
		}else{
			return null;
		}
		
		int monthEnd = 0;
		boolean isContinue2 = true;
		for(int i = 0; i < c.length; i++){
			if(c[i] == '月' && isContinue2){
				monthEnd = i;
				isContinue2 = false;
			}
		}
		if(monthEnd - 2  >= 0){
			String str = text.substring(monthEnd - 2, monthEnd);
			char[] m = str.toCharArray();
			if(m[0] == '0' || m[0] == '1' ||m[0] == '2' ||m[0] == '3' ||m[0] == '4' ||m[0] == '5' ||
					m[0] == '6' ||m[0] == '7' ||m[0] == '8' || m[0] == '9' ){
				month = text.substring(monthEnd - 2, monthEnd);
			}else{
				month = "0" + text.substring(monthEnd - 1, monthEnd);
			}
			
		}else{
			return null;
		}
		
		int dateEnd = 0;
		boolean isContinue3 = true;
		for(int i = 0; i < c.length; i++){
			if((c[i] == '日' || c[i] == '号') && isContinue3){
				dateEnd = i;
				isContinue3 = false;
			}
		}
		if(dateEnd - 2  >= 0){
			String str = text.substring(dateEnd - 2, dateEnd);
			char[] m = str.toCharArray();
			if(m[0] == '0' || m[0] == '1' ||m[0] == '2' ||m[0] == '3' ||m[0] == '4' ||m[0] == '5' ||
					m[0] == '6' ||m[0] == '7' ||m[0] == '8' || m[0] == '9' ){
				date = text.substring(dateEnd - 2, dateEnd);
			}else{
				date = "0" +text.substring(dateEnd - 1, dateEnd);
			}
			
		}
		result[0] = year +"年" +month +"月"+ date;
		result[1] = year + month + date;
		
		
		return result;
		
	}

}
