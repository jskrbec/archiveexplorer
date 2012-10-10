package db;


public class MonitoringDB 
{
	private String m_callerName;
	
	public MonitoringDB(String callerName)
	{
		m_callerName = callerName;
	}
	
	public void queryTime(long timeStart, long timeStop, String query, int maxTime)
	{
		long qTime = timeStop - timeStart;
		if ((maxTime == 0 && qTime > 10)|| (maxTime != 0 && qTime > maxTime))
		{
			String shortStr = query.length() > 200 ? query.substring(0, 200) : query;
			System.out.println(qTime + "ms - " + m_callerName + ": " + shortStr);
		}
	}
}
