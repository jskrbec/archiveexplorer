package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Keyword 
{
	private Connection m_conn;
	private int m_id;
	private String m_name;
	private int m_count;
	private MonitoringDB m_monitoringDB = new MonitoringDB("Keyword.java");
	
	public Keyword(Connection conn)
	{
		m_conn = conn;
	}
	public Keyword(int id, String name)
	{
		m_id = id;
		m_name = name;
	}
	
	public int getKeywordIdByName(String name) throws Exception
	{
		m_id = 0; 
		String query = "SELECT id, name FROM KEYWORD WHERE lower(name) = '" + name.toLowerCase() + "'";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			if (m_id == 0)
			{
				m_id = rs.getInt("id");
			}
			else
			{
				throw new Exception("More than one keyword with same name." + rs.getString("name") + " " + name);
			}
		}
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	public void setName(String name)
	{
		m_name = name;
	}
	public int getId()
	{
		return m_id;
	}
	public void setCount(int count)
	{
		m_count = count;
	}
	public int getCount()
	{
		return m_count;
	}
	
	public void save() throws SQLException
	{
		String sql = "INSERT INTO KEYWORD (name) VALUES (?) RETURNING id";
		PreparedStatement insert = m_conn.prepareStatement(sql);
		insert.setString(1, getName());
		insert.execute();		
		ResultSet kwIds = insert.getResultSet();
		kwIds.next();
		m_id = kwIds.getInt(1);
		insert.close();
	}
	
	@Override
	public boolean equals(Object k)
	{
		return ((Keyword) k).getId() == m_id;
	}
}
