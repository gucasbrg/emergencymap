package util;

public class Parameter {
	public static int day;
	public static int hour;
	public static int minute;
	public static int second;   //��󶼻�Ϊ��Ϊ��λ 
	public static double rate;  //��󶼻�Ϊm/sΪ��λ
	public static double Magnitude; //��
	public static int from;      //��·�ж����
	public static int to;
	public static int getDay() {
		return day;
	}
	public static void setDay(int day) {
		Parameter.day = day;
	}
	public static int getHour() {
		return hour;
	}
	public static void setHour(int hour) {
		Parameter.hour = hour;
	}
	public static int getMinute() {
		return minute;
	}
	public static void setMinute(int minute) {
		Parameter.minute = minute;
	}
	public static int getSecond() {
		return second;
	}
	public static void setSecond(int second) {
		Parameter.second = second;
	}
	public static double getRate() {
		return rate;
	}
	public static void setRate(double rate) {
		Parameter.rate = rate;
	}
	public static double getMagnitude() {
		return Magnitude;
	}
	public static void setMagnitude(double magnitude) {
		Magnitude = magnitude;
	}
	public static int getFrom() {
		return from;
	}
	public static void setFrom(int from) {
		Parameter.from = from;
	}
	public static int getTo() {
		return to;
	}
	public static void setTo(int to) {
		Parameter.to = to;
	}
	
	

}
