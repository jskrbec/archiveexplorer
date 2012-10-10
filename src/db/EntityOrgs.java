package db;

import java.sql.Connection;
import java.sql.ResultSet;

public class EntityOrgs 
{
	private int m_entityId;
	private String m_homepage;
	private MonitoringDB m_monitoringDB = new MonitoringDB("EntityPerson.java");
	
	public EntityOrgs(int entityId)
	{
		m_entityId = entityId;
	}
	
	public void read(Connection conn)
	{
		try{
			String query = "SELECT homepage FROM ENTITY_ORGS " +
					"WHERE entity_id = '" + m_entityId + "'";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while(rs.next())
			{
				m_homepage = rs.getString("homepage");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getHomepage()
	{
		return m_homepage;
	}
}
