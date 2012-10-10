package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class YearList extends ArrayList<Integer>
{
	private static final long serialVersionUID = 1L;

	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("YearList.java");
	
	public YearList(Connection conn)
	{
		m_conn = conn;
	}
	
	public void getYearsByArticleIds(String artIds,int sourceId)
	{
		try {
			String query;
			if (artIds == null || artIds.isEmpty())
			{
				if (sourceId > 0)
				{
					query = "SELECT DISTINCT(EXTRACT(YEAR FROM PUBLISH_DATE)) as year FROM CORPUS WHERE source_id = " + sourceId;
				}
				else
				{
					query = "SELECT DISTINCT(EXTRACT(YEAR FROM PUBLISH_DATE)) as year FROM CORPUS";
				}
			}
			else
			{
            	query = "SELECT DISTINCT(EXTRACT(YEAR FROM PUBLISH_DATE)) as year FROM CORPUS WHERE id IN (" + artIds + ") LIMIT 10";
			}
			
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				Integer year = rs.getInt("year"); 
				if (year != null && year > 1000)
				{
					this.add(year);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
