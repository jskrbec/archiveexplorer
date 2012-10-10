package db;

import java.sql.Connection;
import java.sql.ResultSet;

public class EntityLocs 
{
	private int m_entityId;
	private String m_long;
	private String m_lat;
	private MonitoringDB m_monitoringDB = new MonitoringDB("EntityPerson.java");
	
	public EntityLocs(int entityId)
	{
		m_entityId = entityId;
	}
	
	public void read(Connection conn)
	{
		try{
			String query = "SELECT lat,long FROM ENTITY_LOCS " +
					"WHERE entity_id = '" + m_entityId + "'";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while(rs.next())
			{
				m_long = rs.getString("long");
				m_lat = rs.getString("lat");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getLat()
	{
		return m_lat;
	}
	public String getLong()
	{
		return m_long;
	}
}
