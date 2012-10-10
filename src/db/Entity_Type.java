package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Entity_Type 
{
	private Connection m_conn;
	private int m_id;
	private String m_comment;
	private String m_predicate;
	private String m_type;
	private String m_value;
	private MonitoringDB m_monitoringDB = new MonitoringDB("Entity_Type.java");
	
	public Entity_Type(Connection conn)
	{
		m_conn = conn;
	}
	public Entity_Type(int id, String comment,String predicate, String type, String value)
	{
		m_id = id;
		m_comment = comment;
		m_predicate = predicate;
		m_type = type;
		m_value = value;
	}
	
	public void save() throws SQLException
	{
		m_id = 0;
		String sql = "INSERT INTO ENTITY_TYPE (comment,predicate,type,value) VALUES (?,?,?,?) RETURNING id";
		PreparedStatement insert = m_conn.prepareStatement(sql);
		insert.setString(1, replaceChars(m_comment));
		insert.setString(2, m_predicate);
		insert.setNull(3, Types.NULL);
		insert.setString(4, replaceChars(m_value));
		insert.execute();		
		ResultSet ids = insert.getResultSet();
		ids.next();
		m_id = ids.getInt(1);
		insert.close();
	}
	
	public String getComment()
	{
		return m_comment;
	}
	public void setComment(String comment)
	{
		m_comment = comment;
	}
	public String getPredicate()
	{
		return m_predicate;
	}
	public void setPredicate(String predicate)
	{
		m_predicate = predicate;
	}
	public String getType()
	{
		return m_type;
	}
	public void setType(String type)
	{
		m_type = type;
	}
	public String getValue()
	{
		return m_value;
	}
	public void setValue(String value)
	{
		m_value = value;
	}
	public int getId()
	{
		return m_id;
	}
	
	@Override
	public boolean equals(Object k)
	{
		return ((Entity_Type) k).getId() == m_id;
	}
	
	private String replaceChars(String s)
	{
		if (s != null && !s.isEmpty())
		{
			s = s.replaceAll("&apos;", "&#39;");
			s = s.replaceAll("'", "&#39;");
			s = s.replaceAll("\"", "&quot;");
		}
		return s;
	}
}
