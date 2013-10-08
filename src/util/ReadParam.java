package util;

import java.util.StringTokenizer;

public class ReadParam {
	static Parameter param;	
	public static int totalSecone(String args1) {
		param=new Parameter();
		param.setDay(0);
		param.setHour(0);
		param.setMinute(0);
		param.setSecond(0);
		StringTokenizer st = new StringTokenizer(args1);
		String[] argv = new String[st.countTokens()];
		for(int i=0;i<argv.length;i++)
			argv[i] = st.nextToken();

		for(int i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
			{
				System.err.print("unknown option\n");
				break;
			}
			switch(argv[i-1].charAt(1))
			{
			    case 'd':
					param.setDay(atoi(argv[i]));
					break;
				case 'h':
					param.hour = atoi(argv[i]);
					param.setHour(atoi(argv[i]));
					break;
				case 'm':
					param.minute = atoi(argv[i]);
					break;
				case 's':
					param.second = atoi(argv[i]);
					break;
				default:
					System.err.print("参数错误\n");
			}
		}
		int secone=0;

		return param.day*24*60*60+param.hour*60*60+param.minute*60+param.second;
	}
	public static String totalTime(String args1) {
		param=new Parameter();
		param.day=0;
		param.hour=0;
		param.minute=0;
		param.second=0;
		StringTokenizer st = new StringTokenizer(args1);
		String[] argv = new String[st.countTokens()];
		for(int i=0;i<argv.length;i++)
			argv[i] = st.nextToken();

		for(int i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
			{
				System.err.print("unknown option\n");
				break;
			}
			switch(argv[i-1].charAt(1))
			{
			    case 'd':
					param.day= atoi(argv[i]);
					break;
				case 'h':
					param.hour = atoi(argv[i]);
					break;
				case 'm':
					param.minute = atoi(argv[i]);
					break;
				case 's':
					param.second = atoi(argv[i]);
					break;
				default:
					System.err.print("参数错误\n");
			}
		}

       StringBuffer sb=new StringBuffer();
       if(param.day!=0)sb.append(param.day+"天");
       if(param.hour!=0)sb.append(param.hour+"时");
       if(param.minute!=0)sb.append(param.minute+"分");
       if(param.second!=0)sb.append(param.second+"秒");
       
       
    	   

		return sb.toString();
	}
	public static double totalSpeed(String args2) {
		param=new Parameter();
		param.rate=0;
		StringTokenizer st = new StringTokenizer(args2);
		String[] argv = new String[st.countTokens()];
		for(int i=0;i<argv.length;i++)
			argv[i] = st.nextToken();

		for(int i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
			{
				System.err.print("unknown option\n");
				break;
			}
			switch(argv[i-1].charAt(1))
			{
			    case 'r':
					param.rate= atoi(argv[i]);
					break;
				default:
					System.err.print("参数错误\n");
			}
		}
		return param.rate*1000/3600;
	}

	public static String rateString(String args2) {
		param=new Parameter();
		param.rate=0;
		StringTokenizer st = new StringTokenizer(args2);
		String[] argv = new String[st.countTokens()];
		for(int i=0;i<argv.length;i++)
			argv[i] = st.nextToken();

		for(int i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
			{
				System.err.print("unknown option\n");
				break;
			}
			switch(argv[i-1].charAt(1))
			{
			    case 'r':
					param.rate= atoi(argv[i]);
					break;
				default:
					System.err.print("参数错误\n");
			}
		}
		return String.valueOf(param.rate);
	}
	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}
}
