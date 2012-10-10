package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Source 
{
	private int m_id;
	private String m_name;
	private String m_url;
	private String m_location;
	private Connection m_conn;
	
	public Source(Connection conn)
	{
		m_conn = conn;
	}
	
	public int getId() 
	{
		return m_id;
	}
	public String getName() 
	{
		return m_name;
	}

	public void setName(String name) 
	{
		this.m_name = name;
	}

	public String getUrl() 
	{
		return m_url;
	}

	public void setUrl(String url) 
	{
		this.m_url = url;
	}

	public String getLocation() 
	{
		return m_location;
	}

	public void setLocation(String location) 
	{
		this.m_location = location;
	}
	
	public void getSourceByName(String name)
	{
		ResultSet rs;
		m_id = -1;
		m_url = null;
		m_location = null;
		
		try {
			rs = m_conn.createStatement().executeQuery("SELECT id,url,location FROM SOURCE WHERE name = '" + name.trim() + "'");

			while (rs.next())
			{
				m_id = rs.getInt("id");
				m_url = rs.getString("url");
				m_location = rs.getString("location");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
