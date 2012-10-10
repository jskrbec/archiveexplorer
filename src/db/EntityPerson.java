package db;

import java.sql.Connection;
import java.sql.ResultSet;

public class EntityPerson 
{
	private int m_entityId;
	private String m_birthDate;
	private String m_birthPlace;
	private String m_deathDate;
	private String m_deathPlace;
	private String m_description;
	private MonitoringDB m_monitoringDB = new MonitoringDB("EntityPerson.java");
	
	public EntityPerson(int entityId)
	{
		m_entityId = entityId;
	}
	
	public void read(Connection conn)
	{
		try{
			String query = "SELECT birth_date,birth_place,death_date, death_place,description FROM ENTITY_PERSON " +
					"WHERE entity_id = '" + m_entityId + "'";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while(rs.next())
			{
				m_birthDate = rs.getString("birth_date");
				m_birthPlace = rs.getString("birth_place");
				m_birthPlace = m_birthPlace == null ? "" : m_birthPlace.replace("_",	" ");
				m_deathDate = rs.getString("death_date");
				m_deathPlace = rs.getString("death_place");
				m_deathPlace = m_deathPlace == null ? "" : m_deathPlace.replace("_",	" ");
				m_description = rs.getString("description");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getBirthDate()
	{
		return m_birthDate;
	}
	public String getBirthPlace()
	{
		return m_birthPlace;
	}
	public String getDeathDate()
	{
		return m_deathDate;
	}
	public String getDeathPlace()
	{
		return m_deathPlace;
	}
	public String getDescription()
	{
		return m_description;
	}
}
