package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Author 
{
	private Connection m_conn;
	private int m_id;
	private String m_name;
	private MonitoringDB m_monitoringDB = new MonitoringDB("Author.java");
	
	public Author(Connection conn)
	{
		m_conn = conn;
	}
	
	public Author (int id, String name)
	{
		m_id = id;
		m_name = name;
	}
	
	public int getId()
	{
		return m_id;
	}
	public void setId(int id)
	{
		m_id = id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public List<Integer> getAuthorIdByName(String name) throws Exception
	{
		List<Integer> ids = new ArrayList<Integer>();
		
		String query = "SELECT id FROM AUTHOR WHERE lower(name) = '" + name.toLowerCase() + "'";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			ids.add(rs.getInt("id"));
		}
		if (ids.isEmpty())
		{
			query = "SELECT id FROM AUTHOR WHERE lower(name) LIKE '% " + name.toLowerCase() + " %' OR lower(name) LIKE '" + name.toLowerCase() + "%' OR lower(name) LIKE '% " + name.toLowerCase() + "' LIMIT 50";
			timeStart = System.currentTimeMillis();
			rs = m_conn.createStatement().executeQuery(query);
			timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				ids.add(rs.getInt("id"));
			}
		}
		return ids;
	}
	
	public void getAuthor() throws SQLException
	{
		String query = "SELECT id,name FROM AUTHOR WHERE id = " + m_id;
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			m_name = rs.getString("name");
		}
	}
}
